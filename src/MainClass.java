import java.util.ArrayList;
import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        String p;
        Scanner scan = new Scanner(System.in);
        ArrayList<String> productionRules = new ArrayList<>();
        Grammar g;
        // get start symbol
        char start = scan.next().charAt(0);
        scan.nextLine();
        // get productions. end -> double clicking enter
        while (!(p = scan.nextLine()).isBlank()) {
            productionRules.add(p);
        }
        g = new Grammar(start, productionRules);
        System.out.println(g.getTypeOfGrammar());
        RegularLanguage rl = new RegularLanguage(g);
        FA fa = rl.grammarToNfa();
        System.out.println("\nNFA:");
        System.out.println(fa.print());
        System.out.println("start state : " + fa.getStartState());
        System.out.println("final states : " + fa.getFinalStates());
        System.out.println("\n Reverse:");
        FA rev = fa.rev();
        System.out.println(rev.print());
        System.out.println("start state : " + rev.getStartState());
        System.out.println("final states : " + rev.getFinalStates());
        System.out.println("\nDFA:");
        FA dfa = fa.nfaToDfa();
        System.out.println(dfa);
        System.out.println("start state : " + dfa.getStartState());
        System.out.println("final states : " + dfa.getFinalStates());
        System.out.println("\nComplement:");
        FA comp = fa.getComplement();
        System.out.println(comp);
        System.out.println("start state : " + comp.getStartState());
        System.out.println("final states : " + comp.getFinalStates());
        String input;
        System.out.println("\ninsert string : ");
        while (!(input = scan.nextLine()).isBlank()){
            if (dfa.accepter(input))
                System.out.println("Accepted.");
            else
                System.out.println("Rejected.");
            System.out.println("insert string : ");
        }
    }
}
