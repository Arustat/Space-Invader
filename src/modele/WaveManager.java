package modele;

import java.util.ArrayList;
import java.util.Random;

public class WaveManager {
    private ArrayList<Enemy> currentWave;
    private int currentWaveNumber;
    private int enemiesPerWave;
    private Random random;
    private int screenWidth;
    private int spawnDelay;
    private long lastSpawnTime;

    public WaveManager(int screenWidth) {
        this.screenWidth = screenWidth;
        this.currentWave = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.enemiesPerWave = 5;
        this.random = new Random();
        this.spawnDelay = 1000; // 1 seconde entre chaque spawn
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void startNewWave() {
        currentWaveNumber++;
        enemiesPerWave += 2; // Augmente le nombre d'ennemis par vague
        currentWave.clear();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        
        // Vérifie si on peut spawner un nouvel ennemi
        if (currentWave.size() < enemiesPerWave && 
            currentTime - lastSpawnTime >= spawnDelay) {
            
            // Choisit un type d'ennemi aléatoire
            int enemyType = random.nextInt(3) + 1;
            
            // Position aléatoire en haut de l'écran
            int x = random.nextInt(screenWidth - 50);
            
            // Crée et ajoute le nouvel ennemi
            Enemy enemy = new Enemy(x, 0, enemyType);
            currentWave.add(enemy);
            
            lastSpawnTime = currentTime;
        }

        // Met à jour tous les ennemis de la vague
        for (int i = currentWave.size() - 1; i >= 0; i--) {
            Enemy enemy = currentWave.get(i);
            enemy.move();
            
            // Met à jour la position du label de l'ennemi
            enemy.getLabel().getjLabel().setBounds(enemy.getPosX(), enemy.getPosY(), 
                enemy.getLabel().getjLabel().getWidth(), 
                enemy.getLabel().getjLabel().getHeight());
            
            // Supprime les ennemis qui sont sortis de l'écran
            if (enemy.getPosY() > 700) { // Hauteur de l'écran
                currentWave.remove(i);
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
        return currentWave.isEmpty() && currentWave.size() >= enemiesPerWave;
    }
}