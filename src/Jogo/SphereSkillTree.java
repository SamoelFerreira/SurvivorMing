package Jogo;

import java.awt.*;
import java.awt.event.MouseEvent;
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
        List<Integer> connectedTo; // IDs dos nós vizinhos conectados

        public SkillNode(int id, String name, String description, int x, int y, int cost) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.x = x;
            this.y = y;
            this.cost = cost;
            this.unlocked = false;
            this.connectedTo = new ArrayList<>();
        }
    }

    private Map<Integer, SkillNode> nodes = new HashMap<>();
    private SkillNode selectedCard = null;

    // Retângulos para os botões de interação na tela da Loja/Árvore
    private Rectangle btnBuy = new Rectangle(0, 0, 0, 0);
    private Rectangle btnClose = new Rectangle(300, 520, 200, 40);

    public SphereSkillTree() {
        buildSkillTree30();
    }

    private void buildSkillTree30() {
        nodes.clear();

        // --- NÚCLEO CENTRAL (ID 0) ---
        nodes.put(0, new SkillNode(0, "Núcleo de Poder", "Ponto de partida da árvore", 400, 300, 0));
        nodes.get(0).unlocked = true; // Começa desbloqueado

        // --- RAMO SUPERIOR (ATAQUE / CADÊNCIA) ---
        nodes.put(1, new SkillNode(1, "Ataque I", "Bônus inicial de dano/cadência", 400, 230, 50));
        nodes.put(2, new SkillNode(2, "Ataque II", "Melhoria de projétil", 400, 160, 100));
        nodes.put(3, new SkillNode(3, "Ataque III", "Cadência de tiro aprimorada", 400, 90, 200));
        nodes.put(4, new SkillNode(4, "Fúria", "Aumento crítico de cadência", 330, 60, 350));
        nodes.put(5, new SkillNode(5, "Mira Letal", "Chance de dano duplo", 470, 60, 350));

        // --- RAMO INFERIOR (DEFESA / VIDA) ---
        nodes.put(6, new SkillNode(6, "Resiliência I", "+10 HP Máximo", 400, 370, 50));
        nodes.put(7, new SkillNode(7, "Resiliência II", "+20 HP Máximo", 400, 440, 100));
        nodes.put(8, new SkillNode(8, "Resiliência III", "+30 HP Máximo", 400, 510, 200));
        nodes.put(9, new SkillNode(9, "Carapaça", "Reduz dano recebido", 330, 540, 350));
        nodes.put(10, new SkillNode(10, "Regeneração", "Cura passiva leve", 470, 540, 350));

        // --- RAMO ESQUERDO (MOBILIDADE / VELOCIDADE) ---
        nodes.put(11, new SkillNode(11, "Agilidade I", "+1 Velocidade de Movimento", 330, 300, 50));
        nodes.put(12, new SkillNode(12, "Agilidade II", "+1 Velocidade de Movimento", 260, 300, 100));
        nodes.put(13, new SkillNode(13, "Agilidade III", "Movvimento super fluido", 190, 300, 200));
        nodes.put(14, new SkillNode(14, "Dash Rápido", "Cooldown de dash menor", 140, 240, 300));
        nodes.put(15, new SkillNode(15, "Esquiva", "Chance de evitar dano", 140, 360, 300));

        // --- RAMO DIREITO (ECONOMIA / XP E OURO) ---
        nodes.put(16, new SkillNode(16, "Ganância I", "Mais ouro coletado", 470, 300, 50));
        nodes.put(17, new SkillNode(17, "Ganância II", "Imã atrai de mais longe", 540, 300, 100));
        nodes.put(18, new SkillNode(18, "Sabedoria", "Ganho de XP aprimorado", 610, 300, 200));
        nodes.put(19, new SkillNode(19, "Imã Supremo", "Raio do ímã maximizado", 660, 240, 300));
        nodes.put(20, new SkillNode(20, "Midas", "Ouro extra por chefe", 660, 360, 300));

        // --- DIAGONAIS / NÓS EXPANDIDOS (EXTENSÕES) ---
        nodes.put(21, new SkillNode(21, "Conexão NE-1", "Bônus híbrido ataque/utilidade", 470, 210, 150));
        nodes.put(22, new SkillNode(22, "Conexão NE-2", "Poder elemental de fogo", 540, 140, 250));

        nodes.put(23, new SkillNode(23, "Conexão SE-1", "Bônus híbrido defesa/ouro", 470, 390, 150));
        nodes.put(24, new SkillNode(24, "Conexão SE-2", "Escudo de energia", 540, 460, 250));

        nodes.put(25, new SkillNode(25, "Conexão NO-1", "Bônus híbrido ataque/velocidade", 330, 210, 150));
        nodes.put(26, new SkillNode(26, "Conexão NO-2", "Reflexo rápido", 260, 140, 250));

        nodes.put(27, new SkillNode(27, "Conexão SO-1", "Bônus híbrido defesa/velocidade", 330, 390, 150));
        nodes.put(28, new SkillNode(28, "Conexão SO-2", "Pé leve", 260, 460, 250));

        nodes.put(29, new SkillNode(29, "Ascensão", "Nó Mestre Final (Super Bônus)", 400, 20, 500));

        // --- DEFININDO AS CONEXÕES (LINHAS DA ÁRVORE) ---
        // Centro liga com o primeiro de cada direção principal
        connect(0, 1); connect(0, 6); connect(0, 11); connect(0, 16);

        // Ramo Cima
        connect(1, 2); connect(2, 3); connect(3, 4); connect(3, 5); connect(3, 29);

        // Ramo Baixo
        connect(6, 7); connect(7, 8); connect(8, 9); connect(8, 10);

        // Ramo Esquerda
        connect(11, 12); connect(12, 13); connect(13, 14); connect(13, 15);

        // Ramo Direita
        connect(16, 17); connect(17, 18); connect(18, 19); connect(18, 20);

        // Diagonais e Extensões
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
        // Fundo escuro com leve transparência para a loja
        g.setColor(new Color(15, 15, 25, 240));
        g.fillRect(0, 0, panelWidth, panelHeight);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("ESFERA DE HABILIDADES (30 NÓS)", 230, 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Ouro Disponível: " + gold + " 🪙", 50, 80);

        // Desenha as linhas de conexão primeiro para ficarem atrás dos círculos
        for (SkillNode node : nodes.values()) {
            for (int neighborId : node.connectedTo) {
                SkillNode neighbor = nodes.get(neighborId);
                if (neighbor != null) {
                    if (node.unlocked && neighbor.unlocked) {
                        g.setColor(Color.GREEN); // Conexão ativa
                    } else {
                        g.setColor(new Color(60, 60, 80)); // Conexão bloqueada
                    }
                    g.drawLine(node.x, node.y, neighbor.x, neighbor.y);
                }
            }
        }

        // Desenha os nós (esferas)
        for (SkillNode node : nodes.values()) {
            int radius = 16;
            if (node.unlocked) {
                g.setColor(new Color(0, 200, 100)); // Desbloqueado
            } else if (canUnlock(node)) {
                g.setColor(new Color(200, 180, 0)); // Disponível para comprar
            } else {
                g.setColor(new Color(80, 80, 100)); // Bloqueado sem acesso
            }

            g.fillOval(node.x - radius, node.y - radius, radius * 2, radius * 2);

            if (node == selectedCard) {
                g.setColor(Color.CYAN); // Selecionado
                g.drawRect(node.x - radius - 4, node.y - radius - 4, radius * 2 + 8, radius * 2 + 8);
            } else {
                g.setColor(Color.WHITE);
                g.drawOval(node.x - radius, node.y - radius, radius * 2, radius * 2);
            }

            // Número do ID do nó para referência rápida
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString(String.valueOf(node.id), node.x - 5, node.y + 4);
        }

        // Painel de Detalhes do Nó Selecionado na parte inferior
        if (selectedCard != null) {
            g.setColor(new Color(30, 30, 45));
            g.fillRect(150, 430, 500, 75);
            g.setColor(Color.CYAN);
            g.drawRect(150, 430, 500, 75);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.drawString(selectedCard.name + " (Custo: " + selectedCard.cost + " 🪙)", 170, 455);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.drawString(selectedCard.description, 170, 480);
        }

        // Botão de Fechar Árvore
        g.setColor(new Color(100, 40, 40));
        g.fillRect(btnClose.x, btnClose.y, btnClose.width, btnClose.height);
        g.setColor(Color.RED);
        g.drawRect(btnClose.x, btnClose.y, btnClose.width, btnClose.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Voltar ao Jogo", btnClose.x + 45, btnClose.y + 25);
    }

    private boolean canUnlock(SkillNode node) {
        if (node.unlocked) return false;
        // Precisa ter pelo menos um vizinho conectado que já esteja desbloqueado
        for (int neighborId : node.connectedTo) {
            if (nodes.get(neighborId).unlocked) {
                return true;
            }
        }
        return false;
    }

    // Gerencia o clique. Retorna true se continuar na loja, false se fechou.
    public boolean handleClick(MouseEvent e, GamePainel game) {
        Point p = e.getPoint();

        // Clicou no botão de fechar/voltar
        if (btnClose.contains(p)) {
            return false;
        }

        // Verifica clique em algum nó da árvore
        for (SkillNode node : nodes.values()) {
            double dist = Math.pow(node.x - p.x, 2) + Math.pow(node.y - p.y, 2);
            if (dist <= 16 * 16) { // Raio de clique da esfera
                selectedCard = node;

                // Se clicou em um nó que pode ser comprado e tem ouro suficiente
                if (!node.unlocked && canUnlock(node) && game.gold >= node.cost) {
                    game.gold -= node.cost;
                    node.unlocked = true;
                    applyNodeEffect(node, game);
                }
                break;
            }
        }
        return true;
    }

    private void applyNodeEffect(SkillNode node, GamePainel game) {
        // Aqui você pode adicionar o efeito real de cada ID conforme for criando os bônus com o tempo!
        switch (node.id) {
            case 1: case 2: case 3: case 4: case 5:
                // Exemplo: Melhorias de tiro
                game.shootInterval = Math.max(5, game.shootInterval - 3);
                break;
            case 6: case 7: case 8:
                // Exemplo: Melhorias de HP
                // game.player.maxHp += 10;
                // game.player.hp = game.player.maxHp;
                break;
            case 11: case 12: case 13:
                // Exemplo: Velocidade
                // game.player.speed += 1;
                break;
            default:
                // Outros nós genéricos por enquanto
                break;
        }
    }
}