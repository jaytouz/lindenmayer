/*
 * Copyright 2020 Mikl&oacute;s Cs&#369;r&ouml;s.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lindenmayer;

import java.util.Iterator;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Class illustrating the interface to an L-system. 
 * 
 * <p><strong>Setting up the L-system. </strong>
 * The implementation 
 * reads the L-system specification from a JSON file. 
 * The file is parsed and the implementation builds its data structures 
 * by calling {@link #addSymbol}, {@link #addRule}, {@link #setAxiom} and {@link #setAction}. 
 * 
 * <p><strong>Using the L-system. </strong>
 * The implementation provides access to symbols as instances of {@link Symbol}, and 
 * to sequences as <code>Iterator&lt;Symbol&gt;</code>. (Note that implementing classes do not 
 * need to store an actual backing list or array for the iterator, but could calculate 
 * {@link Iterator#next() } on the fly.) 
 * 
 * <p><strong>Random rewriting. </strong>. If multiple rules have the same left-hand side, then 
 * one of them is chosen randpmly during rewriting. This abstract class sketches the management of 
 * the random-number generator {@link #RND} that extending classes can use. 
 * The {@link #resetRandomGenerator()}  method resets the generator, so 
 * that {@link #applyRules(java.util.Iterator, int)},  
 * {@link #tell(lindenmayer.Turtle, lindenmayer.Symbol, int) } 
 * and {@link #getBoundingBox(lindenmayer.Turtle, java.util.Iterator, int) } can 
 * give matching results across multiple calls. 
 * 
 * @author Mikl&oacute;s Cs&#369;r&ouml;s
 */
public abstract class AbstractLSystem 
{
    /**
     * Random number generator, initialized at instantiation with a knwon seed. 
     */
    protected final Random RND;
    /**
     * Random seed used for resetting the generator.
     */
    private final long init_seed;

    /**
     * Initializes the random-number generator.
     */
    protected AbstractLSystem()
    {
        this((new Random()).nextLong());
    }
    
    /**
     * Initializes the random-number generator with a given seed. 
     * 
     * @param init_random_seed seed used at initialization
     */
    protected AbstractLSystem(long init_random_seed)
    {
        this.init_seed = init_random_seed;
        this.RND = new Random(init_random_seed);
    }
    
    /**
     * Resets the random number generator's seed to its initial value.
     */
    protected void resetRandomGenerator()
    {
        this.RND.setSeed(init_seed);
    }
    
    /**
     * Registers a new character in the alphabet. This method is called while parsing 
     * the input (specifying the alphabet for the L-system).
     * 
     * @param sym character used in the input to denote this symbol
     * @return the corresponding {@link Symbol} in the alphabet
     */
    public abstract Symbol addSymbol(char sym) ;
    /**
     * Adds a new rule to the grammar. This method is called while parsing the input.
     * Symbols on the right-hand side are encoded by 
     * <code>char</code>s in the same way as in {@link #addSymbol(char)}. It is allowed to 
     * add the same rule more than once - each one is stored as an alternative. 
     * 
     * @param sym symbol on left-hand side that is rewritten by this rule
     * @param expansion sequence on right-hand side
     */
    public abstract void addRule(Symbol sym, String expansion);
    /**
     * Associates a turtle action with a symbol. This method is called while parsing the input. 
     * The action must correspond to one of the methods in {@link Turtle}: {@link Turtle#draw() }, {@link Turtle#move() }, 
     * {@link Turtle#turnL() }, {@link Turtle#turnR}, {@link Turtle#stay}, {@link Turtle#pop() }, {@link Turtle#push() }. 
     * 
     * @param sym a symbol corresponding to a turtle action
     * @param action a turtle action
     */
    public abstract void setAction(Symbol sym, String action);
    /**
     * Defines the starting sequence for the L-system. 
     * This method is called when parsing the input. 
     * 
     * Symbols are encoded by <code>char</code>s as in 
     * {@link #addSymbol(char) }. 
     * 
     * @param str starting sequence
     */
    public abstract void setAxiom(String str);
 
    /** 
     * Initializes this instance from a file. Implementing classes may prefer a static 
     * method. 
     */
    protected void readJSONFile(String filename, Turtle turtle) throws java.io.IOException
    {
        throw new java.lang.UnsupportedOperationException("If you want this you have to implement it.");
    }
 
    /**
     * Starting sequence.
     * @return starting sequence
     */
    public abstract Iterator<Symbol> getAxiom();
    /**
     * Applies a symbol's rewriting rule. 
     * If no rule was previously stored with {@link #addRule}, then it returns null. If a single rule 
     * was given, it uses the rule's right-hand side. If multiple rules were given ({@link #addRule} called with the same 
     * {@link Symbol} argument more than once), then one of them is chosen randomly. 
     * 
     * @param sym a symbol that would be rewritten. 
     * @return null if no rule, or one of the applicable rules chosen randomly
     */
    public abstract Iterator<Symbol> rewrite(Symbol sym);
    /**
     * Executes the action corresponding to a symbol (specified by {@link #setAction}) on a given turtle.  
     * 
     * @param turtle used for executing the action
     * @param sym symbol that needs to be executed 
     */
    public abstract void tell(Turtle turtle, Symbol sym);

    /**
     * Calculates the result of multiple rounds of rewriting. Symbols with no reriting rules are simply copied 
     * at each round. 
     * 
     * @param seq starting sequence
     * @param n number of rounds
     * @return sequence obtained after rewriting the entire sequence <var>n</var> times 
     */
    public abstract Iterator<Symbol> applyRules(Iterator<Symbol> seq, int n);
    
    /**
     * Draws the result after multiple rounds of rewriting, starting from a single symbol. 
     * Symbols with no rewriting rules are simply copied 
     * at each round. 
     * 
     * @param turtle turtle used for drawing
     * @param sym the starting sequence in round 0: a single symbol
     * @param rounds number of rounds
     */
    public abstract void tell(Turtle turtle, Symbol sym, int rounds);
    
    /**
     * Calculates the rectangle that would bound the drawing after multiple rounds of rewriting.
     * 
     * @param turtle turtle used for drawing
     * @param seq the starting sequence in round 0
     * @param n number of rounds
     * @return bounding box (union of all visited turtle positions)
     */
    public abstract Rectangle2D getBoundingBox(Turtle turtle, Iterator<Symbol>seq, int n) ;
}