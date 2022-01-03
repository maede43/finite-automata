import java.util.SplittableRandom;

public class Transition {
    private final State start_state;
    private final String input;
    private final State finish_state;

    String SIGMA = "δ";

    public Transition(State start_state, String input, State finish_state){
        this.start_state = start_state;
        this.input = input;
        this.finish_state = finish_state;
    }

    public String toString(){
        return this.SIGMA +  "("
                + this.start_state + ", " + this.input
                + ")->"
                + this.finish_state + "\n";
    }

    public String getInput() {
        return this.input;
    }

    public State getFinish_state() {
        return this.finish_state;
    }

    public State getStart_state(){
        return this.start_state;
    }
}
