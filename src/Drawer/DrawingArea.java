package Drawer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class DrawingArea extends JPanel implements MouseInputListener {
		
	public final static int RADIUS = 20;
	private ArrayList<State> states;
	private ArrayList<Transition> transitions;
	private int height;
	private int width;
	private int prevX;
	private int prevY;
	private State currentState;	
	private Transition currentTransition;
	private int toDraw;
	public final static Font font = new Font("Arial", 1, 12);
	
	public DrawingArea() {
		initialize();
	}
	
	private void initialize() {
		setToolTipText("Click to draw Something");			
		setPreferredSize(new Dimension(600,600));		
		states = new ArrayList<State>();
		transitions = new ArrayList<Transition>();
		addMouseListener(this);
		addMouseMotionListener(this);
		width = 600;
		height = 600;
		toDraw = Action.DRAWSTATE;
	}
	
	public void changeAction(int action) {
		toDraw = action;		
	}
	
	private void deleteState(State s) {
		ArrayList<Transition> toRemove = new ArrayList<Transition>();
		for (int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			if (t.getStart() == s || t.getEnd() == s) {
				toRemove.add(t);
			}
		}
		for (int ii = 0; ii < toRemove.size(); ii++) {
			transitions.remove(toRemove.get(ii));
		}
		states.remove(s);
	}
	
	private void addState(State s) {
		states.add(s);
	}
	
	private void deleteTransition(Transition t) {
		transitions.remove(t);
	}
	
	private void addTransition(Transition t, String label) {
		label = label.trim();
		int i = transitions.indexOf(t);
		if (i < 0) {
			if (label.length() <= 0) {
				label = "e";
			}
			t.label = label;
			transitions.add(t);
		}	else {
			Transition same = transitions.get(i);
			String[] chars = same.label.split(",");
			for (int j = 0; j < chars.length; j++) {
				if (label.contentEquals(chars[j])) {
					return;
				}
			}
			same.label += ","+label;
		}		
	}
	
	public void paintComponent(Graphics g) {	
		super.paintComponent(g);
		this.drawStates(g);
		this.drawTransitions(g);
	}
	
	private void drawStates(Graphics g) {
		g.setFont(font);
		for (int i = 0; i < states.size(); i++) {
			states.get(i).draw(g, i+1);									
//			try {				
//				g.drawImage(ImageIO.read(getClass().getResource("state.png")), s.x-RADIUS,s.y-RADIUS, RADIUS*2,RADIUS*2,this);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}			
		}
		if (currentState != null && toDraw == Action.DRAWSTATE) {
			currentState.draw(g, 0);
		}
	}
	
	private void drawTransitions(Graphics g) {
		for (int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			t.draw(g);
		}
		if (currentTransition != null) {
			currentTransition.draw(g);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {		
		if (toDraw == Action.DRAWSTATE || toDraw == Action.EDIT) {
			if (currentState != null && SwingUtilities.isLeftMouseButton(e)) {
				currentState.x = e.getX();	
				currentState.y = e.getY();
				new Thread(new UpdateState()).start();
			}
		}	else if (toDraw == Action.DRAWLINE) {
			if (currentTransition != null && SwingUtilities.isLeftMouseButton(e)) {
				currentTransition.endX = e.getX();
				currentTransition.endY = e.getY();
				new Thread(new UpdateState()).start();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {				
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (toDraw == Action.DELETE) {			
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}	else if (toDraw == Action.EDIT) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}   else {		
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch(toDraw) {
		case Action.DRAWSTATE:
			if (SwingUtilities.isLeftMouseButton(e)) {
				currentState = new State(e.getX(), e.getY());				
				new Thread(new UpdateState()).start();				
			}
			break;
		case Action.DRAWLINE:			
			if (SwingUtilities.isLeftMouseButton(e)) {
				State s = getHitState(e.getX(), e.getY());
				if (s != null) {					
					currentTransition = new Transition(s);
					new Thread(new UpdateState()).start();
				}
			}
			break;
		case Action.EDIT:			
			if (SwingUtilities.isLeftMouseButton(e)) {
				currentState = getHitState(e.getX(), e.getY());
				if (currentState != null) {
					prevX = currentState.x;
					prevY = currentState.y;
				}
			}
			break;
		case Action.DELETE:
			State toDelete = getHitState(e.getX(), e.getY());
			if (toDelete != null) {
				deleteState(toDelete);
				new Thread(new UpdateState()).start();
				return;
			}
			Transition t = getHitTransition(e.getX(), e.getY());
			if (t != null) {
				deleteTransition(t);
				new Thread(new UpdateState()).start();
				return;
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(toDraw) {
		case Action.DRAWSTATE:
			if (SwingUtilities.isLeftMouseButton(e)) {
				if (currentState.x-RADIUS < 0 || currentState.y-RADIUS < 0) {
					new Thread(new UpdateState()).start();
					currentState = null;
					return;
				}
				for (int i = 0; i < states.size(); i++) {
					if (states.get(i).overlaps(currentState)) {
						new Thread(new UpdateState()).start();
						currentState = null;
						return;
					}
				}
				addState(currentState);
				if (currentState.x+RADIUS+100 > width) {
					width +=50;			
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				if ((currentState.y+RADIUS+100) > height) {
					System.out.println("resize");
					height +=50;
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				currentState = null;
				new Thread(new UpdateState()).start();
			}
			break;
		case Action.EDIT:
			if (SwingUtilities.isLeftMouseButton(e) && currentState != null) {
				if (currentState.x-RADIUS < 0 || currentState.y-RADIUS < 0) {
					currentState.x = prevX;
					currentState.y = prevY;
					currentState = null;
					new Thread(new UpdateState()).start();												
					return;
				}
				for (int i = 0; i < states.size(); i++) {
					State s = states.get(i);
					if (s != currentState && s.overlaps(currentState)) {
						currentState.x = prevX;
						currentState.y = prevY;
						currentState = null;
						new Thread(new UpdateState()).start();												
						return;
					}
				}				
				if (currentState.x+RADIUS+100 > width) {
					width +=50;			
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				if ((currentState.y+RADIUS+100) > height) {
					System.out.println("resize");
					height +=50;
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				currentState = null;
				new Thread(new UpdateState()).start();
			}
		case Action.DRAWLINE:
			if (currentTransition != null && SwingUtilities.isLeftMouseButton(e)) {				
				State s = getHitState(e.getX(), e.getY());
				if (s == null) {
					currentTransition = null;					
				}	else {
					currentTransition.setEnd(s);					
					addTransition(currentTransition, JOptionPane.showInputDialog("Symbol to read:"));
					currentTransition = null;
				}
				new Thread(new UpdateState()).start();
			}
			break;
		default:
			break;
		}
		return;
	}
	
	private State getHitState(int x, int y) {
		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			if (s.isHit(x, y)) {
				return s;
			}
		}
		return null;
	}
	
	private Transition getHitTransition(int x, int y) {
		for (int i = 0; i < transitions.size(); i++) {
			Transition t = transitions.get(i);
			if (t.isHit(x, y)) {
				return t;
			}
		}
		return null;
	}
	
	public class UpdateState implements Runnable {				
		public void run() {						
			repaint();			
		}		
	}
		
}
