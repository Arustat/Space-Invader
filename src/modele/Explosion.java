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
        Label.setNbLabel(Label.getNbLabel()+1);
        explosionLabel.getjLabel().setBounds(posX, posY, 64, 64);
        etape = 0;
        explosionLabel.getjLabel().setVisible(true);
    }
    
    public Explosion(int posX, int posY, JeuServeur jeuServeur) {
        this(posX, posY);
        this.jeuServeur = jeuServeur;
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
                    
                    // Envoyer l'état actuel de l'explosion à tous les clients si on a une référence au jeuServeur
                    if (jeuServeur != null) {
                        jeuServeur.envoi(explosionLabel);
                    }
                } else {
                    timer.cancel();
                    explosionLabel.getjLabel().setVisible(false);
                    
                    // Informer tous les clients que l'explosion est terminée
                    if (jeuServeur != null) {
                        jeuServeur.envoi(explosionLabel);
                    }
                }
            }
        }, 0, 100);
    }

    public Label getLabel() {
        return explosionLabel;
    }
}
