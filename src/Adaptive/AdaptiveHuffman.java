package Adaptive;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class AdaptiveHuffman {

    private Node nytNode;
    private Node root;
    private char[] codeStr;
    private ArrayList<Character> alreadyExist;
    ArrayList<Node> nodeList;
    private String tempCode = "";
    int totalnodes = 0; //keeps track of the inorder number for horiz. scaling 
    int maxheight = 0;//keeps track of the depth of the tree for vert. scaling

    protected ArrayList<String> friendlyOutput;

    public AdaptiveHuffman(char[] codeStr) {
        this.codeStr = codeStr;
        alreadyExist = new ArrayList<>();
        nodeList = new ArrayList<>();
        friendlyOutput = new ArrayList<>();

        //Initialize the nyt Node.
        nytNode = new Node("NYT", 0);
        nytNode.parent = null;
        root = nytNode;
        nodeList.add(nytNode);
    }

    public ArrayList<String> encode() {
        ArrayList<String> result = new ArrayList<>();
        result.add("0");
        char temp;
        for (int i = 0; i < codeStr.length; i++) {
            temp = codeStr[i];
            result.add(getCode(temp));
            this.friendlyOutput.add(getCodeFriendly(temp));
            updateTree(temp);
        }
        return result;
    }

    public String decode() {
        String result = "";
        String symbol;
        char temp;
        Node p = getRoot();

        //The first symbol is of course NEW, so find it by ASCII
        symbol = getByAsc(0);
        result += symbol;
        System.out.println(symbol);
        updateTree(symbol.charAt(0));
        p = getRoot();

        for (int i = 9; i < codeStr.length; i++) {
            temp = codeStr[i];

            if (temp == '0') {
                p = p.left;
            } else {
                p = p.right;
            }

            symbol = visit(p);
            //If reach a leaf
            if (symbol != null) {
                if (symbol == "NYT") {
                    symbol = getByAsc(i);
                    i += 8;
                }
                result += symbol;
                System.out.println(symbol);
                updateTree(symbol.charAt(0));
                p = getRoot();
            }
        }

        return result;
    }

    private void updateTree(char c) {
        //make the last inserted node as old
        recursiveTurnNodeToFalse();
        Node toBeAdd = null;
        if (!isAlreadyExist(c)) {
            Node innerNode = new Node(null, 1);
//            innerNode.isNew=false;
            Node newNode = new Node(String.valueOf(c), 1);
            //make the newly inserted node as new
            newNode.isNew = true;

            innerNode.left = nytNode;
            innerNode.right = newNode;
            innerNode.parent = nytNode.parent;
            if (nytNode.parent != null) {
                nytNode.parent.left = innerNode;
            } else {
                root = innerNode;
            }
            nytNode.parent = innerNode;
            newNode.parent = innerNode;

            nodeList.add(1, innerNode);
            nodeList.add(1, newNode);
            alreadyExist.add(c);
            toBeAdd = innerNode.parent;
        } else {
            toBeAdd = findNode(c);
            toBeAdd.isNew = true;
        }

        while (toBeAdd != null) {
            Node bigNode = findBigNode(toBeAdd.frequency);
            if (toBeAdd != bigNode && toBeAdd.parent != bigNode && bigNode.parent != toBeAdd) {
                swapNode(toBeAdd, bigNode);
            }
            toBeAdd.frequency++;
            toBeAdd = toBeAdd.parent;
        }
    }

    private boolean isAlreadyExist(char temp) {
        // TODO Auto-generated method stub
        for (int i = 0; i < alreadyExist.size(); i++) {
            if (temp == alreadyExist.get(i)) {
                return true;
            }
        }
        return false;
    }

    //Get the symbol using the next 8 bit as a ASCII code.
    private String getByAsc(int index) {
        // TODO Auto-generated method stub
        int asc = 0;
        int tempInt = 0;
        for (int i = 7; i >= 0; i--) {
            tempInt = codeStr[++index] - 48;
            asc += tempInt * Math.pow(2, i);
        }
        char ret = (char) asc;
        return String.valueOf(ret);
    }

    private String visit(Node p) {
        // TODO Auto-generated method stub
        if (p.letter != null) {
            //The symbol has been found.
            return p.letter;
        }
        return null;
    }

    private String getCode(char c) {
        tempCode = "";

        getCodeByTree(getRoot(), String.valueOf(c), "");
        String result = tempCode;
        if (result == "") {
            getCodeByTree(getRoot(), "NYT", "");
            result = "" + tempCode;
            result += toBinary(getAscii(c));
        }
        return result;
    }

    private String getCodeFriendly(char c) {
        tempCode = "";

        getCodeByTree(getRoot(), String.valueOf(c), "");
        String result = tempCode;
        if (result == "") {
            getCodeByTree(getRoot(), "NYT", "");
            result = "" + tempCode;
            result = result + "'" + c + "'";
        }
        return result + "\n";
    }

    //Find the existing node in the tree
    private Node findNode(char c) {
        // TODO Auto-generated method stub
        String temp = String.valueOf(c);
        Node tempNode = null;
        for (int i = 0; i < nodeList.size(); i++) {
            tempNode = nodeList.get(i);
            if (tempNode.letter != null && tempNode.letter.equals(temp)) {
                return tempNode;
            }
        }
        return null;
    }

    private void swapNode(Node n1, Node n2) {
        // TODO Auto-generated method stub
        //note that n1<n2
        //Swap the position in the list firstly
        int i1 = nodeList.indexOf(n1);
        int i2 = nodeList.indexOf(n2);
        nodeList.remove(n1);
        nodeList.remove(n2);
        nodeList.add(i1, n2);
        nodeList.add(i2, n1);

        //Swap the position in the tree then
        Node p1 = n1.parent;
        Node p2 = n2.parent;
        //If the two nodes have different parent node.
        if (p1 != p2) {
            if (p1.left == n1) {
                p1.left = n2;
            } else {
                p1.right = n2;
            }

            if (p2.left == n2) {
                p2.left = n1;
            } else {
                p2.right = n1;
            }
        } else {
            p1.left = n2;
            p1.right = n1;
        }
        n1.parent = p2;
        n2.parent = p1;

    }

    private Node findBigNode(int frequency) {
        // TODO Auto-generated method stub
        Node temp = null;
        for (int i = nodeList.size() - 1; i >= 0; i--) {
            temp = nodeList.get(i);
            if (temp.frequency == frequency) {
                break;
            }
        }
        return temp;
    }

    private void getCodeByTree(Node node, String letter, String code) {
        // TODO Auto-generated method stub
        //Reach a leaf
        if (node.left == null && node.right == null) {
            if (node.letter != null && node.letter.equals(letter)) {
                tempCode = code;
            }
        } else {
            if (node.left != null) {
                getCodeByTree(node.left, letter, code + "0");
            }
            if (node.right != null) {
                getCodeByTree(node.right, letter, code + "1");
            }
        }
    }

    public static int getAscii(char c) {
        return (int) c;
    }

    public static String toBinary(int decimal) {
        String result = "";
        for (int i = 0; i < 8; i++) {
            if (decimal % 2 == 0) {
                result = "0" + result;
            } else {
                result = "1" + result;
            }
            decimal /= 2;
        }
        return result;
    }

    public static double calCompRate(String text, ArrayList<String> code) {
        double compRate = 0;
        double preNum = 8 * text.length();
        double postNum = 0;
        for (String s : code) {
            postNum += s.length();
        }

        compRate = preNum / postNum;
        System.out.println("If simply using ASCII code, there are in total " + (int) preNum + " bits.");
        System.out.println("If using huffman coding, there are in total " + (int) postNum + " bits.");
        System.out.println("The compress rate is: " + compRate);
        return compRate;
    }

    public static void displayList(ArrayList<String> l) {
        for (int i = 0; i < l.size(); i++) {
            System.out.println(l.get(i));
        }
    }

    protected static String catStr(ArrayList<String> l) {
        // TODO Auto-generated method stub
        String result = "";
        for (String s : l) {
            result += s;
        }
        return result;
    }

    private void getStatistics() {
        // TODO Auto-generated method stub
        ArrayList<Symbol> symbolList = new ArrayList<Symbol>();
        preOrder(getRoot(), symbolList);
        Collections.sort(symbolList);
        calRange(symbolList);
        FileHandler.writeSymbolToFile("data/symboltable.txt", symbolList);
    }

    public static void preOrder(Node node, ArrayList<Symbol> symbolList) {
        if (node != null) {
            if (node.letter != null && (!node.letter.equals("NEW"))) {
                Symbol tempSymbol = new Symbol(node.letter, node.frequency);
                symbolList.add(tempSymbol);
            }
            System.out.println(node);
            preOrder(node.left, symbolList);
            preOrder(node.right, symbolList);
        }
    }

    private void calRange(ArrayList<Symbol> symbolList) {
        int total = codeStr.length;
        double low = 0;

        for (Symbol tempSymbol : symbolList) {
            tempSymbol.probability = tempSymbol.frequency / (double) total;
            tempSymbol.low = low;
            tempSymbol.high = low + tempSymbol.probability;
            low += tempSymbol.probability;
        }
        System.out.println("low=" + low);//It should be 1.
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
//        testEncode();
        testDecode();

    }

    public static void testDecode() {
        String code = FileHandler.readFile("compressed.txt", false);
        AdaptiveHuffman ah = new AdaptiveHuffman(code.toCharArray());
        String result = ah.decode();
        FileHandler.writeFile("output.txt", result, false);
    }

    public static void testEncode() {
        String text = FileHandler.readFile("teste.txt", true);
        text = text.substring(0, text.length() - 1);
        AdaptiveHuffman ah = new AdaptiveHuffman(text.toCharArray());
        ArrayList<String> code = ah.encode();
        FileHandler.writeFile("compressed.txt", catStr(code), true);
        FileHandler.writeFile("compressedFriendly.txt", catStr(ah.friendlyOutput), true);
        ah.getStatistics();
        calCompRate(text, code);
    }

    /* DRAW METHODS */
    public int height() {
        return height(getRoot());
    }

    private int height(Node x) {
        if (x == null) {
            return -1;
        }

        return 1 + Math.max(height(x.left), height(x.right));
    }

    /**
     * @return the root
     */
    public Node getRoot() {
        return root;
    }

    public void recursiveTurnNodeToFalse() {
        recursiveTurnNodeToFalse(this.root);
    }

    private void recursiveTurnNodeToFalse(Node n) {
        if (n != null) {
            recursiveTurnNodeToFalse(n.left);
            if (n.isNew) {
                n.isNew = false;
                return;
            }
            recursiveTurnNodeToFalse(n.right);
        }
    }

    public void computeNodePositions() {
        int depth = 1;
        inorder_traversal(getRoot(), depth);
    }

//traverses tree and computes x,y position of each node, stores it in the node
    public void inorder_traversal(Node n, int depth) {
        if (n != null) {
            inorder_traversal(n.left, depth + 1); //add 1 to depth (y coordinate) 
            n.xpos = totalnodes++; //x coord is node number in inorder traversal
            n.ypos = depth; // mark y coord as depth
            inorder_traversal(n.right, depth + 1);
        }
    }

}
