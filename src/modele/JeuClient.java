package modele;

import controleur.Controle;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
		if (info instanceof String) {
			String message = (String) info;
			if (message.equals("Server is full. Cannot accept new players.")) {
				JOptionPane.showMessageDialog(null, "Le serveur est plein. Impossible de se connecter.", "Serveur Plein", JOptionPane.WARNING_MESSAGE);
				System.exit(0);
			} else if ("GAME_OVER".equals(message)) {
				this.controle.evenementModele(this, "game over", null);
			} else {
				this.controle.evenementModele(this, "remplace chat", message);
			}
		}
		else if (info instanceof JPanel) {
			this.controle.evenementModele(this, "ajout panel murs", info);
		}
		else if (info instanceof Label) {
			Label label = (Label)info;
			if (label.getjLabel() instanceof HealthBar) {
				this.controle.evenementModele(this, "ajout healthbar", info);
			} else {
				this.controle.evenementModele(this, "ajout joueur", info);
			}
		}
		else if (info instanceof Integer) {
			this.controle.evenementModele(this, "son", info);
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
		super.envoi(this.connection, info) ;
	}

}
