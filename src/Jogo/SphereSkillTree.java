package Jogo;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SphereSkillTree {

    public static class SkillNode {
        int id;
        String name;
        String description;
        int x, y;
        int cost;
        boolean unlocked;
        String type; // "ATTACK", "DEFENSE", "SPEED", "UTILITY", "CORE", "SPECIAL"
        List<Integer> connectedTo;

        public SkillNode(int id, String name, String description, int x, int y, int cost, String type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.unlocked = false;
            this.type = type;
            this.connectedTo = new ArrayList<>();
        }
    }

    private Map<Integer, SkillNode> nodes = new HashMap<>();
    private SkillNode selectedCard = null;

    // Posições dos botões na tela
    private Rectangle btnBuy = new Rectangle(510, 465, 150, 35);
    private Rectangle btnClose = new Rectangle(300, 525, 200, 40);

    public SphereSkillTree() {
        buildSkillTree30();
    }

    private void buildSkillTree30() {
        nodes.clear();

        // --- NÚCLEO CENTRAL (ID 0) ---
        nodes.put(0, new SkillNode(0, "Núcleo de Poder", "Ponto de partida da árvore cibernética.", 400, 300, 0, "CORE"));
        nodes.get(0).unlocked = true;

        // --- RAMO SUPERIOR (ATAQUE) ---
        nodes.put(1, new SkillNode(1, "Ataque I", "Bônus inicial de cadência e dano.", 400, 230, 50, "ATTACK"));
        nodes.put(2, new SkillNode(2, "Ataque II", "Melhoria balística de projétil.", 400, 160, 100, "ATTACK"));
        nodes.put(3, new SkillNode(3, "Ataque III", "Cadência de tiro altamente aprimorada.", 400, 90, 200, "ATTACK"));
        nodes.put(4, new SkillNode(4, "Fúria de Disparo", "Aumento crítico na velocidade de tiro.", 330, 60, 350, "ATTACK"));
        nodes.put(5, new SkillNode(5, "Mira Letal", "Chance passiva de dano duplo.", 470, 60, 350, "ATTACK"));

        // --- RAMO INFERIOR (DEFESA) ---
        nodes.put(6, new SkillNode(6, "Resiliência I", "+10 de HP Máximo.", 400, 370, 50, "DEFENSE"));
        nodes.put(7, new SkillNode(7, "Resiliência II", "+20 de HP Máximo.", 400, 440, 100, "DEFENSE"));
        nodes.put(8, new SkillNode(8, "Resiliência III", "+30 de HP Máximo.", 400, 510, 200, "DEFENSE"));
        nodes.put(9, new SkillNode(9, "Carapaça Térmica", "Reduz o impacto de dano físico.", 330, 540, 350, "DEFENSE"));
        nodes.put(10, new SkillNode(10, "Nanobôs de Cura", "Regeneração passiva leve de HP.", 470, 540, 350, "DEFENSE"));

        // --- RAMO ESQUERDO (MOBILIDADE) ---
        nodes.put(11, new SkillNode(11, "Agilidade I", "+1 de Velocidade de Movimento.", 330, 300, 50, "SPEED"));
        nodes.put(12, new SkillNode(12, "Agilidade II", "+1 de Velocidade de Movimento.", 260, 300, 100, "SPEED"));
        nodes.put(13, new SkillNode(13, "Propulsão Líquida", "Movimento ultra fluido pelo mapa.", 190, 300, 200, "SPEED"));
        nodes.put(14, new SkillNode(14, "Dash Tático", "Cooldown do Dash reduzido.", 140, 240, 300, "SPEED"));
        nodes.put(15, new SkillNode(15, "Reflexos Extremos", "Desvio otimizado contra hordas.", 140, 360, 300, "SPEED"));

        // --- RAMO DIREITO (ECONOMIA / UTILIDADE) ---
        nodes.put(16, new SkillNode(16, "Coleta Otimizada", "Aumenta o ganho geral de Ouro.", 470, 300, 50, "UTILITY"));
        nodes.put(17, new SkillNode(17, "Campo Magnético", "Raio de atração de XP expandido.", 540, 300, 100, "UTILITY"));
        nodes.put(18, new SkillNode(18, "Sabedoria Arcana", "Ganho de XP aprimorado por orbe.", 610, 300, 200, "UTILITY"));
        nodes.put(19, new SkillNode(19, "Ímã Quântico", "Atração instantânea de itens distantes.", 660, 240, 300, "UTILITY"));
        nodes.put(20, new SkillNode(20, "Protocolo Midas", "Ouro extra ao derrotar chefes.", 660, 360, 300, "UTILITY"));

        // --- NÓS HÍBRIDOS E DIAGONAIS ---
        nodes.put(21, new SkillNode(21, "Overclock Alpha", "Sintonia híbrida Ataque/Velocidade", 470, 210, 150, "SPECIAL"));
        nodes.put(22, new SkillNode(22, "Núcleo de Plasma", "Dano concentrado de energia pura", 540, 140, 250, "SPECIAL"));

        nodes.put(23, new SkillNode(23, "Blindagem de Liga", "Sintonia híbrida Defesa/Utilidade", 470, 390, 150, "SPECIAL"));
        nodes.put(24, new SkillNode(24, "Escudo de Íons", "Barreira protetora secundária", 540, 460, 250, "SPECIAL"));

        nodes.put(25, new SkillNode(25, "Incisão Neural", "Reflexos de combate aprimorados", 330, 210, 150, "SPECIAL"));
        nodes.put(26, new SkillNode(26, "Cinética Pura", "Velocidade máxima de disparos", 260, 140, 250, "SPECIAL"));

        nodes.put(27, new SkillNode(27, "Estabilizador", "Equilíbrio de massa e gravidade", 330, 390, 150, "SPECIAL"));
        nodes.put(28, new SkillNode(28, "Passos Fantasma", "Deslocamento sem atrito", 260, 460, 250, "SPECIAL"));

        nodes.put(29, new SkillNode(29, "Ascensão Cibernética", "Nó Mestre Definitivo do Sistema", 400, 20, 500, "SPECIAL"));

        // --- CONEXÕES ---
        connect(0, 1); connect(0, 6); connect(0, 11); connect(0, 16);
        connect(1, 2); connect(2, 3); connect(3, 4); connect(3, 5); connect(3, 29);
        connect(6, 7); connect(7, 8); connect(8, 9); connect(8, 10);
        connect(11, 12); connect(12, 13); connect(13, 14); connect(13, 15);
        connect(16, 17); connect(17, 18); connect(18, 19); connect(18, 20);
        connect(1, 21); connect(21, 22); connect(22, 3);
        connect(16, 23); connect(23, 24); connect(24, 8);
        connect(11, 25); connect(25, 26); connect(26, 2);
        connect(11, 27); connect(27, 28); connect(28, 7);
    }

    private void connect(int id1, int id2) {
        if (nodes.containsKey(id1) && nodes.containsKey(id2)) {
            nodes.get(id1).connectedTo.add(id2);
            nodes.get(id2).connectedTo.add(id1);
        }
    }

    public void draw(Graphics g, int gold, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo com gradiente radial/escuro simulando painel sci-fi
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(10, 10, 18), panelWidth, panelHeight, new Color(20, 22, 35));
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Grade de fundo sutil (estilo radar/terminal)
        g2d.setColor(new Color(255, 255, 255, 8));
        for (int x = 0; x < panelWidth; x += 40) g2d.drawLine(x, 0, x, panelHeight);
        for (int y = 0; y < panelHeight; y += 40) g2d.drawLine(0, y, panelWidth, y);

        // Título estilizado com sombra
        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString("MATRIZ DE HABILIDADES CIBERNÉTICAS", 202, 42);
        g2d.setColor(Color.CYAN);
        g2d.drawString("MATRIZ DE HABILIDADES CIBERNÉTICAS", 200, 40);

        // Ouro com ícone brilhante
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(Color.YELLOW);
        g2d.drawString("CRÉDITOS: " + gold + " 🪙", 40, 40);

        // Desenhar Conexões
        Stroke defaultStroke = g2d.getStroke();
        for (SkillNode node : nodes.values()) {
            for (int neighborId : node.connectedTo) {
                if (node.id > neighborId) continue;
                SkillNode neighbor = nodes.get(neighborId);
                if (neighbor != null) {
                    if (node.unlocked && neighbor.unlocked) {
                        g2d.setColor(new Color(0, 230, 120, 200));
                        g2d.setStroke(new BasicStroke(2.5f));
                    } else {
                        g2d.setColor(new Color(45, 48, 65, 180));
                        g2d.setStroke(new BasicStroke(1.5f));
                    }
                    g2d.drawLine(node.x, node.y, neighbor.x, neighbor.y);
                }
            }
        }
        g2d.setStroke(defaultStroke);

        // Desenhar os Nós (Esferas)
        for (SkillNode node : nodes.values()) {
            int radius = 17;

            if (node.unlocked) {
                g2d.setColor(new Color(0, 255, 120, 40));
                g2d.fillOval(node.x - radius - 6, node.y - radius - 6, (radius + 6) * 2, (radius + 6) * 2);
            } else if (canUnlock(node)) {
                g2d.setColor(new Color(255, 215, 0, 40));
                g2d.fillOval(node.x - radius - 6, node.y - radius - 6, (radius + 6) * 2, (radius + 6) * 2);
            }

            Color innerColor, outerColor;
            if (node.unlocked) {
                innerColor = new Color(120, 255, 180);
                outerColor = new Color(0, 140, 70);
            } else if (canUnlock(node)) {
                innerColor = new Color(255, 240, 100);
                outerColor = new Color(180, 130, 0);
            } else {
                innerColor = new Color(90, 95, 120);
                outerColor = new Color(40, 42, 55);
            }

            RadialGradientPaint sphereGradient = new RadialGradientPaint(
                    node.x - 4, node.y - 4, radius,
                    new float[]{0.0f, 1.0f},
                    new Color[]{innerColor, outerColor}
            );
            g2d.setPaint(sphereGradient);
            g2d.fill(new Ellipse2D.Double(node.x - radius, node.y - radius, radius * 2, radius * 2));

            if (node == selectedCard) {
                g2d.setColor(Color.CYAN);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawOval(node.x - radius - 3, node.y - radius - 3, (radius + 3) * 2, (radius + 3) * 2);
                g2d.setStroke(defaultStroke);
            } else {
                g2d.setColor(new Color(255, 255, 255, 120));
                g2d.drawOval(node.x - radius, node.y - radius, radius * 2, radius * 2);
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.setColor(node.unlocked || canUnlock(node) ? Color.BLACK : Color.LIGHT_GRAY);
            String label = getNodeSymbol(node.type);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(label, node.x - fm.stringWidth(label) / 2, node.y + 4);
        }

        // Painel de Informações do Nó Selecionado (Inferior)
        if (selectedCard != null) {
            g2d.setColor(new Color(15, 18, 30, 230));
            g2d.fillRoundRect(120, 410, 560, 105, 12, 12);
            g2d.setColor(new Color(0, 200, 255, 150));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(120, 410, 560, 105, 12, 12);
            g2d.setStroke(defaultStroke);

            g2d.setFont(new Font("Arial", Font.BOLD, 15));
            g2d.setColor(Color.YELLOW);
            g2d.drawString(selectedCard.name, 140, 435);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.WHITE);
            g2d.drawString("Efeito: " + selectedCard.description, 140, 458);

            if (selectedCard.unlocked) {
                g2d.setColor(Color.GREEN);
                g2d.drawString("Status: [ JÁ DESBLOQUEADO ]", 140, 488);
            } else {
                g2d.setColor(Color.CYAN);
                g2d.drawString("Custo de Ativação: " + selectedCard.cost + " Créditos", 140, 488);

                // Desenha o Botão de Comprar Verde
                g2d.setColor(new Color(0, 160, 80));
                g2d.fillRoundRect(btnBuy.x, btnBuy.y, btnBuy.width, btnBuy.height, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 13));
                String buyStr = "COMPRAR";
                FontMetrics fmBuy = g2d.getFontMetrics();
                g2d.drawString(buyStr, btnBuy.x + (btnBuy.width - fmBuy.stringWidth(buyStr)) / 2, btnBuy.y + 23);
            }
        }

        // Botão de Fechar Árvore
        GradientPaint btnGradient = new GradientPaint(btnClose.x, btnClose.y, new Color(70, 20, 20), btnClose.x, btnClose.y + btnClose.height, new Color(120, 30, 30));
        g2d.setPaint(btnGradient);
        g2d.fillRoundRect(btnClose.x, btnClose.y, btnClose.width, btnClose.height, 10, 10);
        g2d.setColor(new Color(255, 100, 100));
        g2d.drawRoundRect(btnClose.x, btnClose.y, btnClose.width, btnClose.height, 10, 10);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        String backText = "Retornar ao Combate";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(backText, btnClose.x + (btnClose.width - fm.stringWidth(backText)) / 2, btnClose.y + 25);
    }

    private String getNodeSymbol(String type) {
        switch (type) {
            case "ATTACK": return "ATK";
            case "DEFENSE": return "DEF";
            case "SPEED": return "SPD";
            case "UTILITY": return "ECO";
            case "CORE": return "CORE";
            default: return "MOD";
        }
    }

    private boolean canUnlock(SkillNode node) {
        if (node.unlocked) return false;
        for (int neighborId : node.connectedTo) {
            if (nodes.get(neighborId).unlocked) {
                return true;
            }
        }
        return false;
    }

    public boolean handleClick(MouseEvent e, GamePainel game) {
        Point p = e.getPoint();

        // Se clicou no botão de fechar/voltar
        if (btnClose.contains(p)) {
            return false;
        }

        // Se clicou no botão de COMPRAR (quando há um card selecionado)
        if (selectedCard != null && !selectedCard.unlocked && btnBuy.contains(p)) {
            if (canUnlock(selectedCard) && game.gold >= selectedCard.cost) {
                game.gold -= selectedCard.cost;
                selectedCard.unlocked = true;
                applyNodeEffect(selectedCard, game);
            }
            return true;
        }

        // Verifica clique em algum nó da árvore apenas para SELECIONAR
        for (SkillNode node : nodes.values()) {
            double dist = Math.pow(node.x - p.x, 2) + Math.pow(node.y - p.y, 2);
            if (dist <= 17 * 17) {
                selectedCard = node;
                break;
            }
        }
        return true;
    }

    private void applyNodeEffect(SkillNode node, GamePainel game) {
        switch (node.type) {
            case "ATTACK":
                game.shootInterval = Math.max(5, game.shootInterval - 3);
                break;
            case "SPEED":
                game.player.speed += 1;
                break;
            case "DEFENSE":
                game.player.maxHp += 15;
                game.player.hp = game.player.maxHp;
                break;
            default:
                break;
        }
    }
}