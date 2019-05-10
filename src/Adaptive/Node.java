package Adaptive;

import java.awt.Color;
import java.awt.Graphics;

public class Node {

    Node left;
    Node right;
    Node parent;
    String letter;
    int frequency;
    int xpos;  //stores x and y position of the node in the tree
    int ypos;
    boolean isNew;

    Node(String letter, int frequency) {
        this.frequency = frequency;
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "Node{" + "letter=" + letter + ", frequency=" + frequency + '}';
    }

    void draw(Graphics g, int x, int y) {
        if (isNew) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.CYAN);
        }
        g.fillOval(x, y, 30, 30);
    }


}