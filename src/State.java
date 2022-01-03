import java.util.ArrayList;

public class State {
    private boolean isStart;
    private boolean isFinal;
    private String label;
    private ArrayList<Transition> transitions;
    private boolean visited; // for bfs

    public State(String label, boolean isStart, boolean isFinal){
        this.visited = false;
        this.label = label;
        this.isStart = isStart;
        this.isFinal = isFinal;
        this.transitions = new ArrayList<>();
    }

    public void addTransition(Transition trans) {
        this.transitions.add(trans);
    }

//    public void setTransitions(ArrayList<Transition> transitions) {
//        this.transitions = transitions;
//    }

    public boolean deleteTransition(Transition trans) {
        return this.transitions.remove(trans);
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getLabel() {
        return this.label;
    }

    public ArrayList<Transition> getTransitions() {
        return this.transitions;
    }

    public String toString() {
        return this.label;
    }

    public boolean getStart() {
        return this.isStart;
    }

    public boolean getFinal() {
        return this.isFinal;
    }

    public boolean isVisited() {
        return this.visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public String printTransitionsOfState() {
        String str = "";
        for (Transition trans : transitions) {
            str += trans;
        }
        return str;
    }

    public State getDestinationState(char input) {
        for (Transition t : transitions) {
            if (t.getInput().equals(String.valueOf(input)))
                return t.getFinish_state();
        }
        return null;
    }
}
