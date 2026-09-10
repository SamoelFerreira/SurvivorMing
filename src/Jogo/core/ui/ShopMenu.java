package Jogo.core.ui;

import Jogo.core.skilltree.SphereSkillTree;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ShopMenu {
    private SphereSkillTree sphereSkillTree;

    public ShopMenu(String personagem) {
        this.sphereSkillTree = new SphereSkillTree(personagem);
    }

    public ShopMenu() {
        this.sphereSkillTree = new SphereSkillTree("RazzaBug");
    }

    public void draw(Graphics g, int currentGold, int width, int height) {
        // Renderiza a árvore visual baseada em nós na tela da loja
        sphereSkillTree.draw(g, currentGold, width, height);
    }

    // Processa os cliques na árvore (retorna false se clicou em retornar ao combate)
    public boolean handleClick(MouseEvent e, GamePanel game) {
        return sphereSkillTree.handleClick(e, game);
    }

    // Opcional: permite recriar ou trocar o personagem da árvore se necessário ao abrir a loja
    public void setPersonagem(String personagem) {
        this.sphereSkillTree = new SphereSkillTree(personagem);
    }
}