import java.util.ArrayList;

public class Grammar {
    String LANDA = "λ";
    private ArrayList<Production> productions = new ArrayList<>(1000);
    private char[] variables = new char[100];
//    private char[] terminals = new char[100];
    public enum Type {
        rightlinear,
        leftlinear,
        notlinear
    }
    private Type typeOfGrammar;
    private final char start_symbol;

    public Grammar(char start_symbol, ArrayList<String> productions) {
        this.start_symbol = start_symbol;
        int count = 0;
        for (int i = 0; i < productions.size(); i++) {
            // removing white spaces
            productions.set(i, productions.get(i).replaceAll("\\s",""));
            String[] dummy = productions.get(i).split("->");
            this.variables[count++] = dummy[0].charAt(0);
            String[] rules = dummy[1].split("\\|");
            this.productions.add(new Production(dummy[0].charAt(0), rules));
        }
        typeRecognition();
    }

    public String toString(){
        String out = "";
        for (Production p : productions){
            out += p + "\n";
        }
        return out;
    }

    public Grammar convertToRightLinear() {
        if (this.typeOfGrammar == Type.notlinear) { // Not Linear
            System.out.println("Grammar is not linear!!");
            return null;
        }
        else if (this.typeOfGrammar == Type.leftlinear){ // Left Linear
            ArrayList<String> productions2 = new ArrayList<>();
            for (Production p : this.productions){
                productions2.add(p.rev().toString());
            }
            return new Grammar(this.start_symbol, productions2);
        }
        else { // Right Linear
            System.out.println("Grammar is already right linear :)");
            return this;
        }
    }

    public ArrayList<Production> getProductions() {
        return this.productions;
    }
    public char getStartSymbol() {
        return this.start_symbol;
    }

    // right linear or left linear
    private void typeRecognition() {
        if (RLinear() && !LLinear())
            typeOfGrammar = Type.rightlinear;
        else if (!RLinear() && LLinear())
            typeOfGrammar = Type.leftlinear;
        else
            typeOfGrammar = Type.notlinear;
    }

    // check all of production is right linear
    private boolean RLinear() {
        for (Production p : productions) {
            for (String str : p.getRights()) {
                int len = str.length();
                for (int i = 0; i < len - 1; i++)
                    if (Character.isUpperCase(str.charAt(i))) {
                        return false;
                    }
                }
            }
        return true;
    }
    // check all of production is left linear
    private boolean LLinear() {
        for (Production p : productions) {
            for (String str : p.getRights()) {
                int len = str.length();
                for (int i = 1; i < len; i++){
                    if (Character.isUpperCase(str.charAt(i))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Type getTypeOfGrammar(){
        return this.typeOfGrammar;
    }
}
