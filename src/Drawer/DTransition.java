package Drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class DTransition {
	private DState start;
	private DState end;
	int startX;
	int startY;
	int endX;
	int endY;
	int textX;
	int textY;	
	int style;
	int height;
	boolean current;
	private String label;
	private ArrayList<String> symbols;
	
	public DTransition(DState start) {
		this.start = start; 
		endX = start.x;
		endY = start.y;
		startX = start.x;
		startY = start.y;
		style = 0;
		height = 40;
	}
	//added new constructor
	public DTransition(DState start, DState end, int hMultiplier, int style, String label) {
		this.start = start;
		this.end = end;
		endX = end.x;
		endY = end.y;
		startX = start.x;
		startY = start.y;		
		this.style = style;
		height = hMultiplier;
		this.label = label;
	}
	
	public void draw(Graphics g) {						
		g.setColor(Color.blue);				
		if (start != end) {
			if (style == 2) {
				g.setColor(Color.red);
			}
			if (current) {
				g.setColor(Color.green);
			}
			if (style == 0) { 
				updatePoints();
				g.drawLine(startX, startY, endX, endY);
				drawSArrow(g);
			}	else {				
				drawArc(g);
			}
			if (label != null) {
				g.setColor(Color.black);			
				g.drawString(label, textX, textY);
			}
		}	else {
			if (current) {
				g.setColor(Color.green);
			}
			textX = start.x;
			textY = start.y-DrawingArea.RADIUS*2;
			g.drawArc(start.x-DrawingArea.RADIUS, start.y-(DrawingArea.RADIUS*2), (int)(DrawingArea.RADIUS*1.5), (int) (DrawingArea.RADIUS*1.5), -30, 270);
			drawSArrow(g);
			g.setColor(Color.black);
			g.drawString(label, textX, textY);
		}		
		
	}
	
	public boolean equals(Object o) {
		DTransition t = (DTransition) o;
		if (t.start == start && t.end == end) {
			return true;
		}	else  {
			return false;
		}
	}
	
	public void setStart(DState s) {
		start = s;
	}
	
	public DState getStart() {
		return start;
	}
	
	public void setEnd(DState end) {
		this.end = end;		
	}
	
	public DState getEnd() {
		return end;
	}
	
	public String getLabel() {
		return label;
	}
	
	public ArrayList<String> getSymbols() {
		return symbols;
	}
	
	public void setLabel(String newLabel) {
		String[] syms = newLabel.split(",");
		symbols = new ArrayList<String>();
		String res = "";
		for (int i = 0; i < syms.length; i++) {			
			if (syms[i].length() > 0) {
				if (symbols.indexOf(syms[i]) < 0) {
					res += syms[i] + ",";
					symbols.add(syms[i]);
				}
			}
		}
		label = res.substring(0, res.length()-1);
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
			if (endX == 0 && endY == 0) {
				endX = start.x;
				endY = start.y;
			}
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
		if (startX == 0 && startX == 0) {
			startX = endX;
			startY = endY;
		}
		textX = startX + ((endX-startX)/2);
		textY = startY + ((endY-startY)/2);
		if (start == end) {
			textX = start.x;
			textY = start.y-DrawingArea.RADIUS*2;
		}							
	}
	
	private void drawSArrow(Graphics g) {		
		if (end == start) {
			g.drawLine(start.x+9, start.y-(DrawingArea.RADIUS-2), start.x+15, start.y - (DrawingArea.RADIUS + 5));
			g.drawLine(start.x+9, start.y-(DrawingArea.RADIUS-2), start.x+5, start.y - (DrawingArea.RADIUS + 5));
		}	else {
			double angle = Solver.solveAngle(endX-start.x, start.y-endY);
			double topAngle = angle - 30.0;
			int topAdj = (int)Solver.solveAdj(topAngle, 10);	
			int topOpp = (int)Solver.solveOpp(topAngle, 10);		
			double botAngle = angle + 30.0;		
			int botAdj = (int)Solver.solveAdj(botAngle, 10);		
			int botOpp = (int)Solver.solveOpp(botAngle, 10);		
			if (start.x > endX) {
				botAdj *= -1;
				botOpp *= -1;
				topAdj *= -1;
				topOpp *= -1;			
			}
			int botX = endX - botAdj;
			int botY = endY + botOpp;
			int topX = endX - topAdj;		
			int topY = endY + topOpp;		
			g.drawLine(endX, endY, botX, botY);
			g.drawLine(endX, endY, topX, topY);
		}
	}
	
	private void drawArc(Graphics g) {
		int midX = startX + (endX - startX) / 2;
		int midY = startY + (endY - startY) / 2;
		double origAngle = Solver.solveAngle(endX-start.x, start.y-endY); 
		double angle = origAngle;
		if (startX < endX) {
			angle += 90;
			if (style == 2) {				
				angle = angle - 180;
			}
		}	else if (startX > endX) {			
			angle = 360 - (90 - angle);
			if (style == 2) {				
				angle = angle - 180;				
			}
			
		}
		int adj = (int) Solver.solveAdj(angle, height);		
		int opp = (int) Solver.solveOpp(angle, height);		
		if (style == 2) {
			adj *= -1;
			opp *= -1;
		}
		int hX = midX + adj;
		int hY = midY - opp;
		adj = (int) Solver.solveAdj(angle, height * 0.7);		
		opp = (int) Solver.solveOpp(angle, height * 0.7);		
		if (style == 2) {
			adj *= -1;
			opp *= -1;
		}
		textX = midX + adj;
		textY = midY - opp;
		endX = end.x;
		endY = end.y;			
		angle = Solver.solveAngle(endX-hX, hY-endY);
		double hype = Solver.solveHyp(hX, hY, endX, endY)-DrawingArea.RADIUS;			
		adj = (int) Solver.solveAdj(angle, hype);
		opp = (int) Solver.solveOpp(angle, hype);			
		if (hX > endX) {
			adj *= -1;
			opp *= -1;
		}
		endX = hX + adj;
		endY = hY - opp;
		if (endX == 0 && endY == 0) {
			endX = start.x;
			endY = start.y;
		}		
		if (start.x == end.x) {
			angle = 10;
			if (style == 2) {
				angle = 170;
			}
		}
		angle = Solver.solveAngle(hX-start.x, hY-endY);								
		adj = (int) Solver.solveAdj(angle, DrawingArea.RADIUS);
		opp = (int) Solver.solveOpp(angle, DrawingArea.RADIUS);			
		if (startX > endX) {
			adj *= -1;										
		} else {
			opp *=-1;
		}		
		startX = (start.x + adj);
		startY = (start.y - opp);
		if (startX == 0 && startX == 0) {
			startX = endX;
			startY = endY;
		}
		Graphics2D g2 = (Graphics2D) g;
		Path2D.Double d = new Path2D.Double();
		d.moveTo(start.x, start.y);
		d.curveTo(hX,hY,hX,hY, endX, endY);
		g2.draw(d);
		this.drawCArrow(g, hX, hY, endX, endY);				
	}
	
	private void drawCArrow(Graphics g, int x1, int y1, int x2, int y2) {
		double angle = Solver.solveAngle(x2-x1, y1-y2);
		double topAngle = angle - 30.0;
		int topAdj = (int)Solver.solveAdj(topAngle, 10);	
		int topOpp = (int)Solver.solveOpp(topAngle, 10);		
		double botAngle = angle + 30.0;
		int botAdj = (int)Solver.solveAdj(botAngle, 10);		
		int botOpp = (int)Solver.solveOpp(botAngle, 10);		
		if (x1 > x2) {
			botAdj *= -1;
			botOpp *= -1;
			topAdj *= -1;
			topOpp *= -1;
		}
		int botX = x2 - botAdj;
		int botY = y2 + botOpp;
		int topX = x2 - topAdj;		
		int topY = y2 + topOpp;		
		g.drawLine(x2, y2, botX, botY);
		g.drawLine(x2, y2, topX, topY);
	}		
	
	public boolean isHit(int x, int y) {		
		if (x >= textX && x <= textX+(5*label.length())) {
			if (y+10 >= textY && y <= textY) {				
				return true;
			}
		}
		return false;
	}
}
