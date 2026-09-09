package Jogo;

import java.awt.*;

public class Boss {
    public double x, y;
    public int width = 110;
    public int height = 110;
    public double speed;
    public int hp;
    public int maxHp;
    public String nome;

    // Identificador para sabermos qual chefe é (muda a cor/estilo visual)
    private int tipoBoss;

    // Construtor dinâmico que aceita o nome, vida, velocidade e o tipo visual
    public Boss(double x, double y, String nome, int hp, double speed, int tipoBoss) {
        this.x = x;
        this.y = y;
        this.nome = nome;
        this.hp = hp;
        this.maxHp = hp;
        this.speed = speed;
        this.tipoBoss = tipoBoss;
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

        // Cores mudam dependendo do tipo do boss para ficar visualmente único
        Color corAura, corCorpo, corPlaca, corOlho, corDestaque;

        if (tipoBoss == 1) {
            // Chefe 1: Mutante Roxo/Vermelho original
            corAura = new Color(40, 0, 60, 150);
            corCorpo = new Color(45, 30, 55);
            corPlaca = new Color(20, 20, 25);
            corOlho = Color.RED;
            corDestaque = new Color(180, 0, 0);
        } else if (tipoBoss == 2) {
            // Chefe 2: Colosso de Aço (Tons de Azul Metálico / Gelado)
            corAura = new Color(0, 40, 60, 150);
            corCorpo = new Color(30, 45, 55);
            corPlaca = new Color(15, 25, 35);
            corOlho = Color.CYAN;
            corDestaque = new Color(0, 100, 180);
        } else {
            // Chefe 3: Destruidor Final (Tons de Laranja Fogo / Ameaçador)
            corAura = new Color(60, 20, 0, 150);
            corCorpo = new Color(55, 35, 30);
            corPlaca = new Color(25, 15, 10);
            corOlho = Color.ORANGE;
            corDestaque = new Color(200, 80, 0);
        }

        // Sombra/Aura tóxica
        g2d.setColor(corAura);
        g2d.fillOval((int) x - 10, (int) y - 10, width + 20, height + 20);

        // Corpo principal
        g2d.setColor(corCorpo);
        g2d.fillOval((int) x, (int) y, width, height);

        // Placas de blindagem
        g2d.setColor(corPlaca);
        g2d.fillRect((int) x + 10, (int) y + 10, width - 20, height - 20);

        // Olhos múltiplos brilhantes
        g2d.setColor(corOlho);
        g2d.fillOval((int) x + 25, (int) y + 35, 14, 14);
        g2d.fillOval((int) x + 70, (int) y + 35, 14, 14);
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int) x + 30, (int) y + 40, 4, 4);
        g2d.fillOval((int) x + 75, (int) y + 40, 4, 4);

        // Olho central extra
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int) x + 48, (int) y + 25, 12, 12);

        // Mandíbula / Boca rasgada
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int) x + 30, (int) y + 75, 50, 20);

        g2d.setColor(Color.WHITE);
        int[] denteX1 = {(int)x+35, (int)x+40, (int)x+45};
        int[] denteY1 = {(int)y+75, (int)y+90, (int)y+75};
        g2d.fillPolygon(denteX1, denteY1, 3);

        int[] denteX2 = {(int)x+50, (int)x+55, (int)x+60};
        int[] denteY2 = {(int)y+75, (int)y+90, (int)y+75};
        g2d.fillPolygon(denteX2, denteY2, 3);

        int[] denteX3 = {(int)x+65, (int)x+70, (int)x+75};
        int[] denteY3 = {(int)y+75, (int)y+90, (int)y+75};
        g2d.fillPolygon(denteX3, denteY3, 3);

        // Espinhos e garras
        g2d.setColor(corDestaque);
        g2d.fillRect((int) x - 8, (int) y + 20, 12, 8);
        g2d.fillRect((int) x - 8, (int) y + 70, 12, 8);
        g2d.fillRect((int) x + width - 4, (int) y + 20, 12, 8);
        g2d.fillRect((int) x + width - 4, (int) y + 70, 12, 8);

        g2d.fillRect((int) x + 30, (int) y - 8, 8, 12);
        g2d.fillRect((int) x + 70, (int) y - 8, 8, 12);

        // Contorno final
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval((int) x, (int) y, width, height);

        // --- BARRA DE VIDA DO BOSS ---
        int barWidth = width + 20;
        int barHeight = 8;
        int barX = (int) x - 10;
        int barY = (int) y - 22;

        g2d.setColor(new Color(30, 0, 0));
        g2d.fillRect(barX, barY, barWidth, barHeight);

        int currentBarWidth = (int) ((double) hp / maxHp * barWidth);
        g2d.setColor(new Color(220, 20, 60));
        g2d.fillRect(barX, barY, currentBarWidth, barHeight);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(barX, barY, barWidth, barHeight);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.RED);
        g2d.drawString(nome, barX, barY - 5);
    }
}