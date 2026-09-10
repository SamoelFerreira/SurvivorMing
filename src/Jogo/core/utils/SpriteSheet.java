package Jogo.core.utils;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SpriteSheet {
    private BufferedImage sheet;

    public SpriteSheet(String caminho) {
        try {
            // Carrega a imagem do classpath (ex: /sprites/Archer_Idle.png)
            java.io.InputStream input = getClass().getResourceAsStream(caminho);
            if (input != null) {
                sheet = ImageIO.read(input);
                input.close();
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar a spritesheet: " + caminho + " -> " + e.getMessage());
        }
    }

    // Método para fatiar a spritesheet horizontalmente em vários quadros
    public BufferedImage[] cortarFrames(int totalFrames) {
        if (sheet == null) return new BufferedImage[0];

        if (totalFrames <= 0 || sheet.getWidth() < totalFrames) {
            return new BufferedImage[0];
        }

        int frameWidth = sheet.getWidth() / totalFrames;
        int frameHeight = sheet.getHeight();
        BufferedImage[] frames = new BufferedImage[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }

        return frames;
    }
}
