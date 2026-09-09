package Jogo;

import java.awt.*;
import java.util.List;

public class ShopMenu {
    private Rectangle btnBack = new Rectangle(300, 500, 200, 40);

    public void draw(Graphics g, SkillTree skillTree, int currentGold, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        // Fundo escuro translúcido do painel da loja
        g2d.setColor(new Color(15, 15, 25, 245));
        g2d.fillRect(0, 0, width, height);

        // Título
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        g2d.drawString("ÁRVORE DE HABILIDADES (LOJA)", 180, 70);

        // Ouro disponível
        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Ouro Disponível: " + currentGold + " 🪙", 310, 110);

        // Desenha as opções de skills da árvore
        List<SkillTree.Skill> skills = skillTree.getSkills();
        int startY = 160;

        for (int i = 0; i < skills.size(); i++) {
            SkillTree.Skill skill = skills.get(i);
            int boxY = startY + (i * 75);

            // Caixa do card de skill
            g2d.setColor(new Color(30, 30, 45));
            g2d.fillRect(150, boxY, 500, 60);
            g2d.setColor(Color.CYAN);
            g2d.drawRect(150, boxY, 500, 60);

            // Textos do card
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString(skill.name + " (Nível " + skill.level + "/" + skill.maxLevel + ")", 170, boxY + 25);

            g2d.setFont(new Font("Arial", Font.PLAIN, 13));
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawString(skill.description, 170, boxY + 45);

            // Custo ou status de Máximo
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            if (skill.level >= skill.maxLevel) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("MÁXIMO", 560, boxY + 35);
            } else {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("Custo: " + skill.cost + " 🪙", 540, boxY + 35);
            }
        }

        // Botão de Voltar
        g2d.setColor(new Color(150, 30, 30));
        g2d.fillRect(btnBack.x, btnBack.y, btnBack.width, btnBack.height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Voltar ao Menu", btnBack.x + 45, btnBack.y + 25);
    }

    public Rectangle getBackBounds() {
        return btnBack;
    }

    // Retorna o índice da skill clicada com base na coordenada Y do clique do mouse
    public int getClickedSkillIndex(Point p, int totalSkills) {
        int startY = 160;
        for (int i = 0; i < totalSkills; i++) {
            int boxY = startY + (i * 75);
            Rectangle rect = new Rectangle(150, boxY, 500, 60);
            if (rect.contains(p)) {
                return i;
            }
        }
        return -1;
    }
}
