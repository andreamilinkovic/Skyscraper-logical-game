package skyscraper;

public class Board implements Cloneable{
	private int n;
	private int[][] board;

	public Board(int n) {
		super();
		this.n = n;
		board = new int[n][n];
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int[][] getBoard() {
		return board;
	}
	
	public int getElement(int row, int col) {
		return this.board[row][col];
	}

	public void setElement(int row, int col, int val) {
		this.board[row][col] = val;
	}
	
	public int[] getRow(int row) {
		return this.board[row];
	}
	
	public int[] getColumn(int col) {
		int[] arr = new int[n];
		for(int i = 0; i < n; i++) {
			arr[i] = this.board[i][col];
		}
		return arr;
	}
	
	public int checkDuplicates(int[] arr, int index, int val) {
		for(int i = 0; i < n; i++)
			if(index != i && arr[i] == val)
				return i;
		return -1;
	}

	public void clone(Object obj) {
		if(obj instanceof Board)
			for(int i = 0; i < n; i++)
				for(int j = 0; j < n; j++)
					this.board[i][j] = ((Board)obj).getElement(i, j);	
	}
	
	public int isEqual(Board solution) {
		int zero = -1;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(this.getElement(i, j) != solution.getElement(i, j) && this.getElement(i, j) != 0)
					return i * n + j;
				else if (this.getElement(i, j) == 0)
					zero = i * 4 + j;
			}
		}
		return zero;
	}
}
