package vue;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.JPanel;

public class ParallaxPanel extends JPanel {

    private ArrayList<Layer> layers;

    public ParallaxPanel(ArrayList<Layer> layers) {
        this.layers = layers;
        setOpaque(false);  // Permet de voir ce qu'il y a derrière si nécessaire
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Layer layer : layers) {
            layer.draw(g);
        }
    }

    public void updateLayers() {
        for (Layer layer : layers) {
            layer.update();
        }
        repaint();
    }
}
