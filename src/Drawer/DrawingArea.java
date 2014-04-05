package Drawer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import DrawerRegEx.StateParsers;

import core.Machine;

public class DrawingArea extends JPanel implements MouseInputListener {
		
	public final static int RADIUS = 20;
	private ArrayList<DState> states;
	private ArrayList<DTransition> transitions;
	private int height;
	private int width;
	private int prevX;
	private int prevY;
	private DState currentState;	
	private DTransition currentTransition;
	private int toDraw;
	public final static Font font = new Font("Arial", 1, 12);
	private JPopupMenu stateOptions;
	private JButton setStartState;
	private JButton setFinalState;
	private JButton reset;
	private String message;
	
	public DrawingArea() {
		initialize();
	}
	
	public void initMach(String regEx) {
		Machine regx = new Machine(regEx);
		transitions = new ArrayList<DTransition>();
		StateParsers mParsers = new StateParsers(regx.states,this, regx.startingState);
		states = mParsers.state;
		repaint();
	}
	
	public void initMach(Machine regx) {		
		transitions = new ArrayList<DTransition>();
		StateParsers mParsers = new StateParsers(regx.states,this, regx.startingState);
		states = mParsers.state;		
		repaint();
	}
	
	public ArrayList<DState> getStates() {
		return states;
	}
	
	public void setStates(ArrayList<DState> s) {
		states = s;
	}
	
	public ArrayList<DTransition> getTransitions() {
		return transitions;
	}
	
	public void setTransitions(ArrayList<DTransition> t) {
		transitions = t;
	}
	
	private void initialize() {
		message = "";
		setToolTipText("Click to draw Something");			
		setPreferredSize(new Dimension(800,800));		
		states = new ArrayList<DState>();
		transitions = new ArrayList<DTransition>();
		addMouseListener(this);
		addMouseMotionListener(this);
		width = 800;
		height = 800;
		toDraw = Action.DRAWSTATE;
		stateOptions = new JPopupMenu();
		JPanel menu = new JPanel(new GridLayout(3, 1));
		setStartState = new JButton("Start");
		setFinalState = new JButton("Final");
		reset = new JButton("Reset");
		menu.add(setStartState);
		menu.add(setFinalState);
		menu.add(reset);
		stateOptions.add(menu);
		
		MouseListener m = new MouseListener() {			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub				
			}
			
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == setStartState) {
					resetAllStartState();
					currentState.start = true;
					ArrayList<DState> temp = new ArrayList<>();
					temp.add(currentState);
					for (int i = 0; i < states.size(); i++) {
						if (states.get(i) != currentState) {
							temp.add(states.get(i));
						}
					}
					states = temp;
					stateOptions.setVisible(false);					
				}	else if (e.getSource() == setFinalState) {
					currentState.fin = true;
					stateOptions.setVisible(false);					
				}	else {
					currentState.start = false;
					currentState.fin = false;
					stateOptions.setVisible(false);					
				}								
				new Thread(new UpdateState()).start();				
			}
		};
		
		setStartState.addMouseListener(m);
		setFinalState.addMouseListener(m);
		reset.addMouseListener(m);		
		setStartState.addMouseListener(m);
	}
	
	public void changeAction(int action) {
		toDraw = action;	
		if (toDraw == Action.DELETE) {			
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}	else if (toDraw == Action.EDIT) {
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}   else {		
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	private void deleteState(DState s) {
		ArrayList<DTransition> toRemove = new ArrayList<DTransition>();
		for (int i = 0; i < transitions.size(); i++) {
			DTransition t = transitions.get(i);
			if (t.getStart() == s || t.getEnd() == s) {
				toRemove.add(t);
			}
		}
		for (int ii = 0; ii < toRemove.size(); ii++) {
			transitions.remove(toRemove.get(ii));
		}
		states.remove(s);
	}
	
	private void addState(DState s) {
		states.add(s);
	}
	
	private void deleteTransition(DTransition t) {
		int index = getOppositeTransition(t.getStart(), t.getEnd());
		if (index >= 0) {
			transitions.get(index).style = 0;
		}
		transitions.remove(t);
	}
	
	public void addTransition(DTransition t, String label) {		
		label = label.trim();
		int i = transitions.indexOf(t);
		if (i < 0) {			
			if (label.length() <= 0) {
				label = "e";
			}
			t.setLabel(label);			
			int j = this.getOppositeTransition(t.getStart(), t.getEnd());
			if (j >= 0) {
				transitions.get(j).style = 1;
				t.style = 2;				
			}			
			transitions.add(t);
		}	else {
			DTransition same = transitions.get(i);
			String[] chars = same.getLabel().split(",");
			for (int j = 0; j < chars.length; j++) {
				if (label.contentEquals(chars[j])) {
					return;
				}
			}
			same.setLabel(same.getLabel() + "," + label);
		}
	}
	
	public void paintComponent(Graphics g) {			
		super.paintComponent(g);
		this.drawTransitions(g);
		this.drawStates(g);		
		g.drawString(message, 10, 10);
	}
	
	private void drawStates(Graphics g) {
		g.setFont(font);
		for (int i = 0; i < states.size(); i++) {
			states.get(i).draw(g, i+1);										
		}
		if (currentState != null && toDraw == Action.DRAWSTATE) {
			currentState.draw(g, 0);
		}
	}
	
	private void drawTransitions(Graphics g) {
		for (int i = 0; i < transitions.size(); i++) {
			DTransition t = transitions.get(i);
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
				currentState = new DState(e.getX(), e.getY());				
				new Thread(new UpdateState()).start();				
			}
			break;
		case Action.DRAWLINE:			
			if (SwingUtilities.isLeftMouseButton(e)) {
				DState s = getHitState(e.getX(), e.getY());
				if (s != null) {					
					currentTransition = new DTransition(s);
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
				currentTransition = getHitTransition(e.getX(), e.getY());
				if (currentTransition != null) {
					String newLabel = JOptionPane.showInputDialog("Symbols", currentTransition.getLabel());					
					if (newLabel != null) {
						currentTransition.setLabel(newLabel);
						new Thread(new UpdateState()).start();
					}
				}
			} else if (SwingUtilities.isRightMouseButton(e)) {
				currentState = getHitState(e.getX(), e.getY());
				if (currentState != null) {					
					stateOptions.show(e.getComponent(), e.getX(), e.getY());									
				}
			}
			break;
		case Action.DELETE:
			DState toDelete = getHitState(e.getX(), e.getY());
			if (toDelete != null) {
				deleteState(toDelete);
				new Thread(new UpdateState()).start();
				return;
			}
			DTransition t = getHitTransition(e.getX(), e.getY());
			if (t != null) {
				deleteTransition(t);
				new Thread(new UpdateState()).start();				
			}
			break;
		default:
			break;
		}
		new Thread(new UpdateState()).start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch(toDraw) {
		case Action.DRAWSTATE:
			if (SwingUtilities.isLeftMouseButton(e) && currentState != null) {
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
					width +=100;			
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				if ((currentState.y+RADIUS+100) > height) {					
					height +=100;
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
					DState s = states.get(i);
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
					height +=50;
					setPreferredSize(new Dimension(width, height));
					this.getParent().revalidate();
				}
				currentState = null;
				new Thread(new UpdateState()).start();
			}			
			break;
		case Action.DRAWLINE:
			if (currentTransition != null && SwingUtilities.isLeftMouseButton(e)) {				
				DState s = getHitState(e.getX(), e.getY());
				if (s == null) {
					currentTransition = null;					
				}	else {
					currentTransition.setEnd(s);
					try {
						addTransition(currentTransition, JOptionPane.showInputDialog("Symbol to read:"));
					} catch(Exception ee) {}
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
	
	private DState getHitState(int x, int y) {
		for (int i = 0; i < states.size(); i++) {
			DState s = states.get(i);
			if (s.isHit(x, y)) {
				return s;
			}
		}
		return null;
	}
	
	private DTransition getHitTransition(int x, int y) {		
		for (int i = 0; i < transitions.size(); i++) {
			DTransition t = transitions.get(i);
			if (t.isHit(x, y)) {
				return t;
			}
		}
		return null;
	}
	
	private int getOppositeTransition(DState start, DState end) {
		for (int i = 0; i < transitions.size(); i++) {
			DTransition t = transitions.get(i);
			if (t.getEnd() == start && t.getStart() == end) {
				return i;
			}
		}
		return -1;
	}
	
	private void resetAllStartState() {
		for (int i = 0; i < states.size(); i++) {
			states.get(i).start = false;
		}
	}
	
	public class UpdateState implements Runnable {				
		public void run() {									
			repaint();			
		}		
	}
	
	public void updateCurrentState(int curIndex, int prevIndex, String mess) {		
		DState cur = states.get(curIndex);
		DState prev = states.get(prevIndex);
		for (int i = 0; i < states.size(); i++) {
			states.get(i).current =false;
		}
		cur.current = true;		
		int transIndex = getOppositeTransition(cur, prev);
		for (int i = 0; i < transitions.size(); i++) {
			transitions.get(i).current = false;
		}
		if (transIndex >= 0) {			
			transitions.get(transIndex).current = true;			
		}
		message = mess;
		validate();
    	update(this.getGraphics());
	}
	
	public void reset() {
		for (int i = 0; i < transitions.size(); i++) {
			transitions.get(i).current = false;
		}
		for (int i = 0; i < states.size(); i++) {
			states.get(i).current = false;
		}
		message = "";
		validate();
		update(this.getGraphics());
	}
}
