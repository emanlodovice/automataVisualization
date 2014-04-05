package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JOptionPane;

import Drawer.DrawingArea;

public class Machine {

	public ArrayList<State> states;
	public State startingState;
	State endingState;
	int startingIndex;
	public boolean status;
	
	public Machine(String regEx) {
		regEx = regEx.replaceAll(" ", "");
		if (isRegExValid(regEx)) {
			Machine m = new Machine(regEx, true, false, 0);
			this.states = m.states;
			this.startingState = m.startingState;
			this.endingState = m.endingState;
			this.startingIndex = m.startingIndex;
//			toDFA();
			status = true;
		} else {
			status = false;
		}
	}
	
	public Machine(State startingState, ArrayList<State> states) {
		this.startingState = startingState;
		this.states = states;
		startingIndex = states.size();
		Collections.sort(this.states);
	}
	
	private Machine() {
		states = new ArrayList<>();
	}
	
	private Machine(String regEx, boolean isMainGroup, boolean isRepeating, int sIndex) {
		states = new ArrayList<>();
		startingIndex = sIndex;
		startingState = new State(startingIndex++);
		states.add(startingState);
		State currentState = startingState;
		
		String symbol;
		for (int i = 0; i < regEx.length(); i++) {
			symbol = regEx.charAt(i) + "";
			if (symbol.equals("a") || symbol.equals("b") || symbol.equals("e")) {
				if (i < regEx.length()-1 && regEx.charAt(i+1) == '*') {
					currentState.transitions.add(new Transition(currentState, symbol));
					State s = new State(startingIndex++);
					states.add(s);
					currentState.transitions.add(new Transition(s, "e"));
					currentState = s;
					i++;
				} else {
					State s = new State(startingIndex++);
					states.add(s);
					currentState.transitions.add(new Transition(s, symbol));
					currentState = s;
				}
			} else if (symbol.equals("(")) {
				int openCount = 0;
				i++;
				
				int startIndex = i;
				while (true) {
					symbol = regEx.charAt(i) + "";
					if (symbol.equals("(")) {
						openCount++;
					} else if (symbol.equals(")") && openCount == 0) {
						break;
					} else if (symbol.equals(")")) {
						openCount--;
					}
					i++;
				}
				int endIndex = i;
				
				boolean repeat = false;
				if (i < regEx.length()-1 && regEx.charAt(i+1) == '*') {
					repeat = true;
					i++;
				}
				
				Machine sg = new Machine(regEx.substring(startIndex, endIndex), false, repeat, startingIndex);
				states.addAll(sg.states);
				startingIndex = sg.startingIndex;
				currentState.transitions.add(new Transition(sg.startingState, "e"));
				currentState = sg.endingState;
			} else if (symbol.equals("+")) {
				Machine sg = new Machine(regEx.substring(i+1), false, false, startingIndex);
				states.addAll(sg.states);
				startingIndex = sg.startingIndex;
				startingState.transitions.add(new Transition(sg.startingState, "e"));
				
				State s = new State(startingIndex++);
				states.add(s);
				currentState.transitions.add(new Transition(s, "e"));
				sg.endingState.transitions.add(new Transition(s, "e"));
				
				currentState = s;
				break;
			}
			
		}
		
		endingState = currentState;
		
		if (isRepeating) {
			endingState.transitions.add(new Transition(startingState, "e"));
			endingState = new State(startingIndex++);
			states.add(endingState);
			startingState.transitions.add(new Transition(endingState, "e"));
		}
		
		if (isMainGroup) {
			endingState.isFinalState = true;
		}
		
	}
	
	public void toDFA() {
		fixStringTransitions();
		
		ArrayList<Integer>[] e = new ArrayList[states.size()];
		ArrayList<Integer> finalStates = new ArrayList<>();
		
		for (int i = 0; i < states.size(); i++) {
			e[i] = new ArrayList<>();
			ArrayList<Integer> todo = new ArrayList<>();
			todo.add(i);
			
			while (!todo.isEmpty()) {
				int index = todo.remove(0);
				if	(!e[i].contains(index)) {
					e[i].add(index);
					State s = states.get(index);				
					for (Transition t : s.transitions) {
						if (t.symbol.equals("e")) {
							todo.add(t.destinationState.id);
						}
					}
				}
			}
			Collections.sort(e[i]);
			
			if (states.get(i).isFinalState) {
				finalStates.add(i);
			}
		}
		
		Machine machine = new Machine();
		machine.startingIndex = 0;
		machine.startingState = new State(machine.startingIndex++);
		
		ArrayList<ArrayList<Integer>> done = new ArrayList<>();
		ArrayList<ArrayList<Integer>> todo = new ArrayList<>();
		ArrayList<State> usedStates = new ArrayList<>();
		ArrayList<State> unusedStates = new ArrayList<>();
		done.add(e[0]);
		todo.add(e[0]);
		unusedStates.add(machine.startingState);
		usedStates.add(machine.startingState);
		
		if (hasInterSection(e[0], finalStates)) {
			machine.startingState.isFinalState = true;
		}
		
		while (!todo.isEmpty()) {
			State currentState = unusedStates.remove(0);
			ArrayList<Integer> currentList = todo.remove(0);
			HashSet<Integer> a = new HashSet<>();
			HashSet<Integer> b = new HashSet<>();
			
			for (Integer i : currentList) {
				State s = states.get(i);
				
				for (Transition t : s.transitions) {
					if (t.symbol.equals("a")) {
						a.addAll(e[t.destinationState.id]);
					} else if (t.symbol.equals("b")) {
						b.addAll(e[t.destinationState.id]);
					}
				}
				
			}
			
			ArrayList<Integer> aList = new ArrayList<>(a);
			ArrayList<Integer> bList = new ArrayList<>(b);
			Collections.sort(aList);
			Collections.sort(bList);
			
			int aIndex = done.indexOf(aList);
			
			State aState;
			State bState;
			if (aIndex >= 0) {
				aState = usedStates.get(aIndex);
			} else {
				aState = new State(machine.startingIndex++);
				done.add(aList);
				todo.add(aList);
				usedStates.add(aState);
				unusedStates.add(aState);
				
				if (hasInterSection(aList, finalStates)) {
					aState.isFinalState = true;
				}
			}
			
			int bIndex = done.indexOf(bList);
			if (bIndex >= 0) {
				bState = usedStates.get(bIndex);
			} else {
				bState = new State(machine.startingIndex++);
				done.add(bList);
				todo.add(bList);
				usedStates.add(bState);
				unusedStates.add(bState);
				
				if (hasInterSection(bList, finalStates)) {
					bState.isFinalState = true;
				}
			}
			
			currentState.transitions.add(new Transition(aState, "a"));
			currentState.transitions.add(new Transition(bState, "b"));
		}
	
		machine.states = usedStates;
		
		this.startingState = machine.startingState;
		this.startingIndex = machine.startingIndex;
		this.states = machine.states;
	}
	
	private void fixStringTransitions() {
		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			ArrayList<Transition> toRemove = new ArrayList<>();
			for	(int j = 0; j < s.transitions.size(); j++) {
				Transition t = s.transitions.get(j);
				if (t.symbol.length() > 1) {
					Machine sg = new Machine(t.symbol, false, false, startingIndex);
					states.addAll(sg.states);
					startingIndex = sg.startingIndex;
					s.transitions.add(new Transition(sg.startingState, "e"));
					sg.endingState.transitions.add(new Transition(t.destinationState, "e"));
					toRemove.add(t);
				}
			}
			
			s.transitions.removeAll(toRemove);
		}
		
	}
	
	private boolean hasInterSection(ArrayList<Integer> a, ArrayList<Integer> b) {
		for (int i : a) {
			if (b.contains(i)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isRegExValid(String regEx) {
	    Stack<Character> stack = new Stack<>();
	    boolean isAlpha = false;
	    boolean isQuantified = false;
	    boolean isStar = false;
	    boolean isE = false;
	    for (int i = 0; i < regEx.length(); i++) {
	      if (regEx.charAt(i) == '(') {
	        stack.push('(');
	        isAlpha = false;
	        isQuantified = false;
	        isStar = false;
	        isE = false;
	      } else if (regEx.charAt(i) == ')') {
	        if (stack.isEmpty() || stack.pop() != '(') {
	          return false;
	        }else if (isAlpha || isStar || isE) {
	          isQuantified = true;
	          isAlpha = false;
	          isStar = false;
	          isE = false;
	        } else {
	          return false;
	        }
	      } else if (regEx.charAt(i) == 'a' || regEx.charAt(i) == 'b') {
	        isAlpha = true;
	        isQuantified = false;
	        isStar = false;
	        isE = false;
	      } else if (regEx.charAt(i) == '+' && i != regEx.length() - 1) {
	        if (isAlpha || isQuantified || isE || isStar) {
	          isAlpha = false;
	          isQuantified = false;
	          isStar = false;
	          isE = false;
	        } else {
	          return false;
	        }
	      } else if (regEx.charAt(i) == '*') {
	        if (isAlpha || isQuantified || isE || isStar) {
	          isStar = true;
	          isAlpha = false;
	          isQuantified = false;
	          isE = false;
	        } else {
	          return false;
	        }
	      } else if (regEx.charAt(i) == 'e'){
	        isAlpha = false;
	        isQuantified = false;
	        isStar = false;
	        isE = true;
	      } else {
	        return false;
	      }
	    }
	    if (stack.isEmpty()) {
	      return true;
	    } else {
	      return false;
	    }
	}
	
	public void feedInput(String input, DrawingArea board) {
		if (isValidInput(input)) {
			if (isValidFeed(startingState, input, new ArrayList<State>())) {
				State currState = startingState;
				State prevState;
				ArrayList<Transition> done = new ArrayList<>();
				while (!input.equals("") || !currState.isFinalState) {
					for (Transition t : currState.transitions) {
						if (t.symbol.equals("e") && !done.contains(t)) {
							if (isValidFeed(t.destinationState, input, new ArrayList<State>())) {
								done.add(t);
								prevState = currState;
								currState = t.destinationState;
								board.updateCurrentState(currState.id, prevState.id, input);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								break;
							}
						} else if (t.symbol.equals("e") && done.contains(t) && isValidFeed(t.destinationState, input, new ArrayList<State>())) {
							boolean noChoice = true;
							for (Transition tran : currState.transitions) {
								if ((tran.symbol.equals("e") && isValidFeed(tran.destinationState, input, new ArrayList<State>()) && !done.contains(tran)) ||
									(tran.symbol.length() <= input.length() && tran.symbol.equals(input.substring(0, tran.symbol.length())) && isValidFeed(tran.destinationState, input.substring(tran.symbol.length()), new ArrayList<State>())) ) {
									noChoice = false;
									break;
								}
							}
							if (noChoice) {
								prevState = currState;
								currState = t.destinationState;
								board.updateCurrentState(currState.id, prevState.id, input);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								break;
							}
						} else if (t.symbol.length() <= input.length() && t.symbol.equals(input.substring(0, t.symbol.length()))) {
							if (isValidFeed(t.destinationState, input.substring(t.symbol.length()), new ArrayList<State>())) {
								done.clear();
								prevState = currState;
								currState = t.destinationState;
								input = input.substring(t.symbol.length());
								board.updateCurrentState(currState.id, prevState.id, input);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								break;
							}
						}
					}
				}
				
				JOptionPane.showMessageDialog(null, "ACCEPTED", "RESULT", JOptionPane.INFORMATION_MESSAGE);
			} else {
				tryThenError(startingState, input, new ArrayList<State>(), board);
				JOptionPane.showMessageDialog(null, "DENIED", "RUSULT", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "INVALID INPUT STRING", "RUSULT", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean isValidInput(String input) {
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) != 'a' && input.charAt(i) != 'b') {
				return false;
			}
		}
		return true;
	}
	
	private boolean isValidFeed(State state, String input, ArrayList<State> eStates) {
		if (input.equals("")) {
			if (state.isFinalState) {
				return true;
			} else {
				eStates.add(state);
				for	(Transition t : state.transitions) {
					if (t.symbol.equals("e") && !eStates.contains(t.destinationState) && isValidFeed(t.destinationState, input, eStates)) {
						return true;
					}
				}
				
				return false;
			}
		} else {
			for (Transition t : state.transitions) {
				if (t.symbol.equals("e") && !eStates.contains(t.destinationState)) {
					eStates.add(state);
					if (isValidFeed(t.destinationState, input, eStates)) {
						return true;
					}
				} else if (t.symbol.length() <= input.length() && t.symbol.equals(input.substring(0, t.symbol.length()))) {
					if (isValidFeed(t.destinationState, input.substring(t.symbol.length()), new ArrayList<State>())) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private void tryThenError(State state, String input, ArrayList<State> eStates, DrawingArea board) {
		for (Transition t : state.transitions) {
			if (t.symbol.equals("e") && !eStates.contains(t.destinationState)) {
				board.updateCurrentState(t.destinationState.id, state.id, input);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				eStates.add(state);
				tryThenError(t.destinationState, input, eStates, board);
			} else if (t.symbol.length() <= input.length() && t.symbol.equals(input.substring(0, t.symbol.length()))) {
				board.updateCurrentState(t.destinationState.id, state.id, input.substring(t.symbol.length()));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tryThenError(t.destinationState, input.substring(t.symbol.length()), new ArrayList<State>(), board);
			}
		}
	}
	
}
