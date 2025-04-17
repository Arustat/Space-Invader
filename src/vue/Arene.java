package vue;

import controleur.Controle;
import controleur.Global;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import modele.HealthBar;
import outils.son.Son;

/**
 * Frame de l'arne du jeu
 * @author emds
 *
 */
public class Arene extends JFrame implements Global {

	private JPanel contentPane;
	private JTextField txtSaisie;
	private JPanel jpnMurs ;
	private Son[] lesson = new Son[SON.length];
	private JPanel jpnJeu ;
	private JPanel jpnEnemy;
	private boolean client ; // arene du client ou du serveur ?
	private Controle controle ;
	private JTextArea txtChat ;
	private ArrayList<Layer> layers;
	private JLabel lblGameOver;
	
	/**
	 * Retourne le contenu complet de la zone de chat
	 * @return
	 */
	public String getContenuTxtChat() {
		return txtChat.getText() ;
	}
	
	/**
	 * Remplace le contenu de txtChat par le contenu du paramètre
	 * @param contenuTxtChat
	 */
	public void remplaceChat(String contenuTxtChat) {
		txtChat.setText(contenuTxtChat);
	}
	
	/**
	 * Ajout d'un mur
	 * @param unMur
	 */
	public void ajoutMur(JLabel unMur) {
		jpnMurs.add(unMur);
		jpnMurs.repaint();
	}
	
	public void ajoutEnemy(JLabel unEnemy) {
		SwingUtilities.invokeLater(() -> {
			// Vérifier si l'ennemi est déjà dans le panel
			boolean found = false;
			for (int i = 0; i < jpnEnemy.getComponentCount(); i++) {
				if (jpnEnemy.getComponent(i) == unEnemy) {
					found = true;
					break;
				}
			}
			
			// Si l'ennemi n'est pas déjà dans le panel, l'ajouter
			if (!found) {
				jpnEnemy.add(unEnemy);
			}
			
			// S'assurer que les dimensions et la position sont correctes
			// Pour éviter les problèmes avec setBounds qui peut écraser les dimensions,
			// nous séparons ces appels
			unEnemy.setSize(L_PERSO, H_PERSO);
			unEnemy.setLocation(unEnemy.getX(), unEnemy.getY());
			
			jpnEnemy.repaint();
		});
	}
	
	
	/**
	 * Ajout d'un nouveau personnage (côté serveur)
	 * @param unJoueur
	 */
	public void ajoutJoueur(JLabel unJoueur) {
		jpnJeu.add(unJoueur);
		jpnJeu.repaint();
	}
	
	/**
	 * Ajout ou modification d'un personnage (côté client)
	 * @param num
	 * @param unLabel
	 */
	public void ajoutModifJoueur(int num, JLabel unLabel) {
		SwingUtilities.invokeLater(() -> {
			if (unLabel instanceof HealthBar) {
				// Chercher si une barre de vie existe déjà pour ce numéro
				boolean found = false;
				for (int i = 0; i < jpnJeu.getComponentCount(); i++) {
					if (jpnJeu.getComponent(i) instanceof HealthBar) {
						if (i == num) {
							jpnJeu.remove(i);
							jpnJeu.add(unLabel, i);
							found = true;
							break;
						}
					}
				}
				// Si aucune barre de vie n'existe pour ce numéro, l'ajouter
				if (!found) {
					jpnJeu.add(unLabel, num);
				}
			} else {
				if (num >= 0 && num <= jpnJeu.getComponentCount()) {
					if (num < jpnJeu.getComponentCount()) {
						jpnJeu.remove(num);
					}
					jpnJeu.add(unLabel, num);
				} else {
					jpnJeu.add(unLabel);
				}
			}
			jpnJeu.revalidate();
			jpnJeu.repaint();
		});
	}
	
	
	/**
	 * Ajout de tous les murs en une fois
	 * @param lesMurs
	 */
	public void ajoutPanelMurs(JPanel lesMurs) {
		jpnMurs.add(lesMurs);
		jpnMurs.repaint();
		contentPane.requestFocus();
	}
	
	/**
	 * Ajout de tous les enemies en une fois
	 * @param lesEnemies
	 */
	public void ajoutPanelEnemy(JPanel lesEnemies) {
		jpnEnemy.add(lesEnemies);
		jpnEnemy.repaint();
		contentPane.requestFocus();
	}
	
	/**
	 * Ajout d'une nouvelle phrase dans la zone de chat
	 * @param unePhrase
	 */
	public void ajoutChat(String unePhrase) {
		txtChat.setText(unePhrase+"\r\n"+txtChat.getText());
	}
	
	/**
	 * Traite la touche utilisée
	 * @param arg0
	 */
	private void txtSaisie_keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_ENTER) {
			if (!txtSaisie.getText().equals("")) {
				controle.evenementVue(this, CHAT+SEPARE+txtSaisie.getText());
				txtSaisie.setText("");
				contentPane.requestFocus();
			}
		}
	}
	
	/**
	 * @return jpnMurs
	 */
	public JPanel getJpnMurs() {
		return jpnMurs ;
	}

	public void gererExplosion(JLabel explosionLabel) {
		SwingUtilities.invokeLater(() -> {
			jpnJeu.add(explosionLabel);
			jpnJeu.setComponentZOrder(explosionLabel, 0); // Mettre l'explosion au premier plan
			jpnJeu.revalidate();
			jpnJeu.repaint();
		});
	}

	/**
	 * Create the frame.
	 */
	public Arene(String typeJeu, Controle controle) {
		// arene pour un client ou un serveur ?
		client = (typeJeu.equals("client"));
		// récupération du controleur
		this.controle = controle;
		
		// les objets graphiques
		setTitle("Arena");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,700);
		setLocationRelativeTo(null);  //C'est utilisée pour centrer la fenêtre par rapport à l'écran.
		setResizable(false);
		
		
		contentPane = new JPanel();
		if(client) {
			contentPane.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					contentPane_keyPressed(arg0);
				}
			});
		}
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		jpnJeu = new JPanel();
		jpnJeu.setBounds(0, 0, L_ARENE, H_ARENE);
		jpnJeu.setOpaque(false);
		contentPane.add(jpnJeu);
		jpnJeu.setLayout(null);
		
		jpnMurs = new JPanel();
		jpnMurs.setBounds(0, 0, L_ARENE, H_ARENE);
		jpnMurs.setOpaque(false);
		contentPane.add(jpnMurs);
		jpnMurs.setLayout(null);
		

        lblGameOver = new JLabel("GAME OVER");
		lblGameOver.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
		lblGameOver.setForeground(java.awt.Color.RED);
		lblGameOver.setBounds(L_ARENE/2 - 150, H_ARENE/2 - 50, 300, 100);
		lblGameOver.setVisible(false);
		contentPane.add(lblGameOver);
		jpnEnemy = new JPanel();
		jpnEnemy.setBounds(0, 0, L_ARENE, H_ARENE);
		jpnEnemy.setOpaque(false);
		contentPane.add(jpnEnemy);
		jpnEnemy.setLayout(null);
		
		//Parralax Background
		layers = new ArrayList<>();

		try {
		    layers.add(new Layer(ImageIO.read(new File(FONDARENE + "0.png")), 0.5, L_ARENE, H_ARENE));
		    layers.add(new Layer(ImageIO.read(new File(FONDARENE + "1.png")), 1, L_ARENE, H_ARENE));
		    layers.add(new Layer(ImageIO.read(new File(FONDARENE + "2.png")), 1.5, L_ARENE, H_ARENE));
		} catch (IOException e) {
		    e.printStackTrace();
		}

		// Création du panneau pour afficher les couches en parallaxe
		ParallaxPanel parallaxPanel = new ParallaxPanel(layers);
		parallaxPanel.setBounds(0, 0, L_ARENE, H_ARENE);
		contentPane.add(parallaxPanel);

		// Timer pour mettre à jour les couches
		Timer timer = new Timer(20, e -> parallaxPanel.updateLayers());
		timer.start();

		
		// zone de saisie que pour le client
		if (client) {
			txtSaisie = new JTextField();
			txtSaisie.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent arg0) {
					txtSaisie_keyPressed(arg0);
				}
			});
			txtSaisie.setBounds(0, H_ARENE, L_ARENE, H_SAISIE);
			contentPane.add(txtSaisie);
			txtSaisie.setColumns(10);
		}
		
		JScrollPane jspChat = new JScrollPane();
		jspChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jspChat.setBounds(0, H_ARENE + H_SAISIE, L_ARENE, H_CHAT - H_SAISIE - 7*MARGE);
		contentPane.add(jspChat);
		
		txtChat = new JTextArea();
		jspChat.setViewportView(txtChat);
		
		if(client) {
			for( int i =0; i<=SON.length -1;i++) {
				lesson[i] = new Son(CHEMINSONS+SON[i]);
			}
		}
		(new Son(SONAMBIANCE)).playContinue() ; 
		
	}
	
	public void joueSon(int nbSon) {
		lesson[nbSon].play();
	}
	

	private void contentPane_keyPressed(KeyEvent arg0) {
		int valeur = -1;
		
		switch(arg0.getKeyCode()) {
			case KeyEvent.VK_UP:
				valeur = HAUT;
				break;
			case KeyEvent.VK_DOWN:
				valeur = BAS;
				break;
			case KeyEvent.VK_LEFT:
				valeur = GAUCHE;
				break;
			case KeyEvent.VK_RIGHT:
				valeur = DROITE;
				break;
			case KeyEvent.VK_SPACE:
				valeur = TIRE;
				break;
				
		}
		
		if(valeur != -1) {
			controle.evenementVue(this, ACTION+SEPARE+valeur);
		}
		
	}

	public void afficherGameOver() {
		SwingUtilities.invokeLater(() -> {
			lblGameOver.setVisible(true);
			contentPane.revalidate();
			contentPane.repaint();
		});
	}
}
