package Adaptive;

// Code for popping up a window that displays a custom component
// in this case we are displaying a Binary Search tree  
// reference problem 4.38 of Weiss to compute tree node x,y positions
// input is a text file name that will form the Binary Search Tree
//     java DisplaySimpleTree textfile
import static Adaptive.AdaptiveHuffman.catStr;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class DisplaySimpleTree extends JFrame {

    JScrollPane scrollpane;
    DisplayPanel panel;

    public DisplaySimpleTree(AdaptiveHuffman ah) {
        panel = new DisplayPanel(ah);
        panel.setPreferredSize(new Dimension(1366, 760));
        scrollpane = new JScrollPane(panel);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();  // cleans up the window panel
    }

    public static void main(String[] args) throws InterruptedException {
        drawEncode();
//        drawDecode();
    }

    public static void drawDecode() throws InterruptedException {
        String text = FileHandler.readFile("compressed.txt", false);
        AdaptiveHuffman ah = new AdaptiveHuffman(text.toCharArray());
        String code = ah.decode();
        ah.computeNodePositions();
        ah.maxheight = ah.height();
        FileHandler.writeFile("output.txt", code, true);
        DisplaySimpleTree dt = new DisplaySimpleTree(ah);//get a display panel
        dt.setVisible(true); //show the display

    }

    public static void drawEncode() throws InterruptedException {
        String text = FileHandler.readFile("teste.txt", true);
        text = text.substring(0, text.length() - 1);
        AdaptiveHuffman ah = new AdaptiveHuffman("".toCharArray());
        ah.computeNodePositions();
        ah.maxheight = ah.height();
        DisplaySimpleTree dt = new DisplaySimpleTree(ah);//get a display panel
        dt.setVisible(true); //show the display
        Thread.sleep(2000);
        for (int i = 0; i < text.length(); i++) {
            String aux = text.substring(0, i + 1);
//            AdaptiveHuffman ah = new AdaptiveHuffman(text.toCharArray());
            ah = new AdaptiveHuffman(aux.toCharArray());
            ArrayList<String> code = ah.encode();
            ah.computeNodePositions();
            ah.maxheight = ah.height();
            FileHandler.writeFile("compressed.txt", AdaptiveHuffman.catStr(code), true);
            FileHandler.writeFile("compressedFriendly.txt", catStr(ah.friendlyOutput), true);
            dt = new DisplaySimpleTree(ah);//get a display panel

            dt.setVisible(true); //show the display
            Thread.sleep(2000);
        }
    }
}

class DisplayPanel extends JPanel {

    AdaptiveHuffman ah;
    int xs;
    int ys;

    public DisplayPanel(AdaptiveHuffman ah) {
        this.ah = ah; // allows dispay routines to access the tree
        setBackground(Color.white);
        setForeground(Color.black);
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        Font MyFont = new Font("SansSerif", Font.PLAIN, 10);
        xs = 10;   //where to start printing on the panel
        ys = 20;
        ys = ys + 10;;
        int start = 0;
        MyFont = new Font("SansSerif", Font.BOLD, 12); //bigger font for tree
        g.setFont(MyFont);
        this.drawTree(g, ah.getRoot()); // draw the tree
        revalidate(); //update the component panel
    }

    public void drawTree(Graphics g, Node root) {//actually draws the tree
        int dx, dy, dx2, dy2;
        int SCREEN_WIDTH = 800; //screen size for panel
        int SCREEN_HEIGHT = 500;
        int XSCALE, YSCALE;
        int offset = 100;
        XSCALE = SCREEN_WIDTH / ah.totalnodes; //scale x by total nodes in tree
        YSCALE = (SCREEN_HEIGHT - ys) / (ah.maxheight + 1); //scale y by tree height

        if (root != null) { // inorder traversal to draw each node
            drawTree(g, root.left); // do left side of inorder traversal 
            dx = root.xpos * XSCALE + offset; // get x,y coords., and scale them 
            dy = root.ypos * YSCALE + ys;
            String s = (String) root.letter;
            if (s != null) {
                root.draw(g, dx - 15, dy - 15);
                g.setColor(Color.BLACK);
                if (root.letter.matches("NYT")) {
                    g.drawString(s, dx - 10, dy + 5); // draws the NYT
                } else {
                    g.drawString(s, dx, dy + 5); // draws the word
                }

                g.drawString("" + root.frequency, dx, dy - 20);
            }
// this draws the lines from a node to its children, if any
            if (root.left != null) { //draws the line to left child if it exists
                dx2 = root.left.xpos * XSCALE + offset;
                dy2 = root.left.ypos * YSCALE + ys;
                g.drawLine(dx, dy, dx2, dy2);
            }
            if (root.right != null) { //draws the line to right child if it exists
                dx2 = root.right.xpos * XSCALE + offset;//get right child x,y scaled position
                dy2 = root.right.ypos * YSCALE + ys;
                g.drawLine(dx, dy, dx2, dy2);
            }
            drawTree(g, root.right); //now do right side of inorder traversal 
        }
    }
}

class MyTree {

    String inputString = new String();
    Node root;
    int totalnodes = 0; //keeps track of the inorder number for horiz. scaling 
    int maxheight = 0;//keeps track of the depth of the tree for vert. scaling

    MyTree() {
        root = null;
    }

    public int treeHeight(Node t) {
        if (t == null) {
            return -1;
        } else {
            return 1 + max(treeHeight(t.left), treeHeight(t.right));
        }
    }

    public int max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public void computeNodePositions() {
        int depth = 1;
        inorder_traversal(root, depth);
    }

//traverses tree and computes x,y position of each node, stores it in the node
    public void inorder_traversal(Node t, int depth) {
        if (t != null) {
            inorder_traversal(t.left, depth + 1); //add 1 to depth (y coordinate) 
            t.xpos = totalnodes++; //x coord is node number in inorder traversal
            t.ypos = depth; // mark y coord as depth
            inorder_traversal(t.right, depth + 1);
        }
    }
}
