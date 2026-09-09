package Jogo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePainel extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running = true;

    // Estados do Jogo atualizados com as novas telas de transição e loja
    private enum GameState { PLAYING, LEVEL_UP, STAGE_CLEAR, SHOP, GAME_OVER }
    private GameState gameState = GameState.PLAYING;

    Player player;
    private int pendingLevelUps = 0; // Guarda quantos níveis faltam o jogador escolher
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<XpOrb> xpOrbs;
    private List<Magnet> magnets;
    private EnemyManager enemyManager;
    private Boss boss = null;
    private Random random = new Random();

    // Instanciação da nova árvore de esferas e economia
    private SphereSkillTree sphereSkillTree = new SphereSkillTree();
    int gold = 0;
    private int currentStage = 1;

    // Botões da tela de transição de fase (STAGE_CLEAR)
    private Rectangle btnNextStage = new Rectangle(300, 480, 200, 50);
    private Rectangle btnSkillTree = new Rectangle(300, 410, 200, 50);

    private int score = 0;
    private int shootTimer = 0;
    public int shootInterval = 1;

    private boolean up, down, left, right;

    // Variáveis do Ímã Progressivo
    private int magnetSpawnTimer = 0;
    private int magnetSpawnInterval = 900;

    // Variáveis do Dash
    private int dashCooldownTimer = 0;
    private final int dashCooldownMax = 1800; // 30 segundos (60 FPS * 30)
    private boolean canDash = true;

    // Estrutura para os Upgrades Aleatórios via Mouse
    private static class UpgradeOption {
        String title;
        String description;
        int id;

        public UpgradeOption(String title, String description, int id) {
            this.title = title;
            this.description = description;
            this.id = id;
        }
    }

    private List<UpgradeOption> currentUpgrades = new ArrayList<>();
    private Rectangle[] upgradeRects = new Rectangle[3];
    private int hoveredUpgradeIndex = -1;

    public GamePainel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        initGame();

        // Controles de Teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState == GameState.GAME_OVER) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        restartGame();
                    }
                    return;
                }

                if (gameState == GameState.LEVEL_UP || gameState == GameState.STAGE_CLEAR || gameState == GameState.SHOP) {
                    return; // Nos menus usamos o mouse
                }

                if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) up = true;
                if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) down = true;
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) left = true;
                if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) right = true;

                // Aciona o Dash com a tecla Space
                if (e.getKeyCode() == KeyEvent.VK_SPACE && canDash && gameState == GameState.PLAYING) {
                    player.dash(up, down, left, right);
                    canDash = false;
                    dashCooldownTimer = 0;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) up = false;
                if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) down = false;
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) left = false;
                if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) right = false;
            }
        });

        // Controles de Mouse para os Menus do Jogo
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 1. Menu de Level Up (durante a partida)
                if (gameState == GameState.LEVEL_UP) {
                    for (int i = 0; i < upgradeRects.length; i++) {
                        if (upgradeRects[i] != null && upgradeRects[i].contains(e.getPoint())) {
                            if (i < currentUpgrades.size()) {
                                applyUpgrade(currentUpgrades.get(i).id);
                            }
                        }
                    }
                }
                // 2. Menu de Transição de Fase (quando mata o Boss)
                else if (gameState == GameState.STAGE_CLEAR) {
                    if (btnSkillTree.contains(e.getPoint())) {
                        gameState = GameState.SHOP; // Abre a árvore de skills
                    } else if (btnNextStage.contains(e.getPoint())) {
                        currentStage++;
                        score = 0;
                        // Cria o próximo Boss com mais HP e velocidade progressiva
                        boss = new Boss(400, 50, "CHEFE DA FASE " + currentStage, 25 + (currentStage * 15), 0.5 + (currentStage * 0.1), currentStage % 3 == 0 ? 3 : currentStage % 2 == 0 ? 2 : 1);
                        gameState = GameState.PLAYING;
                    }
                }
                // 3. Menu da Loja / Árvore de Habilidades em Esfera
                else if (gameState == GameState.SHOP) {
                    boolean keepInShop = sphereSkillTree.handleClick(e, GamePainel.this);
                    if (!keepInShop) {
                        gameState = GameState.STAGE_CLEAR;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == GameState.LEVEL_UP) {
                    hoveredUpgradeIndex = -1;
                    for (int i = 0; i < upgradeRects.length; i++) {
                        if (upgradeRects[i] != null && upgradeRects[i].contains(e.getPoint())) {
                            hoveredUpgradeIndex = i;
                            break;
                        }
                    }
                    repaint();
                }
            }
        });
    }

    private void initGame() {
        player = new Player(380, 280);
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        xpOrbs = new ArrayList<>();
        magnets = new ArrayList<>();
        enemyManager = new EnemyManager();
        boss = null;
        score = 0;
        shootInterval = 45;
        magnetSpawnTimer = 0;
        magnetSpawnInterval = 900;
        dashCooldownTimer = 0;
        canDash = true;
        gameState = GameState.PLAYING;
    }

    private void restartGame() {
        currentStage = 1;
        gold = 0;
        initGame();
    }

    private void rollRandomUpgrades() {
        List<UpgradeOption> pool = new ArrayList<>();
        pool.add(new UpgradeOption("Super Velocidade", "+1 de Velocidade de Movimento", 1));
        pool.add(new UpgradeOption("Cadência Rápida", "Disparos mais velozes", 2));
        pool.add(new UpgradeOption("Armadura Pesada", "+20 de HP Máximo e Cura Total", 3));
        pool.add(new UpgradeOption("Vitalidade", "Cura 50% do HP atual", 4));
        pool.add(new UpgradeOption("Fúria de Ataque", "Reduz drasticamente o intervalo de tiro", 5));

        Collections.shuffle(pool);
        currentUpgrades.clear();
        for (int i = 0; i < 3 && i < pool.size(); i++) {
            currentUpgrades.add(pool.get(i));
        }
    }

    private void applyUpgrade(int id) {
        switch (id) {
            case 1:
                player.speed += 1;
                break;
            case 2:
                if (shootInterval > 10) shootInterval -= 5;
                break;
            case 3:
                player.maxHp += 20;
                player.hp = player.maxHp;
                break;
            case 4:
                player.hp = Math.min(player.maxHp, player.hp + (player.maxHp / 2));
                break;
            case 5:
                shootInterval = Math.max(5, shootInterval - 8);
                break;
        }

        // Diminui um nível pendente que acabou de ser escolhido
        pendingLevelUps--;

        // Se ainda sobrou nível para escolher, abre os upgrades de novo
        if (pendingLevelUps > 0) {
            rollRandomUpgrades();
            gameState = GameState.LEVEL_UP;
        } else {
            gameState = GameState.PLAYING;
        }
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            if (gameState == GameState.PLAYING) {
                update();
            }
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // 1. Gerencia o cooldown do Dash
        if (!canDash) {
            dashCooldownTimer++;
            if (dashCooldownTimer >= dashCooldownMax) {
                canDash = true;
                dashCooldownTimer = 0;
            }
        }

        // 2. Atualiza o temporizador do ímã no mapa
        magnetSpawnTimer++;
        if (magnetSpawnTimer >= magnetSpawnInterval) {
            magnetSpawnTimer = 0;
            int randomX = 50 + random.nextInt(700);
            int randomY = 50 + random.nextInt(500);
            magnets.add(new Magnet(randomX, randomY));
            magnetSpawnInterval *= 2;
        }

        // 3. Atualiza o Jogador e a Horda de Inimigos
        player.update(up, down, left, right);
        enemyManager.update(enemies);

        // 4. Sistema de Tiro Automático
        shootTimer++;
        if (shootTimer >= shootInterval && (!enemies.isEmpty() || boss != null)) {
            shootTimer = 0;
            shootAtClosestEnemy();
        }

        // 5. Atualiza a posição de todos os projéteis
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
        }

        // 6. Atualiza a perseguição dos inimigos comuns em direção ao Player
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update(player.x, player.y);
        }

        // 7. Atualiza o Boss (se ele estiver vivo na tela)
        if (boss != null) {
            boss.update(player.x, player.y);
        }

        // --- COLISÕES ---
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet bullet = bIter.next();
            if (bullet.x < 0 || bullet.x > 800 || bullet.y < 0 || bullet.y > 600) {
                bIter.remove();
                continue;
            }

            Rectangle bulletRect = new Rectangle((int) bullet.x, (int) bullet.y, bullet.width, bullet.height);
            boolean bulletHit = false;

            // Colisão: Tiro x Inimigo Comum
            Iterator<Enemy> eIter = enemies.iterator();
            while (eIter.hasNext()) {
                Enemy enemy = eIter.next();
                Rectangle enemyRect = new Rectangle((int) enemy.x, (int) enemy.y, enemy.width, enemy.height);

                if (bulletRect.intersects(enemyRect)) {
                    bIter.remove();
                    xpOrbs.add(new XpOrb((int) enemy.x + 10, (int) enemy.y + 10));
                    eIter.remove();
                    score++;
                    bulletHit = true;

                    // Spawn dos Chefes baseado na pontuação
                    if (score == 30 && boss == null) {
                        boss = new Boss(400, 50, "MUTANTE ALPHA", 25, 0.6, 1);
                    } else if (score == 90 && boss == null) {
                        boss = new Boss(400, 50, "COLOSSO DE AÇO", 45, 0.4, 2);
                    } else if (score == 180 && boss == null) {
                        boss = new Boss(400, 50, "DESTRUIDOR FINAL", 75, 0.5, 3);
                    }
                    break;
                }
            }

            if (bulletHit) continue;

            // Colisão: Tiro x Boss
            if (boss != null) {
                Rectangle bossRect = new Rectangle((int) boss.x, (int) boss.y, boss.width, boss.height);
                if (bulletRect.intersects(bossRect)) {
                    bIter.remove();
                    boss.hp--;

                    if (boss.hp <= 0) {
                        int goldReward = currentStage * 50;
                        gold += goldReward;

                        enemies.clear();
                        xpOrbs.clear();
                        magnets.clear();
                        bullets.clear();

                        boss = null;
                        gameState = GameState.STAGE_CLEAR;
                    }
                }
            }
        }

        Rectangle playerRect = new Rectangle(player.x, player.y, player.width, player.height);

        // Colisão: Player x XP
        Iterator<XpOrb> xpIter = xpOrbs.iterator();
        while (xpIter.hasNext()) {
            XpOrb orb = xpIter.next();
            Rectangle orbRect = new Rectangle(orb.x, orb.y, orb.width, orb.height);

            if (playerRect.intersects(orbRect)) {
                xpIter.remove();
                while (player.gainXp(orb.xpValue)) {
                    pendingLevelUps++;
                }
                if (pendingLevelUps > 0 && gameState == GameState.PLAYING) {
                    rollRandomUpgrades();
                    gameState = GameState.LEVEL_UP;
                }
                break;
            }
        }

        // Colisão: Player x Ímã
        boolean collectedMagnet = false;
        Iterator<Magnet> magnetIter = magnets.iterator();
        while (magnetIter.hasNext()) {
            Magnet magnet = magnetIter.next();
            Rectangle magnetRect = new Rectangle(magnet.x, magnet.y, magnet.width, magnet.height);

            if (playerRect.intersects(magnetRect)) {
                collectedMagnet = true;
                magnetIter.remove();
                break;
            }
        }

        if (collectedMagnet) {
            for (XpOrb orb : xpOrbs) {
                while (player.gainXp(orb.xpValue)) {
                    pendingLevelUps++;
                }
            }
            xpOrbs.clear();

            if (pendingLevelUps > 0 && gameState == GameState.PLAYING) {
                rollRandomUpgrades();
                gameState = GameState.LEVEL_UP;
            }
        }

        // Colisão: Inimigo Comum x Player (Seguro com Iterator caso queira remover ou apenas interagir)
        Iterator<Enemy> enemyDamageIter = enemies.iterator();
        while (enemyDamageIter.hasNext()) {
            Enemy enemy = enemyDamageIter.next();
            Rectangle enemyRect = new Rectangle((int) enemy.x, (int) enemy.y, enemy.width, enemy.height);
            if (playerRect.intersects(enemyRect)) {
                player.hp -= 1;
                if (player.hp <= 0) {
                    gameState = GameState.GAME_OVER;
                }
            }
        }

        // Colisão: Boss x Player
        if (boss != null) {
            Rectangle bossRect = new Rectangle((int) boss.x, (int) boss.y, boss.width, boss.height);
            if (playerRect.intersects(bossRect)) {
                player.hp -= 2;
                if (player.hp <= 0) {
                    gameState = GameState.GAME_OVER;
                }
            }
        }
    }

    private void shootAtClosestEnemy() {
        EntityTarget closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double dist = Math.pow(enemy.x - player.x, 2) + Math.pow(enemy.y - player.y, 2);
            if (dist < minDistance) {
                minDistance = dist;
                closest = new EntityTarget(enemy.x + (enemy.width / 2.0), enemy.y + (enemy.height / 2.0));
            }
        }

        if (boss != null) {
            double distBoss = Math.pow(boss.x - player.x, 2) + Math.pow(boss.y - player.y, 2);
            if (distBoss < minDistance) {
                minDistance = distBoss;
                closest = new EntityTarget(boss.x + (boss.width / 2.0), boss.y + (boss.height / 2.0));
            }
        }

        if (closest != null) {
            double startX = player.x + (player.width / 2.0);
            double startY = player.y + (player.height / 2.0);
            bullets.add(new Bullet(startX, startY, closest.x, closest.y));
        }
    }

    private static class EntityTarget {
        double x, y;
        public EntityTarget(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (XpOrb orb : xpOrbs) orb.draw(g);
        for (Magnet magnet : magnets) magnet.draw(g);
        for (Enemy enemy : enemies) enemy.draw(g);
        if (boss != null) boss.draw(g);
        for (Bullet bullet : bullets) bullet.draw(g);
        player.draw(g);

        // HUD Principal
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Fase: " + currentStage, 20, 30);
        g.drawString("Ouro: " + gold + " 🪙", 20, 55);
        g.drawString("Nível: " + player.level, 20, 80);
        g.drawString("XP: " + player.currentXp + " / " + player.nextLevelXp, 20, 105);
        g.drawString("HP: " + player.hp + " / " + player.maxHp, 20, 130);

        // Status do Dash
        if (canDash) {
            g.setColor(Color.CYAN);
            g.drawString("DASH [SPACE]: PRONTO!", 20, 160);
        } else {
            int segundosRestantes = (dashCooldownMax - dashCooldownTimer) / 60;
            g.setColor(Color.GRAY);
            g.drawString("DASH [SPACE]: Recarregando (" + segundosRestantes + "s)", 20, 160);
        }

        // Menu Level Up Interativo com Mouse (Cards)
        if (gameState == GameState.LEVEL_UP) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 26));
            g.drawString("SUBIU DE NÍVEL - ESCOLHA UM UPGRADE", 140, 90);

            int cardWidth = 220;
            int cardHeight = 300;
            int startX = getWidth() / 2 - (cardWidth * 3 + 40) / 2;
            int startY = 140;

            for (int i = 0; i < currentUpgrades.size(); i++) {
                UpgradeOption opt = currentUpgrades.get(i);
                int cardX = startX + i * (cardWidth + 20);
                int cardY = startY;

                upgradeRects[i] = new Rectangle(cardX, cardY, cardWidth, cardHeight);

                if (i == hoveredUpgradeIndex) {
                    g.setColor(new Color(70, 70, 100));
                    g.fillRect(cardX - 4, cardY - 4, cardWidth + 8, cardHeight + 8);
                    g.setColor(new Color(45, 45, 75));
                } else {
                    g.setColor(new Color(25, 25, 35));
                }
                g.fillRect(cardX, cardY, cardWidth, cardHeight);

                g.setColor(i == hoveredUpgradeIndex ? Color.CYAN : Color.GRAY);
                g.drawRect(cardX, cardY, cardWidth, cardHeight);

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 18));
                g.drawString("OPÇÃO " + (i + 1), cardX + 20, cardY + 40);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString(opt.title, cardX + 20, cardY + 90);

                g.setColor(Color.LIGHT_GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString(opt.description, cardX + 20, cardY + 140);

                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.ITALIC, 13));
                g.drawString("Clique para escolher", cardX + 20, cardY + 260);
            }
        }

        // Tela de Transição de Fase (STAGE_CLEAR)
        if (gameState == GameState.STAGE_CLEAR) {
            g.setColor(new Color(0, 0, 0, 230));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("FASE " + currentStage + " CONCLUÍDA!", 240, 150);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Ouro Coletado: " + gold + " 🪙", 330, 220);
            g.drawString("Escolha sua próxima ação:", 300, 320);

            // Botão Árvore de Skills
            g.setColor(new Color(50, 50, 100));
            g.fillRect(btnSkillTree.x, btnSkillTree.y, btnSkillTree.width, btnSkillTree.height);
            g.setColor(Color.CYAN);
            g.drawRect(btnSkillTree.x, btnSkillTree.y, btnSkillTree.width, btnSkillTree.height);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Esfera de Skills", btnSkillTree.x + 35, btnSkillTree.y + 30);

            // Botão Próxima Fase
            g.setColor(new Color(0, 100, 0));
            g.fillRect(btnNextStage.x, btnNextStage.y, btnNextStage.width, btnNextStage.height);
            g.setColor(Color.GREEN);
            g.drawRect(btnNextStage.x, btnNextStage.y, btnNextStage.width, btnNextStage.height);
            g.drawString("Próxima Fase ➡️", btnNextStage.x + 35, btnNextStage.y + 30);
        }

        // Tela da Loja / Esfera de Habilidades (SHOP)
        if (gameState == GameState.SHOP) {
            sphereSkillTree.draw(g, gold, getWidth(), getHeight());
        }

        // Tela de Game Over
        if (gameState == GameState.GAME_OVER) {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(200, 150, 400, 300);

            g.setColor(Color.RED);
            g.drawRect(200, 150, 400, 300);

            g.setFont(new Font("Arial", Font.BOLD, 26));
            g.drawString("FIM DE JOGO", 310, 210);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Fase alcançada: " + currentStage, 305, 270);
            g.drawString("Nível alcançado: " + player.level, 305, 305);

            g.setColor(Color.YELLOW);
            g.drawString("Pressione [ R ] para Recomeçar", 270, 380);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jogo de Horda - Vampire Style");
        GamePainel game = new GamePainel();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.startGame();
    }
}