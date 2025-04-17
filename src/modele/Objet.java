package modele;

/**
 * Informations communes � tous les objets (joueurs, murs, boules)
 * @author emds
 *
 */
public abstract class Objet {

	// propri�t�s
	protected Integer posX, posY ; // position du jLabel
	protected Label label ;
	
	/**
	 * @return the posX
	 */
	public Integer getPosX() {
		return posX;
	}
	
	/**
	 * @return the posY
	 */
	public Integer getPosY() {
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
	public void setPosX(Integer posX) {
		this.posX = posX;
	}

	/**
	 * @param posY the posY to set
	 */
	public void setPosY(Integer posY) {
		this.posY = posY;
	}

	/**
	 * contr�le si l'objet actuel touche l'objet pass� en param�tre
	 * @param objet
	 * @return vrai si les 2 objets se touchent
	 */
	public boolean toucheObjet(Objet objet) {
		if (objet.label==null) {
			return false;
		} else {
			if (objet.label.getjLabel()==null) {
				return false;
			} else {
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
	}

	
}
