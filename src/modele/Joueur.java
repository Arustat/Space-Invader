package modele;

import controleur.Global;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import outils.connexion.Connection;

/**
 * Gestion des joueurs
 * @author emds
 *
 */
public class Joueur extends Objet implements Global {
	// attention renommer MAXVIE PAR DEPVIE
	// constantes
	private static final int DEPVIE = 10 ; // vie de départ pour tous les joueurs
	private static final int GAIN = 1 ; // gain de points de vie lors d'une attaque
	private static final int PERTE = 2 ; // perte de points de vie lors d'une attaque
	
	// propriétés
	private String pseudo ;
	private int numPerso ;
	private Label message ;
	private JeuServeur jeuServeur ;
	private int vie ; // vie restante du joueur
	private int orientation ; // tourné vers la gauche (0) ou vers la droite (1)
	private int etape ; // numéro d'étape dans l'animation
	private Boule boule ; // la boule du joueur
	private Timer time_animation; //Temps d'animation d'un sprite
	private Explosion explosion;
	private HealthBar healthBar;
	private Label healthBarLabel; // Stockage du Label de la barre de vie

	
	/**
	 * Constructeur
	 */
	public Joueur(JeuServeur jeuServeur) {
		this.jeuServeur = jeuServeur ;
		vie = DEPVIE ;
		etape = 1 ;
		orientation = DROITE ;
	}
	
	/**
	 * @return the pseudo
	 */
	public String getPseudo() {
		return this.pseudo;
	}

	/**
	 * @return the boule
	 */
	public Boule getBoule() {
		return this.boule;
	}

	/**
	 * @return the orientation
	 */
	public int getOrientation() {
		return this.orientation;
	}

	/**
	 * @return the numPerso
	 */
	public int getNumPerso() {
		return this.numPerso;
	}

	/**
	 * Affiche le personnage et son message
	 * @param etat
	 * @param etape
	 */
	public void affiche(int etape) {
		label.getjLabel().setBounds(posX, posY, L_PERSO, H_PERSO);
		label.getjLabel().setIcon(new ImageIcon(PERSO+numPerso+"_"+ etape+ EXTIMAGE));
		// Position du message
		message.getjLabel().setBounds(posX-10, posY+H_PERSO, L_PERSO+10, H_MESSAGE);
		message.getjLabel().setText(pseudo);

		// Position et mise à jour de la barre de vie
		healthBar.setBounds(posX, posY + H_PERSO + 5, 50, 5);
		healthBar.setHealth(vie);

		// envoi du personnage à tous les autres joueurs
		jeuServeur.envoi(label);
		jeuServeur.envoi(message);
		jeuServeur.envoi(healthBarLabel);
	}
	
	/**
	 * Animation des sprites 
	 */
	public void animation() {
		time_animation = new Timer();
		time_animation.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	    		etape = (etape % NBETATS) + 1 ;
	            // Redessine le sprite avec la nouvelle étape
	            affiche(etape);
	        }
	    }, 0, 200);
	}
	
	/**
	 * Initialisation d'un joueur (pseudo et numéro)
	 * @param pseudo
	 * @param numPerso
	 */
	public void initPerso(String pseudo, int numPerso, Hashtable<Connection, Joueur> lesJoueurs) {
		this.pseudo = pseudo ;
		this.numPerso = numPerso ;
		// création de l'affichage du personnage
		this.label = new Label(Label.getNbLabel(), new JLabel()) ;
		Label.setNbLabel(Label.getNbLabel()+1);
		this.label.getjLabel().setHorizontalAlignment(SwingConstants.CENTER);
		this.label.getjLabel().setVerticalAlignment(SwingConstants.CENTER);
		jeuServeur.nouveauLabelJeu(this.label);
		// création de l'affichage du message sous le personnage
		this.message = new Label(Label.getNbLabel(), new JLabel()) ;
		Label.setNbLabel(Label.getNbLabel()+1);
		this.message.getjLabel().setHorizontalAlignment(SwingConstants.CENTER);
		this.message.getjLabel().setFont(new Font("Dialog", Font.PLAIN, 8));
		this.message.getjLabel().setForeground(Color.WHITE); // Texte en blanc
		this.message.getjLabel().setBackground(Color.BLACK); // Fond noir
		this.message.getjLabel().setOpaque(true); // Très important pour afficher le fond
		jeuServeur.nouveauLabelJeu(this.message);

		// Création de la barre de vie
		this.healthBar = new HealthBar(DEPVIE);
		this.healthBar.setSize(50, 5);
		this.healthBar.setVisible(true);
		this.healthBarLabel = new Label(Label.getNbLabel(), this.healthBar);
		Label.setNbLabel(Label.getNbLabel() + 1);
		jeuServeur.nouveauLabelJeu(this.healthBarLabel);

		// calcul de la première position aléatoire
		premierePosition(lesJoueurs) ;
		// affichage du personnage
		affiche(etape) ;
		// création de la boule
		this.boule = new Boule(jeuServeur) ;
		this.jeuServeur.envoi(this.boule.getLabel());
		animation();
	}

	/**
	 * Gère le déplacement du personnage
	 * @param action
	 * @param position
	 * @param orientation
	 * @param lepas
	 * @param max
	 * @param lesJoueurs
	 * @param lesMurs
	 * @return
	 */
	private int deplace(int action, // action sollicitée : aller vers la gauche, la droite
						int position, // position de départ
						int orientation, // orientation de départ
						int lepas, // valeur du déplacement (positif ou négatif)
						int max, // valeur à ne pas dépasser
						Hashtable<Connection, Joueur> lesJoueurs // les autres joueurs (pour éviter les collisions)
						) { // les murs (pour éviter les collisions)
		this.orientation = orientation ;
		int ancpos = position ;
		position += lepas ;
		position = Math.max(position, 0) ;
		position = Math.min(position,  max) ;
		if (action==GAUCHE || action==DROITE) {
			posX = position ;
		}else{
			posY = position ;
		}
		// controle s'il y a collision
		if (toucheJoueur(lesJoueurs)) {
			position = ancpos ;
		}
		return position ;
	}
	
	/**
	 * Gère une action reçue du controleur (déplacement, tire de boule...)
	 * @param action
	 * @param lesJoueurs
	 * @param lesMurs
	 */
	public void action(int action, Hashtable<Connection, Joueur> lesJoueurs) {
		// traite l'action
		switch (action) {
			case GAUCHE : this.posX = deplace(action, posX, GAUCHE,     -LEPAS, L_ARENER - L_PERSO, lesJoueurs) ;break ;
			case DROITE : this.posX = deplace(action, posX, DROITE,      LEPAS, L_ARENER - L_PERSO, lesJoueurs) ;break ;
			case HAUT :   this.posY = deplace(action, posY, orientation,-LEPAS, H_ARENER - H_PERSO - H_MESSAGE, lesJoueurs) ;break ;
			case BAS :    this.posY = deplace(action, posY, orientation, LEPAS, H_ARENER - H_PERSO - H_MESSAGE, lesJoueurs) ;break ;
			case TIRE :
				if(!boule.getLabel().getjLabel().isVisible()) {					
					boule.tireBoule(this,lesJoueurs); 
					
				}
				break;
		}
		// affiche le personnage à sa nouvelle position
		affiche(etape) ;
	}
	
	/**
	 * @return the message
	 */
	public Label getMessage() {
		return message;
	}
	
	/**
	 * Contrôle si le joueur chevauche un des autres joueurs
	 * @param lesJoueurs
	 * @return
	 */
	private boolean toucheJoueur(Hashtable<Connection, Joueur> lesJoueurs) {
		for (Joueur unJoueur : lesJoueurs.values()) {
			if (!unJoueur.equals(this)) {
				if (toucheObjet(unJoueur)) {
					return true ;
				}
			}
		}
		return false ;
	}
	
	
	/**
	 * Calcul de la première position aléatoire du joueur (sans chevaucher un autre joueur ou un mur)
	 * @param lesJoueurs
	 * @param lesMurs
	 */
	private void premierePosition(Hashtable<Connection, Joueur> lesJoueurs) {
		label.getjLabel().setBounds(0, 0, L_PERSO, H_PERSO);
		do {
			posX = (int) Math.round(Math.random() * (L_ARENER - L_PERSO)) ;
			posY = (int) Math.round(Math.random() * (H_ARENER - H_PERSO - H_MESSAGE)) ;
		}while(toucheJoueur(lesJoueurs)) ;
	}
	
	/**
	 * Gain de points de vie après avoir touché un joueur
	 */
	public void gainVie() {
		vie += GAIN;
		healthBar.setHealth(vie);
	}
	
	/**
	 * Perte de points de vie après avoir été touché
	 */
	public void perteVie() {
		vie = Math.max(vie - PERTE, 0);
		healthBar.setHealth(vie);
	}
	
	/**
	 * Perte de points de vie après avoir été touché, avec un montant spécifié
	 * @param montant le nombre de points de vie à perdre
	 */
	public void perteVie(int montant) {
		vie = Math.max(vie - montant, 0);
		healthBar.setHealth(vie);
	}
	
	/**
	 * vrai si la vie est à 0
	 * @return
	 */
	public boolean estMort() {
		return (vie==0);
	}
	//page 79 attention mettre l'ensemble des traitements effectues dans un test
	/**
	 * Le joueur est mort et disparait
	 */
	public void departJoueur() {
		if (!(label==null)) {
			// Initialiser l'explosion
			explosion = new Explosion(posX, posY, jeuServeur);
			jeuServeur.nouveauLabelJeu(explosion.getLabel());
			explosion.startAnimation();

			// Rendre le joueur invisible après l'explosion
			label.getjLabel().setVisible(false);
			message.getjLabel().setVisible(false);
			boule.getLabel().getjLabel().setVisible(false);
			healthBarLabel.getjLabel().setVisible(false); // Cacher la barre de vie aussi
			
			// Envoyer les mises à jour uniquement aux joueurs encore connectés
			try {
				jeuServeur.envoi(label);
				jeuServeur.envoi(message);
				jeuServeur.envoi(boule.getLabel());
				jeuServeur.envoi(healthBarLabel);
			} catch (Exception e) {
				// Ignorer les erreurs de connexion lors de la déconnexion
				System.out.println("Erreur lors de l'envoi des informations de déconnexion : " + e.getMessage());
			}
		}
	}
	
	
	
}
