package modele;

import java.util.ArrayList;
import java.util.Hashtable;

import controleur.Global;
import outils.connexion.Connection;
import outils.son.Son;

/**
 * Gestion de l'attaque (boule tirée, joueur éventuellement touché)
 * @author emds
 *
 */
public class Attaque extends Thread implements Global {

	// propriétés
	private Joueur attaquant ;
	private JeuServeur jeuServeur ;
	private Hashtable<Connection, Joueur> lesJoueurs = new Hashtable<Connection, Joueur>() ;
	
	/**
	 * Constructeur
	 * @param attaquant
	 * @param jeuServeur
	 */
	public Attaque(Joueur attaquant, JeuServeur jeuServeur, Hashtable<Connection, Joueur> lesjoueurs) {
		this.attaquant = attaquant;
		this.jeuServeur = jeuServeur;
		this.lesJoueurs = lesjoueurs;
		this.start();
	}

	/**
	 * méthode dans le thread, pour faire bouger la boule
	 */
	public void run() {
		// l'attaquant est mis à la position 1 de la marche
		attaquant.affiche(1);
		// récupération de la boule
		Boule laboule = attaquant.getBoule();
		laboule.getLabel().getjLabel().setVisible(true);
		
		// gestion de l'éventuel joueur touché par la boule
		Joueur victime = null;
		// gestion de l'éventuel ennemi touché par la boule
		Enemy ennemiTouche = null;
	
		// Initialisation de la vitesse verticale de la boule
		int vitesseVerticale = -LEPAS; // La boule monte (vers le haut)
	
		// boucle sur la trajectoire de la boule (boule se déplace verticalement)
		do {
			laboule.setPosY(laboule.getPosY() + vitesseVerticale); // Déplacement vers le haut
	
			laboule.getLabel().getjLabel().setBounds(laboule.getPosX(), laboule.getPosY(), L_BOULE, H_BOULE);
			pause(5, 0);
			jeuServeur.envoi(laboule.getLabel());
	
			// Vérifier si la boule touche un joueur
			victime = toucheJoueur();
			
			// Vérifier si la boule touche un ennemi
			ennemiTouche = toucheEnnemi();
		} while (laboule.getPosY() >= 0 && laboule.getPosY() <= H_ARENE &&  victime == null && ennemiTouche == null);
	
		if (victime != null && !victime.estMort()) {
			victime.perteVie();
			jeuServeur.envoi(SON[HURT]);
			attaquant.gainVie();
			if (victime.estMort()) {
				jeuServeur.envoi(SON[DEATH]);
				Explosion explosion = new Explosion(victime.getPosX(), victime.getPosY(), jeuServeur);
				jeuServeur.nouveauLabelJeu(explosion.getLabel());
				explosion.startAnimation();
				jeuServeur.envoiUn(victime, "GAME_OVER");
				for (int y = 1; y <= NBETATSMORT; y++) {
					victime.departJoueur();
				}
			} else {
				victime.affiche(1);
			}
			attaquant.affiche(1);
		}
		
		// Gérer l'ennemi touché par la boule
		if (ennemiTouche != null && ennemiTouche.isAlive()) {
			// Faire mourir l'ennemi
			ennemiTouche.takeDamage();
			jeuServeur.envoi(SON[DEATH]);
			
			// Donner des points de vie au joueur qui a tué l'ennemi
			attaquant.gainVie();
		}
		
		// la boule a fini son parcours et redevient invisible
		laboule.getLabel().getjLabel().setVisible(false);
		jeuServeur.envoi(laboule.getLabel());
	}
	
	
	/**
	 * Gestion d'une pause (qui servira à réguler le mouvement de la boule)
	 * @param milli
	 * @param nano
	 */
	private void pause(long milli, int nano) {
		try {
			Thread.sleep(milli, nano);
		} catch (InterruptedException e) {
			System.out.println("Problème sur la pause");
		}
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
	
	/**
	 * Controle si la boule touche un ennemi
	 * @return l'ennemi touché ou null
	 */
	private Enemy toucheEnnemi() {
		ArrayList<Enemy> ennemis = jeuServeur.getWaveManager().getCurrentWave();
		for (Enemy unEnnemi : ennemis) {
			if (unEnnemi.isAlive() && attaquant.getBoule().toucheObjet(unEnnemi)) {
				return unEnnemi;
			}
		}
		return null;
	}
}
