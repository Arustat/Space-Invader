package modele;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;

public class HealthBar extends JLabel {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 5;
    private static final Color HEALTH_COLOR = Color.GREEN;
    private static final Color BACKGROUND_COLOR = Color.RED;
    
    private int currentHealth;
    private int maxHealth;
    
    public HealthBar(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        setSize(WIDTH, HEIGHT);
        updateHealthBar();
    }
    
    public void setHealth(int health) {
        this.currentHealth = Math.max(0, Math.min(health, maxHealth));
        updateHealthBar();
    }
    
    private void updateHealthBar() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Draw background (red)
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Draw current health (green)
        g.setColor(HEALTH_COLOR);
        int healthWidth = (int)((float)currentHealth / maxHealth * WIDTH);
        g.fillRect(0, 0, healthWidth, HEIGHT);
        
        g.dispose();
        setIcon(new javax.swing.ImageIcon(image));
    }
} 