import com.sun.source.tree.BreakTree;

import java.util.ArrayList;

public class RegularLanguage {
    private Grammar g;
    private FA nfa;
    private int count = 0;
    String LANDA = "λ";

    public RegularLanguage(Grammar grammar) {
        this.g = grammar;
    }

    public FA getNfa(){
        return this.nfa;
    }

    public FA grammarToNfa(){
        if (this.g.getTypeOfGrammar() == Grammar.Type.leftlinear) { // left linear
            // converting g to right linear
            // return revers of FA
            return RLGrammarToNfa(this.g.convertToRightLinear()).rev();
        }
        else if (this.g.getTypeOfGrammar() == Grammar.Type.rightlinear){ // right linear
            return RLGrammarToNfa(g);
        }
        // not linear
        System.out.println("Can not convert to FA. its not linear!");
        return null;
    }

    private FA RLGrammarToNfa(Grammar grammar) {
        ArrayList<Production> productions = grammar.getProductions();
        ArrayList<State> states = new ArrayList<>();
        boolean is_final, is_start;
        count = 0;

        // converting every production to transition
        for (Production p : productions) {
            // checking that the state is final
            is_final = isFinal(p);
            is_start = isStart(p.getLeft(), String.valueOf(grammar.getStartSymbol()));
            // creating new state for variable in left side (if it does not exist)
            // if variable in left side already added
            State sState = getStateIfExist(p.getLeft(),states);
            // if the variable in left side has not been added before
            if (sState == null)
                sState = createNewStateAndAdd(p.getLeft(), is_start, is_final, states);
            else
                sState.setFinal(is_final);

            // making a transition for every terminal : B -> abcA : B-a->Q1-b->Q2-c->A
            for (String str : p.getRights()) {
                createTransition(states, str, sState);
            }
        }
        this.nfa = new FA(states, count, FA.Type.NFA);
        return this.nfa;
    }

    // checking that the state is final : has lambda production
    private boolean isFinal(Production p) {
        boolean is_final = false;
        for (String str : p.getRights()){
            if (str.equals(this.LANDA)) {
                is_final = true;
                break;
            }
        }
        return is_final;
    }

    private State getStateIfExist(String labelOfState, ArrayList<State> states) {
        for (int i = 0; i < states.size(); i++) {
            // if variable in left side already added
            if (states.get(i).getLabel().equals(labelOfState)) {
                return states.get(i);
            }
        }
        return null;
    }

    private State createNewStateAndAdd(String label, boolean isStart, boolean isFinal, ArrayList<State> states) {
        State s = new State(label, isStart, isFinal);
        states.add(s);
        return s;
    }

    private boolean isStart(String labelOfState, String StartSymbol) {
        return StartSymbol.equals(labelOfState);
    }

    private void createTransition(ArrayList<State> states, String rightOfProduction, State leftState){
        int len = rightOfProduction.length();
        State rightState = null;
        String input;
        String label;
        // if its lambda production
        if (rightOfProduction.equals(LANDA)) {
            return;
        }
        // if its unit production
        if (len == 1 && Character.isUpperCase(rightOfProduction.charAt(0))) {
            label = rightOfProduction;
            rightState = getStateIfExist(label,states);
            // if the variable in right side has not been added before
            if (rightState == null)
                rightState = createNewStateAndAdd(label, false, false, states);
            leftState.addTransition(new Transition(
                    leftState,
                    LANDA,
                    rightState)
            );
            return;
        }
        //  if ended with terminal
        if (Character.isLowerCase(rightOfProduction.charAt(len - 1))) {
            for(int i = 0; i < len; i++) {
                input = String.valueOf(rightOfProduction.charAt(i));
                label = "Q" + count++;
                rightState = new State(label, false, false);
                leftState.addTransition(new Transition(
                        leftState,
                        input,
                        rightState)
                );
                states.add(rightState);
                leftState = rightState;
            }
            rightState.setFinal(true);
        }
        // if ended with variable
        else {
            for(int i = 0; i < len - 2; i++) {
                input = String.valueOf(rightOfProduction.charAt(i));
                label = "Q" + count++;
                rightState = new State(label, false, false);
                leftState.addTransition(new Transition(
                        leftState,
                        input,
                        rightState)
                );
                states.add(rightState);
                leftState = rightState;
            }
            label = String.valueOf(rightOfProduction.charAt(len - 1));
            input = String.valueOf(rightOfProduction.charAt(len - 2));

            rightState = getStateIfExist(label,states);
            // if the variable in right side has not been added before
            if (rightState == null)
                rightState = createNewStateAndAdd(label, false, false, states);

            leftState.addTransition(new Transition(
                    leftState,
                    input,
                    rightState)
            );
        }
    }

}
