package skyscraper;

import java.util.ArrayList;

public class Skyscraper {
	private int n;
	
	public Skyscraper(int n) {
		super();
		this.n = n;
	}

	public boolean solvePuzzle (Board solution, int[] clues){
		
		// Init
		ArrayList<Integer> values = new ArrayList<Integer>(); // possible values
		for(int i = 1; i <= n; i++)
			values.add(i);

		ArrayList<Integer> vars = new ArrayList<>(); // vars -> fields in matrix (n*n)
		ArrayList<ArrayList<Integer>> domains = new ArrayList<ArrayList<Integer>>(); // domains -> from 1 to n
		for(int i = 0; i < n * n; i++) {
			vars.add(i);
			domains.add(new ArrayList<Integer>(values));
		}
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				if(solution.getElement(i, j) != 0) {
					domains.get(i * n + j).clear();
					domains.get(i * n + j).add(solution.getElement(i, j));
				}
			}
		}
		int lvl = 0;	// level -> max (n * n)	
		 
		return backtrack_search(vars, domains, solution, lvl, clues);
	}
	
	public boolean backtrack_search(ArrayList<Integer> vars, 
			ArrayList<ArrayList<Integer>> domains, Board solution, int lvl, int[] clues) 
	{
		
		if (lvl == vars.size())
			return true; // end of matrix
		
		int v = vars.get(lvl);
		int row = v / n;
		int col = v % n;
		
		for (int val : domains.get(v)) {
			solution.setElement(row, col, val);
			
			// last element in row?
			// check row constraints
			
			if((v + 1) % n == 0) {
				int count_asc = check_clues_asc(solution.getRow(row));
				int count_desc = check_clues_desc(solution.getRow(row));
				if((count_asc != clues[(n * 4 - 1) - row] && clues[(n * 4 - 1) - row] != 0) ||
						(count_desc != clues[n + row] && clues[n + row] != 0)) {
					solution.setElement(row, col, 0);
					continue;
				}
			}
			
			// last element in column?
			// check column constraints
			if(row == n - 1) {
				int count_asc = check_clues_asc(solution.getColumn(col));
				int count_desc = check_clues_desc(solution.getColumn(col));
				if((count_asc != clues[col] && clues[col] != 0) 
						|| (count_desc != clues[(n * 3 - 1) - col] && clues[(n * 3 - 1) - col] != 0)) {
					solution.setElement(row, col, 0);
					continue;
				}
			}
			
			// copy domains
			ArrayList<ArrayList<Integer>> new_domains = new ArrayList<ArrayList<Integer>>();
			for(ArrayList<Integer> elem : domains)
				new_domains.add((ArrayList<Integer>) elem.clone());
			new_domains.get(v).clear();
			new_domains.get(v).add(val);
			
			// update domains
			for(int i = 0; i < n; i++) {
				int index_row = row * n + i;
				int index_col = col + i * n;
				
				if(index_row != v && new_domains.get(index_row).contains(val))
					new_domains.get(index_row).remove(Integer.valueOf(val));

				if(index_col != v && new_domains.get(index_col).contains(val))
					new_domains.get(index_col).remove(Integer.valueOf(val));
			}
			
			// recursion
			if (backtrack_search(vars, new_domains, solution, lvl+1, clues))
				return true;
				
			solution.setElement(row, col, 0);
		}
		
		return false;
	}
	
	public int check_clues_asc(int[] arr) {
		if(arr[0] == n) return 1; //opt
        
		int count = 1;
		int top = 1;
		
		if(arr[0] > top)
			top = arr[0];
					
		for(int i = 1; i < n; i++) {
			if(arr[i] > arr[i - 1] && arr[i] > top) {
				top = arr[i];
				count++;
			}
		}
		
		return count;
	}
	
	public int check_clues_desc(int[] arr) {
		if(arr[n - 1] == n) return 1; //opt
        
		int count = 1;
		int top = 1;
		
		if(arr[n - 1] > top)
			top = arr[n - 1];
					
		for(int i = n - 2; i >= 0; i--) {
			if(arr[i] > arr[i + 1] && arr[i] > top) {
				top = arr[i];
				count++;
			}
		}
		
		return count;
	}
	
	public void printMatrix(int[][] matrix, int[] clues) {
		System.out.print("  | ");
		for(int i = 0; i < n; i++) {
			System.out.print(clues[i] + " ");
		}
		System.out.println("|  ");
		for(int i = 0; i < n + 4; i++) {
			System.out.print("- ");
		}
		System.out.println();
		
		for(int i = 0; i < n; i++) {
			System.out.print(clues[(4 * n - 1) - i] + " | ");
			for(int j = 0; j < n; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println("| " + clues[n + i]);
		}
		
		for(int i = 0; i < n + 4; i++) {
			System.out.print("- ");
		}
		System.out.println();
		System.out.print("  | ");
		for(int i = 0; i < n; i++) {
			System.out.print(clues[(2 * n) + i] + " ");
		}
		System.out.print("|  ");
		
		System.out.println();
	}
}
