package Drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Line2D;

public class Transition {
	private State start;
	private State end;
	int startX;
	int startY;
	int endX;
	int endY;
	int textX;
	int textY;
	String label;	
	
	public Transition(State start) {
		this.start =start; 
		endX = start.x;
		endY = start.y;
		startX = start.x;
		startY = start.y;
	}
	
	public void draw(Graphics g) {						
		g.setColor(Color.blue);		
		updatePoints();
		if (start != end) {
			g.drawLine(startX, startY, endX, endY);
			if (label != null) {
				g.setColor(Color.black);			
				g.drawString(label, textX, textY);
			}
		}	else {
			g.drawArc(start.x-DrawingArea.RADIUS/2, start.y-(DrawingArea.RADIUS*2), (int)(DrawingArea.RADIUS*1.5), (int) (DrawingArea.RADIUS*1.5), -50, 280);
			g.setColor(Color.black);
			g.drawString(label, textX, textY);
		}
	}
	
	public boolean equals(Object o) {
		Transition t = (Transition) o;
		if (t.start == start && t.end == end) {
			return true;
		}	else  {
			return false;
		}
	}
	
	public void setStart(State s) {
		start = s;
	}
	
	public State getStart() {
		return start;
	}
	
	public void setEnd(State end) {
		this.end = end;		
	}
	
	public State getEnd() {
		return end;
	}
	
	private void updatePoints() {
		if (end != null) {
			endX = end.x;
			endY = end.y;		
			int xDif = endX - start.x;
			if (Math.abs(xDif) > DrawingArea.RADIUS) {
				if (xDif > 0) {
					endX -= DrawingArea.RADIUS/1.2;
				}	else {
					endX += DrawingArea.RADIUS/1.2;
				}
			}
			int yDif = endY - start.y;
			if (Math.abs(yDif) > DrawingArea.RADIUS) {
				if (yDif > 0) {
					endY -= DrawingArea.RADIUS/1.2;
				}	else {
					endY += DrawingArea.RADIUS/1.2;
				}
			}
		}		
		
		int xDifE = endX - start.x;
		if (Math.abs(xDifE) > DrawingArea.RADIUS) {
			startX = start.x;
			if (xDifE > 0) {				
				startX += DrawingArea.RADIUS/1.2;
			}	else {
				startX -= DrawingArea.RADIUS/1.2;
			}
		}
		int yDifE = endY - start.y;
		if (Math.abs(yDifE) > DrawingArea.RADIUS) {
			startY = start.y;
			if (yDifE > 0) {
				startY += DrawingArea.RADIUS/2;
			}	else {
				startY -= DrawingArea.RADIUS/2;
			}
		}
		textX = startX + ((endX-startX)/2);
		textY = startY + ((endY-startY)/2);
		if (start == end) {
			textX = startX;
			textY = startY-DrawingArea.RADIUS*2;
		}
	}
	
	public boolean isHit(int x, int y) {				
		if (x >= textX && x <= textX+10) {
			if (y+10 >= textY && y <= textY) {				
				return true;
			}
		}
		return false;
	}
		
	
}
