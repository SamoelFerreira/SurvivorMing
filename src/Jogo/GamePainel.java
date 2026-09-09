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
import java.awt.image.BufferedImage;

public class GamePainel extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running = true;

    private enum GameState {CHARACTER_SELECT, PLAYING, LEVEL_UP, STAGE_CLEAR, SHOP, GAME_OVER}
    private GameState gameState = GameState.PLAYING;

    Player player;
    private int pendingLevelUps = 0;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<XpOrb> xpOrbs;
    private List<Magnet> magnets;
    private EnemyManager enemyManager;
    private Boss boss = null;
    private Random random = new Random();

    private SphereSkillTree sphereSkillTree = new SphereSkillTree();
    int gold = 0;
    private int currentStage = 1;

    private Rectangle btnNextStage = new Rectangle(700, 580, 200, 50);
    private Rectangle btnSkillTree = new Rectangle(700, 510, 200, 50);

    private int score = 0;
    private int shootTimer = 0;
    public int shootInterval = 1;

    private boolean up, down, left, right;
    private int selectedCharacter = 1;
    private final BufferedImage[] characterPreviews = new BufferedImage[5];
    private final Rectangle[] characterCards = new Rectangle[5];

    private int magnetSpawnTimer = 0;
    private int magnetSpawnInterval = 900;

    private int dashCooldownTimer = 0;
    private final int dashCooldownMax = 1800;
    private boolean canDash = true;

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
        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        carregarPreviewsPersonagens();
        initGame();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState == GameState.CHARACTER_SELECT) {
                    if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_5) {
                        selectedCharacter = e.getKeyCode() - KeyEvent.VK_0;
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        player = criarPersonagemSelecionado();
                        sphereSkillTree = new SphereSkillTree(player.getNome());
                        gameState = GameState.PLAYING;
                    }
                    return;
                }
                if (gameState == GameState.GAME_OVER) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        restartGame();
                    }
                    return;
                }

                if (gameState == GameState.LEVEL_UP || gameState == GameState.STAGE_CLEAR || gameState == GameState.SHOP) {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) up = true;
                if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) down = true;
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) left = true;
                if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) right = true;

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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState == GameState.LEVEL_UP) {
                    for (int i = 0; i < upgradeRects.length; i++) {
                        if (upgradeRects[i] != null && upgradeRects[i].contains(e.getPoint())) {
                            if (i < currentUpgrades.size()) {
                                applyUpgrade(currentUpgrades.get(i).id);
                            }
                        }
                    }
                } else if (gameState == GameState.CHARACTER_SELECT) {
                    for (int i = 0; i < characterCards.length; i++) {
                        if (characterCards[i] != null && characterCards[i].contains(e.getPoint())) {
                            selectedCharacter = i + 1;
                            repaint();
                            break;
                        }
                    }
                } else if (gameState == GameState.STAGE_CLEAR) {
                    if (btnSkillTree.contains(e.getPoint())) {
                        gameState = GameState.SHOP;
                    } else if (btnNextStage.contains(e.getPoint())) {
                        currentStage++;
                        score = 0;
                        boss = new Boss(400, 50, "CHEFE DA FASE " + currentStage, 25 + (currentStage * 15), 0.5 + (currentStage * 0.1), currentStage % 3 == 0 ? 3 : currentStage % 2 == 0 ? 2 : 1);
                        gameState = GameState.PLAYING;
                    }
                } else if (gameState == GameState.SHOP) {
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
        player = criarPersonagemSelecionado();
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
        gameState = GameState.CHARACTER_SELECT;
    }

        private void carregarPreviewsPersonagens() {
        characterPreviews[0] = Player.carregarPreview(
            "/sprites/RazzaBug_Archer", "Archer_Idle.png", 6);
        characterPreviews[1] = Player.carregarPreview(
            "/sprites/MaziMage_Lancer", "Lancer_Idle.png", 12);
        characterPreviews[2] = Player.carregarPreview(
            "/sprites/Rafengels_Warrior", "Warrior_Idle.png", 8);
        characterPreviews[3] = Player.carregarPreview(
            "/sprites/PutinhaRica_Monk", "Idle.png", 6);
        characterPreviews[4] = Player.carregarPreview(
            "/sprites/TotoLove_Pawn", "Pawn_Idle.png", 8);
        }

    private Player criarPersonagemSelecionado() {
        switch (selectedCharacter) {
            case 2: return new MaziMage(800, 450);
            case 3: return new Rafengels(800, 450);
            case 4: return new PutinhaRica(800, 450);
            case 5: return new TotoLove(800, 450);
            default: return new RazzaBug(800, 450);
        }
    }

    private void restartGame() {
        currentStage = 1;
        gold = 0;
        selectedCharacter = 1;
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
            case 1: player.speed += 1; break;
            case 2: if (shootInterval > 10) shootInterval -= 5; break;
            case 3: player.maxHp += 20; player.hp = player.maxHp; break;
            case 4: player.hp = Math.min(player.maxHp, player.hp + (player.maxHp / 2)); break;
            case 5: shootInterval = Math.max(5, shootInterval - 8); break;
        }
        pendingLevelUps--;
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
        if (!canDash) {
            dashCooldownTimer++;
            if (dashCooldownTimer >= dashCooldownMax) {
                canDash = true;
                dashCooldownTimer = 0;
            }
        }

        magnetSpawnTimer++;
        if (magnetSpawnTimer >= magnetSpawnInterval) {
            magnetSpawnTimer = 0;
            int randomX = player.x - 400 + random.nextInt(800);
            int randomY = player.y - 400 + random.nextInt(800);
            magnets.add(new Magnet(randomX, randomY));
            magnetSpawnInterval *= 2;
        }

        player.update(up, down, left, right);
        player.atualizarPassiva(enemies);

        enemyManager.update(enemies, player.x, player.y);

        shootTimer++;
        int personagemShootInterval = Math.max(1, (int) (shootInterval / player.getVelocidadeAtaque()));
        if (shootTimer >= personagemShootInterval && (!enemies.isEmpty() || boss != null)) {
            shootTimer = 0;
            shootAtClosestEnemy();
        }

        for (Bullet bullet : bullets) bullet.update();
        for (Enemy enemy : enemies) enemy.update(player.x, player.y);
        if (boss != null) boss.update(player.x, player.y);

        // Colisões de Tiros
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet bullet = bIter.next();
            Rectangle bulletRect = new Rectangle((int) bullet.x, (int) bullet.y, bullet.width, bullet.height);
            boolean bulletHit = false;

            Iterator<Enemy> eIter = enemies.iterator();
            while (eIter.hasNext()) {
                Enemy enemy = eIter.next();
                Rectangle enemyRect = new Rectangle((int) enemy.x, (int) enemy.y, enemy.width, enemy.height);

                if (bulletRect.intersects(enemyRect)) {
                    bIter.remove();
                    enemy.hp -= (int) Math.ceil(bullet.damage);
                    if (enemy.hp <= 0) {
                        xpOrbs.add(new XpOrb((int) enemy.x + 10, (int) enemy.y + 10));
                        player.aoMatarInimigo(false, 5);
                        gold += player instanceof PutinhaRica ? 6 : 5;
                        eIter.remove();
                        score++;
                    }
                    bulletHit = true;
                    break;
                }
            }

            if (bulletHit) continue;

            if (boss != null) {
                Rectangle bossRect = new Rectangle((int) boss.x, (int) boss.y, boss.width, boss.height);
                if (bulletRect.intersects(bossRect)) {
                    bIter.remove();
                    boss.hp -= (int) Math.ceil(bullet.damage);
                    if (boss.hp <= 0) {
                        player.aoMatarInimigo(true, currentStage * 50);
                        gold += (int) Math.round(currentStage * 50 * (player instanceof PutinhaRica ? 1.3 : 1.0));
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

        // Coleta de XP
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

        // Coleta de Ímã
        Iterator<Magnet> magnetIter = magnets.iterator();
        while (magnetIter.hasNext()) {
            Magnet magnet = magnetIter.next();
            Rectangle magnetRect = new Rectangle(magnet.x, magnet.y, magnet.width, magnet.height);
            if (playerRect.intersects(magnetRect)) {
                magnetIter.remove();
                for (XpOrb orb : xpOrbs) {
                    while (player.gainXp(orb.xpValue)) pendingLevelUps++;
                }
                xpOrbs.clear();
                if (pendingLevelUps > 0 && gameState == GameState.PLAYING) {
                    rollRandomUpgrades();
                    gameState = GameState.LEVEL_UP;
                }
                break;
            }
        }

        // Dano no Player
        for (Enemy enemy : enemies) {
            Rectangle enemyRect = new Rectangle((int) enemy.x, (int) enemy.y, enemy.width, enemy.height);
            if (playerRect.intersects(enemyRect) && !enemy.isAliado()) {
                player.receberDano(1);
                if (player.hp <= 0) gameState = GameState.GAME_OVER;
            }
        }

        if (boss != null) {
            Rectangle bossRect = new Rectangle((int) boss.x, (int) boss.y, boss.width, boss.height);
            if (playerRect.intersects(bossRect)) {
                player.receberDano(2);
                if (player.hp <= 0) gameState = GameState.GAME_OVER;
            }
        }
    }

    private void shootAtClosestEnemy() {
        EntityTarget closest = null;
        double minDistance = Double.MAX_VALUE;

        // Raio máximo de alcance do tiro (ex: 700 pixels ao redor do player)
        double maxShootRange = 450.0;

        for (Enemy enemy : enemies) {
            double distSq = Math.pow(enemy.x - player.x, 2) + Math.pow(enemy.y - player.y, 2);

            // Verifica se é o mais próximo E se está dentro do alcance máximo
            if (distSq < minDistance && distSq <= (maxShootRange * maxShootRange)) {
                minDistance = distSq;
                closest = new EntityTarget(enemy.x + (enemy.width / 2.0), enemy.y + (enemy.height / 2.0));
            }
        }

        if (boss != null) {
            double distBossSq = Math.pow(boss.x - player.x, 2) + Math.pow(boss.y - player.y, 2);
            if (distBossSq < minDistance && distBossSq <= (maxShootRange * maxShootRange)) {
                minDistance = distBossSq;
                closest = new EntityTarget(boss.x + (boss.width / 2.0), boss.y + (boss.height / 2.0));
            }
        }

        // Só dispara se encontrou um alvo válido dentro da distância permitida
        if (closest != null) {
            double startX = player.x + (player.width / 2.0);
            double startY = player.y + (player.height / 2.0);
            player.triggerAttack();
            bullets.add(new Bullet(startX, startY, closest.x, closest.y, player.getDanoAtaque()));
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
        Graphics2D g2d = (Graphics2D) g;

        int screenWidth = 1600;
        int screenHeight = 900;

        // --- APLICAR ZOOM DA CÂMERA AQUI ---
        double zoom = 1.25; // Aumente para aproximar mais (ex: 1.3 ou 1.4) ou diminua (ex: 1.1)
        g2d.scale(zoom, zoom);

        // Como o zoom redimensiona a tela, recalculamos a largura/altura efetiva para a câmera centralizar certo:
        int adjustedWidth = (int) (screenWidth / zoom);
        int adjustedHeight = (int) (screenHeight / zoom);

        int cameraX = player.x - (adjustedWidth / 2) + (player.width / 2);
        int cameraY = player.y - (adjustedHeight / 2) + (player.height / 2);

        // 2. CENÁRIO INFINITO (Grid)
        int tileSize = 64;
        int startX = cameraX / tileSize - 1;
        int endX = startX + (screenWidth / tileSize) + 3;
        int startY = cameraY / tileSize - 1;
        int endY = startY + (screenHeight / tileSize) + 3;

        for (int row = startY; row <= endY; row++) {
            for (int col = startX; col <= endX; col++) {
                int worldX = col * tileSize;
                int worldY = row * tileSize;
                int screenDrawX = worldX - cameraX;
                int screenDrawY = worldY - cameraY;

                g2d.setColor(new Color(35, 35, 35));
                g2d.fillRect(screenDrawX, screenDrawY, tileSize, tileSize);
                g2d.setColor(new Color(45, 45, 45));
                g2d.drawRect(screenDrawX, screenDrawY, tileSize, tileSize);
            }
        }

        // 3. DESENHO DAS ENTIDADES COM A CÂMERA APLICADA
        for (XpOrb orb : xpOrbs) orb.draw(g2d, cameraX, cameraY);
        for (Magnet magnet : magnets) magnet.draw(g2d, cameraX, cameraY);
        for (Enemy enemy : enemies) enemy.draw(g2d, cameraX, cameraY);
        if (boss != null) boss.draw(g2d, cameraX, cameraY);
        for (Bullet bullet : bullets) bullet.draw(g2d, cameraX, cameraY);

        player.draw(g2d, cameraX, cameraY);

        // --- HUD PRINCIPAL (Fixo na Tela) ---
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Fase: " + currentStage, 20, 30);
        g2d.drawString("Ouro: " + gold + " 🪙", 20, 55);
        g2d.drawString("Nível: " + player.level, 20, 80);
        g2d.drawString("XP: " + player.currentXp + " / " + player.nextLevelXp, 20, 105);
        g2d.drawString("HP: " + player.hp + " / " + player.maxHp, 20, 130);

        if (canDash) {
            g2d.setColor(Color.CYAN);
            g2d.drawString("DASH [SPACE]: PRONTO!", 20, 160);
        } else {
            int segundosRestantes = (dashCooldownMax - dashCooldownTimer) / 60;
            g2d.setColor(Color.GRAY);
            g2d.drawString("DASH [SPACE]: Recarregando (" + segundosRestantes + "s)", 20, 160);
        }

        if (gameState == GameState.CHARACTER_SELECT) {
            g2d.setColor(new Color(0, 0, 0, 235));
            g2d.fillRect(0, 0, screenWidth, screenHeight);
            g2d.setColor(Color.CYAN);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("ESCOLHA SEU PERSONAGEM", screenWidth / 2 - 190, 120);

            String[] nomes = {"RazzaBug", "MaziMage", "Rafengels", "PutinhaRica", "TotoLove"};
            String[] descricoes = {
                    "Glitch aleatorio: velocidade e stun em area",
                    "Fica invisivel com pouca vida e regenera",
                    "Recebe 30% menos dano",
                    "Ganha mais ouro e dano conforme enriquece",
                    "Pode converter ate 5 inimigos em aliados"
            };
            for (int i = 0; i < nomes.length; i++) {
                int cardX = 180 + i * 250;
                int cardY = 260;
                characterCards[i] = new Rectangle(cardX, cardY, 220, 220);
                g2d.setColor(i + 1 == selectedCharacter ? new Color(20, 100, 120) : new Color(35, 35, 45));
                g2d.fillRect(cardX, cardY, 220, 220);
                g2d.setColor(i + 1 == selectedCharacter ? Color.YELLOW : Color.GRAY);
                g2d.drawRect(cardX, cardY, 220, 220);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString((i + 1) + ". " + nomes[i], cardX + 15, cardY + 40);

                if (characterPreviews[i] != null) {
                    g2d.drawImage(characterPreviews[i], cardX + 65, cardY + 52, 90, 90, null);
                } else {
                    g2d.setColor(Color.RED);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.drawString("Sprite indisponível", cardX + 48, cardY + 100);
                }

                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.setColor(Color.WHITE);
                g2d.drawString(descricoes[i], cardX + 15, cardY + 85);
            }
            g2d.setColor(Color.GREEN);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("Pressione 1-5 para selecionar e ENTER para iniciar", screenWidth / 2 - 260, 600);
        }

        // --- MENU LEVEL UP ---
        if (gameState == GameState.LEVEL_UP) {
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 26));
            g2d.drawString("SUBIU DE NÍVEL - ESCOLHA UM UPGRADE", screenWidth / 2 - 280, 150);

            int cardWidth = 220;
            int cardHeight = 300;
            int startCardX = screenWidth / 2 - (cardWidth * 3 + 40) / 2;
            int startCardY = 220;

            for (int i = 0; i < currentUpgrades.size(); i++) {
                UpgradeOption opt = currentUpgrades.get(i);
                int cardX = startCardX + i * (cardWidth + 20);
                int cardY = startCardY;

                upgradeRects[i] = new Rectangle(cardX, cardY, cardWidth, cardHeight);

                if (i == hoveredUpgradeIndex) {
                    g2d.setColor(new Color(70, 70, 100));
                    g2d.fillRect(cardX - 4, cardY - 4, cardWidth + 8, cardHeight + 8);
                    g2d.setColor(new Color(45, 45, 75));
                } else {
                    g2d.setColor(new Color(25, 25, 35));
                }
                g2d.fillRect(cardX, cardY, cardWidth, cardHeight);

                g2d.setColor(i == hoveredUpgradeIndex ? Color.CYAN : Color.GRAY);
                g2d.drawRect(cardX, cardY, cardWidth, cardHeight);

                g2d.setColor(Color.YELLOW);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString("OPÇÃO " + (i + 1), cardX + 20, cardY + 40);

                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString(opt.title, cardX + 20, cardY + 90);

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString(opt.description, cardX + 20, cardY + 140);

                g2d.setColor(Color.GREEN);
                g2d.setFont(new Font("Arial", Font.ITALIC, 13));
                g2d.drawString("Clique para escolher", cardX + 20, cardY + 260);
            }
        }

        // --- TELA DE TRANSIÇÃO DE FASE (STAGE_CLEAR) ---
        if (gameState == GameState.STAGE_CLEAR) {
            g2d.setColor(new Color(0, 0, 0, 230));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("FASE " + currentStage + " CONCLUÍDA!", screenWidth / 2 - 180, 250);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Ouro Coletado: " + gold + " 🪙", screenWidth / 2 - 90, 320);
            g2d.drawString("Escolha sua próxima ação:", screenWidth / 2 - 110, 420);

            // Botão Esfera de Skills
            g2d.setColor(new Color(50, 50, 100));
            g2d.fillRect(btnSkillTree.x, btnSkillTree.y, btnSkillTree.width, btnSkillTree.height);
            g2d.setColor(Color.CYAN);
            g2d.drawRect(btnSkillTree.x, btnSkillTree.y, btnSkillTree.width, btnSkillTree.height);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("Esfera de Skills", btnSkillTree.x + 35, btnSkillTree.y + 30);

            // Botão Próxima Fase
            g2d.setColor(new Color(0, 100, 0));
            g2d.fillRect(btnNextStage.x, btnNextStage.y, btnNextStage.width, btnNextStage.height);
            g2d.setColor(Color.GREEN);
            g2d.drawRect(btnNextStage.x, btnNextStage.y, btnNextStage.width, btnNextStage.height);
            g2d.drawString("Próxima Fase ➡️", btnNextStage.x + 35, btnNextStage.y + 30);
        }

        // --- TELA DA LOJA / ESFERA ---
        if (gameState == GameState.SHOP) {
            sphereSkillTree.draw(g2d, gold, getWidth(), getHeight());
        }

        // --- TELA DE GAME OVER ---
        if (gameState == GameState.GAME_OVER) {
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300);

            g2d.setColor(Color.RED);
            g2d.drawRect(screenWidth / 2 - 200, screenHeight / 2 - 150, 400, 300);

            g2d.setFont(new Font("Arial", Font.BOLD, 26));
            g2d.drawString("FIM DE JOGO", screenWidth / 2 - 90, screenHeight / 2 - 90);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            g2d.drawString("Fase alcançada: " + currentStage, screenWidth / 2 - 95, screenHeight / 2 - 30);
            g2d.drawString("Nível alcançado: " + player.level, screenWidth / 2 - 95, screenHeight / 2 + 5);

            g2d.setColor(Color.YELLOW);
            g2d.drawString("Pressione [ R ] para Recomeçar", screenWidth / 2 - 130, screenHeight / 2 + 80);
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