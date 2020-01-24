package lindenmayer;

import java.awt.geom.Point2D;
import java.util.Stack;

public class TurtleStack implements Turtle {
    Stack<State> memory; //Stack les états de la tortue
    State state;

    private class State{
        double posX;
        double posY;
        double teta;
        private State(double x, double y, double t){
            posX = x;
            posY = y;
            teta = t;
        }
        private State(){};
    }

    public TurtleStack(){
        memory = new Stack<State>();
        state = new State();
    }

    @Override
    public void draw() {

    }

    @Override
    public void move() {

    }

    @Override
    public void turnR() {

    }

    @Override
    public void turnL() {

    }

    @Override
    public void push() {
        memory.push(state);
    }

    @Override
    public void pop() {

    }

    @Override
    public void stay() {

    }

    @Override
    public void init(Point2D pos, double angle_deg) {
//        state = new State(pos.getX(), pos.getY(), angle_deg);
    }

    @Override
    public Point2D getPosition() {
        return null;
    }

    @Override
    public double getAngle() {
        return 0;
    }

    @Override
    public void setUnits(double step, double delta) {

    }



    public static void main(String[] args) {
        TurtleStack t = new TurtleStack();
    }
}
