package cn.yangtaocun.reversi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reversi {
	
	private static int BLACK = 1;
	private static int EMPTY = 0;
	private static int WHITE = -1;

	public int[][] getChessBoard() {
		return chessBoard;
	}

	private int[][] chessBoard;
	// true - Black false-White
	private boolean isBlackTurn;
	private boolean canPlaceFlagBlack;
	private boolean canPlaceFlagWhite;
	
	private int[][] values;
	
	private int bestX;
	private int bestY;
	private int bestValue;
	
	public Reversi() {
		chessBoard = new int[8][8];
		values = new int[8][8];
		newGame();
	}
	
	public void newGame(){
		chessBoard[3][3]=WHITE;
		chessBoard[3][4]=BLACK;
		chessBoard[4][3]=BLACK;
		chessBoard[4][4]=WHITE;
		isBlackTurn=true;
		canPlaceFlagBlack=true;
		canPlaceFlagWhite=true;
	}
	
	private void generateMoves(){
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				values[i][j]=0;
			}
		}
		bestValue=0;
		if(isBlackTurn) canPlaceFlagBlack=false; else canPlaceFlagWhite=false;
		int mine = isBlackTurn?BLACK:WHITE,opponent = isBlackTurn?WHITE:BLACK;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(chessBoard[i][j]==mine){
					//left
					searchMoves(i,j,0,-1,opponent);
					//up left
					searchMoves(i,j,-1,-1,opponent);
					//up
					searchMoves(i,j,-1,0,opponent);
					//up right
					searchMoves(i,j,-1,1,opponent);
					//right
					searchMoves(i,j,0,1,opponent);
					//down right
					searchMoves(i,j,1,1,opponent);
					//down
					searchMoves(i,j,1,0,opponent);
					//down left
					searchMoves(i,j,1,-1,opponent);
				}
			}
		}
		
		//System.out.println("black:" + canPlaceFlagBlack+" white:"+canPlaceFlagWhite);
		
//		if(!canPlaceFlagWhite){
//			System.out.println("No move!" + (canPlaceFlagBlack||canPlaceFlagWhite));
//			System.exit(0);
//		}
		
	}

	private void searchMoves(int fromX,int fromY,int directionX,int directionY,int opponent) {	
		boolean findOpponent=false;
		int x=fromX+directionX,y=fromY+directionY;
		while(x>=0&&y>=0&&x<8&&y<8 && chessBoard[x][y]==opponent){
			findOpponent=true;
			x+=directionX;
			y+=directionY;
		}
		if(findOpponent && x>=0&&y>=0&&x<8&&y<8 && chessBoard[x][y]==EMPTY){
			values[x][y] += -opponent*(Math.abs(directionX==0?fromY-y:fromX-x)-1);
			if(isBlackTurn){
				if(values[x][y]>bestValue){
					bestX = x;
					bestY = y;
					bestValue=values[x][y];
				}
				canPlaceFlagBlack=true;
			}
			else{
				if(values[x][y]<bestValue){
					bestX = x;
					bestY = y;
					bestValue=values[x][y];
				}
				canPlaceFlagWhite=true;
			}
		}
	}
	
	public boolean isGameOver(){
		return !(canPlaceFlagBlack || canPlaceFlagWhite);
	}
	
	public String getCurrentPlayer(){
		return isBlackTurn?"BLACK":"WHITE";
	}
	
	public int count(int color){
		int result = 0;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(chessBoard[i][j]==color){
					result++;
				}
			}
		}
		return result;		
	}
	
	public int judge(){
		return count(BLACK)-count(WHITE);		
	}
	
	public void move(int[] position) throws InvalidMoveException{
		move(position[0],position[1]);
	}
	
	public void move(int x,int y) throws InvalidMoveException{
		if(x<0||y<0||x>7||y>7 || chessBoard[x][y]!=EMPTY) throw new InvalidMoveException("Invalid Move!");
		int mine = isBlackTurn?BLACK:WHITE,opponent = isBlackTurn?WHITE:BLACK;		
		boolean anyFlip = false;		
		if(flipPieces(x,y,-1,-1,mine)) anyFlip =true;
		if(flipPieces(x,y,-1,0,mine)) anyFlip =true;
		if(flipPieces(x,y,-1,1,mine)) anyFlip =true;
		if(flipPieces(x,y,0,-1,mine)) anyFlip =true;
		if(flipPieces(x,y,0,1,mine)) anyFlip =true;
		if(flipPieces(x,y,1,-1,mine)) anyFlip =true;
		if(flipPieces(x,y,1,0,mine)) anyFlip =true;
		if(flipPieces(x,y,1,1,mine)) anyFlip =true;
		if(!anyFlip) throw new InvalidMoveException("Invalid Move!");
		chessBoard[x][y] = mine;
		isBlackTurn = !isBlackTurn;
	}
	
	private boolean flipPieces(int fromX, int fromY, int directionX, int directionY,int mine) {
		boolean anyFlip = false;
		int x=fromX,y=fromY;
		x+=directionX;
		y+=directionY;
		while(x>=0&&y>=0&&x<8&&y<8 && chessBoard[x][y]==-mine){			
			x+=directionX;
			y+=directionY;			
		}
		if(x>=0&&y>=0&&x<8&&y<8 && !(fromX==x&&fromY==y) && chessBoard[x][y]==mine){
			int i=fromX+directionX,j=fromY+directionY;
			//System.out.println("flip from "+fromX+" "+fromY+" to "+x+" "+y);
			while(i!=x||j!=y){
				chessBoard[i][j]=mine;
				//System.out.println("flip "+i+" "+j);
				i+=directionX;
				j+=directionY;
				anyFlip = true;
			}
		}
		return anyFlip;
	}

//	private boolean canMove(int x,int y){
//		return values[x][y]!=0;
//	}
	
	public int[] getBestMove(){
		generateMoves();
		if((isBlackTurn&&!canPlaceFlagBlack) || (!isBlackTurn&&!canPlaceFlagWhite)) {
			isBlackTurn  = !isBlackTurn;
			return null;
		}
		//showValues();
		//System.out.println("best move is "+bestX+" "+bestY);
		return new int[]{bestX,bestY};

	}

	// Black for + While for -
//	public int[] findMaxValue(){
//		if(possibleMoves==null||possibleMoves.size()<1) return null;
//		Move bestChoice = possibleMoves.get(0);
//		for(int i=1;i<possibleMoves.size();i++){
//			if(isBlack){
//				if(possibleMoves.get(i).value>bestChoice.value) bestChoice = possibleMoves.get(i);
//			}
//			else{
//				if(possibleMoves.get(i).value<bestChoice.value) bestChoice = possibleMoves.get(i);
//			}
//		}		
//		return bestChoice;
//		
//	}
	
	public void show(){
		//System.out.println(" abcdefgh");
		System.out.println(" 01234567");
		StringBuilder line = new StringBuilder();
		for(int i=0;i<8;i++){
			line.setLength(0);
			line.append(i);
			for(int j=0;j<8;j++){
				if(chessBoard[i][j]>0){
					line.append("b");
				}
				else if(chessBoard[i][j]<0){
					line.append("w");
				}
				else{
					line.append(".");
				}
			}
			System.out.println(line);
		}
	}
	
	private void showValues(){
		System.out.println("---values---");
		StringBuilder line = new StringBuilder();
		for(int i=0;i<8;i++){
			line.setLength(0);
			for(int j=0;j<8;j++){
				if(chessBoard[i][j]>0){
					line.append(" b");
				}
				else if(chessBoard[i][j]<0){
					line.append(" w");
				}
				else{
					line.append(String.format("%2d", values[i][j]));
				}
			}
			System.out.println(line);
		}
	}
	
	public void newConsoleGame(){
		newGame();
		show();
		int x,y;
		Scanner scan = new Scanner(System.in);
		while(!isGameOver()){
			
			try {
				int[] aiMove = getBestMove();
				// player
				if(aiMove!=null){
					System.out.print(getCurrentPlayer()+" input where you want to place your piece:");
					x = scan.nextInt();
					y = scan.nextInt();
					move(x, y);
				}
				else{
					System.out.println(getCurrentPlayer()+" has no possible move! Go to opponent's turn!");
				}				
				show();
				//AI
				aiMove = getBestMove();
				if(aiMove!=null){
					System.out.println("The computer placed its piece at "+aiMove[0]+","+aiMove[1]);
					move(aiMove);
					show();
				}
				else{
					System.out.println(getCurrentPlayer()+" has no possible move! Go to opponent's turn!");
				}				
			} catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		scan.close();
		int b = count(BLACK),w=count(WHITE);
		System.out.println("BLACK : "+b+" WHITE :"+w);
		if(b==w) System.out.println("draw!");
		System.out.println(b-w>0?"BLACK wins by "+(b-w)+" pieces!":"WHITE wins by "+(w-b)+" pieces!");
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Reversi r = new Reversi();
		r.newConsoleGame();

	}

	public class InvalidMoveException extends Exception {
		public InvalidMoveException(String s) {
			super(s);
		}
	}
}
