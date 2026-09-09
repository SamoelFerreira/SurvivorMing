package Jogo;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage sheet;

    public SpriteSheet(String caminho) {
        try {
            // Carrega a imagem do classpath (ex: /sprites/Archer_Idle.png)
            sheet = ImageIO.read(getClass().getResourceAsStream(caminho));
        } catch (IOException e) {
            System.out.println("Erro ao carregar a spritesheet: " + caminho + " -> " + e.getMessage());
        }
    }

    // Método para fatiar a spritesheet horizontalmente em vários quadros
    public BufferedImage[] cortarFrames(int totalFrames) {
        if (sheet == null) return new BufferedImage[0];

        int frameWidth = sheet.getWidth() / totalFrames;
        int frameHeight = sheet.getHeight();
        BufferedImage[] frames = new BufferedImage[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }

        return frames;
    }
}
