package modele;

import controleur.Global;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Explosion implements Global {
    private Label explosionLabel;
    private int etape;
    private Timer timer;
    private JeuServeur jeuServeur;

    public Explosion(int posX, int posY) {
        explosionLabel = new Label(Label.getNbLabel(), new JLabel());
        explosionLabel.getjLabel().setBounds(posX, posY, 64, 64);
        etape = 0;
        explosionLabel.getjLabel().setVisible(true);
    }

    public void startAnimation() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (etape < NBETATSMORT) {
                    etape++;
                    String imagePath = CHEMINMORT + etape + EXTIMAGE;
                    ImageIcon icon = new ImageIcon(imagePath);
                    icon.setDescription("Explosion"); // Marquer l'icône comme explosion
                    explosionLabel.getjLabel().setIcon(icon);
                } else {
                    timer.cancel();
                    explosionLabel.getjLabel().setVisible(false);
                }
            }
        }, 0, 100);
    }

    public Label getLabel() {
        return explosionLabel;
    }
}
