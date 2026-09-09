package Jogo;

import java.awt.*;

public class Magnet {
    public int x, y;
    public int width = 25;
    public int height = 25;

    // Construtor que recebe as posições onde o ímã vai nascer
    public Magnet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int cameraX, int cameraY) {
        // Converte a posição do mundo para a tela usando a câmera
        int screenX = this.x - cameraX;
        int screenY = this.y - cameraY;

        Graphics2D g2d = (Graphics2D) g;

        // Desenho visual do Ímã
        g2d.setColor(Color.CYAN);
        g2d.fillRect(screenX, screenY, width, height);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(screenX, screenY, width, height);
    }
}
