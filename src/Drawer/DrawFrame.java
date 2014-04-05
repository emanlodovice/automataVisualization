package Drawer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import core.Machine;
import core.State;
import core.Transition;


public class DrawFrame extends JFrame{
		
	private JButton drawState;
	private JButton drawArrow;
	private JButton delete;
	private JButton edit;
	private JComboBox actions;
	private DrawingArea board;
	private Listener handler;
	private ArrayList<JButton> buts;
	private String[] actionList = {"Building", "Convert to DFA", "Test Inputs", "Input RegEx"};
			
	public DrawFrame(String regEx) {
		this();
		board.initMach(regEx);
	}
	
	public DrawFrame(Machine m) {
		this();
		board.initMach(m);
	}
	
	public DrawFrame() {
		super("Draw a DFA or NFA");		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		
		handler =  new Listener();
		setSize(700, 700);	
		buts = new ArrayList<JButton>();
		this.initializeLook();
		this.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				switch(e.getKeyChar()) {
				case 'a':
				case 'q':
					board.changeAction(Action.DRAWSTATE);
					resetButtons();
					drawState.setBackground(Color.red);
					break;
				case 's':
				case 'w':
					board.changeAction(Action.DRAWLINE);
					resetButtons();
					drawArrow.setBackground(Color.red);
					break;
				case 'd':
				case 'e':
					board.changeAction(Action.DELETE);
					resetButtons();
					delete.setBackground(Color.red);
					break;
				case 'f':
				case 'r':
					board.changeAction(Action.EDIT);
					resetButtons();
					edit.setBackground(Color.red);
					break;
				default:
					break;
				}
			}
			public void resetButtons() {
				for(int i = 0; i < buts.size(); i++) {
					buts.get(i).setBackground(Color.WHITE);
				}
			}
		});
	}
	
	private void initializeLook() {		
		setLayout(new BorderLayout());					
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JPanel buttons = new JPanel();	
		buttons.setMaximumSize(new Dimension(1000, 100));
		drawState = new JButton(new ImageIcon(((new ImageIcon(getClass().getResource("state.png")).getImage().getScaledInstance(25, 25,java.awt.Image.SCALE_SMOOTH)))));
		drawState.setPreferredSize(new Dimension(35,35));
		drawState.setBackground(Color.red);
		drawState.setFocusable(false);
		drawState.setToolTipText("Draw state");
		drawState.addActionListener(handler);
		buts.add(drawState);
		buttons.add(drawState);
				
		drawArrow = new JButton(new ImageIcon(((new ImageIcon(getClass().getResource("arrow.png")).getImage().getScaledInstance(25, 25,java.awt.Image.SCALE_SMOOTH)))));		
		drawArrow.setPreferredSize(new Dimension(35,35));
		drawArrow.setBackground(Color.WHITE);
		drawArrow.setFocusable(false);
		drawArrow.setToolTipText("Draw transition");
		drawArrow.addActionListener(handler);
		buts.add(drawArrow);
		buttons.add(drawArrow);
		
		delete = new JButton(new ImageIcon(((new ImageIcon(getClass().getResource("delete.png")).getImage().getScaledInstance(25, 25,java.awt.Image.SCALE_SMOOTH)))));
		delete.setPreferredSize(new Dimension(35,35));
		delete.setBackground(Color.WHITE);
		delete.setFocusable(false);
		delete.setToolTipText("Delete an element in the pane");
		delete.addActionListener(handler);
		buts.add(delete);
		buttons.add(delete);		
		
		edit = new JButton(new ImageIcon(((new ImageIcon(getClass().getResource("edit.png")).getImage().getScaledInstance(25, 25,java.awt.Image.SCALE_SMOOTH)))));
		edit.setPreferredSize(new Dimension(35,35));
		edit.setBackground(Color.WHITE);
		edit.setFocusable(false);
		edit.setToolTipText("Edit the objects in the pane!");
		edit.addActionListener(handler);
		buts.add(edit);
		buttons.add(edit);
		actions = new JComboBox<String>(actionList);
		actions.setPreferredSize(new Dimension(100,35));
		actions.addActionListener(handler);
		actions.setFocusable(false);
		buttons.add(actions);
		add(BorderLayout.NORTH,buttons);
				
		
		board = new DrawingArea();		
		board.setBackground(Color.WHITE);
		JScrollPane scrollPane = new JScrollPane(board);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);		
		add(BorderLayout.CENTER,scrollPane);
		scrollPane.revalidate();		
		this.setVisible(true);
		this.setResizable(true);		
	}
	
	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent action) {			
			if (action.getSource() == drawState) {
				board.changeAction(Action.DRAWSTATE);
				resetButtons();
				drawState.setBackground(Color.red);
			}	else if (action.getSource() == drawArrow) {
				board.changeAction(Action.DRAWLINE);
				resetButtons();
				drawArrow.setBackground(Color.red);
			}	else if (action.getSource() == delete) {
				board.changeAction(Action.DELETE);
				resetButtons();
				delete.setBackground(Color.red);				
			}	else if (action.getSource() == edit) {
				board.changeAction(Action.EDIT);
				resetButtons();
				edit.setBackground(Color.red);
			}	else if (action.getSource() == actions) {
				if (actions.getSelectedItem().toString() != "Building") {															
					if (actions.getSelectedItem().toString().contentEquals("Test Inputs") || actions.getSelectedItem().toString().contentEquals("Convert to DFA")) {
						if (!validateDrawing()) {
							JOptionPane.showMessageDialog(null, "Invalid Machine");
							return;
						}		
						Machine m = convertToMachine();	
						if (actions.getSelectedItem().toString().contentEquals("Test Inputs")) {
							while (JOptionPane.showConfirmDialog(null, "Do you want to test a string?", "Feed Input", 2) == 0) {
								try{
									m.feedInput(JOptionPane.showInputDialog("Input String"), board);
								} catch(Exception e) {}
							}
						}	else {
							m.toDFA();
							new DrawFrame(m);
						}
						board.reset();
					}	else if (actions.getSelectedItem().toString() == "Input RegEx") {
						try	{
							Machine m = new Machine(JOptionPane.showInputDialog("Enter a valid Regular Expression"));
							if (m.status) {
								new DrawFrame(m);
							}	else {
								JOptionPane.showMessageDialog(null, "INVALID REGULAR EXPRESSION", "ALERT", JOptionPane.ERROR_MESSAGE);
							}
						} catch (Exception e) {
						}
						
					}	
				}
			}
		}
		
		public void resetButtons() {
			for(int i = 0; i < buts.size(); i++) {
				buts.get(i).setBackground(Color.WHITE);
			}
		}
		
		private Machine convertToMachine() {
			ArrayList<DState> dStates = board.getStates();
			ArrayList<DTransition> dTransitions = board.getTransitions();
			State start = null;
			ArrayList<State> states = new ArrayList<State>();
			
			for (int i = 0; i < dStates.size(); i++) {
				State s = new State(i);
				DState ds = dStates.get(i);
				if (ds.start) {
					start = s;
				}
				if (ds.fin) {					
					s.isFinalState = true;
				}
				states.add(s);
			}
			
			for (int i = 0; i < dTransitions.size(); i++) {
				DTransition dt = dTransitions.get(i);
				State s = states.get(dStates.indexOf(dt.getStart()));
				State e = states.get(dStates.indexOf(dt.getEnd()));
				ArrayList<String> symbols = dt.getSymbols();
				for (int j = 0; j < symbols.size(); j++) {
					s.transitions.add(new Transition(e, symbols.get(j)));
				}
			}
			return new Machine(start, states);
		}
		
		public boolean validateDrawing() {
			ArrayList<DState> states = board.getStates();
			ArrayList<DTransition> transitions = board.getTransitions();
			boolean hasStart = false;
			for (int i = 0; i < states.size(); i++) {
				DState s = states.get(i);
				boolean hasTransition = false;
				for (int j = 0; j < transitions.size(); j++) {
					DTransition t = transitions.get(j);
					if (t.getStart() == s || t.getEnd() == s) {
						hasTransition = true;
						break;
					}
				}
				if (!hasTransition && states.size() > 1) {
					return false;
				}
				if (s.start == true) {
					hasStart = true;
				}
			}
			return hasStart;
		}
	}	

}
