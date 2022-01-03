public class Production {
    private final char left;
    private final String[] rights;

    public Production(char var, String[] rules) {
        this.left = var;
        this.rights = rules;
    }

    public String toString(){
        String str = "";
        for (int i = 0; i < this.rights.length - 1; i++){
            str += this.rights[i] + "|";
        }
        str += this.rights[this.rights.length - 1];
        return this.left + "->" + str;
    }

    public Production rev() {
        String[] rules = new String[rights.length];
        for (int j = 0; j < this.rights.length; j++) {
            String reversedString = "";
            String str = this.rights[j];
            for (int i = str.length() - 1; i >= 0; i--) {
                reversedString = reversedString + str.charAt(i);
            }
            rules[j] = reversedString;
        }
        return new Production(this.left, rules);
    }

    public String getLeft() {
        return String.valueOf(this.left);
    }

    public String[] getRights() {
        return this.rights;
    }
}
