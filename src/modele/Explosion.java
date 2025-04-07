package modele;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Explosion {
	//propriété privée de la classe
	
	private BufferedImage[] frames;
	private int currentFrame = 0;
	private int x,y;
	private boolean finish = false;
	
	
	public Explosion(BufferedImage[] frames, int x, int y) {
        this.frames = frames;
        this.x = x;
        this.y = y;
    }

    public void update() {
        currentFrame++;
        if (currentFrame >= frames.length) {
            finish = true;
        }
    }

    public void draw(Graphics g) {
        if (!finish) {
            g.drawImage(frames[currentFrame], x, y, null);
        }
    }

    public boolean isFinished() {
        return finish;
    }
}
