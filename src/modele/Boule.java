package modele;

import controleur.Global;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import outils.connexion.Connection;

/**
 * Gestion de la boule
 * @author emds
 *
 */
public class Boule extends Objet implements Global {

	// propriétés
	private JeuServeur jeuServeur ;
	private Attaque attaque;
	
	/**
	 * Constructeur
	 */
	public Boule(JeuServeur jeuServeur) {
		// création de la boule, centrage, taille, et invisible pour le moment
		label = new Label(Label.getNbLabel(), new JLabel());
		Label.setNbLabel(Label.getNbLabel()+1);
		label.getjLabel().setHorizontalAlignment(SwingConstants.CENTER);
		label.getjLabel().setVerticalAlignment(SwingConstants.CENTER);
		label.getjLabel().setBounds(0, 0, L_BOULE, H_BOULE);
		label.getjLabel().setIcon(new ImageIcon(BOULE));
		label.getjLabel().setVisible(false);
		// récupération du jeu serveur
		this.jeuServeur = jeuServeur ;
		// ajout du label dans le jeu
		jeuServeur.nouveauLabelJeu(label);
	}
	
	/**
	 * 
	 * @param attaquant
	 */
	public void tireBoule(Joueur attaquant, ArrayList<Mur> lesmurs, Hashtable<Connection, Joueur> lesjoueurs) {
		// Positionner la boule : milieu haut du joueur
		posX = attaquant.getPosX() + (L_PERSO / 2) - (L_BOULE / 2);
		posY = attaquant.getPosY() - H_BOULE; // Juste au-dessus du joueur
	
		// Afficher la boule
		label.getjLabel().setBounds(posX, posY, L_BOULE, H_BOULE);
		label.getjLabel().setVisible(true);
	
		// Démarrer l'attaque (déplacement de la boule vers le haut)
		attaque = new Attaque(attaquant, jeuServeur, lesmurs, lesjoueurs);
	}
	
	
	
}
