package Jogo;

import java.util.List;

public class Rafengels extends Player {
    public Rafengels(int x, int y) {
        super(x, y, "Rafengels", 130, 12, 4.75, 0.85,
            "/sprites/Rafengels_Warrior", "Warrior_Idle.png", 8,
            "Warrior_Run.png", 6, "Warrior_Attack1.png", 4);
    }

    @Override
    public void receberDano(double dano) {
        super.receberDano(dano * 0.7);
    }

    @Override
    public void atualizarPassiva(List<Enemy> inimigos) {
        // A reducao de dano e constante.
    }
}