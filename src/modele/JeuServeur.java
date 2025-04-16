package modele;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import controleur.Controle;
import controleur.Global;
import outils.connexion.Connection;

/**
 * Gestion du jeu c�t� serveur
 * @author emds
 *
 */
public class JeuServeur extends Jeu implements Global {

	// propri�t�s
	private ArrayList<Mur> lesMurs = new ArrayList<Mur>() ;
	private Hashtable<Connection, Joueur> lesJoueurs = new Hashtable<Connection, Joueur>() ;
	private ArrayList<Joueur> lesJoueursDansLordre = new ArrayList<Joueur>() ;
	private static final int MAX_PLAYERS = 2;
	
	/**
	 * Constructeur
	 * @param controle
	 */
	public JeuServeur(Controle controle) {
		super.controle = controle ;
		// initialisation du rang du dernier label m�moris�
		Label.setNbLabel(0);
	}
	
	/**
	 * G�n�ration des murs
	 */
	public void constructionMurs() {
		for (int k=0 ; k<NBMURS ; k++) {
			lesMurs.add(new Mur()) ;
			this.controle.evenementModele(this, "ajout mur", lesMurs.get(lesMurs.size()-1).getLabel().getjLabel());
		}
	}
	
	/**
	 * Demande au controleur d'ajouter un joueuer dans l'ar�ne
	 * @param label
	 */
	public void nouveauLabelJeu(Label label) {
		this.controle.evenementModele(this, "ajout joueur", label.getjLabel());
	}
	
	/**
	 * Envoi � tous les clients
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
				// envoi des pr�c�dents joueurs au nouveau joueur
				for(Joueur joueur : lesJoueursDansLordre) {
					super.envoi(connection, joueur.getLabel());
					super.envoi(connection, joueur.getMessage());
					super.envoi(connection, joueur.getBoule().getLabel());
				}
				// initialisation du nouveau joueur (positionnement al�atoire...)
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
