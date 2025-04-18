package modele;

import controleur.Global;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Enemy extends Objet implements Global {
    private int health;
    private int speed;
    private int points;
    private boolean isAlive;
    private int type; // Type d'ennemi (pour différentes variétés)
    private int currentFrame;
    private Timer animationTimer;
    private JeuServeur jeuServeur; // Référence au JeuServeur
    private Explosion explosion; // Référence à l'explosion

    public Enemy(int x, int y, int type, JeuServeur jeuServeur) {
        this.posX = x;
        this.posY = y;
        this.type = type;
        this.isAlive = true;
        this.currentFrame = 1;
        this.jeuServeur = jeuServeur;
        
        // Configuration selon le type d'ennemi
        switch(type) {
            case 1: // Ennemi basique
                this.health = 1;
                this.speed = 2;
                this.points = 10;
                break;
            case 2: // Ennemi rapide
                this.health = 1;
                this.speed = 4;
                this.points = 20;
                break;
            case 3: // Ennemi tank
                this.health = 3;
                this.speed = 1;
                this.points = 30;
                break;
        }

        // Création du label pour l'ennemi
        label = new Label(-1, new JLabel());
        label.getjLabel().setHorizontalAlignment(SwingConstants.CENTER);
        label.getjLabel().setVerticalAlignment(SwingConstants.CENTER);
        label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
        updateSprite();

        // Timer pour l'animation
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentFrame = (currentFrame % 4) + 1; // On suppose 4 frames par animation
                updateSprite();
            }
        }, 0, 200); // Mise à jour toutes les 200ms
    }

    private void updateSprite() {
        if (label != null && label.getjLabel() != null) {
            ImageIcon icon = new ImageIcon(CHEMINENEMY + "Enemy" + type + "/" + currentFrame + EXTIMAGE);
            // Définir explicitement la description pour faciliter l'identification côté client
            icon.setDescription("Enemy" + type + "/" + currentFrame);
            label.getjLabel().setIcon(icon);
            // S'assurer que la taille et la position sont toujours fixées
            label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
            label.getjLabel().setSize(L_PERSO, H_PERSO);
            label.getjLabel().setVisible(true);
        }
    }

    public void move() {
        // Les ennemis se déplacent vers le bas
        posY += speed;
        
        // Met à jour la position du label
        if (label != null && label.getjLabel() != null) {
            label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
        }
    }

    public void takeDamage() {
        health--;
        if (health <= 0) {
            isAlive = false;
            if (animationTimer != null) {
                animationTimer.cancel();
            }
            
            // Créer et démarrer l'animation d'explosion
            if (jeuServeur != null) {
                explosion = new Explosion(posX, posY, jeuServeur);
                jeuServeur.nouveauLabelJeu(explosion.getLabel());
                explosion.startAnimation();
                
                // Jouer le son de mort
                jeuServeur.envoi(SON[DEATH]);
                
                // Mettre à jour le score commun avec les points de l'ennemi
                System.out.println("Enemy: Ajout de " + points + " points au score commun");
                jeuServeur.updateScoreCommun(points);
                
                // Rendre l'ennemi invisible immédiatement
                if (label != null && label.getjLabel() != null) {
                    label.getjLabel().setVisible(false);
                    
                    // Mettre à jour l'ennemi chez tous les clients
                    jeuServeur.updateEnemy(this);
                }
            }
        }
    }

    public boolean isAlive() {
        // Un ennemi n'est pas vivant si son état interne dit qu'il est mort
        // ou si son label est invisible
        if (!isAlive) {
            return false;
        }
        
        if (label != null && label.getjLabel() != null) {
            return label.getjLabel().isVisible();
        }
        
        return isAlive;
    }

    public int getPoints() {
        return points;
    }

    public int getType() {
        return type;
    }
}
