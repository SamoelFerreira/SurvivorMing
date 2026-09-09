package Jogo;

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

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(x, y, width, height);
    }
}
