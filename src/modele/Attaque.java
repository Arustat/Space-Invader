package modele;

import java.util.ArrayList;
import java.util.Hashtable;

import controleur.Global;
import outils.connexion.Connection;
import outils.son.Son;

/**
 * Gestion de l'attaque (boule tir�e, joueur �ventuellement touch�)
 * @author emds
 *
 */
public class Attaque extends Thread implements Global {

	// propri�t�s
	private Joueur attaquant ;
	private JeuServeur jeuServeur ;
	private ArrayList<Mur> lesMurs = new ArrayList<Mur>() ;
	private Hashtable<Connection, Joueur> lesJoueurs = new Hashtable<Connection, Joueur>() ;
	
	/**
	 * Constructeur
	 * @param attaquant
	 * @param jeuServeur
	 */
	public Attaque(Joueur attaquant, JeuServeur jeuServeur, ArrayList<Mur> lesmurs, Hashtable<Connection, Joueur> lesjoueurs) {
		this.attaquant = attaquant;
		this.jeuServeur = jeuServeur;
		this.lesMurs = lesmurs;
		this.lesJoueurs = lesjoueurs;
		this.start();
	}

	/**
	 * m�thode dans le thread, pour faire bouger la boule
	 */
	public void run() {
		// l'attaquant est mis � la position 1 de la marche
		attaquant.affiche(1);
		// r�cup�ration de la boule et orientation de l'attaquant
		Boule laboule = attaquant.getBoule() ;
		int orientation = attaquant.getOrientation() ;
		laboule.getLabel().getjLabel().setVisible(true);
		// gestion de l'�ventuel joueur touch� par la boule
		Joueur victime = null ;
		// boucle sur la trajectoire de la boule
		do {
			if (orientation==GAUCHE) {
				laboule.setPosX(laboule.getPosX()-LEPAS) ;
			}else{
				laboule.setPosX(laboule.getPosX()+LEPAS) ;				
			}
			laboule.getLabel().getjLabel().setBounds(laboule.getPosX(), laboule.getPosY(), L_BOULE, H_BOULE);
			pause(5, 0);
			jeuServeur.envoi(laboule.getLabel());
			victime = toucheJoueur() ;
		}while(laboule.getPosX()>=0 && laboule.getPosX()<=L_ARENE && toucheMur()== false && victime == null) ;
		
		if(victime != null && !victime.estMort()) {
			victime.perteVie();
			jeuServeur.envoi(HURT);
			attaquant.gainVie();
			/*for(int i = 1; i<=NBETATSBLESSE;i++) {
				victime.affiche(BLESSE, i);
				pause(80,0);
			}*/
			if(victime.estMort()) {
				jeuServeur.envoi(DEATH);
				// Créer une explosion à la position du joueur mort
				Explosion explosion = new Explosion(victime.getPosX(), victime.getPosY());
				jeuServeur.nouveauLabelJeu(explosion.getLabel());
				explosion.startAnimation();
				// Envoyer un message game over au joueur mort
				jeuServeur.envoiUn(victime, "GAME_OVER");
				for(int y =1; y<=NBETATSMORT;y++) {
					victime.departJoueur();
				}
			}else {				
				victime.affiche(1);
			}
			attaquant.affiche(1);
		}
		// la boule a fini son parcourt et redevient invisible
		laboule.getLabel().getjLabel().setVisible(false);		
		jeuServeur.envoi(laboule.getLabel());
	}
	
	/**
	 * Gestion d'une pause (qui servira � r�guler le mouvement de la boule)
	 * @param milli
	 * @param nano
	 */
	private void pause(long milli, int nano) {
		try {
			Thread.sleep(milli, nano);
		} catch (InterruptedException e) {
			System.out.println("Probl�me sur la pause");
		}
	}
	
	/**
	 * Controle si la boule touche un mur
	 * @return
	 */
	private boolean toucheMur() {
		for (Mur unMur : lesMurs) {
			if (attaquant.getBoule().toucheObjet(unMur)) {
				return true ;
			}
		}
		return false ;		
	}

	/**
	 * Controle si la boule touche un joueur
	 * @return
	 */
	private Joueur toucheJoueur() {
		for (Joueur unJoueur : lesJoueurs.values()) {
			if (attaquant.getBoule().toucheObjet(unJoueur)) {
				return unJoueur ;
			}
		}
		return null ;
	}
	
	
}
