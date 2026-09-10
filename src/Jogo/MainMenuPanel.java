package Jogo;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private GameWindow window;

    public MainMenuPanel(GameWindow window) {
        this.window = window;
        setPreferredSize(new Dimension(1600, 900));
        setBackground(Color.BLACK);
        setLayout(null); // Usaremos posicionamento manual simples para os botões

        // Botão de Iniciar Jogo
        JButton btnStart = new JButton("START GAME");
        btnStart.setBounds(700, 380, 200, 50);
        btnStart.setFont(new Font("Arial", Font.BOLD, 18));
        btnStart.setBackground(new Color(0, 100, 0));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e -> window.startGame()); // Chama direto o startGame da janela
        add(btnStart);

        // Botão de Sair
        JButton btnExit = new JButton("SAIR");
        btnExit.setBounds(700, 460, 200, 50);
        btnExit.setFont(new Font("Arial", Font.BOLD, 18));
        btnExit.setBackground(new Color(100, 0, 0));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e -> System.exit(0));
        add(btnExit);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Título do Jogo no Menu
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 48));
        String titulo = "VAMPIRE HORDE SURVIVOR";
        int larguraTexto = g2d.getFontMetrics().stringWidth(titulo);
        g2d.drawString(titulo, (1600 - larguraTexto) / 2, 220);
    }
}
