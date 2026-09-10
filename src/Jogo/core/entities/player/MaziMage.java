package Jogo.core.entities.player;

import Jogo.core.entities.enemies.Enemy;
import Jogo.core.entities.Player;

import java.util.List;

public class MaziMage extends Player {
    private int invisibilidadeTimer;

    public MaziMage(int x, int y) {
        super(x, y, "MaziMage", 80, 25, 5.5, 0.9,
            "/sprites/MaziMage_Lancer", "Lancer_Idle.png", 12,
            "Lancer_Run.png", 6, "Lancer_Right_Attack.png", 3);
    }

    @Override
    public void receberDano(double dano) {
        if (invisibilidadeTimer > 0) return;
        super.receberDano(dano);
        if (hp < maxHp * 0.3) invisibilidadeTimer = 360;
    }

    @Override
    public void atualizarPassiva(List<Enemy> inimigos) {
        if (invisibilidadeTimer > 0) {
            invisibilidadeTimer--;
            hp = Math.min(maxHp, hp + Math.max(1, (int) (maxHp * 0.05 / 60)));
        }
    }

    public boolean estaInvisivel() { return invisibilidadeTimer > 0; }
}