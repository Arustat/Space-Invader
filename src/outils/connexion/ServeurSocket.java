package outils.connexion;

import controleur.Controle;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Gestion d'un serveur : attente de connexions de clients
 * @author emds
 *
 */
public class ServeurSocket extends Thread {

	// propriétés
	private Object leRecepteur ;
	private ServerSocket serverSocket ;
	
	/**
	 * Constructeur
	 * @param leRecepteur
	 * @param port
	 */
	public ServeurSocket(Object leRecepteur, int port) {
		this.leRecepteur = leRecepteur ;
		// création du socket serveur d'écoute des clients
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("erreur grave création socket serveur : "+e);
			System.exit(0);
		}
		// démarrage du thread d'écoute (attente d'un client)
		this.start();
	}
	
	/**
	 * Méthode thread qui va attendre la connexion d'un client
	 */
	public void run() {
		Socket socket;
		while (true) {
			try {
				System.out.println("le serveur attend");
				socket = serverSocket.accept();
				
				// Vérifier si le serveur est plein
				if (leRecepteur instanceof Controle) {
					Controle controle = (Controle) leRecepteur;
					if (controle.isFull()){
						// Informer le client que le serveur est plein
						Connection connection = new Connection(socket, null);
						connection.envoi("Server is full. Cannot accept new players.");
						try {
							connection.getSocket().close();
						} catch (IOException e) {
							System.out.println("Erreur lors de la fermeture de la connexion : " + e);
						}
						continue;
					}
				}
				System.out.println("un client s'est connecté");
				@SuppressWarnings("unused")
				Connection connection = new Connection(socket, leRecepteur);
			} catch (IOException e) {
				System.out.println("erreur sur l'objet serverSocket : " + e);
				System.exit(0);
			}
		}
	}
}