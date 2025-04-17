package modele;

import controleur.Controle;
import controleur.Global;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import outils.connexion.Connection;

/**
 * Gestion du jeu cté client
 * @author emds
 *
 */
public class JeuClient extends Jeu implements Global {

	// proprits
	private Connection connection ;
	private HashMap<Integer, JLabel> enemyLabels = new HashMap<>();
	
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
				// C'est un joueur normal
				this.controle.evenementModele(this, "ajout joueur", info);
			}
		}
		else if (info instanceof EnemyData) {
			// Traitement d'un EnemyData
			EnemyData enemyData = (EnemyData)info;
			
			// Vérifier si cet ennemi existe déjà
			JLabel enemyLabel;
			if (enemyLabels.containsKey(enemyData.getId())) {
				// Mise à jour du label existant
				enemyLabel = enemyLabels.get(enemyData.getId());
				enemyLabel.setBounds(enemyData.getPosX(), enemyData.getPosY(), L_PERSO, H_PERSO);
			} else {
				// Création d'un nouveau label
				enemyLabel = new JLabel();
				enemyLabel.setHorizontalAlignment(SwingConstants.CENTER);
				enemyLabel.setVerticalAlignment(SwingConstants.CENTER);
				enemyLabel.setBounds(enemyData.getPosX(), enemyData.getPosY(), L_PERSO, H_PERSO);
				enemyLabels.put(enemyData.getId(), enemyLabel);
			}
			
			// Mise à jour de l'image
			String imagePath = CHEMINENEMY + "Enemy" + enemyData.getType() + "/" + enemyData.getCurrentFrame() + EXTIMAGE;
			enemyLabel.setIcon(new ImageIcon(imagePath));
			
			// Envoi au controleur pour affichage
			this.controle.evenementModele(this, "ajout enemy", enemyLabel);
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
