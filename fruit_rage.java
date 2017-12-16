import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;


public class homework {
	static int n =0;
	boolean minimaxCutoff = false;
	
	public int isConnected(int row, int col, int[][] arr){

		boolean placeUp = ((row-1)>=0);
		boolean placeDown = ((row+1)< arr.length);
		boolean placeLeft = ((col -1)>=0);
		boolean placeRight = ((col+1)< arr.length);
		
		int cell=arr[row][col];
		arr[row][col]= -1;
		int up=0,down=0,left=0,right=0;
		int connectedCells=0;
		
		if (placeUp && arr[row-1][col] == cell)
        {
            up = isConnected(row-1,col,arr);
        }
        if (placeDown && arr[row+1][col] == cell )
        {
            down = isConnected(row+1,col,arr);
        }
        if (placeLeft && arr[row][col-1] == cell)
        {
            left = isConnected(row,col-1,arr);
        }
        if (placeRight && arr[row][col+1] == cell)
        {
            right = isConnected(row,col+1,arr);
        }
        
        connectedCells = up + down + left + right + 1;
        return connectedCells;

	}
	
	// ** Apply gravity on the move selected
	public int[][] Shrink_matrix(int x,int y, int[][] shrink_array){

		int i,j;
		for(i=n-2;i>=0;i--){
			for(j=n-1;j>=0;j--){
				if(shrink_array[i+1][j]==-1){
					int k = i+1;
					while(shrink_array[k][j]==-1 && k<n){
						shrink_array[k][j]=shrink_array[k-1][j];
						shrink_array[k-1][j]=-1;
						if(k==n-1) {
							break;
						}
						else k++;					
					}				
				}
			}
			
		}

	return shrink_array;	
	
	}
	
	public ArrayList<MatrixScore> getMoves(int[][] mat){
		
		int[][] temp_mat = new int[n][n];
		int i,j;
		int connected=0;
		ArrayList <MatrixScore> moves = new ArrayList<MatrixScore> ();

		for(i=0;i<n;i++){
			for(j=0;j<n;j++){
				temp_mat[i][j]=mat[i][j];
		  }
		}
		for(i=0;i<n;i++){
			for(j=0;j<n;j++){
				if(temp_mat[i][j] != -1){
					connected=isConnected(i,j,temp_mat);
					MatrixScore valid_moves = new MatrixScore();
					valid_moves.x=i;
					valid_moves.y=j;
					valid_moves.score=connected;
					moves.add(valid_moves);
				}
			}
		}
		//Sort moves
		Collections.sort(moves, new cellComparator());
		/*
		System.out.println("----------------------------");
		for(MatrixScore move: moves){
			System.out.println(move.x+" : "+move.y+" = "+move.score);
		}
		*/
		return moves;
		
		
	}
	
	// ** Apply Minimax Algorithm
	public int miniMax(Board board, int depth, int alpha, int beta, long startTime, long timeLimit,int prevScore){
		int score=0;
		int row=0,col=0;
		int i,j;
		boolean aiMove = board.aiAgent;
	  if(!aiMove) {
	  	score=prevScore*prevScore;
	  }
	  else{
	  	score=Math.negateExact(prevScore*prevScore);
	  }

		ArrayList <MatrixScore> valid_moves = new ArrayList<MatrixScore> ();
		valid_moves=getMoves(board.state);
		long currentTime =System.currentTimeMillis();
		long elapsedTime = (currentTime - startTime);
		
		if (elapsedTime >= timeLimit) {
			minimaxCutoff = true;
		}
		if (minimaxCutoff || (depth == 0) || (valid_moves.size() == 0)) {
			return score;
		}
		
		if(aiMove){

			for(MatrixScore move: valid_moves){
				
				// ** Cloning the state
				int[][] child_mat = new int[n][n];
				for(i=0;i<n;i++){
					for(j=0;j<n;j++){
						child_mat[i][j]=board.state[i][j];
				  }
				}

				Board tempBoard = new Board(child_mat, board.aiAgent);
			    tempBoard.doMove(move.x, move.y);
			    
				alpha = Math.max(alpha, miniMax(tempBoard, depth - 1, alpha, beta, startTime, timeLimit,move.score));
				
		
				if (beta <= alpha) {
					break;
				}				
			}

			return alpha;			
		}
		else{

			for(MatrixScore move: valid_moves){

				int[][] child_mat = new int[n][n];
				for(i=0;i<n;i++){
					for(j=0;j<n;j++){
						child_mat[i][j]=board.state[i][j];
				  }
				}
			
				Board tempBoard = new Board(child_mat, board.aiAgent);
			    tempBoard.doMove(move.x, move.y);
			beta = Math.min(beta, miniMax(tempBoard, depth - 1, alpha, beta, startTime, timeLimit,move.score));
			
			if (beta <= alpha) {
				break;
			}				
		}
		return beta;
			
		}			
	}

	
	// ** Iterative Deepening search
	public int idSearch(Board board, long timeLimit,int ai_score,int depth_limit){
		int score = 0;
		int depth = 1;
		minimaxCutoff = false;
		long startTime = (long)System.currentTimeMillis();
		long endTime = startTime + timeLimit;

		while(depth <depth_limit){
			long currentTime =System.currentTimeMillis();
			
			if (currentTime >= endTime) {
				break;
			}
			score = miniMax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, currentTime, endTime - currentTime,ai_score);
			depth = depth+1;
		}
		return score;
	}
	
	// ** Parse the matrix to get the valid moves
	public void matrixParser(int n, int[][] arr,long givenTime) throws IOException{
		int row=0,col=0,i=0,j=0;
		int ai_score=Integer.MIN_VALUE;
		int max_score=Integer.MIN_VALUE;
		int score;
		int depth_limit=0;
		MatrixScore best_move=null;

		ArrayList <MatrixScore> cell = new ArrayList<MatrixScore> ();
		cell=getMoves(arr);

		// ** Get the best move by checking all the components and applying minimax algorithm
		for(MatrixScore move :cell){
			int[][] new_mat = new int[arr.length][arr.length];
			
			for(i=0;i< arr.length;i++){
				for(j=0;j<arr.length;j++){
					new_mat[i][j]=arr[i][j];
			  }
			}
			// ** Make the move
			System.out.println("## AI plays");
			Board solnBoard = new Board(new_mat, true);
			solnBoard.doMove(move.x, move.y);
			//System.out.println("moves -->"+cell.size());
			long timeLimit = (givenTime / (cell.size()));
			if(cell.size()>=100) depth_limit=3;
			else depth_limit=4;


			score = idSearch(solnBoard,timeLimit,move.score,depth_limit);
			if(score>max_score){
				max_score=score;
				best_move=move;

			}	
		}

		isConnected(best_move.x,best_move.y,arr);
		
		arr=Shrink_matrix(best_move.x,best_move.y,arr);

		String val = (char)(best_move.y+'A') + String.valueOf(best_move.x+1);

		 	File file =new File("output.txt");
		 	FileWriter fw = new FileWriter(file);
		 	BufferedWriter bw = new BufferedWriter(fw);
		 	bw.append(val);
		 	bw.newLine();
		 	for(i=0;i< n;i++){
				for(j=0;j<n;j++){
					if(arr[i][j]==-1){
						bw.append("*");
					}
					else{
						bw.append(String.valueOf(arr[i][j]));
					}
  				}
				bw.newLine();
		 		}
		 	bw.close();
		 	fw.close();

	}
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {		

		Scanner in = new Scanner(new File("input.txt"));
		n=Integer.parseInt(in.nextLine());

		int p=Integer.parseInt(in.nextLine());

		float Time =(Float.valueOf(in.next()))*1000;
		long givenTime =(long)Time;
		
		int arr[][]=new int[n][n];
		char[] mat_row=new char[n];

		for(int i=0;i<n;i++){
			mat_row=in.next().toCharArray();
			for(int j=0;j<n;j++){
				if(mat_row[j]=='*'){
					arr[i][j]=-1;
				}
				else{
					arr[i][j]=mat_row[j]-'0';
				}
			}
		}
		try{
			homework soln = new homework();
			soln.matrixParser(n,arr,givenTime);
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}

}
class cellComparator implements Comparator<MatrixScore> {
    public int compare(MatrixScore o1, MatrixScore o2) {
        return o2.getScore() - o1.getScore();
    }
}
class MatrixScore {
    int x;
    int y;
    int score;
    public int getScore(){
    	return score;
    }
}
class Board {
	int[][] state;
	boolean aiAgent;

	public Board(int[][] boardState, boolean agent) {
		this.state = boardState;
		this.aiAgent = agent;
	}
	
	public void doMove(int x, int y) {
		homework makeMove = new homework();
		makeMove.isConnected(x, y, this.state);
		aiAgent = (this.aiAgent)?false:true;
		makeMove.Shrink_matrix(x, y, this.state);
	}
}

