package Jogo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player {
    public int x, y;
    public int width = 96, height = 96;
    public int speed = 5;
    public int maxHp = 100;
    public int hp = 100;

    // Sistema de XP
    public int level = 1;
    public int currentXp = 0;
    public int nextLevelXp = 100;

    // Animações e Estados
    private BufferedImage[] idleFrames;
    private BufferedImage[] runFrames;
    private BufferedImage[] shootFrames;
    private boolean facingLeft = false;

    private int animFrame = 0;
    private int animTimer = 0;
    private int frameDelay = 8; // Velocidade da animação

    private boolean isMoving = false;
    private boolean isAttacking = false;
    private int attackTimer = 0;
    private final int attackDuration = 20; // Quantos frames a animação de tiro dura

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        carregarSprites();
    }

    public void faceTarget(double targetX) {
        if (targetX < this.x) {
            facingLeft = true;
        } else {
            facingLeft = false;
        }
    }

    private void carregarSprites() {
        SpriteSheet idleLoader = new SpriteSheet("/sprites/archeridle.png");
        idleFrames = idleLoader.cortarFrames(6);

        SpriteSheet runLoader = new SpriteSheet("/sprites/archerrun.png");
        runFrames = runLoader.cortarFrames(4);

        SpriteSheet shootLoader = new SpriteSheet("/sprites/archershoot.png");
        shootFrames = shootLoader.cortarFrames(8);
    }

    public void triggerAttack() {
        isAttacking = true;
        attackTimer = 0;
    }

    public void update(boolean up, boolean down, boolean left, boolean right) {
        isMoving = false;

        if (up) { y -= speed; isMoving = true; }
        if (down) { y += speed; isMoving = true; }
        if (left) {
            x -= speed;
            isMoving = true;
            facingLeft = true;  // Adiciona isso aqui
        }
        if (right) {
            x += speed;
            isMoving = true;
            facingLeft = false; // Adiciona isso aqui
        }

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

        // Desenha o frame atual na tela com suporte a espelhamento horizontal
        if (currentFrames != null && currentFrames[frameIndex] != null) {
            BufferedImage frameToDraw = currentFrames[frameIndex];

            if (facingLeft) {
                // Inverte horizontalmente (-width e screenX + width)
                g.drawImage(frameToDraw, screenX + width, screenY, -width, height, null);
            } else {
                // Desenha normalmente
                g.drawImage(frameToDraw, screenX, screenY, width, height, null);
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(screenX, screenY, width, height);
        }
    }
}