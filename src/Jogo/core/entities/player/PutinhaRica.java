package Jogo.core.entities.player;

import Jogo.core.entities.enemies.Enemy;
import Jogo.core.entities.Player;

import java.util.List;

public class PutinhaRica extends Player {
    public PutinhaRica(int x, int y) {
        super(x, y, "PutinhaRica", 90, 18, 5.25, 1.1,
            "/sprites/PutinhaRica_Monk", "Idle.png", 6,
            "Run.png", 4, "Heal.png", 11);
    }

    @Override
    public void aoMatarInimigo(boolean ehBoss, double recompensaBase) {
        super.aoMatarInimigo(ehBoss, recompensaBase * (ehBoss ? 1.3 : 1.2));
        danoAtaque = 18 + Math.floor(ouro / 100.0);
    }

    @Override
    public void atualizarPassiva(List<Enemy> inimigos) {
        danoAtaque = 18 + Math.floor(ouro / 100.0);
    }
}