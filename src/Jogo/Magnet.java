package Jogo;

import java.awt.*;

public class Magnet {
    public int x, y;
    public int width = 16;
    public int height = 16;

    public Magnet(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void draw(Graphics g) {
        // Desenha um ítem roxo/magenta representando o Ímã
        g.setColor(Color.MAGENTA);
        g.fillRect(x, y, width, height);

        // Um detalhezinho visual no meio para parecer um ítem especial
        g.setColor(Color.WHITE);
        g.fillRect(x + 4, y + 4, width - 8, height - 8);
    }
}
