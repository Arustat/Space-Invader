package modele;

import controleur.Controle;
import controleur.Global;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
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
			} else if (message.startsWith("SCORE")) {
				String[] parts = message.split(SEPARE);
				if (parts.length > 1) {
					this.controle.evenementModele(this, "SCORE", parts[1]);
				}
			} else {
				this.controle.evenementModele(this, "remplace chat", message);
			}
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
			
			// Mise à jour de la visibilité
			enemyLabel.setVisible(enemyData.isVisible());
			
			// Si l'ennemi n'est plus visible, le retirer complètement de l'affichage
			if (!enemyData.isVisible()) {
				// Suppression du label de la vue
				if (enemyLabel.getParent() != null) {
					enemyLabel.getParent().remove(enemyLabel);
				}
				
				// Si c'est un ennemi qui vient d'être tué, on peut le retirer du HashMap
				// après un petit délai pour s'assurer que toutes les mises à jour sont terminées
				final int enemyId = enemyData.getId();
				new Timer(500, e -> {
					enemyLabels.remove(enemyId);
					((Timer)e.getSource()).stop();
				}).start();
			} else {
				// Mise à jour de l'image uniquement si l'ennemi est visible
				String imagePath = CHEMINENEMY + "Enemy" + enemyData.getType() + "/" + enemyData.getCurrentFrame() + EXTIMAGE;
				enemyLabel.setIcon(new ImageIcon(imagePath));
				
				// Envoi au controleur pour affichage
				this.controle.evenementModele(this, "ajout enemy", enemyLabel);
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
