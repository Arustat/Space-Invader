package vue;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

import controleur.Global;

public class Layer implements Global {
    private int x;
    private int y;
    private int width;
    private int height;
    private double speed;
    private Image image;

    public Layer(Image img, double speed, int width, int height) {
        this.image = img;
        this.speed = speed;
        this.y = 0;
        this.width = width;
        this.height = height;
    }

    public void update() {
        y += speed;  // On déplace l'image vers le bas
        
        if (y >= height) {  // Si l'image sort de l'écran en bas, on la réinitialise
        	 y -=  height;  // On la replace immédiatement en haut
        }
    }


    public void draw(Graphics g) {
        g.drawImage(image, 0, y, width, height, null);  // Dessine l'image principale
        g.drawImage(image, 0, y - height, width, height, null);  // Dessine l'image suivante pour un défilement continu
    }
}
