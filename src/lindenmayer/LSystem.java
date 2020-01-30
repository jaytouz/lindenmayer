package lindenmayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LSystem extends AbstractLSystem { // Il faut que ca extends, parce qu'on doit utiser randomNumberGenerator
    private Map<Character, Symbol> symbols;
    private Map<Symbol, List<Iterator>> rules; //Je suis pas sûr si private ou non...
    private ArrayList<Symbol> axiom = new ArrayList<Symbol>();

    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */

    //TODO Map<char, Symbol> qui est le lien entre les caractères lus et le Symbol associé
    //TODO Map<Symbol, List<Iterator>> qui est le lien entre le Symbol et sa règle associée
    public LSystem() {
    }

    ;

    /* méthodes d'initialisation de système */
    public Symbol addSymbol(char sym) {
        Symbol newSymbol = new Symbol(sym);
        symbols.put(sym, newSymbol);
        return newSymbol;
    }

    //On appelle la méthode à chaque fois qu'on rajoute une règle. S'il n'y a pas de règle associé au symbole envoyé on
    //initialise le tout. S'il y en a déjà une, on la rajoute parmi ses règles associées.
    //TODO change string to symbol?
    public void addRule(Symbol sym, String expansion) {
        ArrayList<String> receivedRule = new ArrayList<String>(); //Une des règles associée au symbole
        ArrayList<Iterator> multipleRules = new ArrayList<Iterator>(); //La liste de toutes les règles associées au symbole
        for (int i = 0; i < expansion.length(); i++) {
            receivedRule.add(expansion.charAt(i) + ""); //On transforme le String reçu en ArrayList
        }

        if (!rules.containsKey(sym)) {
            multipleRules.add(receivedRule.iterator());
            rules.put(sym, multipleRules);
        } else {
            rules.get(sym).add(receivedRule.iterator());
        }

    }

    public void setAction(Symbol sym, String action) {
        symbols.get(sym.sym).action = action;
    }

    public void setAxiom(String str) {
        ArrayList<Symbol> receivedSymbol = new ArrayList<Symbol>();
        for (int i = 0; i < str.length(); i++) {
            receivedSymbol.add(symbols.get(str.charAt(i)));
        }
        axiom = receivedSymbol;
    }


    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException {
        JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        //ALPHABET + RULES + ACTIONS
        JSONArray alphabet = input.getJSONArray("alphabet");


        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère


            /*TODO On lit le JSON file et on en extrait les données. Le char va dans notre objet Map<char, Symbol>, qui
            sert à lier les caratères de l'alphabet à leur bonne action. Ensuite, pour pouvoir stocker les règles,
            on associe un SYMBOL avec sa règle, qui va dans notre Map<Symbol, List<Iterator>>.
            --JE PENSE-- qu'on call les méthodes addRule(), setAction(), etc. à partir de readJSONFile lorsqu'on
            extrait les données associés. Pas sûr tho.*/
        }
        //RULES

        //AXIOM
        String axiom = input.getString("axiom");
        system.setAxiom(axiom);

    }

    /* accès aux règles et exécution */
    public Iterator getAxiom() {
        return axiom.iterator();
    }

    public Iterator rewrite(Symbol sym) {
        if (!rules.get(sym).isEmpty()) {
            if (rules.get(sym).size() == 1) {
                return rules.get(sym).get(0);
            } else {
                int bound_size = rules.get(sym).size();
                int rnd_idx_rule = RND.nextInt(bound_size);
                return rules.get(sym).get(rnd_idx_rule); //Retourne une regle aleatoire borne par la taille de rules.
            }
        }
        return null;
    }

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

    /* opérations avancées */
    public Iterator applyRules(Iterator seq, int n) {

    }

    public void tell(Turtle turtle, Symbol sym, int rounds) {
    }

    public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {
    }


    public static void main(String[] args) {
        LSystem ls = new LSystem();
        Turtle t = new GhostTurtle();
        try {
            ls.readJSONFile("/sample_json/buisson.json", ls, t);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

