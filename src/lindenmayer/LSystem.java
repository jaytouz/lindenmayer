package lindenmayer;

import java.awt.geom.Point2D;
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
    private Map<Symbol, List<Iterator>> rules;
    private ArrayList<Symbol> axiom = new ArrayList<Symbol>();

    /**
     * constructeur vide monte un système avec alphabet vide et sans règles
     */
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
    public void addRule(Symbol sym, String expansion) {
        ArrayList<String> receivedRule = new ArrayList<String>(); //TODO Je crois que ca doit etre une liste de symbole (JT)
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

    //TODO verifier s'il y a tous les elements necessaires dans la methode readJSONfile.
    /* initialisation par fichier */
    public static void readJSONFile(String filename, LSystem system, Turtle turtle) throws java.io.IOException {
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
            Symbol sym = system.addSymbol(letter.charAt(0)); // un caractère

            //RULES
            if (rules.has(letter)) {
                //si la lettre a une JSONARRAY comme regle, ajouter chaque element
                int num_rules = rules.getJSONArray(letter).length();
                for (int j = 0; i < num_rules; j++) {
                    // on ajoute toutes les règles associés
                    String expansion = (String) rules.getJSONArray(letter).get(j); // expansion
                    system.addRule(sym, expansion);
                }
            }
        }

        //AXIOM
        system.setAxiom(axiom);

        //Parameter //TODO je suis pas sur si ca doit etre int ou double (JT). Je crois que c'est double pour les positions (LVP)
        double xIni = (double) parameters.getJSONArray("start").get(0);
        double yIni = (double) parameters.getJSONArray("start").get(1);
        double tetaIni = (double) parameters.getJSONArray("start").get(2);

        turtle.init(new Point2D.Double(xIni, yIni), tetaIni);
        turtle.setUnits((double) parameters.get("step"), (double) parameters.get("angle"));

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
    //Ici j'ai rajouté des <Symbol> à côté des Iterator, commme dans AbstractLSystem pour pouvoir faire ce que j'ai fait
    //Peut-être que ça va fuck up quelque part
    public Iterator applyRules(Iterator<Symbol> seq, int n) {
        ArrayList<Symbol> newAxiom = new ArrayList<Symbol>();

        for (int i = 0; i < n; i++) {                             //Nombre de générations
            while (seq.hasNext()) {                               //À chaque gen, on itère à travers la séquence de départ
                Symbol nextSymbol = seq.next();
                Iterator<Symbol> temp = this.rewrite(nextSymbol);
                if (temp != null) {
                    while (temp.hasNext()) {                      //On itère à travers la règle reçue
                        newAxiom.add(temp.next());
                    }
                } else {
                    newAxiom.add(nextSymbol);
                }
            }
            seq = newAxiom.iterator();                            //On crée la séquence de départ de la prochaine gen
            newAxiom.clear();
        }

        return seq;                                               //Gen finale
    }

    //C'est vraiment complexe
    public void tell(Turtle turtle, Symbol sym, int rounds) {
        ArrayList<Symbol> temp = new ArrayList<Symbol>();
        temp.add(sym);
        if (rounds == 0) {
            tell(turtle, sym);

        } else {
            Iterator<Symbol> itr = applyRules(temp.iterator(), 1);
            while (itr.hasNext()) {
                tell(turtle, itr.next(), rounds - 1);
            }
        }

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

