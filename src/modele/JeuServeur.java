package modele;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import controleur.Controle;
import controleur.Global;
import outils.connexion.Connection;

/**
 * Gestion du jeu ct serveur
 * @author emds
 *
 */
public class JeuServeur extends Jeu implements Global {

	// proprits
	private ArrayList<Mur> lesMurs = new ArrayList<Mur>() ;
	private Hashtable<Connection, Joueur> lesJoueurs = new Hashtable<Connection, Joueur>() ;
	private ArrayList<Joueur> lesJoueursDansLordre = new ArrayList<Joueur>() ;
	private static final int MAX_PLAYERS = 2;
	private WaveManager waveManager;
	private Timer waveTimer;
	private int nextEnemyId = 0; // Compteur pour assigner des ID uniques aux ennemis
	private HashMap<Integer, Enemy> enemiesById = new HashMap<>(); // Map des ennemis par ID
	
	/**
	 * Constructeur
	 * @param controle
	 */
	public JeuServeur(Controle controle) {
		super.controle = controle ;
		// initialisation du rang du dernier label mémorisé
		Label.setNbLabel(0);
		
		// Initialisation du WaveManager
		waveManager = new WaveManager(L_ARENE, this);
		
		// Timer pour mettre à jour les vagues d'ennemis
		waveTimer = new Timer(16, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				waveManager.update();
				
				// Créer une liste de tous les ennemis actuellement dans la vague
				Set<Enemy> currentEnemies = new HashSet<>(waveManager.getCurrentWave());
				
				// Envoyer les ennemis actifs aux clients et les afficher sur le serveur
				for (Enemy enemy : currentEnemies) {
					// Affichage sur le serveur
					controle.evenementModele(JeuServeur.this, "ajout enemy", enemy.getLabel().getjLabel());
					
					// Vérifier si cet ennemi a déjà un ID
					int enemyId = -1;
					for (int id : enemiesById.keySet()) {
						if (enemiesById.get(id) == enemy) {
							enemyId = id;
							break;
						}
					}
					
					// Si pas d'ID, en assigner un nouveau
					if (enemyId == -1) {
						enemyId = nextEnemyId++;
						enemiesById.put(enemyId, enemy);
					}
					
					// Créer un EnemyData pour l'envoi au client
					EnemyData enemyData = new EnemyData(enemy, enemyId);
					
					// Envoi aux clients
					for (Connection connection : lesJoueurs.keySet()) {
						envoi(connection, enemyData);
					}
				}
				
				// Identifier les ennemis qui ne sont plus dans la vague actuelle
				Set<Integer> idsToRemove = new HashSet<>();
				for (Map.Entry<Integer, Enemy> entry : enemiesById.entrySet()) {
					int id = entry.getKey();
					Enemy enemy = entry.getValue();
					
					if (!currentEnemies.contains(enemy) || !enemy.isAlive()) {
						idsToRemove.add(id);
						
						// Si l'ennemi n'est pas dans la vague actuelle ou n'est plus vivant, 
						// envoyer une dernière mise à jour pour s'assurer qu'il disparaît chez les clients
						if (enemy.getLabel() != null && enemy.getLabel().getjLabel() != null) {
							enemy.getLabel().getjLabel().setVisible(false);
							EnemyData lastUpdate = new EnemyData(enemy, id);
							for (Connection connection : lesJoueurs.keySet()) {
								envoi(connection, lastUpdate);
							}
						}
					}
				}
				
				// Supprimer les ennemis marqués pour suppression
				for (int id : idsToRemove) {
					enemiesById.remove(id);
				}
				
				// Si la vague est terminée, on commence une nouvelle vague
				if (waveManager.isWaveComplete()) {
					waveManager.startNewWave();
					controle.evenementModele(JeuServeur.this, "ajout phrase", "*** Nouvelle vague d'ennemis ! ***");
				}
			}
		});
		waveTimer.start();
	}
	
	/**
	 * Gnration des murs
	 */
	public void constructionMurs() {
		for (int k=0 ; k<NBMURS ; k++) {
			lesMurs.add(new Mur()) ;
			this.controle.evenementModele(this, "ajout mur", lesMurs.get(lesMurs.size()-1).getLabel().getjLabel());
		}
	}
	
	/**
	 * Demande au controleur d'ajouter un joueuer dans l'arène
	 * @param label
	 */
	public void nouveauLabelJeu(Label label) {
		this.controle.evenementModele(this, "ajout joueur", label.getjLabel());
	}
	
	/**
	 * Envoi à tous les clients
	 */
	public void envoi(Object info) {
		for (Connection connection : lesJoueurs.keySet()) {
			super.envoi(connection, info);
		}
	}

	@Override
	public void setConnection(Connection connection) {
        if (lesJoueurs.size() < MAX_PLAYERS) {
			this.lesJoueurs.put(connection, new Joueur(this));
		} else {
			// Informer le client que le serveur est plein
			super.envoi(connection, "Server is full. Cannot accept new players.");
			try {
				connection.getSocket().close();
			} catch (IOException e) {
				System.out.println("Erreur lors de la fermeture de la connexion : " + e);
			}
		}
    }
	

	@Override
	public void reception(Connection connection, Object info) {
		// Vérifiez si le joueur existe dans la collection
		if (!lesJoueurs.containsKey(connection)) {
			System.out.println("Connexion non reconnue ou joueur non initialisé");
			return;
		}
		
		String[] infos = ((String)info).split(SEPARE);
		String laPhrase;
		switch(Integer.parseInt(infos[0])) {
			// un nouveau joueur vient d'arriver
			case PSEUDO : 
				// envoi des murs au nouveau joueur
				controle.evenementModele(this, "envoi panel murs", connection);
				// envoi des prédents joueurs au nouveau joueur
				for(Joueur joueur : lesJoueursDansLordre) {
					super.envoi(connection, joueur.getLabel());
					super.envoi(connection, joueur.getMessage());
					super.envoi(connection, joueur.getBoule().getLabel());
				}
				// initialisation du nouveau joueur (positionnement aléatoire...)
				lesJoueurs.get(connection).initPerso(infos[1], Integer.parseInt(infos[2]), lesJoueurs, lesMurs);
				// insertion du nouveau joueur dans la liste dans l'ordre, pour l'envoyer dans l'ordre aux joueurs suivants
				lesJoueursDansLordre.add(lesJoueurs.get(connection)) ;
				laPhrase = "***"+lesJoueurs.get(connection).getPseudo()+" vient de se connecter ***" ;
				controle.evenementModele(this, "ajout phrase", laPhrase);
				break ;
			case CHAT :
				laPhrase = lesJoueurs.get(connection).getPseudo()+" > "+infos[1] ;
				controle.evenementModele(this, "ajout phrase", laPhrase);
				break ;
			case ACTION:
				if(!lesJoueurs.get(connection).estMort()) {
					// Nouvelle version - gestion des actions multiples
					// Format: ACTION~action1,action2,action3...
					String[] actions = infos[1].split(",");
					for (String actionStr : actions) {
						try {
							int actionCode = Integer.parseInt(actionStr.trim());
							lesJoueurs.get(connection).action(actionCode, lesJoueurs, lesMurs);
						} catch (NumberFormatException e) {
							System.out.println("Action invalide: " + actionStr);
						}
					}
				}
				break;
		}
	}
	
	@Override
	public void deconnection(Connection connection) {
		((Joueur)this.lesJoueurs.get(connection)).departJoueur();
		this.lesJoueurs.remove(connection);
		
	}

	public void envoiUn(Joueur joueur, Object info) {
		for (Connection connection : lesJoueurs.keySet()) {
			if (lesJoueurs.get(connection) == joueur) {
				super.envoi(connection, info);
				break;
			}
		}
	}

	public boolean isFull() {
		return lesJoueurs.size() >= MAX_PLAYERS;
	}
	
	/**
	 * Vérifie si le serveur est plein, en tenant compte d'un joueur supplémentaire potentiel
	 * @param countNewPlayer Si true, considère qu'un nouveau joueur est en train d'essayer de se connecter
	 * @return true si le serveur est plein
	 */
	public boolean isFull(boolean countNewPlayer) {
		int count = lesJoueurs.size();
		if (countNewPlayer) {
			count++; // On compte comme si le joueur était déjà connecté
		}
		return count >= MAX_PLAYERS;
	}
	
	/**
	 * Récupère la collection des joueurs
	 * @return la table de hachage des joueurs
	 */
	public Hashtable<Connection, Joueur> getJoueurs() {
		return lesJoueurs;
	}
	
	/**
	 * Récupère le gestionnaire de vagues
	 * @return le gestionnaire de vagues
	 */
	public WaveManager getWaveManager() {
		return waveManager;
	}

	/**
	 * Envoie une mise à jour concernant un ennemi à tous les clients
	 * @param enemy L'ennemi à mettre à jour chez les clients
	 */
	public void updateEnemy(Enemy enemy) {
		// Chercher l'ID de l'ennemi
		int enemyId = -1;
		for (int id : enemiesById.keySet()) {
			if (enemiesById.get(id) == enemy) {
				enemyId = id;
				break;
			}
		}
		
		// S'assurer que l'ennemi a un ID
		if (enemyId != -1) {
			// Créer et envoyer les données de l'ennemi
			EnemyData enemyData = new EnemyData(enemy, enemyId);
			for (Connection connection : lesJoueurs.keySet()) {
				envoi(connection, enemyData);
			}
		}
	}

}
