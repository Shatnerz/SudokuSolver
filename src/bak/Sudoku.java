import java.util.*;
import java.io.*;


class Sudoku
{
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For 
     * a standard Sudoku puzzle, SIZE is 3 and N is 9. */
    int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0. */
    int Grid[][];
    
    //myGrid contains all the numbers as classes the store the possible values and
    //a list of all other boxes they influence
    Box[][] myGrid;
    
	//List of unsolved Boxes
	List<Box> unsolved = new ArrayList<>();
	//List of solved Boxes
	List<Box> solved = new ArrayList<>();
	//List of all units(ie groups) on the grid
	List<List> unitList = new ArrayList<>();
    
	//Remember N=SIZE*SIZE
	
    //Class to represent each box which contains a number
    class Box {
    	
		//values is a List of all possible integers a Box could be
		//values only contains one number when the box is solved
    	//Set values = new HashSet(); //Set is probably not optimal
    	List<Integer> values = new ArrayList<>();
    	
    	//peers is an array of all Boxes which influence the possible
    	//			value of the Box
    	Box[] peers; //could try Set (avoids duplicates)
    	
    	//list of units a box belongs to
    	//should always be 3 - a row, a column, and a square
    	List<List> units = new ArrayList<>();
    	
    	//maybe store the coordinates of the Box on the grid
    	
    	//Constructor
    	Box(int value) {
    		//Set value if know, otherwise set values to all possible options
    		if (value != 0) {values.add(value);}
    		else {
    			for (int i=1; i<=N; i++) {
    				values.add(i);
    			}
    		}
    		//initialize peers array
    		//to be filled once we create an array of Boxes
    		peers = new Box[(N-1)*2+(SIZE-1)*(SIZE-1)];
    	}
    	
    	void setPeers(int y, int x) {
    		int counter=0;
    		for (int i=0; i<N; i++) {
    			//Add row peers
    			if (i != x) {
    				peers[counter] = myGrid[y][i];
    				counter++;
    			}
    			//Add column peers
    			if (i != y) {
    				peers[counter] = myGrid[i][x];
    				counter++;
    			}
    		}
    		//Add square section peers
    		int X = x/SIZE;
    		int Y = y/SIZE;
    		for (int i=Y*SIZE; i<Y*SIZE+SIZE; i++) {
    			if (i!=y){
    				for (int j=X*SIZE; j<X*SIZE+SIZE; j++) {
    					if (j!=x) {
    						peers[counter] = myGrid[i][j];
    						counter++;
    					}
    				}
    			}
    		}
    		//testing
//    		System.out.println(counter);
//    		System.out.println();
//    		for (int i=0; i<8; i++) {
//    			if (peers[i].values.size()==1) {System.out.print(peers[i] + " ");}
//    		}
//    		System.out.println();
    	}
    	
    	void setUnits() {
    		for (int i = 0; i<unitList.size(); i++) {
    			if (unitList.get(i).contains(this)) {
    				units.add(unitList.get(i));
    			}
    		}
    	}
    	
    	void eliminate(int d) {
    		//remove d from values, if only one value, eliminate that value from peers
    		if (values.size()>1) {
    			values.remove(new Integer(d));
    			//check if anything can be assigned in each unit
    			//checkUnits();
    			//If Box is constrained to 1 value, propagate constraints
    			if (values.size()==1) {
    				int value = values.get(0);
    				this.assign(value);
    			}
    		}
    	}
    	
    	void checkUnits() {
    		//Check units to see if a number has only one available spot
			for (int i=0; i<units.size(); i++) {
				List<Box> unit = units.get(i);
				for (int n=1; n<=N; n++) {
					List<Box> places = new ArrayList<>(); //list of places an integer can go in a unit
					for (int j=0; j<unit.size(); j++) {
						if (unit.get(j).values.size()>1 && unit.get(j).values.contains(new Integer(n))) {
							places.add(unit.get(j));
						}
					}
					if (places.size()==1) {places.get(0).assign(n);}
				}
			}
    	}
    	
    	void eliminateFromPeers(int d) {
    		for (int i=0; i<peers.length; i++) {
				peers[i].eliminate(d);
			}
    	}
    	
    	void assign(int value) {
    		//assign a single value to a Box and then
    		//propagates the constraints
    		values.clear();
    		values.add(value);
    		unsolved.remove(this); //update unsolved list
    		solved.add(this);//update solved list-NOTE: could be causing to eliminateFromPeers twice
			//do we really need to update solved list?
    		eliminateFromPeers(value);
    	}
    	
    	public String toString() {
    		return values.toString();
    	}
    }//End of Box Class

    void updateUnits() {
    	//Update the list of all the units on the grid
    	//Note - could probably combine some of these loops
    	//		but its easy to grasp while separated
    	//Column units
    	for (int x=0; x<N; x++){
    		List<Box> unit = new ArrayList<>();
    		for (int y=0; y<N; y++) {
    			unit.add(myGrid[y][x]);
    		}
    		unitList.add(unit);
    	}
    	//Row units
    	for (int y=0; y<N; y++){
    		List<Box> unit = new ArrayList<>();
    		for (int x=0; x<N; x++) {
    			unit.add(myGrid[y][x]);
    		}
    		unitList.add(unit);
    	}
    	//Square units
    	for (int Y=0; Y<SIZE; Y++) { //Y is coordinate of large square
    		for (int X=0; X<SIZE; X++) {
    			List<Box> unit = new ArrayList<>();
    			for (int i=Y*SIZE; i<Y*SIZE+SIZE; i++) {
    				for (int j=X*SIZE; j<X*SIZE+SIZE; j++) {
    					unit.add(myGrid[i][j]);
    				}
    			}
    			unitList.add(unit);
    		}
    	}
    	//Test results
    	//System.out.println();
    	//for (int i=0; i<9; i++) {
    	//	System.out.print(unitList.get(26).get(i) + " ");
    	//}
    	//System.out.println();
    	//System.out.println(unitList.size());
    }
    
    void CheckUnits() {
		//Check units to see if a number has only one available spot
		for (int i=0; i<unitList.size(); i++) {
			List<Box> unit = unitList.get(i);
			for (int n=1; n<=N; n++) {
				List<Box> places = new ArrayList<>(); //list of places an integer can go in a unit
				for (int j=0; j<unit.size(); j++) {
					if (unit.get(j).values.size()>1 && unit.get(j).values.contains(new Integer(n))) {
						places.add(unit.get(j));
					}
				}
				if (places.size()==1) {places.get(0).assign(n);}
			}
		}
    }
    
    boolean isValid(Box[][] grid) {
    	return true;
    }

    /* The solve() method should remove all the unknown characters ('x') in the Grid
     * and replace them with the numbers from 1-9 that satisfy the Sudoku puzzle. */
    public void solve()
    {    	
    	//Create a duplicate of Grid, but with the Box class
    	//and place solved and unsolved Boxes in separate List
    	myGrid = new Box[N][N];
    	for (int y= 0; y<N; y++){
    		for (int x=0; x<N; x++) {
    			myGrid[y][x] = new Box(Grid[y][x]);
    			if (Grid[y][x] == 0) {unsolved.add(myGrid[y][x]);}
    			else {solved.add(myGrid[y][x]);}
    		}
    	}
    	//Create a list of all the units on the grid
    	updateUnits();

    	//Now assign the peers of each Box - and units box belongs to
    	//Note - I implemented units after peers
    	//	as such I could simplify peer assignment
    	for (int x= 0; x<N; x++){
    		for (int y=0; y<N; y++) {
    			myGrid[y][x].setPeers(y, x);
    			myGrid[y][x].setUnits();
    		}
    	}
    	
    	//apply constraints and remove solved values from possible peer values
    	for (int i=0; i<solved.size(); i++) {
    		Box temp = solved.get(i);
    		temp.eliminateFromPeers(temp.values.get(0));
    	}
    	//Check units if integer can only go in one Box in the unit
    	//Repeat until it solves no additional Boxes
    	int numUnsolved;
    	do {
    		numUnsolved = unsolved.size();
    		CheckUnits();
    	} while (unsolved.size()<numUnsolved);
    	
    	//return to original grid of integers
    	for (int y=0; y<N; y++){
    		for (int x=0; x<N; x++) {
    			if (myGrid[y][x].values.size()==1) {
    				Grid[y][x] = myGrid[y][x].values.get(0);
    			}
    		}
    	}
        print();
        System.out.println();
        
        System.out.println("Solved: " + solved.size());
        System.out.println("Unsolved: " + unsolved.size());
        
//        for (int i=0; i<9; i++) {
//        	System.out.println();
//        	for (int j=0; j<9; j++) {
//        		System.out.print(myGrid[i][j] + ", ");
//        	}
//        }
//        System.out.println();
    }


    /*****************************************************************************/
    /* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE FUNCTIONS BELOW THIS LINE. */
    /*****************************************************************************/
 
    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    public Sudoku( int size )
    {
        SIZE = size;
        N = size*size;

        Grid = new int[N][N];
        for( int i = 0; i < N; i++ ) 
            for( int j = 0; j < N; j++ ) 
                Grid[i][j] = 0;
    }


    /* readInteger is a helper function for the reading of the input file.  It reads
     * words until it finds one that represents an integer. For convenience, it will also
     * recognize the string "x" as equivalent to "0". */
    static int readInteger( InputStream in ) throws Exception
    {
        int result = 0;
        boolean success = false;

        while( !success ) {
            String word = readWord( in );

            try {
                result = Integer.parseInt( word );
                success = true;
            } catch( Exception e ) {
                // Convert 'x' words into 0's
                if( word.compareTo("x") == 0 ) {
                    result = 0;
                    success = true;
                }
                // Ignore all other words that are not integers
            }
        }

        return result;
    }


    /* readWord is a helper function that reads a word separated by white space. */
    static String readWord( InputStream in ) throws Exception
    {
        StringBuffer result = new StringBuffer();
        int currentChar = in.read();
	String whiteSpace = " \t\r\n";
        // Ignore any leading white space
        while( whiteSpace.indexOf(currentChar) > -1 ) {
            currentChar = in.read();
        }

        // Read all characters until you reach white space
        while( whiteSpace.indexOf(currentChar) == -1 ) {
            result.append( (char) currentChar );
            currentChar = in.read();
        }
        return result.toString();
    }


    /* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
     * grid is filled in one row at at time, from left to right.  All non-valid
     * characters are ignored by this function and may be used in the Sudoku file
     * to increase its legibility. */
    public void read( InputStream in ) throws Exception
    {
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                Grid[i][j] = readInteger( in );
            }
        }
    }


    /* Helper function for the printing of Sudoku puzzle.  This function will print
     * out text, preceded by enough ' ' characters to make sure that the printint out
     * takes at least width characters.  */
    void printFixedWidth( String text, int width )
    {
        for( int i = 0; i < width - text.length(); i++ )
            System.out.print( " " );
        System.out.print( text );
    }


    /* The print() function outputs the Sudoku grid to the standard output, using
     * a bit of extra formatting to make the result clearly readable. */
    public void print()
    {
        // Compute the number of digits necessary to print out each number in the Sudoku puzzle
        int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

        // Create a dashed line to separate the boxes 
        int lineLength = (digits + 1) * N + 2 * SIZE - 3;
        StringBuffer line = new StringBuffer();
        for( int lineInit = 0; lineInit < lineLength; lineInit++ )
            line.append('-');

        // Go through the Grid, printing out its values separated by spaces
        for( int i = 0; i < N; i++ ) {
            for( int j = 0; j < N; j++ ) {
                printFixedWidth( String.valueOf( Grid[i][j] ), digits );
                // Print the vertical lines between boxes 
                if( (j < N-1) && ((j+1) % SIZE == 0) )
                    System.out.print( " |" );
                System.out.print( " " );
            }
            System.out.println();

            // Print the horizontal line between boxes
            if( (i < N-1) && ((i+1) % SIZE == 0) )
                System.out.println( line.toString() );
        }
    }


    /* The main function reads in a Sudoku puzzle from the standard input, 
     * unless a file name is provided as a run-time argument, in which case the
     * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
     * outputs the completed puzzle to the standard output. */
    public static void main( String args[] ) throws Exception
    {
        InputStream in;
        if( args.length > 0 ) 
            in = new FileInputStream( args[0] );
        else
            in = System.in;

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        Sudoku s = new Sudoku( puzzleSize );

        // read the rest of the Sudoku puzzle
        s.read( in );

        // Solve the puzzle.  We don't currently check to verify that the puzzle can be
        // successfully completed.  You may add that check if you want to, but it is not
        // necessary.
        long startTime = System.nanoTime(); //added by andrew
        s.solve();
        long endTime = System.nanoTime(); //added by andrew
        System.out.println("Running time: "+ (endTime-startTime)/1000.0 + " nanoseconds"); //added by andrew
        

        // Print out the (hopefully completed!) puzzle
        //s.print(); //Commented out by Andrew
    }
}

