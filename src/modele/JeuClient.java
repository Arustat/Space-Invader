package modele;

import javax.swing.JPanel;

import controleur.Controle;
import outils.connexion.Connection;

/**
 * Gestion du jeu c�t� client
 * @author emds
 *
 */
public class JeuClient extends Jeu {

	// propri�t�s
	private Connection connection ;
	
	/**
	 * Controleur
	 * @param controle
	 */
	public JeuClient(Controle controle) {
		super.controle = controle ;
	}
	
	@Override
	public void setConnection(Connection connection) {
		this.connection = connection ;
	}

	@Override
	public void reception(Connection connection, Object info) {
		// arriv�e du panel des murs
		if(info instanceof JPanel) {
			controle.evenementModele(this, "ajout panel murs", info);
		// arriv�e d'un label correspondant � un personnage
		}else if(info instanceof Label) {
			controle.evenementModele(this, "ajout joueur", info);
		// arriv�e du contenu du chat	
		}else if(info instanceof String) {
			controle.evenementModele(this, "remplace chat", info);
		}else if(info instanceof Integer) {
			controle.evenementModele(this, "son", info);
		}
		
	}

	@Override
	public void deconnection(Connection connection) {
		System.exit(0);
	}
	
	/**
	 * Envoi d'une information vers l'ordinateur distant
	 * @param info
	 */
	public void envoi(Object info) {
		super.envoi(connection, info) ;
	}

}
