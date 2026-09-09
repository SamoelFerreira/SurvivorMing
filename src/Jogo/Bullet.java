package Jogo;

import java.awt.*;

public class Bullet {
    public double x, y;
    public double velocityX, velocityY;
    public int width = 10;
    public int height = 10;
    public double speed = 7.0; // Velocidade do tiro

    public Bullet(double startX, double startY, double targetX, double targetY) {
        this.x = startX;
        this.y = startY;

        // Calcula a direção rumo ao alvo (inimigo)
        double diffX = targetX - startX;
        double diffY = targetY - startY;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (distance > 0) {
            this.velocityX = (diffX / distance) * speed;
            this.velocityY = (diffY / distance) * speed;
        }
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public void draw(Graphics g, int cameraX, int cameraY) {
        int screenX = (int) this.x - cameraX;
        int screenY = (int) this.y - cameraY;

        g.setColor(Color.YELLOW);
        g.fillOval(screenX, screenY, width, height);
    }
}