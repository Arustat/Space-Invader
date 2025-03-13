/**
 * 
 */
package modele;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class MurTest {

	/**
	 * Test method for {@link modele.Objet#toucheObjet(modele.Objet)}.
	 */
	Mur mur1 = new Mur();
	Mur mur2 = new Mur();
	@Test
	public void testToucheObjet() {
		 mur1.posX=10;
		 mur1.posY=10;
		 mur2.posX=50;
		 mur2.posY=50;
		 assertTrue(mur1.toucheObjet(mur2));
	}

}
