import java.util.*;
import java.util.concurrent.RecursiveTask;

public class FA {
    private ArrayList<State> states;
    private ArrayList<State> finalStates = new ArrayList<>();
    private State startState;
    String LANDA = "λ";
    private int LastQTypeState;
    public enum Type {
        NFA,
        DFA
    }
    private Type typeOfFA;

    public FA(ArrayList<State> states, int LastQTypeState, Type typeOfFA) {
        this.typeOfFA = typeOfFA;
        this.LastQTypeState = LastQTypeState;
        this.states = states;
        for (State s : states) {
            if (s.getFinal())
                finalStates.add(s);
            if (s.getStart())
                startState = s;
        }
    }

    public String toString() {
        String str = "";
        for (State s : this.states) {
            for (Transition trans : s.getTransitions()) {
                str += trans.toString();
            }
        }
        return str;
    }

    public ArrayList<State> getFinalStates() {
        return finalStates;
    }

    public State getStartState() {
        State ss = null;
        for (State s : states) {
            if (s.getStart())
                ss = s;
        }
        return ss;
    }

    public String print() {
        String str = "";
        for (State s : states) {
            str += s.printTransitionsOfState();
        }
        return str;
    }

    // reversing language accepted by the automata
    public FA rev() {
        // merging final states to one
        if (this.getFinalStates().size() > 1) {
            mergeFinalStates();
        }
        ArrayList<State> states2 = new ArrayList<>();
        // creates states of reversed FA
        for (State s : states) {
            states2.add(new State(s.getLabel(), s.getFinal(), s.getStart())); // convert final state to start state and vice versa
        }
        // create reversed transitions
        for (State s : states) {
            for (Transition t : s.getTransitions()) {
                State ss = findState(t.getStart_state(), states2);
                State sf = findState(t.getFinish_state(), states2);
                if (ss != null && sf != null) {
                    sf.addTransition(new Transition(
                            sf,
                            t.getInput(),
                            ss
                    ));
                }
            }
        }
        return new FA(states2, this.LastQTypeState, this.typeOfFA);
    }

    private State findState(State state, ArrayList<State> states2) {
        for (int i = 0; i < states2.size(); i++) {
            if (states2.get(i).getLabel().equals(state.getLabel()))
                return states2.get(i);
        }
        return null;
    }

    private void mergeFinalStates() {
        String label = "Q" + (LastQTypeState++);
        int len = finalStates.size();
        State f_s = new State(label, false, true);
        for (int i = 0; i < len; i++) {
            finalStates.get(i).addTransition(new Transition(
                    finalStates.get(i),
                    LANDA,
                    f_s
            ));
        }
        states.add(f_s);
        finalStates.clear();
        finalStates.add(f_s);
    }

    // return all transitions of a super state except lambda transition
    private ArrayList<Transition> getExternalTransitionOfSuperState(State superState) {
        ArrayList<State> states = decomposeLabelofSuperStateToStates(superState);
        ArrayList<Transition> transitions = new ArrayList<>();
        for (State s : states) {
            for (Transition t : s.getTransitions()) {
                if (!t.getInput().equals(LANDA)) {
                    transitions.add(t);
                }
            }
        }
        return transitions;
    }

    public FA nfaToDfa() {
        if (this.typeOfFA == Type.DFA) {
            System.out.println("it is already deterministic!");
            return this;
        }
        State origin = this.startState;
        State destination;
        ArrayList<State> sts = getConnectedStatesWithLambdaTransition(origin);
        State superStates = makeSuperState(sts, true);
        ArrayList<State> dfaStates = new ArrayList<>();
        dfaStates.add(superStates);
        Queue<State> lastAdded = new LinkedList<>();  // BFS QUEUE
        lastAdded.add(superStates);

        // until every state in lastAdded array is checked
        while (lastAdded.size() > 0) {
            origin = lastAdded.remove();
            ArrayList<Transition> dummyTransitions = getExternalTransitionOfSuperState(origin);
            while (dummyTransitions.size() > 0) {
                Transition t = dummyTransitions.get(0);
                destination = getDestSuperState(t.getInput(), origin, dummyTransitions);
                // add destination super state into lasAdded and dfa states (if it is not exist);
                if (findState(destination.getLabel(), dfaStates) == null) {
                    dfaStates.add(destination);
                    lastAdded.add(destination);
                } else {
                    destination = findState(destination.getLabel(), dfaStates);
                }
                Transition trans = new Transition(
                        origin,
                        t.getInput(),
                        destination
                );
                origin.addTransition(trans);
            }
        }
        return new FA(dfaStates, this.LastQTypeState, Type.DFA);
    }

    // get origin state and pass Array list of connected states (all destination states of lambda transitions)
    // semi bfs
    private ArrayList<State> getConnectedStatesWithLambdaTransition(State origin) {
        ArrayList<State> sts = new ArrayList<>();
        Queue<State> lastAdded = new LinkedList<>(); // all destination of lambda transitions
        lastAdded.add(origin);
        sts.add(origin);

        setVisitedOfAllStates(false);

        // until every state in lastAdded array is checked
        while (lastAdded.size() > 0) {
            origin = lastAdded.remove();
            for (Transition t : origin.getTransitions()) {
                if (t.getInput().equals(LANDA) && !origin.isVisited()) {
                    origin.setVisited(true);
                    State dest = t.getFinish_state();
                    lastAdded.add(dest);
                    sts.add(dest);
                }
            }
        }
        // super state
        return sts;
    }

    // create super state with given array of state + remove corresponding transition
    private State makeSuperState(ArrayList<State> sts, boolean isStart) {
        boolean isFinal = sts.get(0).getFinal();
        String label = sts.get(0).getLabel();
        for (int i = 1; i < sts.size(); i++) {
            if (!isRepetitive(label, sts.get(i).getLabel())) {
                label += "." + sts.get(i).getLabel();
                if (sts.get(i).getFinal())
                    isFinal = true;
            }
        }
        return new State(label, isStart, isFinal);
    }

    private boolean isRepetitive(String labelOfSuperState, String label) {
        String[] dummy = labelOfSuperState.split("\\.");
        for (String str : dummy) {
            if (str.equals(label))
                return true;
        }
        return false;
    }

    // initializing visited variable of all nfa states
    private void setVisitedOfAllStates(boolean visited) {
        for (int i = 0; i < states.size(); i++) {
            states.get(i).setVisited(visited);
        }
    }

    // decomposing a super state to nfa states
    private ArrayList<State> decomposeLabelofSuperStateToStates(State superState) {
        ArrayList<State> sts = new ArrayList<>();
        String Superlabel = superState.getLabel();
        String[] dummy = Superlabel.split("\\.");
        for (String label : dummy) {
            State s = findState(label, states);
            if (s == null) {
                System.out.println("Something goes wrong!");
            } else {
                sts.add(s);
            }
        }
        return sts;
    }

    // search and return state (with given label) in given array of states
    private State findState(String stateLabel, ArrayList<State> states) {
        for (State s : states) {
            if (s.getLabel().equals(stateLabel))
                return s;
        }
        return null;
    }

    // return destination "super state" form given origin super state and special input
    private State getDestSuperState(String input, State originSuperState, ArrayList<Transition> dummyTransitions) {
        ArrayList<State> destSuperState = new ArrayList<>();
        ArrayList<State> statesOfSuperState = decomposeLabelofSuperStateToStates(originSuperState);
        ArrayList<Transition> dummyTrans = getExternalTransitionOfSuperState(originSuperState);
        for (State s : statesOfSuperState) {
            String label = s.getLabel();
            for (Transition t : dummyTrans) {
                String startLabel = t.getStart_state().getLabel();
                if (t.getInput().equals(input) && startLabel.equals(label)) {
                    destSuperState.addAll(getConnectedStatesWithLambdaTransition(t.getFinish_state()));
                    dummyTransitions.remove(t);
                }
            }
        }
        return makeSuperState(destSuperState, false);
    }

    public ArrayList<State> getStates() {
        return this.states;
    }

    public FA getComplement() {
        ArrayList<State> compStates = this.states;
        if (this.typeOfFA == Type.NFA) {
            compStates = nfaToDfa().getStates();
        }
        for (State s : compStates) {
            s.setFinal(!s.getFinal());
        }
        return new FA(compStates, this.LastQTypeState, Type.DFA);
    }

    public boolean accepter(String input) {
        State currentState = startState;
        for (int i = 0; i < input.length(); i++){
            currentState = currentState.getDestinationState(input.charAt(i));
            if (currentState == null)
                return false;
        }
        return currentState.getFinal();
    }
}
