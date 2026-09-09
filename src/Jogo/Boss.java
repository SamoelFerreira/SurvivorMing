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

    public void draw(Graphics g, int cameraX, int cameraY) {
        Graphics2D g2d = (Graphics2D) g;

        // Converte a posição do mundo para a tela usando a câmera
        int screenX = (int) this.x - cameraX;
        int screenY = (int) this.y - cameraY;

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

        // Sombra oval profunda nos pés do chefe (maior para proporcional ao tamanho dele)
        g2d.setColor(new Color(0, 0, 0, 110));
        g2d.fillOval(screenX + 10, screenY + height - 12, width - 20, 16);

        // Sombra/Aura tóxica
        g2d.setColor(corAura);
        g2d.fillOval(screenX - 10, screenY - 10, width + 20, height + 20);

        // Corpo principal
        g2d.setColor(corCorpo);
        g2d.fillOval(screenX, screenY, width, height);

        // Placas de blindagem
        g2d.setColor(corPlaca);
        g2d.fillRect(screenX + 10, screenY + 10, width - 20, height - 20);

        // Olhos múltiplos brilhantes
        g2d.setColor(corOlho);
        g2d.fillOval(screenX + 25, screenY + 35, 14, 14);
        g2d.fillOval(screenX + 70, screenY + 35, 14, 14);
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(screenX + 30, screenY + 40, 4, 4);
        g2d.fillOval(screenX + 75, screenY + 40, 4, 4);

        // Olho central extra
        g2d.setColor(Color.WHITE);
        g2d.fillOval(screenX + 48, screenY + 25, 12, 12);

        // Mandíbula / Boca rasgada
        g2d.setColor(Color.BLACK);
        g2d.fillRect(screenX + 30, screenY + 75, 50, 20);

        g2d.setColor(Color.WHITE);
        int[] denteX1 = {screenX+35, screenX+40, screenX+45};
        int[] denteY1 = {screenY+75, screenY+90, screenY+75};
        g2d.fillPolygon(denteX1, denteY1, 3);

        int[] denteX2 = {screenX+50, screenX+55, screenX+60};
        int[] denteY2 = {screenY+75, screenY+90, screenY+75};
        g2d.fillPolygon(denteX2, denteY2, 3);

        int[] denteX3 = {screenX+65, screenX+70, screenX+75};
        int[] denteY3 = {screenY+75, screenY+90, screenY+75};
        g2d.fillPolygon(denteX3, denteY3, 3);

        // Espinhos e garras
        g2d.setColor(corDestaque);
        g2d.fillRect(screenX - 8, screenY + 20, 12, 8);
        g2d.fillRect(screenX - 8, screenY + 70, 12, 8);
        g2d.fillRect(screenX + width - 4, screenY + 20, 12, 8);
        g2d.fillRect(screenX + width - 4, screenY + 70, 12, 8);

        g2d.fillRect(screenX + 30, screenY - 8, 8, 12);
        g2d.fillRect(screenX + 70, screenY - 8, 8, 12);

        // Contorno final
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawOval(screenX, screenY, width, height);

        // --- BARRA DE VIDA DO BOSS ---
        int barWidth = width + 20;
        int barHeight = 8;
        int barX = screenX - 10;
        int barY = screenY - 22;

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