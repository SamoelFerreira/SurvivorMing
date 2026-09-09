package Jogo;

import java.util.List;
import java.util.Random;

public class EnemyManager {
    private int spawnTimer = 0;
    private int spawnInterval = 90; // Começa gerando a cada 1.5 segundos
    private int difficultyTimer = 0;
    private Random random = new Random();

    public void update(List<Enemy> enemies) {
        spawnTimer++;
        difficultyTimer++;

        // A cada ~10 segundos (600 frames a 60 FPS), o jogo fica mais difícil!
        if (difficultyTimer >= 600) {
            difficultyTimer = 0;
            if (spawnInterval > 25) { // Limite mínimo para não travar o jogo
                spawnInterval -= 10; // Nascimento fica mais rápido
            }
        }

        // Hora de nascer um novo inimigo
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnEnemy(enemies);
        }
    }

    private void spawnEnemy(List<Enemy> enemies) {
        double x = 0;
        double y = 0;
        int edge = random.nextInt(4);

        switch (edge) {
            case 0: // Topo
                x = random.nextInt(800);
                y = -30;
                break;
            case 1: // Embaixo
                x = random.nextInt(800);
                y = 630;
                break;
            case 2: // Esquerda
                x = -30;
                y = random.nextInt(600);
                break;
            case 3: // Direita
                x = 830;
                y = random.nextInt(600);
                break;
        }

        enemies.add(new Enemy(x, y));
    }
}