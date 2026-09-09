package Jogo;

import java.awt.Color;
import java.awt.Graphics;

public class Boss {
    public double x, y;
    public int width = 70;
    public int height = 70;
    public double speed = 1.2;
    public int maxHp = 300;
    public int hp = 300;

    public Boss(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double angle = Math.atan2(dy, dx);

        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
    }

    public void draw(Graphics g) {
        // Cor vermelha sangue bem escura
        g.setColor(new Color(139, 0, 0));
        g.fillRect((int) x, (int) y, width, height);

        // Barra de vida do Boss
        g.setColor(Color.RED);
        g.fillRect((int) x, (int) y - 10, width, 6);

        g.setColor(Color.GREEN);
        int healthBarWidth = (int) ((double) hp / maxHp * width);
        g.fillRect((int) x, (int) y - 10, healthBarWidth, 6);
    }
}