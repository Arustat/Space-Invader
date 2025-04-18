package modele;

/**
 * Informations communes à tous les objets (joueurs, murs, boules)
 * @author emds
 *
 */
public abstract class Objet {

	// propriétés
	protected int posX = 0, posY = 0; // position du jLabel
	protected Label label;
	
	/**
	 * @return the posX
	 */
	public int getPosX() {
		return posX;
	}
	
	/**
	 * @return the posY
	 */
	public int getPosY() {
		return posY;
	}
	
	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}
	
	/**
	 * @param posX the posX to set
	 */
	public void setPosX(int posX) {
		this.posX = posX;
	}

	/**
	 * @param posY the posY to set
	 */
	public void setPosY(int posY) {
		this.posY = posY;
	}

	/**
	 * contrôle si l'objet actuel touche l'objet passé en paramètre
	 * @param objet
	 * @return vrai si les 2 objets se touchent
	 */
	public boolean toucheObjet(Objet objet) {
		if (objet == null || objet.label == null || objet.label.getjLabel() == null) {
			return false;
		}
		
		if (this.label == null || this.label.getjLabel() == null) {
			return false;
		}
		
		// Vérifier que les deux objets sont visibles
		if (!this.label.getjLabel().isVisible() || !objet.label.getjLabel().isVisible()) {
			return false;
		}
	
		int l_obj = objet.label.getjLabel().getWidth();
		int h_obj = objet.label.getjLabel().getHeight();
		int l_this = this.label.getjLabel().getWidth();
		int h_this = this.label.getjLabel().getHeight();
		
		return !((this.posX+l_this<objet.posX ||
			this.posX>objet.posX+l_obj) || 
			(this.posY+h_this<objet.posY ||
			this.posY>objet.posY+h_obj));
	}
}
