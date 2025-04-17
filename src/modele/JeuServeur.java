package modele;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	/**
	 * Constructeur
	 * @param controle
	 */
	public JeuServeur(Controle controle) {
		super.controle = controle ;
		// initialisation du rang du dernier label mémorisé
		Label.setNbLabel(0);
		
		// Initialisation du WaveManager
		waveManager = new WaveManager(L_ARENE);
		
		// Timer pour mettre à jour les vagues d'ennemis
		waveTimer = new Timer(16, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				waveManager.update();
				
				// Envoi des ennemis aux clients
				for (Enemy enemy : waveManager.getCurrentWave()) {
					if (enemy.isAlive()) {
						controle.evenementModele(JeuServeur.this, "ajout enemy", enemy.getLabel().getjLabel());
					}
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
        this.lesJoueurs.put(connection, new Joueur(this)) ;
    }
	

	@Override
	public void reception(Connection connection, Object info) {
		String[] infos = ((String)info).split(SEPARE) ;
		String laPhrase ;
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
					lesJoueurs.get(connection).action(Integer.parseInt(infos[1]), lesJoueurs, lesMurs);
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

}
