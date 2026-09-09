package Jogo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Personagem {
    public int x, y;
    public int width = 48, height = 48;
    public double speed = 5;
    protected final java.util.Random random = new java.util.Random();

    // Sistema de XP
    public int level = 1;
    public int currentXp = 0;
    public int nextLevelXp = 100;

    // Animações e Estados
    private BufferedImage[] idleFrames;
    private BufferedImage[] runFrames;
    private BufferedImage[] shootFrames;

    private int animFrame = 0;
    private int animTimer = 0;
    private int frameDelay = 8; // Velocidade da animação

    private boolean isMoving = false;
    private boolean isAttacking = false;
    private int attackTimer = 0;
    private final int attackDuration = 20; // Quantos frames a animação de tiro dura

    public Player(int startX, int startY) {
        this(startX, startY, "Arqueiro", 100, 15, 5, 1.0);
    }

    protected Player(int startX, int startY, String nome, int vidaMax, double danoAtaque,
                     double velocidadeMovimento, double velocidadeAtaque,
                     String spriteFolder, String idleSprite, int idleFramesCount,
                     String runSprite, int runFramesCount,
                     String attackSprite, int attackFramesCount) {
        super(nome, vidaMax, danoAtaque, velocidadeMovimento, velocidadeAtaque);
        this.x = startX;
        this.y = startY;
        this.speed = velocidadeMovimento;
        carregarSprites(spriteFolder, idleSprite, idleFramesCount, runSprite, runFramesCount,
                attackSprite, attackFramesCount);
    }

    private Player(int startX, int startY, String nome, int vidaMax, double danoAtaque,
                   double velocidadeMovimento, double velocidadeAtaque) {
        this(startX, startY, nome, vidaMax, danoAtaque, velocidadeMovimento, velocidadeAtaque,
                "/sprites", "archeridle.png", 6, "archerrun.png", 4, "archershoot.png", 8);
    }

    private void carregarSprites(String spriteFolder, String idleSprite, int idleFramesCount,
                                 String runSprite, int runFramesCount,
                                 String attackSprite, int attackFramesCount) {
        idleFrames = carregarFrames(spriteFolder, idleSprite, idleFramesCount);
        runFrames = carregarFrames(spriteFolder, runSprite, runFramesCount);
        shootFrames = carregarFrames(spriteFolder, attackSprite, attackFramesCount);
    }

    private BufferedImage[] carregarFrames(String folder, String fileName, int frameCount) {
        SpriteSheet loader = new SpriteSheet(folder + "/" + fileName);
        return loader.cortarFrames(frameCount);
    }

    public static BufferedImage carregarPreview(String folder, String fileName, int frameCount) {
        SpriteSheet loader = new SpriteSheet(folder + "/" + fileName);
        BufferedImage[] frames = loader.cortarFrames(frameCount);
        return frames.length == 0 ? null : frames[0];
    }

    public void triggerAttack() {
        isAttacking = true;
        attackTimer = 0;
    }

    public void update(boolean up, boolean down, boolean left, boolean right) {
        isMoving = false;

        if (up) { y -= (int) speed; isMoving = true; }
        if (down) { y += (int) speed; isMoving = true; }
        if (left) { x -= (int) speed; isMoving = true; }
        if (right) { x += (int) speed; isMoving = true; }

        // Controla o tempo da animação de tiro
        if (isAttacking) {
            attackTimer++;
            if (attackTimer >= attackDuration) {
                isAttacking = false;
            }
        }

        // Atualiza os quadros da animação
        animTimer++;
        if (animTimer >= frameDelay) {
            animTimer = 0;
            animFrame++;
        }
    }

    public void dash(boolean up, boolean down, boolean left, boolean right) {
        int dashDistance = 80;
        if (up) y -= dashDistance;
        if (down) y += dashDistance;
        if (left) x -= dashDistance;
        if (right) x += dashDistance;

        if (!up && !down && !left && !right) {
            y -= dashDistance;
        }
    }

    public boolean gainXp(int amount) {
        currentXp += amount;
        if (currentXp >= nextLevelXp) {
            currentXp -= nextLevelXp;
            level++;
            nextLevelXp += 50;
            return true;
        }
        return false;
    }

    @Override
    public void atualizarPassiva(java.util.List<Enemy> inimigos) {
        // O personagem base nao possui passiva adicional.
    }

    public void draw(Graphics g, int cameraX, int cameraY) {
        int screenX = this.x - cameraX;
        int screenY = this.y - cameraY;

        // Sombra oval nos pés
        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(screenX + 6, screenY + height - 8, width - 12, 8);

        // Define qual array de imagens usar baseado no estado atual
        BufferedImage[] currentFrames = idleFrames;

        if (isAttacking && shootFrames != null && shootFrames.length > 0) {
            currentFrames = shootFrames;
        } else if (isMoving && runFrames != null && runFrames.length > 0) {
            currentFrames = runFrames;
        }

        // Evita erro caso o array esteja vazio
        if (currentFrames == null || currentFrames.length == 0) {
            currentFrames = idleFrames;
        }

        int frameIndex = animFrame % currentFrames.length;

        // Desenha o frame atual na tela
        if (currentFrames != null && currentFrames[frameIndex] != null) {
            g.drawImage(currentFrames[frameIndex], screenX, screenY, width, height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(screenX, screenY, width, height);
        }
    }
}