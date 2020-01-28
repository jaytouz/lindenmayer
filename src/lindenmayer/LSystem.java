package lindenmayer;

import java.io.IOException;
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
    private String axiom;

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

    public void addRule(Symbol sym, String expansion) {
    //Comment on utilise iterator??
    }

    public void setAction(Symbol sym, String action) {
        symbols.get(sym).action = action;
    }

    public void setAxiom(String str) {
        axiom = str;
    }


    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException {
        JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        JSONArray alphabet = input.getJSONArray("alphabet");
        String axiom = input.getString("axiom");
        system.setAxiom(axiom);
        for (int i = 0; i < alphabet.length(); i++) {
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère

            /*TODO On lit le JSON file et on en extrait les données. Le char va dans notre objet Map<char, Symbol>, qui
            sert à lier les caratères de l'alphabet à leur bonne action. Ensuite, pour pouvoir stocker les règles,
            on associe un SYMBOL avec sa règle, qui va dans notre Map<Symbol, List<Iterator>>.
            --JE PENSE-- qu'on call les méthodes addRule(), setAction(), etc. à partir de readJSONFile lorsqu'on
            extrait les données associés. Pas sûr tho.*/
        }
    }

    /* accès aux règles et exécution */
    public Iterator getAxiom() {
        return rules.get(axiom).iterator(); //vrm pas sûr
    }

    public Iterator rewrite(Symbol sym) {
        
    }

    public void tell(Turtle turtle, Symbol sym) {
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

