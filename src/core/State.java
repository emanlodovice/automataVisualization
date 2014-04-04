package core;

import java.util.ArrayList;

public class State implements Comparable<State> {
	
	public ArrayList<Transition> transitions;
	public boolean isFinalState;
	public int id;
	
	public State(int id) {
		transitions = new ArrayList<>();
		isFinalState = false;
		this.id = id;
	}

	@Override
	public int compareTo(State s) {
		return this.id - s.id;
	}
	
	@Override
	public String toString() {
		return id+"";
	}

	@Override
	public boolean equals(Object s) {
		return id == ((State)s).id;
	}
	
}
