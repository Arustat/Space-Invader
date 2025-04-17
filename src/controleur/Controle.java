package controleur;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modele.Jeu;
import modele.JeuClient;
import modele.JeuServeur;
import modele.Label;
import outils.connexion.ClientSocket;
import outils.connexion.Connection;
import outils.connexion.ServeurSocket;
import vue.Arene;
import vue.ChoixJoueur;
import vue.EntreeJeu;

/**
 * Controleur de l'application
 * @author emds
 *
 */
public class Controle implements Global {
	
	// propriétés
	private EntreeJeu frmEntreeJeu ;
	private Jeu leJeu ;
	private Arene frmArene ;
	private ChoixJoueur frmChoixJoueur ;
	private Connection connection ;

	/**
	 * Méthode de démarrage
	 * @param args
	 */
	public static void main(String[] args) {
		new Controle();
	}
	
	/**
	 * Constructeur
	 */
	private Controle() {
		this.frmEntreeJeu = new EntreeJeu(this) ;
		this.frmEntreeJeu.setVisible(true);
	}
	
	/**
	 * Récuépration de la connexion
	 * @param connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection ;
		if (this.leJeu instanceof JeuServeur) {
			this.leJeu.setConnection(connection);
		}
	}
	
	public void deconnection(Connection objet) {
		this.leJeu.deconnection(objet);
	}
	
	/**
	 * Reception d'une information de l'ordi distant, et transfert vers le jeu
	 * @param connection
	 * @param info
	 */
	public void receptionInfo(Connection connection, Object info) {
		this.leJeu.reception(connection, info);
	}
	
	/* **********************************************************************************************
	 * Evénements provenant de la vue
	 * **********************************************************************************************/
	
	/**
	 * Gére les événements provenant de la vue
	 * @param uneFrame
	 * @param info
	 */
	public void evenementVue(JFrame uneFrame, Object info) {
		// quelle est la frame qui demande ?
		if (uneFrame instanceof EntreeJeu) {
			this.evenementEntreeJeu(info);
		}else if (uneFrame instanceof ChoixJoueur) {
			this.evenementChoixJoueur(info);
		}else if (uneFrame instanceof Arene) {
			this.evenementArene(info);
		}
	}

	/**
	 * Gére les événements provenant de la frame EntreeJeu
	 * @param info
	 */
	private void evenementEntreeJeu(Object info) {
		if ((String)info=="serveur") {
			new ServeurSocket(this, PORT);
			this.leJeu = new JeuServeur(this) ;
			this.frmEntreeJeu.dispose();
			this.frmArene = new Arene("serveur", this);
			((JeuServeur)leJeu).constructionMurs();
			this.frmArene.setVisible(true);
		}else{
			if ((new ClientSocket((String)info, PORT, this)).isConnexionOk()) {
				this.leJeu = new JeuClient(this) ;
				this.leJeu.setConnection(connection);
				this.frmEntreeJeu.dispose();
				this.frmArene = new Arene("client", this);
				this.frmChoixJoueur = new ChoixJoueur(this);
				this.frmChoixJoueur.setVisible(true);
			} 
		}
		
	}

	/**
	 * Gére les événements provenant de la frame ChoixJoueur
	 * @param info
	 */
	private void evenementChoixJoueur(Object info) {
		// envoi de l'information vers le serveur
		((JeuClient)this.leJeu).envoi(info);
		// fermeture de la frame du choix du jouer
		this.frmChoixJoueur.dispose();
		this.frmArene.setVisible(true);
	}

	/**
	 * Gére les événements provenant de la frame Arene
	 * @param info
	 */
	private void evenementArene(Object info) {
		((JeuClient)this.leJeu).envoi(info);
	}
	
	
	/* **********************************************************************************************
	 * Evénements provenant du modèle
	 * **********************************************************************************************/

	/**
	 * Gére les événéments provenant du modèle (de jeuClient ou jeuServeur)
	 * @param unJeu
	 * @param ordre
	 * @param info
	 */
	public void evenementModele(Object unJeu, String ordre, Object info) {
		if(unJeu instanceof JeuServeur) {
			this.evenementJeuServeur(ordre, info) ;
		}else if(unJeu instanceof JeuClient) {
			this.evenementJeuClient(ordre, info) ;
		}
		else if (ordre.equals("game over")) {
			this.frmArene.afficherGameOver();
		}
		
	}
	
	/**
	 * Gére les événements provenant du jeu serveur
	 * @param ordre
	 * @param info
	 */
	private void evenementJeuServeur(String ordre, Object info) {
		if(ordre.equals("ajout mur")) {
			this.frmArene.ajoutMur((JLabel)info);
		}else if(ordre.equals("envoi panel murs")) {
			((JeuServeur)leJeu).envoi((Connection)info, frmArene.getJpnMurs());
		}else if(ordre.equals("ajout joueur")) {
			this.frmArene.ajoutJoueur((JLabel)info);
		}else if(ordre.equals("ajout phrase")) {
			this.frmArene.ajoutChat((String)info);
			((JeuServeur)leJeu).envoi(frmArene.getContenuTxtChat());
		} else if(ordre.equals("ajout enemy")) {
			frmArene.ajoutEnemy((JLabel)info);
		}
	}
	
	/**
	 * Gére les événements provenant du jeu client
	 * @param ordre
	 * @param info
	 */
	private void evenementJeuClient(String ordre, Object info) {
		// Si frmArene n'est pas encore initialisée, on ignore les événements graphiques
		if (this.frmArene == null) {
			return;
		}
		
		if(ordre.equals("ajout panel murs")) {
			this.frmArene.ajoutPanelMurs((JPanel)info) ;
		}else if (ordre.equals("ajout joueur")) {
			this.frmArene.ajoutModifJoueur(((Label)info).getNumLabel(), ((Label)info).getjLabel());
		}else if (ordre.equals("ajout healthbar")) {
			this.frmArene.ajoutModifJoueur(((Label)info).getNumLabel(), ((Label)info).getjLabel());
		}else if (ordre.equals("remplace chat")) {
			this.frmArene.remplaceChat((String)info);
		}else if (ordre.equals("son")) {
			this.frmArene.joueSon((Integer)info);
		}
	}

	public boolean isFull() {
		if (this.leJeu instanceof JeuServeur) {
			return ((JeuServeur) this.leJeu).isFull(false);
		}
		return false;
	}

}
