package Jogo.core.entities;

import java.awt.*;

public class XpOrb {
    public int x, y;
    public int width = 10;
    public int height = 10;
    public int xpValue = 1;

    public XpOrb(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g, int cameraX, int cameraY) {
        int screenX = this.x - cameraX;
        int screenY = this.y - cameraY;

        g.setColor(Color.MAGENTA); // ou a cor que você usa para o XP
        g.fillOval(screenX, screenY, width, height);
    }
}
