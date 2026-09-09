package Jogo;

import java.util.ArrayList;
import java.util.List;

public class SkillTree {

    public static class Skill {
        public String id;
        public String name;
        public String description;
        public int cost;
        public int level;
        public int maxLevel;

        public Skill(String id, String name, String description, int cost, int maxLevel) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.level = 0;
            this.maxLevel = maxLevel;
        }
    }

    private List<Skill> skills;

    public SkillTree() {
        skills = new ArrayList<>();
        // Definindo as habilidades da árvore
        skills.add(new Skill("max_hp", "Resistência Vital", "Aumenta o HP máximo em +25", 50, 5));
        skills.add(new Skill("speed", "Passos Leves", "Aumenta a velocidade de movimento", 70, 3));
        skills.add(new Skill("fire_rate", "Cadência de Disparo", "Reduz o intervalo entre os tiros", 80, 4));
        skills.add(new Skill("damage", "Projéteis Pesados", "Aumenta o poder de dano geral", 100, 3));
    }

    public List<Skill> getSkills() {
        return skills;
    }

    // Tenta comprar uma skill e aplica o efeito no Player
    public boolean buySkill(Skill skill, Player player, GamePainel gamePanel) {
        if (skill.level < skill.maxLevel) {
            // Aqui você pode checar se o gamePanel tem ouro suficiente (vamos passar o gold por referência ou gerenciar)
            // Vamos tratar a verificação de ouro direto no painel, mas aplicamos a evolução aqui:
            skill.level++;

            // Aplica o efeito prático no Player ou nas regras do jogo
            switch (skill.id) {
                case "max_hp":
                    player.maxHp += 25;
                    player.hp = player.maxHp;
                    break;
                case "speed":
                    player.speed += 0.5;
                    break;
                case "fire_rate":
                    if (gamePanel.shootInterval > 10) {
                        gamePanel.shootInterval -= 4;
                    }
                    break;
            }

            // Aumenta o custo para a próxima compra da mesma habilidade
            skill.cost += 25;
            return true;
        }
        return false;
    }
}