package Drawer;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class DrawFrame extends JFrame{
		
	private JButton drawState;
	private JButton drawArrow;
	private JButton delete;
	private JButton edit;
	private JComboBox actions;
	private DrawingArea board;
	private Listener handler;
	private ArrayList<JButton> buts;
	
	private Canvas canvas;
	public DrawFrame() {
		super("Draw a DFA or NFA");		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		handler =  new Listener();
		setSize(500, 500);	
		buts = new ArrayList<JButton>();
		this.initializeLook();
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
		add(BorderLayout.NORTH,buttons);			
		
		board = new DrawingArea();		
		board.setBackground(Color.LIGHT_GRAY);
		JScrollPane scrollPane = new JScrollPane(board);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);		
		add(BorderLayout.CENTER,scrollPane);
		scrollPane.revalidate();
		this.setVisible(true);
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
			}
		}
		
		public void resetButtons() {
			for(int i = 0; i < buts.size(); i++) {
				buts.get(i).setBackground(Color.WHITE);
			}
		}
		
	}

}
