package Adaptive;

import static Adaptive.AdaptiveHuffman.catStr;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class DisplaySimpleTree extends JFrame {

    private DisplaySimpleTree(AdaptiveHuffman ah) {
        DisplayPanel panel = new DisplayPanel(ah);
        panel.setPreferredSize(new Dimension(1366, 760));
        JScrollPane scrollpane = new JScrollPane(panel);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();  // cleans up the window panel
    }

    public static void main(String[] args) {
        drawEncode();
        drawDecode();
    }

    private static void drawDecode() {
        String text = FileHandler.readFile("compressed.txt", false);
        AdaptiveHuffman ah = new AdaptiveHuffman();
        String code = ah.decode(text);
        ah.computeNodePositions();

        FileHandler.writeFile("output.txt", code, false);
        DisplaySimpleTree dt = new DisplaySimpleTree(ah);//get a display panel
        dt.setVisible(true); //show the display

    }

    private static void drawEncode() {
        String text = FileHandler.readFile("teste.txt", true);
        text = text.substring(0, text.length() - 1);
        AdaptiveHuffman ah = new AdaptiveHuffman();
        ArrayList<String> code = ah.encode(text);
        ah.computeNodePositions();
        DisplaySimpleTree dt = new DisplaySimpleTree(ah);//get a display panel
        dt.setVisible(true); //show the display
        FileHandler.writeFile("compressed.txt", AdaptiveHuffman.catStr(code), true);
        FileHandler.writeFile("compressedFriendly.txt", catStr(ah.getFriendlyOutput()), true);
        //Thread.sleep(2000);
        /*for (int i = 0; i < text.length(); i++) {
            String aux = text.substring(0, i + 1);
            ArrayList<String> code = ah.encode(aux);
            ah.computeNodePositions();
            ah.maxheight = ah.height();
            FileHandler.writeFile("compressed.txt", AdaptiveHuffman.catStr(code), true);
            FileHandler.writeFile("compressedFriendly.txt", catStr(ah.friendlyOutput), true);
            dt = new DisplaySimpleTree(ah);//get a display panel

            dt.setVisible(true); //show the display
            Thread.sleep(2000);
        }*/
    }
}

class DisplayPanel extends JPanel {

    private AdaptiveHuffman ah;

    DisplayPanel(AdaptiveHuffman ah) {
        this.ah = ah; // allows dispay routines to access the tree
        setBackground(Color.white);
        setForeground(Color.black);
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground()); //colors the window
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground()); //set color and fonts
        Font myFont = new Font("SansSerif", Font.BOLD, 12); //bigger font for tree
        g.setFont(myFont);
        this.drawTree(g, ah.getRoot()); // draw the tree
        revalidate(); //update the component panel
    }

    private void drawTree(Graphics g, Node root) {//actually draws the tree
        int dx, dy, dx2, dy2;
        int SCREEN_WIDTH = 800; //screen size for panel
        int SCREEN_HEIGHT = 500;
        int xs = 10;
        int offset = 400 + xs;
        int XSCALE = SCREEN_WIDTH / ah.getTotalNodes(); //scale x by total nodes in tree
        int ys = 30;
        int YSCALE = (SCREEN_HEIGHT - ys) / (ah.height() + 1); //scale y by tree height

        if (root != null) { // inorder traversal to draw each node
            drawTree(g, root.left); // do left side of inorder traversal 
            dx = root.xpos * XSCALE + offset; // get x,y coords., and scale them
            dy = root.ypos * YSCALE + ys;
            String s = root.letter;
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