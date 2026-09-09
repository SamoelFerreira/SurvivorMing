package Jogo;

import java.awt.*;

public class Player {
    public int x, y;
    public int width = 40;
    public int height = 40;

    //Status e Atributos
    public int speed = 5;
    public int maxHp = 100;
    public int hp = 100;

    // Sistema de XP
    public int level = 1;
    public int currentXp = 0;
    public int nextLevelXp = 1; // XP necessario para o proximo nivel

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update(boolean up, boolean down, boolean left, boolean right) {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

        // Limites da tela (800x600)
        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;
        if (y < 0) y = 0;
        if (y > 600 - height) y = 600 - height;
    }

    public void dash(boolean up, boolean down, boolean left, boolean right) {
        int dashDistance = 80;

        if (up) y -= dashDistance;
        if (down) y += dashDistance;
        if (left) x -= dashDistance;
        if (right) x += dashDistance;

        if (!up && !down && !left && !right) {
            y -= dashDistance; // Dash para cima se estiver parado
        }

        if (x < 0) x = 0;
        if (x > 800 - width) x = 800 - width;
        if (y < 0) y = 0;
        if (y > 600 - height) y = 600 - height;
    }

    public boolean gainXp(int amount) {
        currentXp += amount;
        if (currentXp >= nextLevelXp) {
            currentXp -= nextLevelXp; // Desconta o XP do nível atual, mantendo o "resto" para o próximo
            level++;
            nextLevelXp += 1; // Ou a sua lógica de aumento de XP necessário
            return true; // Retorna true indicando que subiu de nível
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
