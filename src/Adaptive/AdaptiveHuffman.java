package Adaptive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdaptiveHuffman {

    private Node nytNode=new Node("NYT", 0);
    private Node root;
    private char[] message;
    private char[] code=new char[0];
    private String tempCode = "";
    private int totalNodes = 0; //keeps track of the inorder number for horiz. scaling
    private ArrayList<String> friendlyOutput = new ArrayList<>();

    public AdaptiveHuffman(char[] message) {
        this();
        this.message = message;
    }

    public AdaptiveHuffman() {
        nytNode.parent = null;
        root = nytNode;
    }

    int getTotalNodes() {
        return totalNodes;
    }

    ArrayList<String> getFriendlyOutput() {
        return friendlyOutput;
    }

    Node getRoot(){
        return root;
    }

    ArrayList<String> encode(String message) {
        nytNode.parent = null;
        root = nytNode;
        this.message=message.toCharArray();
        ArrayList<String> result = new ArrayList<>();
        result.add("0");
        StringBuilder sb=new StringBuilder();
        sb.append("0");
        for (char c : message.toCharArray()) {
            String currentCode=getCode(c);
            result.add(currentCode);
            sb.append(currentCode);
            this.friendlyOutput.add(getCurrentLetterCodeFriendly(c));
            updateTree(c);
        }
        this.code=sb.toString().toCharArray();
        return result;
    }

    private void updateTree(char c) {
        //make the last inserted node as old
//        recursiveTurnNodeToFalse();
        //check if the char exists or not and returns a node
        Node currentNode = getNode(c);
        //update nodes until the parent
        updateNodes(currentNode);
    }

    private Node getNode(char c) {

        //char doesn't exists, create a new node
        if (!alreadyExists(c)) {
            Node innerNode = new Node(null, 1);
            Node newNode = new Node(String.valueOf(c), 1);
            //make the newly inserted node as new
            newNode.isNew = true;

            innerNode.left = nytNode;
            innerNode.right = newNode;
            innerNode.parent = nytNode.parent;
            if (nytNode.parent == null) {
                //first node
                root = innerNode;

            } else {
                nytNode.parent.left = innerNode;
            }
            nytNode.parent = innerNode;
            newNode.parent = innerNode;

            return innerNode.parent;
        }
        //char exists, get the existing node
        return findNode(c);
    }

    private void updateNodes(Node node) {
        if (node == null) return;
        //bignode is the tallest node with the same frequency as the node
        Node bigNode = findBigNode(node.frequency);
        //swaps node and bigNode if are not the same or doesn't have parent/child relationship
        if (bigNode!=null && node != bigNode && node.parent != bigNode && bigNode.parent != node) {
            swapNode(node, bigNode);
        }
        node.frequency++;
        updateNodes(node.parent);
    }


    private List<Node> traverseTree(){
        List<Node> nodes=new ArrayList<>();
        traverseTree(this.root,nodes);
        return nodes;

    }

    private void traverseTree(Node node,List<Node> nodes){
        if(node==null){
            return;
        }
        nodes.add(node);
        if(node.left!=null)
            traverseTree(node.left,nodes);
        if(node.right!=null)
            traverseTree(node.right,nodes);

    }

    private boolean alreadyExists(char temp) {
        for(Node node:traverseTree()){
            if(node.letter!=null && node.letter.equals(""+temp)){
                return true;
            }
        }
        return false;
    }

    private String getCode(char c) {
        tempCode = "";

        getCurrentLetterCode(this.root, String.valueOf(c), "");
        String result = tempCode;
        if (result.equals("")) {
            getCurrentLetterCode(this.root, "NYT", "");
            result = "" + tempCode;
            result += toBinary(getAscii(c));
        }
        return result;
    }

    private void getCurrentLetterCode(Node node, String letter, String code) {
        //Reach a leaf
        if (node.left == null && node.right == null) {
            if (node.letter != null && node.letter.equals(letter)) {
                tempCode = code;
            }
        } else {
            if (node.left != null) {
                getCurrentLetterCode(node.left, letter, code + "0");
            }
            if (node.right != null) {
                getCurrentLetterCode(node.right, letter, code + "1");
            }
        }
    }

    private String getCurrentLetterCodeFriendly(char c) {
        tempCode = "";
        StringBuilder result = new StringBuilder();
        getCurrentLetterCode(this.root, String.valueOf(c), "");
        if (result.toString().equals("")) {
            getCurrentLetterCode(this.root, "NYT", "");
            result.append(tempCode);
            result.append("'");
            result.append(c);
            result.append("'");
        }
        return result.toString();
    }

    //Find the existing node in the tree
    private Node findNode(char c) {
        String temp = String.valueOf(c);
        for (Node node : traverseTree()) {
            if ((node.letter != null) && node.letter.equals(temp)) {
                return node;
            }
        }
        return null;
    }

    private void swapNode(Node node1, Node node2) {
        //note that node1<node2
        //get both nodes parents
        Node parent1 = node1.parent;
        Node parent2 = node2.parent;

        //Optimization: if nodes are siblings, just change their positions
        if (parent1 == parent2) {
            parent1.left = node2;
            parent1.right = node1;
            return;
        }

        //If the two nodes have different parent node
        //if node1 is left child of parent1
        if (parent1.left == node1) {
            parent1.left = node2;
        }
        //if node1 is right child of parent1
        else {
            parent1.right = node2;
        }

        //if node2 is left child of parent2
        if (parent2.left == node2) {
            parent2.left = node1;
        }
        //if node2 is right child of parent2
        else {
            parent2.right = node1;
        }
        //change parents of both nodes
        node1.parent = parent2;
        node2.parent = parent1;
    }


    private List<Node> levelTraversal(){
        List<Node> nodes=new ArrayList<>();
        int level=1;
        while(levelTraversal(this.root,level,nodes)){
            level++;
        }
        return nodes;
    }

    private boolean levelTraversal(Node root,int level,List<Node> nodes){
        if(root==null)return false;
        if(level==1){
            nodes.add(root);
            return true;
        }
        boolean left=levelTraversal(root.left,level-1,nodes);
        boolean right=levelTraversal(root.right,level-1,nodes);
        return  left || right;
    }

    private Node findBigNode(int frequency) {
        for(Node node:levelTraversal()){
            if(node.frequency==frequency){
                return node;
            }
        }
        return null;
    }

    String decode(String code) {
        this.code=code.toCharArray();
        String result = "";
        String symbol;

        nytNode.parent = null;
        root = nytNode;

        //The first symbol is of course NEW, so find it by ASCII
        symbol = getByAsc(0);
        result += symbol;
        updateTree(symbol.charAt(0));
        Node p = this.root;

        for (int i = 9; i < code.length(); i++) {
            char temp = code.charAt(i);

            if (temp == '0') {
                p = p.left;
            } else {
                p = p.right;
            }

            symbol = visit(p);
            //If reach a leaf
            if (symbol != null) {
                if (symbol.equals("NYT")) {
                    symbol = getByAsc(i);
                    i += 8;
                }
                result = result.concat(symbol);
                updateTree(symbol.charAt(0));
                p = this.root;
            }
        }

        return result;
    }

    //Get the symbol using the next 8 bit as a ASCII code.
    private String getByAsc(int index) {
        int asc = 0;
        for (int i = 7; i >= 0; i--) {
            int tempInt = this.code[++index] - 48;
            asc += tempInt * Math.pow(2, i);
        }
        char ret = (char) asc;
        return String.valueOf(ret);
    }

    private String visit(Node p) {
        if (p.letter != null) {
            //The symbol has been found.
            return p.letter;
        }
        return null;
    }


    private int getAscii(char c) {
        return (int) c;
    }

    private String toBinary(int decimal) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (decimal % 2 == 0) {
                result.insert(0, "0");
            } else {
                result.insert(0, "1");
            }
            decimal /= 2;
        }
        return result.toString();
    }

    private void calCompRate(String text, ArrayList<String> code) {

        int preNum = 8 * text.length();
        int postNum = 0;
        for (String s : code) {
            postNum += s.length();
        }

        double compRate = (double)preNum / postNum;
        System.out.println("If simply using ASCII code, there are in total " +  preNum + " bits.");
        System.out.println("If using huffman coding, there are in total " +  postNum + " bits.");
        System.out.println("The compress rate is: " + compRate);
    }

    static String catStr(ArrayList<String> l) {
        StringBuilder result = new StringBuilder();
        for (String s : l) {
            result.append(s);
        }
        return result.toString();
    }

    private void getStatistics() {
        ArrayList<Symbol> symbolList = new ArrayList<>();
        preOrder(this.root, symbolList);
        Collections.sort(symbolList);
        calRange(symbolList);
        FileHandler.writeSymbolToFile(symbolList);
    }

    private void preOrder(Node node, ArrayList<Symbol> symbolList) {
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
        int total = message.length;
        double low = 0;

        for (Symbol tempSymbol : symbolList) {
            tempSymbol.probability = tempSymbol.frequency / (double) total;
            tempSymbol.low = low;
            tempSymbol.high = low + tempSymbol.probability;
            low += tempSymbol.probability;
        }
        System.out.println("low=" + low);//It should be 1.
    }

    public static void main(String[] args){
        testEncode();
        testDecode();
    }

    private static void testDecode() {
        String code = FileHandler.readFile("compressed.txt", false);
//        AdaptiveHuffman ah = new AdaptiveHuffman(code.toCharArray());
        AdaptiveHuffman ah = new AdaptiveHuffman();
//        ah.code=code.toCharArray();
        String result = ah.decode(code);
        System.out.println("width"+ah.width());
        FileHandler.writeFile("output.txt", result, false);
    }

    private static void testEncode() {
        String text = FileHandler.readFile("teste.txt", true);
        text = text.substring(0, text.length() - 1);
//        AdaptiveHuffman ah = new AdaptiveHuffman(text.toCharArray());
        AdaptiveHuffman ah = new AdaptiveHuffman();
        ArrayList<String> code = ah.encode(text);
        FileHandler.writeFile("compressed.txt", catStr(code), true);
        FileHandler.writeFile("compressedFriendly.txt", catStr(ah.friendlyOutput), true);
        ah.getStatistics();
        System.out.println("width"+ah.width());
        ah.calCompRate(text, code);
    }

    /* DRAW METHODS */
    int height() {
        return height(this.root);
    }

    private int height(Node x) {
        if (x == null) {
            return -1;
        }

        return 1 + Math.max(height(x.left), height(x.right));
    }

    void computeNodePositions() {
        int depth = 1;
        inorder_traversal(this.root, depth);
    }

    private int width(){
        List<Node> nodes=new ArrayList<>();
        int maxWidth=1;
        int level=1;
        while(levelTraversal(this.root,level,nodes)){
            if(nodes.size()>maxWidth){
                maxWidth=nodes.size();
            }
            level++;
            nodes=new ArrayList<>();
        }
        return maxWidth;

    }

    //traverses tree and computes x,y position of each node, stores it in the node
    private void inorder_traversal(Node n, int depth) {
        if (n != null) {
            inorder_traversal(n.left, depth + 1); //add 1 to depth (y coordinate) 
            n.xpos = totalNodes++; //x coord is node number in inorder traversal
            n.ypos = depth; // mark y coord as depth
            inorder_traversal(n.right, depth + 1);
        }
    }

}
