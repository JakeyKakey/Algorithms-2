package cs21120.assignment.solution;

import cs21120.assignment.CompetitionManager;
import cs21120.assignment.IBinaryTree;
import cs21120.assignment.IManager;
import cs21120.assignment.Match;
import cs21120.assignment.NoNextMatchException;
import java.util.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
 
public class BTSingleElimnc3 implements IManager {
   
 
    public static class Node implements IBinaryTree { //Initialising the Node
        String player; //Name of team
 
        Node leftChild; //left child node
        Node rightChild; //right child node
        Node parent; // parent node
       
        int position; //position in competition (level)
        int score; // score held by that node for that team
       
 
        @Override
        public IBinaryTree getLeft() { //returns the left child unless there is no node
            if (leftChild != null) {
                return leftChild;
            } else {
                return null;
            }
        }
 
        @Override
        public IBinaryTree getRight() { //returns the right child unless there is no node
            if (rightChild != null) {
                return rightChild;
            } else {
                return null;
            }
        }
 
        @Override
        public String getPlayer() { //return the teams name
            return this.player;
        }
 
        @Override
        public int getScore() { //returns the score
           
            return this.score;
        }
    }
   
 
    IManager manager;
    static Node root = new Node(); //root node
    Stack<Node> playerStack = new Stack<Node>();
 
    public static void main(String[] args) throws FileNotFoundException {
        BTSingleElimnc3 main = new BTSingleElimnc3();
        CompetitionManager compManager = new CompetitionManager(main);
        String fileLocation = "C:\\Users\\Jakey\\Dropbox\\Computer Science\\Algo 2\\cs21120\\assignment\\solution\\teams.txt";
        compManager.runCompetition(fileLocation);// Starts the competition
    }
   
    public void buildTree(Node focusNode, ArrayList<String> players) {
        ArrayList<String> tempList1 = new ArrayList<String>();
        ArrayList<String> tempList2 = new ArrayList<String>();
        if (players.size() > 1) {
            for (int i = 0; i < players.size(); i++) {
                if(i<(players.size()/2)){
                    tempList1.add(players.get(i));
                } else {
                    tempList2.add(players.get(i));
                }
            }
           
           
            focusNode.leftChild = new Node();
            focusNode.leftChild.parent = focusNode;
            focusNode.rightChild = new Node();
            focusNode.rightChild.parent = focusNode;
 
            buildTree(focusNode.leftChild, tempList1);
            buildTree(focusNode.rightChild, tempList2);
           
        } else {
            focusNode.player = players.get(0);
        }
       
    }
 
    @Override
    public void setPlayers(ArrayList<String> players) {
        Node node = root;
        buildTree(node, players); //passes the root and the arrayList into buildTree7
        setUpMatches(root);
    }
 
    @Override
    public boolean hasNextMatch() {
        if(root.player != null){
        return false;
        } else {
            return true;
        }
    }
 
    @Override
    public Match nextMatch() throws NoNextMatchException {
        Node tempNode;
 
        tempNode = playerStack.peek();
        Match match = new Match(tempNode.leftChild.player, tempNode.rightChild.player);
        return match;
    }
   
    private void setUpMatches(Node rootNode){
        Queue<Node> tempQueue =  new LinkedList<>();
        Node tempNode = null;
        int count = 0;
       
        tempQueue.add(rootNode);
 
        while(!tempQueue.isEmpty()){
            tempNode = tempQueue.remove();
            tempNode.position = count;
            count++;            
 
            if(tempNode.getLeft() != null || tempNode.getRight() != null){
                tempQueue.add(tempNode.leftChild);
                tempQueue.add(tempNode.rightChild);
               
                playerStack.push(tempNode);
               
            }
        }
    }
   
    @Override
    public void setMatchScore(int p1, int p2) {
        Node tempNode = playerStack.pop();
       
        tempNode.leftChild.score = p2;
        tempNode.rightChild.score = p1;
       
        if(tempNode.leftChild.score < tempNode.rightChild.score){
            tempNode.player = tempNode.rightChild.player;
        } else {
            tempNode.player = tempNode.leftChild.player;
        }
       
    }
   
   
 
    @Override
    public String getPosition(int n) {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public IBinaryTree getCompetitionTree() {
        return root;
    }
}