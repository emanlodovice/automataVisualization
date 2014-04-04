package Drawer;

import java.awt.Color;
import java.awt.Graphics;

public class DState {
	public int id;
	public int x;
	public int y;
	public boolean start;
	public boolean fin;
	private int RADIUS = DrawingArea.RADIUS;
	public boolean current;
	
	public DState(int x, int y) {
		this(x,y,0);
	}
	//added new constructor
	public DState(int x, int y, int id) {
		this.x = x;
		this.y = y;
		start = false;
		fin = false;
		current = false;
		this.id = id;
	}
	
	public boolean isHit(int nX, int nY) {
		if ((nX >= x-RADIUS) && (nX <= x+RADIUS)) {
			if ((nY >= y-RADIUS) && (nY <= y+RADIUS)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean overlaps(DState n) {			
		if ((x-RADIUS <= n.x-RADIUS && x+RADIUS >= n.x-RADIUS) || (x-RADIUS <= n.x+RADIUS && x+RADIUS >= n.x+RADIUS)) {				
			if ((y-RADIUS <= n.y-RADIUS && y+RADIUS >= n.y-RADIUS) || (y-RADIUS <= n.y+RADIUS && y+RADIUS >= n.y+RADIUS)) {					
				return true;
			}
		}
		return false;
	}
	
	public void draw(Graphics g, int i) {		
		if (start) {
			g.setColor(Color.BLACK);
			g.drawLine(x-RADIUS, y, (int)(x - (RADIUS * 1.5)), y-(RADIUS / 2));
			g.drawLine(x-RADIUS, y, (int)(x - (RADIUS * 1.5)), y+(RADIUS / 2));			
		}				
		g.setColor(Color.yellow);
		if (current) {
			g.setColor(Color.green);
		}
		g.fillOval(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
		g.setColor(Color.BLACK);		
		g.drawOval(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
		if (fin) {
			g.drawOval(x-RADIUS+4, y-RADIUS+4, (RADIUS-4)*2, (RADIUS-4)*2);
		}
		if (i > 0) {
			String label = "q"+i;
			g.drawString(label, x-(5+(label.length())), y+4);
		}
	}
}
