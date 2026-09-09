package Jogo;

import java.util.List;
import java.util.Random;

public class EnemyManager {
    private int spawnTimer = 0;
    private int spawnInterval = 90; // Começa gerando a cada 1.5 segundos
    private int difficultyTimer = 0;
    private Random random = new Random();

    public void update(List<Enemy> enemies, double playerX, double playerY) {
        spawnTimer++;
        difficultyTimer++;

        if (difficultyTimer >= 600) {
            difficultyTimer = 0;
            if (spawnInterval > 25) {
                spawnInterval -= 10;
            }
        }

        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnEnemy(enemies, playerX, playerY); // Passa a posição do player aqui
        }
    }

    private void spawnEnemy(List<Enemy> enemies, double playerX, double playerY) {
        double x = 0;
        double y = 0;
        int edge = random.nextInt(4);

        // Margem maior para garantir que nasçam bem fora da tela de 1600x900
        switch (edge) {
            case 0: // Topo (acima da visão)
                x = playerX - 900 + random.nextInt(1800);
                y = playerY - 600;
                break;
            case 1: // Embaixo (abaixo da visão)
                x = playerX - 900 + random.nextInt(1800);
                y = playerY + 600;
                break;
            case 2: // Esquerda (à esquerda da visão)
                x = playerX - 900;
                y = playerY - 600 + random.nextInt(1200);
                break;
            case 3: // Direita (à direita da visão)
                x = playerX + 900;
                y = playerY - 600 + random.nextInt(1200);
                break;
        }

        enemies.add(new Enemy(x, y));
    }
}