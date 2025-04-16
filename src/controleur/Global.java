package controleur;

/**
 * Regroupement des constantes de l'application
 * @author emds
 *
 */
public interface Global {
	
	public static final Integer PORT = 6666 ;
	
	// fichiers
	public static final String
		SEPARATOR = "//",
		CHEMIN = "media" + SEPARATOR,
		CHEMINS = "assets" + SEPARATOR,
		CHEMINFOND = CHEMINS + "Background" + SEPARATOR,
		CHEMINPERSOS = CHEMINS + "Player" + SEPARATOR,
		CHEMINENEMY = CHEMINS + "Enemy" + SEPARATOR,
		CHEMINMORT = CHEMINS + "Explosion" + SEPARATOR,
		CHEMINMURS = CHEMIN + "murs" + SEPARATOR,
		CHEMINBOULES = CHEMIN+"boules"+ SEPARATOR ,
		PERSO = CHEMINPERSOS + "perso",
		EXTIMAGE = ".png" ;

	// images
	public static final String
	BACKGROUND = CHEMINFOND + "start.png",
	FONDARENE = CHEMINFOND + "Layers/",
	MUR = CHEMINMURS + "mur.gif",
	BOULE = CHEMINBOULES +"boule.gif";
	
	
	// personnages
	public static final int
		GAUCHE = 0,
		DROITE = 1,
		HAUT = 2,
		BAS = 3,
		TIRE = 4,
		ACTION = 2,
		LEPAS = 10,
		NBPERSOS = 3,
		L_BOULE = 17,
		H_BOULE = 17,
		H_PERSO = 44,
		L_PERSO = 39 ,
		NBETATS = 4,
		NBETATSMORT = 3;
		
	
	// messages serveurs
	public static final String SEPARE = "~" ;
	public static final int
		PSEUDO = 0,
		CHAT = 1 ;
	
	// tailles
	public static final int
		H_ARENE = 700,
		L_ARENE = 800,
		H_CHAT = 0,
		H_MESSAGE = 8,
		H_SAISIE = 25,
		MARGE = 5 ; // �carts entre les objets
	
	// murs
	public static final int
		NBMURS = 20, // nombre de murs
		H_MUR = 35, // hauteur du mur
		L_MUR = 34 ; // largeur du mur
	
	// musique 
	public static final String
		CHEMINSONS = CHEMIN + "sons/",
		SONPRECEDENT = CHEMINSONS + "precedent.wav",
		SONSUIVANT = CHEMINSONS + "suivant.wav", 
		SONGO =CHEMINSONS + "go.wav",
		SONWELCOME = CHEMINSONS + "welcome.wav",
		SONAMBIANCE = CHEMINSONS + "ambiance.wav";
	
	public static final Integer 
		HURT = 1,
		DEATH = 2,
		FIGHT = 0;
		
	
	public static final String[]
		SON = {"fight.wav","hurt.wav","death.wav"};
		
		
	
}
