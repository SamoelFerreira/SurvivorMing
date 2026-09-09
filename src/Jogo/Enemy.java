package Jogo;

import java.awt.*;

public class Enemy {
    public double x, y;
    public int width = 35;
    public int height = 35;
    public double speed = 1.5;
    public int hp = 15;
    private int stunTimer;
    private int allyTimer;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double playerX, double playerY) {
        if (stunTimer > 0) {
            stunTimer--;
            return;
        }
        if (allyTimer > 0) {
            allyTimer--;
            return;
        }
        double dx = playerX - x;
        double dy = playerY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    public void stun(int duration) { stunTimer = Math.max(stunTimer, duration); }

    public void converter(int duration) { allyTimer = Math.max(allyTimer, duration); }

    public boolean isAliado() { return allyTimer > 0; }

    public void draw(Graphics g, int cameraX, int cameraY) {
        Graphics2D g2d = (Graphics2D) g;

        // Converte a posição do mundo para a posição na tela usando a câmera
        int screenX = (int) this.x - cameraX;
        int screenY = (int) this.y - cameraY;

        // Sombra oval nos pés do inimigo para profundidade
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fillOval(screenX + 3, screenY + height - 6, width - 6, 8);

        // Corpo principal do inimigo (estilo monstro/drone blindado)
        g2d.setColor(new Color(80, 30, 100));
        g2d.fillOval(screenX, screenY, width, height);

        // Borda de contorno
        g2d.setColor(new Color(140, 60, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(screenX, screenY, width, height);

        // Olhos brilhantes ameaçadores (vermelhos)
        g2d.setColor(Color.RED);
        g2d.fillRect(screenX + 7, screenY + 11, 6, 6);
        g2d.fillRect(screenX + 22, screenY + 11, 6, 6);

        // Brilho central nos olhos
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(screenX + 9, screenY + 13, 2, 2);
        g2d.fillRect(screenX + 24, screenY + 13, 2, 2);
    }
}