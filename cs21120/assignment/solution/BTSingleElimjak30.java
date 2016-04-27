package cs21120.assignment.solution;

import cs21120.assignment.CompetitionManager;
import cs21120.assignment.IBinaryTree;
import cs21120.assignment.IManager;
import cs21120.assignment.Match;
import cs21120.assignment.NoNextMatchException;
import java.util.*;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Main single program class, implementing IManager with a TreeNode inner class that implements IBinary
 *
 * @author Jakey
 */
public class BTSingleElimjak30 implements IManager{

 /*
    public static void main(String [ ] args){
        
        BTSingleElimjak30 game = new BTSingleElimjak30();
        CompetitionManager cm = new CompetitionManager(game);
            
                    try {
                        cm.runCompetition(args[0]);                           
                    } catch (Exception e) {
                        System.out.println("File Not Found: " + e);
            }      
    }
    
// */
    
        TreeNode root;
        Deque stack;
        
        /**
         * Constructor initialises the first TreeNode and 'stack' Deque as the 
         * only two structures actually needing to stay permanent.
         * 
         * Deque picked instead of Stack as they both do same things, 
         * but Deque is newer and supposedly more efficient.
         */
        
        BTSingleElimjak30(){
        
               root = new TreeNode();
               stack = new LinkedList<>();

        }
        
        /**
         * Takes in the root node and performs a breadth-first traverse 
         * across the tree in order to determine match order. 
         * 
         * Also keeps a counter of each object traversed and assigns it 
         * to the object as position, to avoid having to traverse the tree twice.
         * 
         * @param rootN The top node of the tree.
         */
        
        private void setUpMatches(TreeNode rootN){

            Queue queue =  new LinkedList<>();
            TreeNode temp;
            int count = 0;

            queue.add(rootN);

            while(!queue.isEmpty()){

                temp = (TreeNode) queue.remove();
                temp.setPos(count); count++;            

                if(temp.getLeft()!= null || temp.getRight() != null){
                    queue.add(temp.getLeft());
                    queue.add(temp.getRight());
                    stack.push(temp);
                }
            }
        }

    /**
     * 
     * Creates binary tree by splitting the list of players in half and passing
     * it down recursively into child nodes until the player list is 1. 
     * Then sets player to the node.
     * 
     * @param node Current node in the tree.
     * @param p Current list or sublist of players.
     */    
        
        private void makeTree(TreeNode node, ArrayList<String> p){

            if (p.size() > 1){

                ArrayList<String> tempA = new ArrayList<>(p.subList(0, p.size()/2) );
                ArrayList<String> tempB = new ArrayList<>(p.subList(p.size()/2 , p.size() ));

                node.setL(new TreeNode());
                node.setR(new TreeNode());

                makeTree((TreeNode)node.getLeft(), tempA);
                makeTree((TreeNode)node.getRight(), tempB);
            }

            else if (p.size() == 1){
                node.setName(p.get(0));
            }
        }

        /**
         * 
         * Takes the list of players from competition manager and uses that
         * to generate a tree via {@link BTSingleElimjak30#makeTree}
         * and set the match order via (@link BTSingleElimjak30#setUpMatches)
         * 
         * @param players List of players provided by competition manager.
         */
        
        @Override
        public void setPlayers(ArrayList<String> players) {
            makeTree(root,players);
            setUpMatches(root);
        }

        /**
         * 
         * Check whether there is another match.
         * 
         * @return Whether the stack is empty or not.
         */
        
        @Override
        public boolean hasNextMatch() {
            return !stack.isEmpty();
     }

    /**
     * 
     * Returns the next Match to play.
     * 
     * Stack uses peek as opposed to pop to avoid removing the object and 
     * losing it permanently if Competition Manager didn't account for draws. 
     * 
     * This also saves having to store current node in a temporary global
     * variable since it's not removed from the stack while still needed later 
     * in {@link BTSingleElimjak30#setMatchScore}.
     * 
     * 
     * @return Match object containing the details of next Match.
     * @throws NoNextMatchException Self explanatory.
     */
        
        @Override
        public Match nextMatch() throws NoNextMatchException {
           
            Match m;
            TreeNode temp;
 
            temp = (TreeNode) stack.peekFirst();
            m = new Match(temp.getLeft().getPlayer(), temp.getRight().getPlayer());

            return m;
          
        }

        /**
         * 
         * Gets current match node and player child nodes, sets the scores for players,
         * compares them and copies the winner into the current match node.
         * 
         * @param p1 Player 1 Score
         * @param p2 Player 2 Score
         */
        
        @Override
        public void setMatchScore(int p1, int p2) {

               if (p1 == p2){ // the interface specifies no draws are allowed, but
                   return;    // that's already accounted for in CompManager.java 
               }              // ~ doing this just in case

               TreeNode temp = (TreeNode) stack.pollFirst();
               TreeNode tempL = (TreeNode) temp.getLeft();
               TreeNode tempR = (TreeNode) temp.getRight();

               tempL.setScore(p1);
               tempR.setScore(p2);

               if (p1 > p2){
                temp.setName(tempL.getPlayer());
               } else {
                  temp.setName(tempR.getPlayer());
               }
        }

     /**
      * 
      * Gets name of the player/node at specified position by iterating through 
      * the tree breadth first and comparing node position numbers with n provided.
      * 
      * I don't believe there is any notably more efficient way of doing this.
      * 
      * @param n Position number
      * @return Name of the player
      */   
        
        @Override
        public String getPosition(int n) {

            Queue queue =  new LinkedList<>();
            TreeNode temp;

            if(hasNextMatch()){
                return null;
            }

            queue.add(root);

            while(!queue.isEmpty()){

                temp = (TreeNode) queue.remove();

                if(temp.getPos() == n){
                    return temp.getPlayer();
                }

                if(temp.getLeft()!= null || temp.getRight() != null){
                    queue.add(temp.getLeft());
                    queue.add(temp.getRight());
                }
            }

            return null;
        }

        /**
         * Returns the tree via the root node.
         * 
         * @return Top node of the tree. 
         */
        
        @Override
        public IBinaryTree getCompetitionTree() {
            return root;    
        }
        
        /**
         * A node class for the Tree. Holds player name, player score,
         * node position and left/right child nodes.
         *
         * All the methods consist of setters and getters.
         * 
         */
    
    private static class TreeNode implements IBinaryTree{

       private String tName;
       private int tScore, tposition;
       TreeNode leftN, rightN;
        
       /**
        * Sets position of the node.
        * 
        * @param pos Position of the node.
        */
       
       public void setPos(int pos){
           tposition = pos;
       }
       
       /**
        * Gets position of the node.
        * 
        * @return Position of the node.
        */
       
       public int getPos(){
           return tposition;
       }
       
       /**
        * Sets the name of the player in the node.
        * 
        * @param n Name of the player in the node.
        */
       
        public void setName(String n){
            tName = n;
        }
        
        /**
         * 
         * Sets score in the node.
         * 
         * @param s Score
         */
        
        public void setScore(int s){
         tScore = s;  
        }
        
        /**
         * 
         * Sets left child of the node;
         * 
         * @param L Node
         */
        
        public void setL(TreeNode L){
         leftN = L;   
        }
        
        /**
         * 
         * Sets right child of the node;
         * 
         * @param R Node
         */
        
        public void setR(TreeNode R){
         rightN = R;   
        }
        
        @Override
        public IBinaryTree getLeft() {
            if(leftN != null){
                return leftN;
            } else return null;
        }

        @Override
        public IBinaryTree getRight() {
            if(rightN != null) {
                return rightN;   
            } else return null;
        }
        
        @Override
        public String getPlayer() {
            return tName;
        }

        @Override
        public int getScore() {
            return tScore;
        }
        
        
    }
    
}