package vue;

import controleur.Controle;
import controleur.Global;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import outils.son.Son;

/**
 * Frame du choix du joueur
 * @author emds
 *
 */
public class ChoixJoueur extends JFrame implements Global {

	// propri�t�s
	private Integer numPerso ;
	private Controle controle ;
	
	// objets graphiques
	private JPanel contentPane;
	private JTextField txtPseudo;
	private JLabel lblPersonnage;
	 private JLabel lblPrecedent, lblSuivant, lblGo;
	
	//son 
	private Son precedent;
	private Son suivant;
	private Son go;
	private Son welcome;
	
	//Fonts
	private Font retroFont;
	
	/**
	 * Affichage du personnage
	 */
	private void affichePerso() {
		lblPersonnage.setIcon(new ImageIcon(PERSO+numPerso+"_1.png"));
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
	
	/**
	 * Clic sur la fl�che "pr�c�dent"
	 */
	private void lblPrecedent_clic() {
		numPerso = ((numPerso+1)%NBPERSOS)+1;
		precedent.play();
		affichePerso();
	}
	
	/**
	 * Clic sur la fl�che "suivant"
	 */
	private void lblSuivant_clic() {
		numPerso = (numPerso%NBPERSOS)+1 ;
		suivant.play();
		affichePerso();
	}
	
	/**
	 * Clic sur GO
	 */
	private void lblGo_clic() {
		// contr�le si le pseudo a �t� saisi
		if (txtPseudo.getText().equals("")) {
			JOptionPane.showMessageDialog(null, "La saisie du pseudo est obligatoire");
			txtPseudo.requestFocus();
		}else{
			suivant.play();
			System.out.println("Pseudo: " + txtPseudo.getText());
			System.out.println("Numéro de personnage: " + numPerso);
			controle.evenementVue(this, PSEUDO+SEPARE+txtPseudo.getText()+SEPARE+numPerso);
			
		}
	}
	
	/**
	 * Change l'apparence de la souris en "normal"
	 */
	private void souris_normale() {
		contentPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Change l'apparence de la souris en "doigt"
	 */
	private void souris_doigt() {
		contentPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Create the frame.
	 * @param controle 
	 */
	public ChoixJoueur(Controle controle) {
		
		loadRetroFont();
		
		setTitle("Space Invader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        Background backgroundPanel = new Background(BACKGROUND);
        backgroundPanel.setBounds(0, 0, 800, 700);
        layeredPane.add(backgroundPanel, Integer.valueOf(0));

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setOpaque(false);
        contentPane.setBounds(100, 300, 600, 300);
        layeredPane.add(contentPane, Integer.valueOf(1));

        lblPersonnage = new JLabel();
        lblPersonnage.setHorizontalAlignment(SwingConstants.CENTER);
        lblPersonnage.setBounds(210, 50, 180, 120);
        lblPersonnage.setOpaque(true);  // Permet de rendre le fond visible
        lblPersonnage.setBackground(new Color(11,10,31,255));  // Fond sombre, rappelant le fond des affiches d'arcade
        lblPersonnage.setBorder(new LineBorder(new Color(255,231,34,255), 4));
        contentPane.add(lblPersonnage);
        
        
        Color textcolor = Color.WHITE;
        
        JLabel lblShip = new JLabel("SELECT A SHIP :");
        lblShip.setForeground(textcolor);
        lblShip.setFont(retroFont);
        lblShip.setBounds(150, 0, 400, 25);
        contentPane.add(lblShip);

        lblPrecedent = new JLabel(new ImageIcon("assets/buttons/Previous_Idle.png"));
        lblPrecedent.setBounds(100, 80, 65, 65);
        lblPrecedent.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lblPrecedent.setIcon(new ImageIcon("assets/buttons/Previous_Pushed.png"));
            }
            public void mouseReleased(MouseEvent e) {
                lblPrecedent.setIcon(new ImageIcon("assets/buttons/Previous_Idle.png"));
                lblPrecedent_clic();
            }
        });
        contentPane.add(lblPrecedent);

        lblSuivant = new JLabel(new ImageIcon("assets/buttons/Next_Idle.png"));
        lblSuivant.setBounds(450, 80, 65, 65);
        lblSuivant.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lblSuivant.setIcon(new ImageIcon("assets/buttons/Next_Pushed.png"));
            }
            public void mouseReleased(MouseEvent e) {
                lblSuivant.setIcon(new ImageIcon("assets/buttons/Next_Idle.png"));
                lblSuivant_clic();
            }
        });
        contentPane.add(lblSuivant);
        
        JLabel lbltext = new JLabel("Pseudo:");
        lbltext.setForeground(textcolor);
        Font textFont = retroFont.deriveFont(14f);
        lbltext.setFont(textFont);
        lbltext.setBounds(100, 200, 150, 30);
        contentPane.add(lbltext);
        
        txtPseudo = new JTextField();
        txtPseudo.setBounds(200, 200, 150, 30);
        contentPane.add(txtPseudo);

        lblGo = new JLabel(new ImageIcon("assets/buttons/Play_Idle.png"));
        lblGo.setBounds(380, 190, 65, 65);
        lblGo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                lblGo.setIcon(new ImageIcon("assets/buttons/Play_Pushed.png"));
            }
            public void mouseReleased(MouseEvent e) {
                lblGo.setIcon(new ImageIcon("assets/buttons/Play_Idle.png"));
                lblGo_clic();
            }
        });
        contentPane.add(lblGo);
        
        JLabel copyrightLabel = new JLabel("Copyright © GAMEYNOV, INC. 2012");
		copyrightLabel.setForeground(Color.WHITE);
		Font copyrightFont = retroFont.deriveFont(11f);
		copyrightLabel.setFont(copyrightFont);  
		copyrightLabel.setBounds(125, 279, 400, 30); 
		contentPane.add(copyrightLabel);

        this.controle = controle;
        numPerso = 1;
        affichePerso();
        precedent = new Son(SONPRECEDENT);
        go = new Son(SONGO);
        suivant = new Son(SONSUIVANT);
        welcome = new Son(SONWELCOME);

        welcome.play();
    }
}
