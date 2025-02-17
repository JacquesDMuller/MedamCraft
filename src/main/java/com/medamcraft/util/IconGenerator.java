package com.medamcraft.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class IconGenerator {
    public static void main(String[] args) {
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Fundo
        g2d.setColor(new Color(64, 64, 128));
        g2d.fillRect(0, 0, 128, 128);
        
        // Texto
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Medam", 30, 60);
        g2d.drawString("Craft", 35, 85);
        
        g2d.dispose();
        
        try {
            File outputDir = new File("src/main/resources/assets/medamcraft");
            outputDir.mkdirs();
            ImageIO.write(image, "PNG", new File(outputDir, "icon.png"));
            System.out.println("Ícone gerado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 