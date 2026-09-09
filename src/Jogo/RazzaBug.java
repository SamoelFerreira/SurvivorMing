package Jogo;

import java.util.List;

public class RazzaBug extends Player {
    private int glitchTimer;

    public RazzaBug(int x, int y) {
        super(x, y, "RazzaBug", 100, 15, 5.0, 1.0,
            "/sprites/RazzaBug_Archer", "Archer_Idle.png", 6,
            "Archer_Run.png", 4, "Archer_Shoot.png", 8);
    }

    @Override
    public void atualizarPassiva(List<Enemy> inimigos) {
        if (glitchTimer > 0) {
            glitchTimer--;
            if (glitchTimer == 0) {
                velocidadeMovimento /= 1.5;
                velocidadeAtaque /= 1.5;
                speed /= 1.5;
            }
        } else if (random.nextDouble() < 0.002) {
            glitchTimer = 300;
            velocidadeMovimento *= 1.5;
            velocidadeAtaque *= 1.5;
            speed *= 1.5;
            for (Enemy enemy : inimigos) {
                if (distanciaQuadrada(enemy) <= 140 * 140) enemy.stun(45);
            }
        }
    }

    private double distanciaQuadrada(Enemy enemy) {
        double dx = enemy.x - x;
        double dy = enemy.y - y;
        return dx * dx + dy * dy;
    }
}