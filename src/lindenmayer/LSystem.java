package lindenmayer;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;
import java.awt.geom.Rectangle2D;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.sound.midi.Sequencer;

public class LSystem extends AbstractLSystem {
    private Map<Character, Symbol> symbols = new HashMap<Character, Symbol>();
    private Map<Symbol, List<Iterator>> rules = new HashMap<Symbol, List<Iterator>>();
    private ArrayList<Symbol> axiom = new ArrayList<Symbol>();

    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */
    public LSystem() {
    }

    ;

    /**
     * Registers a new character in the alphabet. This method is called while parsing
     * the input (specifying the alphabet for the L-system).
     *
     * @param sym character used in the input to denote this symbol
     * @return the corresponding {@link Symbol} in the alphabet
     */
    public Symbol addSymbol(char sym) {
        Symbol newSymbol = new Symbol(sym);
        symbols.put(sym, newSymbol);
        return newSymbol;
    }

    /**
     * Adds a new rule to the grammar. This method is called while parsing the input.
     * Symbols on the right-hand side are encoded by
     * <code>char</code>s in the same way as in {@link #addSymbol(char)}. It is allowed to
     * add the same rule more than once - each one is stored as an alternative.
     *
     * @param sym       symbol on left-hand side that is rewritten by this rule
     * @param expansion sequence on right-hand side
     */
    public void addRule(Symbol sym, String expansion) {
        ArrayList<Symbol> receivedRule = new ArrayList<Symbol>();
        ArrayList<Iterator> multipleRules = new ArrayList<Iterator>(); //La liste de toutes les règles associées au symbole
        for (int i = 0; i < expansion.length(); i++) {
            char letter = expansion.charAt(i); //on recherche la lettre de l'expansion.
            receivedRule.add(symbols.get(letter)); //On ajoute le symbol correspondant
        }
        if (!rules.containsKey(sym)) {
            multipleRules.add(receivedRule.iterator());
            rules.put(sym, multipleRules);
        } else {
            rules.get(sym).add(receivedRule.iterator());
        }

    }

    /**
     * Associates a turtle action with a symbol. This method is called while parsing the input.
     * The action must correspond to one of the methods in {@link Turtle}: {@link Turtle#draw() }, {@link Turtle#move() },
     * {@link Turtle#turnL() }, {@link Turtle#turnR}, {@link Turtle#stay}, {@link Turtle#pop() }, {@link Turtle#push() }.
     *
     * @param sym    a symbol corresponding to a turtle action
     * @param action a turtle action
     */
    public void setAction(Symbol sym, String action) {
        symbols.get(sym.sym).action = action;
    }

    /**
     * Defines the starting sequence for the L-system.
     * This method is called when parsing the input.
     * <p>
     * Symbols are encoded by <code>char</code>s as in
     * {@link #addSymbol(char) }.
     *
     * @param str starting sequence
     */
    public void setAxiom(String str) {
        ArrayList<Symbol> receivedSymbol = new ArrayList<Symbol>();
        for (int i = 0; i < str.length(); i++) {
            receivedSymbol.add(symbols.get(str.charAt(i)));
        }
        axiom = receivedSymbol;
    }

    /**
     * Initializes this instance from a file. Implementing classes may prefer a static
     * method.
     */
    public void readJSONFile(String filename, Turtle turtle) throws java.io.IOException {
        JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        //get data from input json
        JSONArray alphabet = input.getJSONArray("alphabet");
        JSONObject rules = input.getJSONObject("rules");
        String axiom = input.getString("axiom");
        JSONObject actions = input.getJSONObject("actions");
        JSONObject parameters = input.getJSONObject("parameters");

        // Add data to LSysteme

        // add alphabet
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = addSymbol(letter.charAt(0)); // un caractère
            //ACTION
            setAction(sym, actions.getString(letter));

        }
        //RULES
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);

            if (rules.has(letter)) {
                //si la lettre a une JSONARRAY comme regle, ajouter chaque element
                int num_rules = rules.getJSONArray(letter).length();
                for (int j = 0; j < num_rules; j++) {
                    // on ajoute toutes les règles associés
                    String expansion = (String) rules.getJSONArray(letter).get(j); //TODO il y a un null ici but idk why
                    addRule(symbols.get(letter.charAt(0)), expansion);
                }
            }
        }
        //AXIOM
        setAxiom(axiom);

        //Parameter //TODO je suis pas sur si ca doit etre int ou double (JT). Je crois que c'est double pour les positions (LVP)
        double xIni = parameters.getJSONArray("start").getDouble(0);
        double yIni = parameters.getJSONArray("start").getDouble(1);
        double tetaIni = parameters.getJSONArray("start").getDouble(2);

        turtle.init(new Point2D.Double(xIni, yIni), tetaIni);
        turtle.setUnits(parameters.getDouble("step"), parameters.getDouble("angle"));
    }

    /**
     * Starting sequence.
     *
     * @return starting sequence
     */
    public Iterator getAxiom() {
        return axiom.iterator();
    }

    /**
     * Applies a symbol's rewriting rule.
     * If no rule was previously stored with {@link #addRule}, then it returns null. If a single rule
     * was given, it uses the rule's right-hand side. If multiple rules were given ({@link #addRule} called with the same
     * {@link Symbol} argument more than once), then one of them is chosen randomly.
     *
     * @param sym a symbol that would be rewritten.
     * @return null if no rule, or one of the applicable rules chosen randomly
     */
    public Iterator rewrite(Symbol sym) {
        if (rules.get(sym) != null && !rules.get(sym).isEmpty()) {
//            if (rules.get(sym).size() == 1) {
//                return rules.get(sym).get(0);
//            } else {
            int bound_size = rules.get(sym).size();
            int rnd_idx_rule = RND.nextInt(bound_size);
            Iterator<Symbol> itr = getRule(sym, rnd_idx_rule);

//            return rules.get(sym).get(rnd_idx_rule); //Retourne une regle aleatoire borne par la taille de rules.
            return itr;
        }
        return null;
    }

    /**
     * Methode pour retourner une instance de l'iterateur comtenant la regle avec un compteur initialiser a zero.
     * Assure egalement que le compteur dans rules soit a zero apres l'utilisation de rewrite.
     * @param sym
     * @param idx_rules
     * @return iterator comptenant la regle associe au symbol.
     */
    private Iterator<Symbol> getRule(Symbol sym, int idx_rules){
        ArrayList<Symbol> copy_list_itr = new ArrayList<Symbol>();
        Iterator<Symbol> itr = rules.get(sym).get(idx_rules);

        //copier les elements de la regle
        while(itr.hasNext()){
            copy_list_itr.add(itr.next());
        }
        //remettre un iterateur dans rules avec un compteur a zero
        rules.get(sym).set(idx_rules, copy_list_itr.iterator());

        return copy_list_itr.iterator();
    }

    /**
     * Executes the action corresponding to a symbol (specified by {@link #setAction}) on a given turtle.
     *
     * @param turtle used for executing the action
     * @param sym    symbol that needs to be executed
     */
    public void tell(Turtle turtle, Symbol sym) {
        switch (sym.action) {
            case "draw":
                turtle.draw();
                break;
            case "push":
                turtle.push();
                break;
            case "pop":
                turtle.pop();
                break;
            case "turnR":
                turtle.turnR();
                break;
            case "turnL":
                turtle.turnL();
                break;
        }
    }

    /**
     * Calculates the result of multiple rounds of rewriting. Symbols with no reriting rules are simply copied
     * at each round.
     *
     * @param seq starting sequence
     * @param n   number of rounds
     * @return sequence obtained after rewriting the entire sequence <var>n</var> times
     */
    public Iterator applyRules(Iterator<Symbol> seq, int n) {
        ArrayList<Symbol> newAxiom = new ArrayList<Symbol>();
        Iterator<Symbol> lastAxiom = seq;
        if (n == 0) {
            return seq;
        } else {
            for (int i = 0; i < n; i++) {                             //Nombre de générations
                newAxiom.clear();
                while (lastAxiom.hasNext()) {                               //À chaque gen, on itère à travers la séquence de départ
                    Symbol nextSymbol = lastAxiom.next();
                    Iterator<Symbol> temp = rewrite(nextSymbol);
                    if (temp != null) {
                        while (temp.hasNext()) {                      //On itère à travers la règle reçue
                            Symbol s = temp.next();
                            newAxiom.add(s);
                        }
                    } else {
                        newAxiom.add(nextSymbol);
                    }
                }
                lastAxiom = newAxiom.iterator();
            }
            return seq;                                               //Gen finale
        }
    }

    /**
     * Draws the result after multiple rounds of rewriting, starting from a single symbol.
     * Symbols with no rewriting rules are simply copied
     * at each round.
     *
     * @param turtle turtle used for drawing
     * @param sym    the starting sequence in round 0: a single symbol
     * @param rounds number of rounds
     */
    public void tell(Turtle turtle, Symbol sym, int rounds) {
        if (rounds == 0) {
            System.out.println("TELL - " + sym.sym);
            tell(turtle, sym);

        } else {
            System.out.println(sym + "--------------" + rounds);
            Iterator<Symbol> itr = rewrite(sym);
            if (itr != null){
                while (itr.hasNext()) {
                    tell(turtle, itr.next(), rounds - 1);
                }
            }else{
                tell(turtle, sym, rounds -1);
            }


        }
    }

    /**
     * Calculates the rectangle that would bound the drawing after multiple rounds of rewriting.
     *
     * @param turtle turtle used for drawing
     * @param seq    the starting sequence in round 0
     * @param n      number of rounds
     * @return bounding box (union of all visited turtle positions)
     */
    public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {
        double width = 0;
        double height = 0;
        double x = 0;
        double X = 0;
        double y = 0;
        double Y = 0;

        Rectangle2D bbox = new Rectangle2D.Double(0, 0, X - x, Y - y);
        Iterator<Symbol> seq_actions = applyRules(seq, n); //TODO APPLYRULES RETURN NOTHING.
        while (seq_actions.hasNext()) {
            System.out.println("in BBBOX");
            Symbol sym = seq_actions.next();
            tell(turtle, sym);
            x = Math.min(x, turtle.getPosition().getX());
            X = Math.max(X, turtle.getPosition().getX());
            y = Math.min(y, turtle.getPosition().getY());
            Y = Math.max(Y, turtle.getPosition().getY());
            System.out.println(x + " " + X + " " + y + " " + Y);
            bbox.createUnion(new Rectangle2D.Double(0, 0, X - x, Y - y));
        }

        return bbox;
    }
}

