package core;

public class Transition {

	public State destinationState;
	public String symbol;
	
	public Transition(State desState, String sym) {
		destinationState = desState;
		symbol = sym;
	}
	
}

