package modele;

import javax.swing.ImageIcon;

import controleur.Global;

public class Enemy extends Objet implements Global {

	private static final int MAXVIEENEMY = 10 ; // vie de départ pour tous les joueurs
	private static final int PERTE = 2 ; // perte de points de vie lors d'une attaque
	
	//propriétés
	private JeuServeur jeuServeur;
	private int vie;
	private Label message ;
	private int numPerso ;
	private int orientation ;


	
	public Enemy(JeuServeur jeuServeur) {
		this.jeuServeur = jeuServeur;
		vie = MAXVIEENEMY;
				
	}
	
	public void affiche(String etat, int etape) {
		label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
		label.getjLabel().setIcon(new ImageIcon(PERSO+numPerso+etat+etape+"d"+orientation+EXTIMAGE));
		message.getjLabel().setBounds(posX-10, posY+H_PERSO, L_PERSO+10, H_MESSAGE);
		// envoi du personnage à tous les autres joueurs
		jeuServeur.envoi(label);
	}
}
