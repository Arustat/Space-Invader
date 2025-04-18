package modele;

import controleur.Global;
import java.util.ArrayList;
import java.util.Random;

public class WaveManager implements Global {
    private ArrayList<Enemy> currentWave;
    private int currentWaveNumber;
    private int enemiesPerWave;
    private Random random;
    private int screenWidth;
    private int spawnDelay;
    private long lastSpawnTime;
    private JeuServeur jeuServeur;
    private boolean wavesActive; // Nouvelle variable pour suivre l'état des vagues

    public WaveManager(int screenWidth, JeuServeur jeuServeur) {
        this.screenWidth = screenWidth;
        this.currentWave = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.enemiesPerWave = 5;
        this.random = new Random();
        this.spawnDelay = 1000; // 1 seconde entre chaque spawn
        this.lastSpawnTime = System.currentTimeMillis();
        this.jeuServeur = jeuServeur;
        this.wavesActive = false; // Les vagues ne sont pas actives par défaut
    }

    public void startNewWave() {
        currentWaveNumber++;
        enemiesPerWave += 2; // Augmente le nombre d'ennemis par vague
        currentWave.clear();
    }

    /**
     * Active ou désactive les vagues d'ennemis
     * @param active true pour activer les vagues, false pour les désactiver
     */
    public void setWavesActive(boolean active) {
        this.wavesActive = active;
        if (!active) {
            // Si on désactive les vagues, on nettoie la vague actuelle
            currentWave.clear();
        }
    }

    public void update() {
        if (!wavesActive) return; // Ne rien faire si les vagues ne sont pas actives
        
        long currentTime = System.currentTimeMillis();
        
        // Vérifie si on peut spawner un nouvel ennemi
        if (currentWave.size() < enemiesPerWave && 	
            currentTime - lastSpawnTime >= spawnDelay) {
            
            // Choisit un type d'ennemi aléatoire
            int enemyType = random.nextInt(3) + 1;
            
            // Position aléatoire en haut de l'écran
            int x = random.nextInt(screenWidth - 50);
            
            // Crée et ajoute le nouvel ennemi
            Enemy enemy = new Enemy(x, 0, enemyType, jeuServeur);
            currentWave.add(enemy);
            
            lastSpawnTime = currentTime;
        }

        // Met à jour tous les ennemis de la vague
        for (int i = currentWave.size() - 1; i >= 0; i--) {
            Enemy enemy = currentWave.get(i);
            
            // Ne traiter que les ennemis vivants
            if (enemy.isAlive()) {
                enemy.move();
                
                // Met à jour la position du label de l'ennemi
                enemy.getLabel().getjLabel().setBounds(enemy.getPosX(), enemy.getPosY(), 
                    enemy.getLabel().getjLabel().getWidth(), 
                    enemy.getLabel().getjLabel().getHeight());
                
                // Vérifie les collisions avec les joueurs
                checkPlayerCollisions(enemy);
            }
            
            // Supprime les ennemis qui sont sortis de l'écran ou qui sont morts
            if (enemy.getPosY() > 700 || !enemy.isAlive()) {
                currentWave.remove(i);
                
                // Si l'ennemi est simplement sorti de l'écran, s'assurer qu'il est bien invisible
                if (enemy.getPosY() > 700 && enemy.isAlive()) {
                    enemy.getLabel().getjLabel().setVisible(false);
                    jeuServeur.envoi(enemy.getLabel());
                }
            }
        }
    }
    
    /**
     * Vérifie les collisions entre un ennemi et les joueurs
     * @param enemy L'ennemi à vérifier
     */
    private void checkPlayerCollisions(Enemy enemy) {
        if (!enemy.isAlive()) return;
        
        for (Joueur joueur : jeuServeur.getJoueurs().values()) {
            if (!joueur.estMort() && enemy.toucheObjet(joueur)) {
                // Collision détectée
                if (enemy.getType() == 2) { // Ennemi rapide
                    joueur.perteVie(2); // Perd 2 points de vie
                } else {
                    joueur.perteVie(); // Perd 1 point de vie par défaut
                }
                
                // Détruire l'ennemi après collision mais ne pas le faire freezer
                enemy.takeDamage();
                
                // Envoyer le son
                jeuServeur.envoi(SON[HURT]);
                
                // Vérifier si le joueur est mort
                if (joueur.estMort()) {
                    Explosion explosion = new Explosion(joueur.getPosX(), joueur.getPosY(), jeuServeur);
                    jeuServeur.nouveauLabelJeu(explosion.getLabel());
                    explosion.startAnimation();
                    jeuServeur.envoiUn(joueur, "GAME_OVER");
                    joueur.departJoueur();
                    jeuServeur.envoi(SON[DEATH]);
                }
                
                // Ne pas faire de return ici - on veut que l'ennemi continue à être traité
                // dans la boucle d'update.
                break; // Sortir de la boucle des joueurs après avoir traité une collision
            }
        }
    }

    public ArrayList<Enemy> getCurrentWave() {
        return currentWave;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    public boolean isWaveComplete() {
        // Une vague est complète si la liste des ennemis est vide
        // ET si tous les ennemis prévus ont déjà été générés
        return currentWave.isEmpty() && enemiesPerWave <= getEnemiesSpawned();
    }
    
    /**
     * Compte le nombre total d'ennemis générés pour la vague actuelle
     * Cette valeur est approximative car elle est calculée à partir du delai
     * @return le nombre d'ennemis générés
     */
    private int getEnemiesSpawned() {
        // Calcule approximativement le nombre d'ennemis qui devraient être générés
        // basé sur le temps écoulé depuis le début de la vague
        long timeElapsed = System.currentTimeMillis() - lastSpawnTime + (spawnDelay * enemiesPerWave);
        return Math.min((int)(timeElapsed / spawnDelay), enemiesPerWave);
    }
}