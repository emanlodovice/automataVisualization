package DrawerRegEx;

import java.util.ArrayList;
import java.util.Collections;

import Drawer.DState;
import Drawer.DTransition;
import Drawer.DrawingArea;
import Drawer.Solver;
import core.State;

public class StateParsers {
	public ArrayList<DState> state = new ArrayList<>();
	public ArrayList<DTransition> transitions = new ArrayList<>();
	public DrawingArea board= null;
	private int distance = 0;
	
	public StateParsers(ArrayList<State> s, DrawingArea b, State start) {
		board = b;
		parseObject(s, start );
	}

	private void parseObject(ArrayList<State> s, State start) {
		Collections.sort(s);
		for (int i = 0; i < s.size(); i++) {
			state.add(new DState(distance+=100, 200,i));
			if (s.get(i) == start) {
				state.get(i).start = true;
			} 
			if (s.get(i).isFinalState) {
				state.get(i).fin = true;
			}
		}
		for (int i = 0; i < state.size(); i++) {
			for (int j = 0; j < s.get(i).transitions.size(); j++) {
				int diff = s.get(i).id - s.get(i).transitions.get(j).destinationState.id;				
				int hMul = ((int) Solver.solveHyp(state.get(i).x, state.get(i).y, state.get(s.get(i).transitions.get(j).destinationState.id).x, state.get(s.get(i).transitions.get(j).destinationState.id).y))/2;
				int style = (diff > 0)? 1: 2;
				if(diff == 1 || diff == -1) {
					board.addTransition(new DTransition(state.get(i),state.get(s.get(i).transitions.get(j).destinationState.id) , hMul, 0, s.get(i).transitions.get(j).symbol +""),s.get(i).transitions.get(j).symbol +"");
				} else {
					board.addTransition(new DTransition(state.get(i),state.get(s.get(i).transitions.get(j).destinationState.id) , hMul, style, s.get(i).transitions.get(j).symbol +""), s.get(i).transitions.get(j).symbol +"");
				}
			}
					
		}
	}
}

