/*
Rohan Bendapudi - 1/21/23
 */

import java.util.ArrayList;
import java.util.Arrays;


public class AStarTwo
{

  public static void main(String[] args)
  {
    //create the state for the A Star
    int[][] state = new int[3][3];
    state[0][0] = 8;
    state[0][1] = 3;
    state[0][2] = 2;
    state[1][0] = 4;
    state[1][1] = 7;
    state[1][2] = 1;
    state[2][0] = 0;
    state[2][1] = 5;
    state[2][2] = 6;

    //System.out.print(calculateAnotherManhattanDistance(state));
    AStarTree solutionTree = new AStarTree(state);
    
    if (solutionTree.root.isSolvable())//checks to see if the solution is solvable 
    {
      solutionTree.initializeTree();
    }
    else
    {
      System.out.println("is not solvable");
    }
    

    //System.out.print("The total manhattan distance is " + calculateManhattanDistance(state));
  }
}

class AStarTree
{
  //declaring attributes
  public TreeNode root = null;
  public ArrayList<TreeNode> pathsVisited = new ArrayList<>();
  public ArrayList<TreeNode> leavesList = new ArrayList<>();

  // blank constructor
  public AStarTree()
  {
    this.root = null;
  }

  public void swapList(ArrayList<TreeNode> objCandidates, int best, int replace)
  {
    TreeNode tempNode = objCandidates.get(best);
    objCandidates.set(best, objCandidates.get(replace));
    objCandidates.set(replace, tempNode);
  }

  //overloaded constructor
  public AStarTree(int[][] state)
  {
    int rowLength = state.length;
    TreeNode objTreeNode = new TreeNode(rowLength, null, 0, state, -1, -1);
    this.root = objTreeNode;
  }

  public void initializeTree()
  {
    // declares initial variables
    System.out.println("Hello");
    TreeNode selectedState = new TreeNode();
    TreeNode goalNode = new TreeNode();
    boolean goalFound = false;

    if (this.root.hValue == 0)//if the root value itself is alr at the goal --> state it has been found
    {
      System.out.println("solution has been found");
      goalFound = true;
    }
    else //otherwise begin adding the next node
    {
      pathsVisited.add(root);
      selectedState = root;
    }

    while (goalFound == false)//until the goal node is reached
    {
      //declare all necessary data structures
      ArrayList<TreeNode> objCandidates = new ArrayList<>();
      ArrayList<TreeNode> trueCandidates = new ArrayList<>();
      objCandidates = selectedState.generateNodes();
      trueCandidates = optimizeCandidates(objCandidates);
      leavesList.addAll(trueCandidates);
      leavesList = root.sortNodes(leavesList);//sorts all the leaves to find the cheapest path/ 

      /*if (leavesList.isEmpty()==true)
      {
        leavesList.addAll(trueCandidates);
      }
      else
      {
        //System.out.println("yes");
        for (int i = 0; i < trueCandidates.size(); i++)
        {
          for (int j = 0; j < leavesList.size(); j++)
          {

            if (trueCandidates.get(i).fValue < leavesList.get(j).fValue)
            {
              leavesList.add(j, trueCandidates.get(i));
              j = leavesList.size();
            }
          }
        }
      }*/
      

      //System.out.println(leavesList);
      //chooses the first node as the selected state
      selectedState = leavesList.get(0);

      if (selectedState.hValue == 0)//once the manhatan distance is zero --> the solution node has been found
      {
        goalFound = true;
        goalNode = selectedState;
        System.out.println("solution has been found " + goalNode);
      }
      else//moves the current node from the leaves list and makes it path visited
      {
        leavesList.remove(0);
        pathsVisited.add(selectedState);
      }
    }
    findPath(goalNode, "");

  }

  //recursion to find the path and keep on adding to the string until it reaches a null pointer (hence the root)
  public void findPath(TreeNode node, String moves)
  {
    if (node.parentNode == null)
    {
      System.out.println(moves);
    }
    else
    {
      findPath(node.parentNode, node.associatedMove + ", " + moves);
    }
  }

  public ArrayList<TreeNode> optimizeCandidates(ArrayList<TreeNode> objCandidates)
  {
    ArrayList<TreeNode> trueCandidates = new ArrayList<>();//the final candidates that haven't been visited
    
    for (int i = 0; i < objCandidates.size(); i++)//compares values between available tree nodes and path visited
    {
      boolean inVisited = false;

      for (int j = 0; j < pathsVisited.size(); j++)
      {
        // System.out.println("yo");
        //if state is already in paths visited --> doesn't use that state as a candidate
        if ((Arrays.deepEquals(objCandidates.get(i).state, pathsVisited.get(j).state)))
        {
          inVisited = true;
          //System.out.println(inVisited);
        }
      }
      
      if (inVisited == false)//adds state as a candidate if it is a new path
      {
        trueCandidates.add(objCandidates.get(i));
      }
    }
    
    return trueCandidates;
  }
}

//class for each node on the AStarTree
class TreeNode
{

  public int COLUMN_NUMBER;
  public int[][] state;
  public int previousRow;
  public int previousColumn;
  public int gValue;
  public int hValue;
  public int fValue;
  public String associatedMove;
  public TreeNode parentNode;

  //basic tree node
  public TreeNode()
  {
    this.COLUMN_NUMBER = 3;
    this.gValue = 0;
    this.hValue = Integer.MAX_VALUE;
    this.fValue = Integer.MAX_VALUE;
    this.state = new int[COLUMN_NUMBER][COLUMN_NUMBER];
  }

  //overloaded tree node
  public TreeNode(int COLUMN_NUMBER, TreeNode parentNode, int parentGValue, int[][] state, int previousRow, int previousColumn)
  {
    this.COLUMN_NUMBER = COLUMN_NUMBER;
    this.state = state;
    this.hValue = this.calculateManhattanDistance(state);
    this.gValue = parentGValue + 1;
    this.fValue = hValue + gValue;
    this.parentNode = parentNode;
    //this.childrenNodes = generateNodes(state, previousColumn, previousRow);//may continually create a tree in constructor
    this.previousRow = previousRow;
    this.previousColumn = previousColumn;
  }

  //generate children nodes
  public ArrayList<TreeNode> generateNodes()
  {
    //System.out.println(10);
    ArrayList<TreeNode> objCandidates = new ArrayList<>();

    // go through each member of the state until the blank is found
    for (int i = 0; i < state.length; i++)
    {
      //find blank in specific area
      for (int j = 0; j < state.length; j++)
      {
        if (state[i][j] == 0)
        {
          objCandidates = findCandidates(state, previousRow, previousColumn, i, j);//finds possible candidates
          j = state.length;
        }
      }
    }

    objCandidates = sortNodes(objCandidates);

    return objCandidates;
  }

  //expands the childrenNodes to find all the possible candidates for the next movei thi
  public ArrayList<TreeNode> findCandidates(int[][] state, int previousRow, int previousColumn, int currentRow, int currentColumn)
  {
    // System.out.println("This is the current node");
    // System.out.println(this.calculateManhattanDistance(state));
    ArrayList<TreeNode> objCandidates = new ArrayList<>();

    int leftColumn = currentColumn - 1;    // sets up possible movements for if statements
    int rightColumn = currentColumn + 1;
    int upRow = currentRow - 1;
    int downRow = currentRow + 1;
    int[][] leftState = new int[state.length][state.length];
    int[][] rightState = new int[state.length][state.length];
    int[][] upState = new int[state.length][state.length];
    int[][] downState = new int[state.length][state.length];

    // left is not a possible move
    if (leftColumn < 0 /*|| (leftColumn == this.previousColumn & currentRow == this.previousRow)*/)
    {
    //System.out.println("Left is not a possible move");
    }
    else // if it is a possible movie
    {
      //creating a new state where the left column is switched
      leftState = copyArray(this.state);
      swapColumns(leftState, currentColumn, currentRow, leftColumn);
      //System.out.println("This is the left move");
      TreeNode objLeftNode = new TreeNode(this.COLUMN_NUMBER, this, this.gValue, leftState, currentRow, currentColumn);
      objLeftNode.associatedMove = "Left";

      //add new candidate w left node
      objCandidates.add(objLeftNode);

    //System.out.println("Left is a possible move");
    }
    // up is not a possible move
    if (upRow < 0 /*|| upRow >= this.COLUMN_NUMBER || (currentColumn == this.previousColumn & upRow == this.previousRow)*/)
    {
    //System.out.println("Up is not a possible move");
    }
    else // if it is a possible movie
    {
      //creating a new state where the up row is switched
      upState = copyArray(this.state);
      swapRowCells(upState, upRow, currentRow, currentColumn);
      //System.out.println("This is the up move");
      TreeNode objUpNode = new TreeNode(this.COLUMN_NUMBER, this, this.gValue, upState, currentRow, currentColumn);
      objUpNode.associatedMove = "Up";

      //add to candidates
      objCandidates.add(objUpNode);

    //System.out.println("Up is a possible move");
    }
    // right is not a possible move
    if (rightColumn >= this.COLUMN_NUMBER /*|| (rightColumn == this.previousColumn && currentRow == this.previousRow)*/)
    {
      //System.out.println(rightColumn);
      //System.out.println("Right is not a possible move");
    }
    else
    {
      //creating a new state where the right column is switched
      rightState = copyArray(this.state);
      swapColumns(rightState, currentColumn, currentRow, rightColumn);
      //System.out.println("This is the right move");
      TreeNode objRightNode = new TreeNode(this.COLUMN_NUMBER, this, this.gValue, rightState, currentRow, currentColumn);
      objRightNode.associatedMove = "Right";

      //add new candidate w right node
      objCandidates.add(objRightNode);

    //System.out.println("Right is a possible move");
    }
    // down is not a possible move
    if (downRow >= this.COLUMN_NUMBER /*|| downRow < 0 || (currentColumn == this.previousColumn & downRow == this.previousRow)*/)
    {
    //System.out.println("Down is not a possible move");
    }
    else // if it is a possible movie
    {
      //creating a new state where the up row is switched
      downState = copyArray(this.state);
      swapRowCells(downState, downRow, currentRow, currentColumn);
      //      System.out.println("This is the down move");
      TreeNode objDownNode = new TreeNode(this.COLUMN_NUMBER, this, this.gValue, downState, currentRow, currentColumn);
      objDownNode.associatedMove = "Down";

      //add to candidates
      objCandidates.add(objDownNode);

      //System.out.println("Down is a possible move");
    }
    //return possible candidates
    return objCandidates;
  }

  //swaps cells of separate rows at specific indices
  private void swapRowCells(int[][] twoD, int rowIndexOne, int rowIndexTwo, int c)
  {
    //uses a temp value to swap corresponding indices of two rows
    int temp = twoD[rowIndexOne][c];
    twoD[rowIndexOne][c] = twoD[rowIndexTwo][c];
    twoD[rowIndexTwo][c] = temp;
  }
  //swaps columns
  public void swapColumns(int[][] newState, int currentColumn, int currentRow, int newColumn)
  {
    int temp = newState[currentRow][currentColumn];
    newState[currentRow][currentColumn] = newState[currentRow][newColumn];
    newState[currentRow][newColumn] = temp;
  }

  //checks if the puzzle is solvable
  public boolean isSolvable()
  {
    ArrayList<Integer> oneDList = new ArrayList<>();
    int rowFromUp = -1;
    int inversionCount = 0;
    System.out.println("yo");

    //adds all elements to a one day array list
    for (int i = 0; i < state.length; i++)
    {
      for (int j = 0; j < state.length; j++)
      {
        oneDList.add(state[i][j]);

        //finds where zero is from the bottom
        if (state[i][j] == 0)
        {
          rowFromUp = (state.length) - i;
        }
      }
    }

    for (int i = 0; i < oneDList.size() - 1; i++)//determines if it is solvable based on inversion count for odd 2D arrays
    {
      if (oneDList.get(i) > oneDList.get(i + 1))
      {
        inversionCount++;
      }
    }
    if (oneDList.size() % 2 == 1 & inversionCount % 2 == 0)//determines if it is solvable based on inversion count for even 2d arrays
    {
      return true;
    }
    else if ((oneDList.size() % 2 == 0 & rowFromUp % 2 == 0 & inversionCount % 2 == 1) || (oneDList.size() % 2 == 0 & rowFromUp % 2 == 1 & inversionCount % 2 == 0))
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  //calculate manhattan distance for N by N grid
  public int calculateManhattanDistance(int[][] state)
  {
    //initialize local manhattan distance
    int manhattanDistance = 0;

    for (int i = 0; i < state.length; i++)
    {
      int localManhattanDistance = 0;

      for (int j = 0; j < state.length; j++)
      {
        int slotValue = state[i][j];
        //System.out.print(slotValue);
        int xGoal = 0;
        int yGoal = 0;

        // calculates where the goal should be
        xGoal = slotValue % 3;
        yGoal = slotValue / 3;

        int xDistance = Math.abs(j - xGoal);
        int yDistance = Math.abs(i - yGoal);

        localManhattanDistance = xDistance + yDistance;
        manhattanDistance = localManhattanDistance + manhattanDistance;
      }
      //System.out.println("");
    }
    this.hValue = manhattanDistance;

    return manhattanDistance;
  }

  public ArrayList<TreeNode> sortNodes(ArrayList<TreeNode> objCandidates) //uses selection sort to sort all nodes based on f measure
  {
    int best;

    for (int k = 0; k < objCandidates.size(); k++)
    {
      best = k;

      for (int q = k + 1; q < objCandidates.size() - 1; q++)//if the fValue at q is less than the fValue at best--> sorts the nodes
      {
        if (objCandidates.get(q).fValue < objCandidates.get(best).fValue)
        {
          best = q;
        }
      }
      swapList(objCandidates, best, k);//swaps nodes

    }

    return objCandidates;
  }

  //swaps two nodes using a temp
  public void swapList(ArrayList<TreeNode> objCandidates, int best, int replace)
  {
    TreeNode tempNode = objCandidates.get(best);
    objCandidates.set(best, objCandidates.get(replace));
    objCandidates.set(replace, tempNode);
  }

  //copy array
  public int[][] copyArray(int[][] state)
  {
    int[][] copyState = new int[state.length][state.length];
    for (int i = 0; i < copyState.length; i++)
    {
      for (int j = 0; j < copyState.length; j++)
      {
        copyState[i][j] = state[i][j];
        //System.out.print(copyState[j][j]);
      }
        //System.out.println("");
    }
    return copyState;
  }
}
