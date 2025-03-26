package vue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import controleur.Controle;
import controleur.Global;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

/**
 * Frame d'entr�e dans le jeu
 * @author emds
 *
 */
public class EntreeJeu extends JFrame implements Global{

	// propri�t�s
	private JPanel contentPane;
	private JTextField txtIp;
	private Controle controle;
	private Font retroFont;
	private Font dotFont;

	/**
	 * clic sur le bouton Start pour lancer le serveur
	 */
	private void btnStart_clic() {
		controle.evenementVue(this, "serveur");
	}
	
	/**
	 * clic sur le bouton Exit pour arr�ter l'application
	 */
	private void btnExit_clic() {
		System.exit(0);
	}
	
	/**
	 * clic sur le bouton Connect pour se connecter � un serveur
	 */
	private void btnConnect_clic() {
		controle.evenementVue(this, txtIp.getText());
	}
	
	/**
	 * Méthode privé qui charge la police personnalisée 
	 */
	private void loadRetroFont() {
	    try {
	        // Charge la police depuis les ressources
	        InputStream is = getClass().getResourceAsStream("/font/PressStart2P-Regular.ttf");
	        
	        if (is != null) {
	            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	            retroFont = font.deriveFont(20f);  // Taille de la police (20 points)
	            
	            // Enregistre la police dans le système graphique
	            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	            ge.registerFont(retroFont);
	        } else {
	            System.err.println("Police non trouvée.");
	            retroFont = new Font("Monospaced", Font.BOLD, 20);  // Police par défaut si erreur
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        retroFont = new Font("Monospaced", Font.BOLD, 20);  // Police par défaut si erreur
	    }
	}
	private void loadDotFont() {
	    try {
	        // Charge la police depuis les ressources
	        InputStream is = getClass().getResourceAsStream("/font/DOTMATRI.TTF");
	        
	        if (is != null) {
	            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	            dotFont = font.deriveFont(30f);  // Taille de la police (20 points)
	            
	            // Enregistre la police dans le système graphique
	            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	            ge.registerFont(dotFont);
	        } else {
	            System.err.println("Police non trouvée.");
	            dotFont = new Font("Monospaced", Font.BOLD, 20);  // Police par défaut si erreur
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        dotFont = new Font("Monospaced", Font.BOLD, 20);  // Police par défaut si erreur
	    }
	}
	
	
	/**
	 * Create the frame.
	 * @param controle 
	 */
	public EntreeJeu(Controle controle) {
		
		loadRetroFont();
		
		setTitle("Space Invader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 700);
		setLocationRelativeTo(null);  //C'est utilisée pour centrer la fenêtre par rapport à l'écran.
		setResizable(false); // Empêche de changer la taille de la fenêtre
		

		// Création d'un JLayeredPane pour gérer plusieurs couches
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		setContentPane(layeredPane);

		// Ajouter le panneau d'image de fond au niveau inférieur (niveau 0)
		Background backgroundPanel = new Background(BACKGROUND);
		backgroundPanel.setBounds(0, 0, 800, 700); // Définit la taille du Background
		layeredPane.add(backgroundPanel, Integer.valueOf(0));

		// Ajouter le contentPane au niveau supérieur (niveau 1)
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setOpaque(false); // Important pour que l'image de fond soit visible
		contentPane.setBounds(100, 300, 600, 300); // Rectangle noir adapté à l'image réduite
		layeredPane.add(contentPane, Integer.valueOf(1)); // Niveau supérieur

        Color textcolor = Color.WHITE;
        
        JLabel lblIpServer = new JLabel("IP server:");
        lblIpServer.setFont(retroFont);
        lblIpServer.setForeground(textcolor);
        lblIpServer.setBounds(200, 0, 200, 25);
        contentPane.add(lblIpServer);
        
        txtIp = new JTextField();
		txtIp.setText("127.0.0.1");
		txtIp.setBounds(200, 40, 200, 30);
		contentPane.add(txtIp);
		
		JButton btnConnect = createRetroButton("Play", 200, 80);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnConnect_clic() ;
			}
		});
		contentPane.add(btnConnect);

		
		JLabel lblStartAServer = new JLabel("Start a server:");
		lblStartAServer.setFont(retroFont);
        lblStartAServer.setForeground(textcolor);
		lblStartAServer.setBounds(160, 140, 400, 25);
		contentPane.add(lblStartAServer);
		
		JButton btnStart = createRetroButton("Start", 200, 180);
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnStart_clic();
			}
		});
		contentPane.add(btnStart);
	
			
		JButton btnExit = createRetroButton("Exit", 200, 230);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				btnExit_clic();
			}
		});
		contentPane.add(btnExit);
		
		JLabel copyrightLabel = new JLabel("Copyright © GAMEYNOV, INC. 2012");
		copyrightLabel.setForeground(Color.WHITE);
		Font copyrightFont = retroFont.deriveFont(11f);
		copyrightLabel.setFont(copyrightFont);  
		copyrightLabel.setBounds(125, 279, 400, 30); 
		contentPane.add(copyrightLabel);

		
		// r�cup�ration du controleur
		this.controle = controle ;
	}
	private JButton createRetroButton(String text, int x, int y) {
		
		loadRetroFont();
		loadDotFont();
		
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 40);

        // Style rétro minimaliste
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(retroFont);
        button.setFocusPainted(false);

        // Bordure standard blanche (ou cyan, si tu préfères)
        button.setBorder(new LineBorder(Color.WHITE, 2));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Rendre le bouton Connect plus flashy
        if ("Start".equals(text)) {
            button.setFont(dotFont);
        }else if ("Play".equals(text)) {
        	Font playFont = retroFont.deriveFont(25f);
        	button.setFont(playFont);
        }
        
        // Animation de hover
        final int normalWidth = 200;
        final int normalHeight = 40;
        final int enlargedWidth = 220;  
        final int enlargedHeight = 45;
        final int animationStep = 1;

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                new Thread(() -> {
                    for (int i = normalWidth; i <= enlargedWidth; i += animationStep) {
                        final int currentWidth = i;
                        final int currentHeight = (int) (normalHeight + (i - normalWidth) * ((double) (enlargedHeight - normalHeight) / (enlargedWidth - normalWidth)));
                        
                        button.setBounds(x - (currentWidth - normalWidth) / 2, y - (currentHeight - normalHeight) / 2, currentWidth, currentHeight);
                        try {
                            Thread.sleep(5); 
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                new Thread(() -> {
                    for (int i = enlargedWidth; i >= normalWidth; i -= animationStep) {
                        final int currentWidth = i;
                        final int currentHeight = (int) (normalHeight + (i - normalWidth) * ((double) (enlargedHeight - normalHeight) / (enlargedWidth - normalWidth)));
                        
                        button.setBounds(x - (currentWidth - normalWidth) / 2, y - (currentHeight - normalHeight) / 2, currentWidth, currentHeight);
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return button;
    }
}
