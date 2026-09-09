package Jogo;

import java.awt.*;

public class Enemy {
    public double x, y;
    public int width = 35;
    public int height = 35;
    public double speed = 1.5;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            x += (dx / distance) * speed;
            y += (dy / distance) * speed;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Corpo principal do inimigo (estilo monstro/drone blindado)
        g2d.setColor(new Color(80, 30, 100));
        g2d.fillOval((int) x, (int) y, width, height);

        // Borda de contorno
        g2d.setColor(new Color(140, 60, 180));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval((int) x, (int) y, width, height);

        // Olhos brilhantes ameaçadores (vermelhos)
        g2d.setColor(Color.RED);
        g2d.fillRect((int) x + 7, (int) y + 11, 6, 6);
        g2d.fillRect((int) x + 22, (int) y + 11, 6, 6);

        // Brilho central nos olhos
        g2d.setColor(Color.YELLOW);
        g2d.fillRect((int) x + 9, (int) y + 13, 2, 2);
        g2d.fillRect((int) x + 24, (int) y + 13, 2, 2);
    }
}