package lindenmayer;
import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class LSystem {
    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */
    public LSystem(){};
    /* méthodes d'initialisation de système */
    public Symbol addSymbol(char sym) {return new Symbol();}
    public void addRule(Symbol sym, String expansion) {}
    public void setAction(Symbol sym, String action) {}
    public void setAxiom(String str){}

    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException
    {
        JSONObject input = new JSONObject(new JSONTokener(new java.io.FileReader(filename))); // lecture de fichier JSON avec JSONTokener
        JSONArray alphabet = input.getJSONArray("alphabet");
        String axiom = input.getString("axiom");
        system.setAxiom(axiom);
        for (int i=0; i<alphabet.length(); i++){
            String letter = alphabet.getString(i);
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère
    }

    /* accès aux règles et exécution */
    public Iterator getAxiom(){}
    public Iterator rewrite(Symbol sym) {}
    public void tell(Turtle turtle, Symbol sym) {}

    /* opérations avancées */
    public Iterator applyRules(Iterator seq, int n) {}
    public void tell(Turtle turtle, Symbol sym, int rounds){}
    public Rectangle2D getBoundingBox(Turtle turtle, Iterator seq, int n) {}

}