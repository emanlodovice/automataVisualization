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
		if (end != null && end != start) {			
			endX = end.x;
			endY = end.y;			
			double angle = Solver.solveAngle(endX-start.x, start.y-endY);
			double hype = Solver.solveHyp(start.x, start.y, endX, endY)-DrawingArea.RADIUS;			
			double adj = Solver.solveAdj(angle, hype);
			double opp = Solver.solveOpp(angle, hype);			
			if (start.x > endX) {
				adj *= -1;
				opp *= -1;
			}
			endX = start.x + (int)adj;
			endY = start.y - (int)opp;
		}		
		
		double angle = Solver.solveAngle(endX-start.x, start.y-endY);									
		double adj = Solver.solveAdj(angle, DrawingArea.RADIUS);
		double opp = Solver.solveOpp(angle, DrawingArea.RADIUS);			
		if (start.x > endX) {
			adj *= -1;
			opp *= -1;
		}
		startX = (int) (start.x + adj);
		startY = (int) (start.y - opp);
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
