package Jogo;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    private MainMenuPanel mainMenuPanel;
    private GamePanel currentGamePanel;

    public GameWindow() {
        setTitle("Vampire Horde Survivor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Inicializa o Menu Principal
        mainMenuPanel = new MainMenuPanel(this);

        // Adiciona o menu no container de telas
        mainContainer.add(mainMenuPanel, "MENU");

        add(mainContainer);
        cardLayout.show(mainContainer, "MENU"); // Começa mostrando o Menu Principal
    }

    public void showMainMenu() {
        if (currentGamePanel != null) {
            currentGamePanel.stop();
            mainContainer.remove(currentGamePanel);
            currentGamePanel = null;
        }
        cardLayout.show(mainContainer, "MENU");
        mainMenuPanel.requestFocusInWindow();
    }

    public void startGame() {
        // Se já houver um jogo rodando, remove ele antes de criar um novo
        if (currentGamePanel != null) {
            currentGamePanel.stop();
            mainContainer.remove(currentGamePanel);
        }

        // Cria o painel do jogo passando a referência da janela
        currentGamePanel = new GamePanel(this);
        mainContainer.add(currentGamePanel, "GAME");

        cardLayout.show(mainContainer, "GAME");
        currentGamePanel.requestFocusInWindow();
        currentGamePanel.startGame(); // Inicia a thread do jogo
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}