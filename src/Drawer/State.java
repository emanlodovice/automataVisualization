package Drawer;

import java.awt.Color;
import java.awt.Graphics;

public class State {
	public int x;
	public int y;
	public boolean start;
	private int RADIUS = DrawingArea.RADIUS;
	
	public State(int x, int y) {
		this.x = x;
		this.y = y;
		start = false;		
	}
	
	public boolean isHit(int nX, int nY) {
		if ((nX >= x-RADIUS) && (nX <= x+RADIUS)) {
			if ((nY >= y-RADIUS) && (nY <= y+RADIUS)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean overlaps(State n) {			
		if ((x-RADIUS <= n.x-RADIUS && x+RADIUS >= n.x-RADIUS) || (x-RADIUS <= n.x+RADIUS && x+RADIUS >= n.x+RADIUS)) {				
			if ((y-RADIUS <= n.y-RADIUS && y+RADIUS >= n.y-RADIUS) || (y-RADIUS <= n.y+RADIUS && y+RADIUS >= n.y+RADIUS)) {					
				return true;
			}
		}
		return false;
	}
	
	public void draw(Graphics g, int i) {
		g.setColor(Color.yellow);
		if (start) {
			g.setColor(Color.red);
		}					
		g.fillOval(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
		g.setColor(Color.BLACK);
		if (i > 0) {
			String label = "q"+i;
			g.drawString(label, x-(5+(label.length())), y+4);
		}
	}
}
