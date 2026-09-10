package Jogo.core.entities.player;

import Jogo.core.entities.enemies.Enemy;
import Jogo.core.entities.Player;

import java.util.List;

public class TotoLove extends Player {
    private static final int LIMITE_ALIADOS = 5;

    public TotoLove(int x, int y) {
        super(x, y, "TotoLove", 95, 10, 5.0, 1.0,
            "/sprites/TotoLove_Pawn", "Pawn_Idle.png", 8,
            "Pawn_Run.png", 6, "Pawn_Interact Hammer.png", 3);
    }

    @Override
    public void atualizarPassiva(List<Enemy> inimigos) {
        long aliados = inimigos.stream().filter(Enemy::isAliado).count();
        if (aliados >= LIMITE_ALIADOS) return;
        for (Enemy enemy : inimigos) {
            if (!enemy.isAliado() && random.nextDouble() < 0.0015) {
                enemy.converter(900);
                aliados++;
                if (aliados >= LIMITE_ALIADOS) return;
            }
        }
    }
}