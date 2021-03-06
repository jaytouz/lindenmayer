/**
 * @title Devoir 1 - IFT2015 - Hiv2020
 * @author Louis-Vincent Poellhuber (p1234802 - 20161115)
 * @author Jérémie Tousignant (p1038501 - TOUJ14059307)
 */
package lindenmayer;
import java.awt.geom.Point2D;
import java.util.Stack;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Il s'agit de la class qui implemente tortu pour stocker les actions. Cette tortue ne dessine rien,
 * elle est utilisé par EPSTurtle pour dessiner ce qui est en mémoire
 */
public class GhostTurtle implements Turtle {
    Stack<State> memory; //Stack les états de la tortue
    State currentState;

    double step; // amount of displacement (d)
    double delta; // amount of rotation (teta +or- delta)

    private class State {
        double posX;
        double posY;
        double teta;

        private State(double x, double y, double t) {
            posX = x;
            posY = y;
            teta = t;
        }

        private State() {};

        @Override
        public String toString() {
            return "State{" +
                    "posX=" + posX +
                    ", posY=" + posY +
                    ", teta=" + teta +
                    '}';
        }
    }


    public GhostTurtle() {
        memory = new Stack<State>();
        currentState = new State();
    }

    /**
     * Adraw: bouger en avant par une distance unitaire (d) en traçant une ligne.
     * L’état de la tortue change de (x,y,theta) à (x+ d*cos(theta), y+d*sin(theta)
     * theta).
     */
    @Override
    public void draw() {
        currentState.posX += step * cos(Math.toRadians(currentState.teta));
        currentState.posY += step * sin(Math.toRadians(currentState.teta));
    }

    /**
     * move: bouger en avant par une distance unitaire (d) sans tracer. L’état de la tortue
     * change de (x,y,theta) à  (x+d*cos(theta), y+d*sin(theta), theta).
     */
    @Override
    public void move() {
        currentState.posX += step * cos(Math.toRadians(currentState.teta));
        currentState.posY += step * sin(Math.toRadians(currentState.teta));
    }

    /**
     * turnR: tourner vers le sens d’aiguille par une angle unitaire (delta).
     * L’état de la tortue change de (x,y,theta) à (x, y, theta-delta)
     */
    @Override
    public void turnR() {
        currentState.teta -= delta;
    }

    /**
     * turnR: tourner vers le sens d’aiguille par une angle unitaire (delta).
     * L’état de la tortue change de (x,y,theta) à (x, y, theta+delta)
     */
    @Override
    public void turnL() {
        currentState.teta += delta;
    }

    /**
     * push: sauvegarde (empile) l’état courant (position + angle) de la tortue.
     * L’état ne change pas
     */
    @Override
    public void push() {
        State toSave = new State();
        toSave.posX = currentState.posX;
        toSave.posY = currentState.posY;
        toSave.teta = currentState.teta;
        memory.push(toSave);
    }

    /**
     * pop: dépile l’état le plus récemment sauvegardé. L’état change à (x*, y*, theta*)
     * ce qui est l’état au dernier push.
     */
    @Override
    public void pop() {
        currentState = memory.pop();
    }

    /**
     * stay: l’état ne change pas.
     */
    @Override
    public void stay() {

    }

    /**
     * "initialisation de la position et de l'angle de la ghostTurtle
     * @param pos turtle position
     * @param angle_deg angle in degrees (90=up, 0=right)
     */
    @Override
    public void init(Point2D pos, double angle_deg) {
        currentState = new State(pos.getX(), pos.getY(), angle_deg);
    }

    /**
     * Retourne la position actuelle en x et y
     * @return pos : la position de l'etat actuel
     */
    @Override
    public Point2D getPosition() {
        Point2D pos = new Point2D.Double(currentState.posX, currentState.posY);
        return pos;
    }

    /**
     * retourne l'angle actuel
     * @return (double) currentState.teta
     */
    @Override
    public double getAngle() {
        return currentState.teta;
    }

    /**
     * definition des unites de deplacement et de rotation
     * @param step length of an advance (move or draw)
     * @param delta unit angle change in degrees (for turnR and turnL)
     */
    @Override
    public void setUnits(double step, double delta) {
        this.step = step;
        this.delta = delta;
    }

    @Override
    public String toString() {
        return "GhostTurtle{" +
                "memory=" + memory +
                ", currentState=" + currentState +
                ", step=" + step +
                ", delta=" + delta +
                '}';
    }
}
