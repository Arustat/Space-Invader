package modele;

import java.io.Serializable;

/**
 * Classe permettant de sérialiser les données d'un ennemi
 * pour les transmettre du serveur au client
 */
public class EnemyData implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int posX;
    private int posY;
    private int type;
    private int currentFrame;
    private int id; // Identifiant unique pour cet ennemi
    private boolean visible; // Visibilité de l'ennemi
    
    /**
     * Constructeur à partir d'un ennemi existant
     * @param enemy L'ennemi dont on veut extraire les données
     */
    public EnemyData(Enemy enemy, int id) {
        this.posX = enemy.getPosX();
        this.posY = enemy.getPosY();
        this.type = enemy.getType();
        this.currentFrame = 1; // Frame par défaut
        this.id = id;
        this.visible = (enemy.getLabel() != null && 
                       enemy.getLabel().getjLabel() != null && 
                       enemy.getLabel().getjLabel().isVisible());
    }
    
    /**
     * @return La position X de l'ennemi
     */
    public int getPosX() {
        return posX;
    }
    
    /**
     * @return La position Y de l'ennemi
     */
    public int getPosY() {
        return posY;
    }
    
    /**
     * @return Le type de l'ennemi
     */
    public int getType() {
        return type;
    }
    
    /**
     * @return La frame actuelle de l'animation
     */
    public int getCurrentFrame() {
        return currentFrame;
    }
    
    /**
     * @return L'identifiant unique de l'ennemi
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return La visibilité de l'ennemi
     */
    public boolean isVisible() {
        return visible;
    }
} 