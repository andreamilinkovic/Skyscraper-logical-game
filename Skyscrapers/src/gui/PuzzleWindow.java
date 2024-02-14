package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import skyscraper.Board;
import skyscraper.Skyscraper;
import timer.Timer;


public class PuzzleWindow extends Frame{
	
	private HomeWindow owner = null;
	private int n;
	
	private Board mySolution;
	private int[] clues;
	
	private Board solution; 
	
	private boolean isCorrect = false;
	private int currValue;
	private int hint_row = -1, hint_col = -1;
	private int hintCount = 3; 
	
	private Timer timer;
	private JLabel time = new JLabel("00:00");
	
	private void populateWindow() {
		// formatting input
		NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format) {
	    	@Override
	        public Object stringToValue(String text) throws ParseException {
	            if (text.length() == 0)
	                return null;
	            return super.stringToValue(text);
	        }
	    };
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(1);
	    formatter.setMaximum(n);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
	    
		JPanel mainPanel = new JPanel(new GridLayout(n + 4, n + 4, 0, 0));
		JPanel panelHolder[][] = new JPanel[n + 4][n + 4]; 
		JFormattedTextField solutionTextField[][] = new JFormattedTextField[n][n];
		JFormattedTextField cluesTextField[] = new JFormattedTextField[n * 4];
		
		// WEST
		JPanel westPanel = new JPanel();
		westPanel.setPreferredSize(new Dimension(30, 30));
		add(westPanel, BorderLayout.WEST);
				
		// EAST
		JPanel eastPanel = new JPanel();
		eastPanel.setPreferredSize(new Dimension(30, 30));
		add(eastPanel, BorderLayout.EAST);
				
		// SOUTH
		JPanel southPanel = new JPanel(new GridLayout(2, 1, 0, 0));
		JPanel southSubPanel = new JPanel();
		southSubPanel.setPreferredSize(new Dimension(30, 30));
		southPanel.add(southSubPanel);
		
		// NORTH
		JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		timePanel.add(new JLabel("Vreme: "));
		timePanel.add(time);
		add(timePanel, BorderLayout.NORTH);
		
		// SOUTH
		JPanel buttonPanel = new JPanel();
		
		JButton timeButton = new JButton("Startuj vreme"); 
		timeButton.setBackground(Color.LIGHT_GRAY);
		timeButton.setPreferredSize(new Dimension(120, 30));
		timeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		buttonPanel.add(timeButton);
		
		JButton hintButton = new JButton("Pomoc(" + hintCount + ")");  
		hintButton.setBackground(Color.LIGHT_GRAY);
		hintButton.setPreferredSize(new Dimension(120, 30));
		hintButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		hintButton.setEnabled(false);
		buttonPanel.add(hintButton);
		
		JButton solveButton = new JButton("Resi");
		solveButton.setBackground(Color.LIGHT_GRAY);
		solveButton.setPreferredSize(new Dimension(120, 30));
		solveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		solveButton.setEnabled(false);
		buttonPanel.add(solveButton);
		
		southPanel.add(buttonPanel);
		
		add(southPanel, BorderLayout.SOUTH);
		
		// MENU
		Menu mainMenu = new Menu("Igra");
		mainMenu.setFont(new Font(Font.SANS_SERIF, 0, 14));
		
		MenuItem newGame = new MenuItem("Nova igra");
		
		MenuItem quitMenu = new MenuItem("Izadji", new MenuShortcut(KeyEvent.VK_Q));
		quitMenu.addActionListener((ae) -> {
			if(timer != null)
				timer.interrupt();
			dispose();
			owner.dispose();
		});
		
		mainMenu.add(newGame);
		mainMenu.addSeparator();
		mainMenu.add(quitMenu);
		
		Menu instructionMenu = new Menu("Pomoc");
		instructionMenu.setFont(new Font(Font.SANS_SERIF, 0, 14));
		
		MenuItem rules = new MenuItem("Pravila");
		MenuItem instruction = new MenuItem("Uputstvo");
		
		instructionMenu.add(rules);
		instructionMenu.add(instruction);
		
		MenuBar menuBar = new MenuBar();
		menuBar.add(mainMenu);
		menuBar.add(instructionMenu);
		setMenuBar(menuBar);
		
		//listeners
		newGame.addActionListener((ae) -> {
			owner.setLocation(getX() + getWidth() / 4, getY() + getHeight() / 4);
			owner.setVisible(true);
		});
		
		rules.addActionListener((ae) -> {
			new RulesDialog(this);
		});
		
		instruction.addActionListener((ae) -> {
			new InstructionDialog(this);
		});
		
		// CENTER
		for (int i = 0; i < n + 4; i++){
			for (int j = 0; j < n + 4; j++){
				panelHolder[i][j] = new JPanel();
				panelHolder[i][j].setPreferredSize(new Dimension(45, 45));
				mainPanel.add(panelHolder[i][j]);
			}
		}
		
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				// solution
				solutionTextField[i][j] = new JFormattedTextField(formatter);
				solutionTextField[i][j].setPreferredSize(new Dimension(40, 40));
				solutionTextField[i][j].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
				solutionTextField[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
				solutionTextField[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				panelHolder[i + 2][j + 2].add(solutionTextField[i][j]);
				
				final int innerI = i;
				final int innerJ = j;
				solutionTextField[innerI][innerJ].getDocument().addDocumentListener(new DocumentListener() {
				    @Override
				    public void insertUpdate(DocumentEvent e) {
				    	int val = Integer.parseInt(solutionTextField[innerI][innerJ].getText());
						currValue = Integer.parseInt(solutionTextField[innerI][innerJ].getText());
				    	mySolution.setElement(innerI, innerJ, val);
				    	//check row and col
				    	int ret_col = mySolution.checkDuplicates(mySolution.getRow(innerI), innerJ, val);
				    	int ret_row = mySolution.checkDuplicates(mySolution.getColumn(innerJ), innerI, val);
				    	if(ret_col == -1 && ret_row == -1){
				    		solutionTextField[innerI][innerJ].setBackground(getColor(val));
				    	} else if (ret_col != -1 && ret_row != -1){
				    		solutionTextField[innerI][innerJ].setBackground(getColor(7));
				    		solutionTextField[innerI][ret_col].setBackground(getColor(7));
				    		solutionTextField[ret_row][innerJ].setBackground(getColor(7));
				    	} else if (ret_col != -1) {
				    		solutionTextField[innerI][innerJ].setBackground(getColor(7));
			    			solutionTextField[innerI][ret_col].setBackground(getColor(7));
				    	} else if (ret_row != -1) {
				    		solutionTextField[innerI][innerJ].setBackground(getColor(7));
				    		solutionTextField[ret_row][innerJ].setBackground(getColor(7));
				    	}
				    	int ret = mySolution.isEqual(solution);
						// if(ret == -1) equals
						if(ret == -1) {
							timer.interrupt();
							hintButton.setEnabled(false);
							solveButton.setEnabled(false);
							return;
						}
				    }

				    @Override
				    public void removeUpdate(DocumentEvent e) {
				    	mySolution.setElement(innerI, innerJ, 0);
				    	solutionTextField[innerI][innerJ].setBackground(getColor(0));
				    	
				    	//check row and col
				    	int ret_col = mySolution.checkDuplicates(mySolution.getRow(innerI), innerJ, currValue);
				    	int ret_row = mySolution.checkDuplicates(mySolution.getColumn(innerJ), innerI, currValue);
				    	if(ret_col == -1 && ret_row == -1) return;
				    	int ret_col_new = -1, ret_row_new = -1;
				    	if(ret_col != -1) {
				    		ret_col_new = mySolution.checkDuplicates(mySolution.getRow(innerI), ret_col, currValue);
				    		if(ret_col_new == -1) {
				    			ret_row_new = mySolution.checkDuplicates(mySolution.getColumn(ret_col), innerI, currValue);
				    			if(ret_row_new == -1) {
				    				solutionTextField[innerI][ret_col].setBackground(getColor(currValue));
				    			}
				    				
				    		}
				    	}
				    	if(ret_row != -1) {
				    		ret_col_new = mySolution.checkDuplicates(mySolution.getRow(ret_row), innerJ, currValue);
				    		if(ret_col_new == -1) {
				    			ret_row_new = mySolution.checkDuplicates(mySolution.getColumn(innerJ), ret_row, currValue);
				    			if(ret_row_new == -1) {
				    				solutionTextField[ret_row][innerJ].setBackground(getColor(currValue));
				    			}
				    				
				    		}
				    	}
				    }

				    @Override
				    public void changedUpdate(DocumentEvent e) {}
				});
				
				solutionTextField[innerI][innerJ].addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						if (solutionTextField[innerI][innerJ].getText().isEmpty())
							currValue = 0;
						else
							currValue = Integer.parseInt(solutionTextField[innerI][innerJ].getText());
					}
				});
			}
		}
		for(int i = 0; i < n * 4; i++) {
			cluesTextField[i] = new JFormattedTextField(formatter);
			cluesTextField[i].setPreferredSize(new Dimension(40, 40));
			cluesTextField[i].setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			cluesTextField[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			cluesTextField[i].setHorizontalAlignment(SwingConstants.CENTER);

			final int innerI = i;
			cluesTextField[i].getDocument().addDocumentListener(new DocumentListener() {
			    @Override
			    public void insertUpdate(DocumentEvent e) {
			    	clues[innerI] = Integer.parseInt(cluesTextField[innerI].getText());
			    }

			    @Override
			    public void removeUpdate(DocumentEvent e) {
			    	clues[innerI] = 0;
			    }

			    @Override
			    public void changedUpdate(DocumentEvent e) {}
			});
		}
		for (int i = 0; i < n; i++){
			// clues
			panelHolder[0][i + 2].add(cluesTextField[i]);
			panelHolder[i + 2][n + 3].add(cluesTextField[i + n]);
			panelHolder[n + 3][n + 1 - i].add(cluesTextField[i + 2 * n]);
			panelHolder[n + 1 - i][0].add(cluesTextField[i + 3 * n]);
		}
		
		add(mainPanel, BorderLayout.CENTER);
		
		// listeners
		
		timeButton.addActionListener((ae) -> {
			// disable textFields
			for(int i = 0; i < n * 4; i++)
				cluesTextField[i].setEditable(false);
			for(int i = 0; i < n; i++) {
				for(int j = 0; j < n; j++) {
					if(!solutionTextField[i][j].getText().isEmpty())
						solutionTextField[i][j].setEditable(false);
				}
			}
			
			//solve skyscraper
			Skyscraper skyscraper = new Skyscraper(n); 
			solution.clone(mySolution);
			isCorrect = skyscraper.solvePuzzle(solution, clues);
			timeButton.setEnabled(false);
			
			//enable buttons
			hintButton.setEnabled(true);
			solveButton.setEnabled(true);
			
			//start timer
			if(timer != null)
				timer.interrupt();
			timer = new Timer(time);
			timer.start();
			timer.go();
		});
		
		hintButton.addActionListener((ae) -> {
			if(!isCorrect)
				new ErrorDialog(this);
			else
			{
				int ret = mySolution.isEqual(solution);
				// if(ret == -1) equals
				if(ret == -1) {
					timer.interrupt();
					hintButton.setEnabled(false);
					solveButton.setEnabled(false);
					return;
				}
				hint_row = ret / n;
				hint_col = ret % n;
				if(!solutionTextField[hint_row][hint_col].getText().isEmpty())
					currValue = Integer.parseInt(solutionTextField[hint_row][hint_col].getText());
				solutionTextField[hint_row][hint_col].setValue(solution.getElement(hint_row, hint_col));
				solutionTextField[hint_row][hint_col].setBackground(getColor(8));
				//solutionTextField[hint_row][hint_col].setEditable(false);
				hintCount--;
				hintButton.setLabel("Pomoc(" + hintCount +")");
				if(hintCount == 0)
					hintButton.setEnabled(false);
			}
		});
		
		solveButton.addActionListener((ae) -> {
			if(!isCorrect)
				new ErrorDialog(this);
			else {
				for (int i = 0; i < n; i++){
					for (int j = 0; j < n; j++){
						solutionTextField[i][j].setText(solution.getElement(i, j) + "");
						solutionTextField[i][j].setBackground(getColor(solution.getElement(i, j)));
						solutionTextField[i][j].setEditable(false);
					}
				}
				timer.interrupt();
				hintButton.setEnabled(false);
				solveButton.setEnabled(false);
				return;
			}
		});
	}
	
	public Color getColor(int num) {
		String color = owner.getColor();
		switch(num) {
		case 0:
			return Color.WHITE;
		case 1:
			return (color.equals("Plava") ? new Color(84, 153, 199) : (color.equals("Zelena") ? new Color(72, 201, 176) : new Color(175, 122, 197)));
		case 2:
			return (color.equals("Plava") ? new Color(41, 128, 185) : (color.equals("Zelena") ? new Color(26, 188, 156) : new Color(155, 89, 182)));
		case 3:
			return (color.equals("Plava") ? new Color(36, 113, 163) : (color.equals("Zelena") ? new Color(23, 165, 137) : new Color(136, 78, 160)));
		case 4:
			return (color.equals("Plava") ? new Color(31, 97, 141) : (color.equals("Zelena") ? new Color(20, 143, 119) : new Color(118, 68, 138)));
		case 5:
			return (color.equals("Plava") ? new Color(26, 82, 118) : (color.equals("Zelena") ? new Color(17, 120, 100) : new Color(99, 57, 116)));
		case 6:
			return (color.equals("Plava") ? new Color(21, 67, 96) : (color.equals("Zelena") ? new Color(14, 98, 81) : new Color(81, 46, 95)));
		case 7:
			return new Color(236, 112, 99);
		case  8:
			return new Color(244, 208, 63);
		default:
			return Color.WHITE;
		}
	}
	
	public PuzzleWindow(HomeWindow owner){
		this.owner = owner;
		n = owner.getN();
		mySolution = new Board(n);
		solution = new Board(n);
		clues = new int[n * 4];
		
		setTitle("Soliteri");
		setBounds(600, 200, 500, 500);
		setResizable(false);
		setBackground(new Color(240, 240, 240));
		
		populateWindow();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(timer != null)
					timer.interrupt();
				dispose();
				owner.dispose();
			}
		});
		
		pack();
		
		setVisible(true);
	}
	
	private class ErrorDialog extends Dialog{
		private Button ok = new Button("OK");
		
		public void paint(Graphics g) {
			g.drawString("Za zadate uslove ne postoji rešenje.", 40, 70);
			super.paint(g);
		}
		
		public ErrorDialog(Frame owner){
			super(owner);
			
			setTitle("Soliteri");
			setBounds(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2, 290, 150);
			setResizable(false);
			setModalityType(ModalityType.APPLICATION_MODAL);
			
			Panel buttons = new Panel();
			
			ok.addActionListener((ae) -> {
				ErrorDialog.this.dispose();
			});
			
			buttons.add(ok);
			add(buttons, BorderLayout.SOUTH);
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			
			setVisible(true);
		}
	}
	
	private class RulesDialog extends Dialog{
		
		private String text;
		
		public RulesDialog(Frame owner){
			super(owner);
			setTitle("Pravila");
			setBounds(100, 100, 900, 500);
			setResizable(false);
			
			text = " Dimenzije matrice mogu biti 4x4, 5x5 ili 6x6. \n"
			+ " Brojevi koji se mogu uneti u polja matrice mogu biti u rasponu od 1 do n(n je velicina matrice) i predstavljaju visinu solitera. \n"
			+ " U svakom redu i koloni moraju se naci svi brojevi od 1 do n i to tacno jedanput. \n"
			+ " Brojevi oko centralnog polja pokazuju koliko se solitera moze videti u tom redu ili koloni posmatrajuci sa te tacke ka centalnom delu table. \n"
			+ " Uzeti u obzir da soliteri mogu da zaklanjaju jedni druge(visi soliter zaklanja nizi). ";
			
			JPanel content = new JPanel();
			content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); 
			
			JPanel panel0 = new JPanel();
			JPanel panel1 = new JPanel();    
			JPanel panel2 = new JPanel();
			JPanel panel3 = new JPanel();

			panel0.setBackground(Color.WHITE);
			panel1.setBackground(Color.WHITE);
			panel2.setBackground(Color.WHITE);
			panel3.setBackground(Color.WHITE);

			content.add(panel0);
			content.add(panel1);
			content.add(panel2);
			content.add(panel3);
			
			JTextArea textArea0 = new JTextArea();
			textArea0.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			textArea0.setText(" PARVILA IGRE: \n");
			textArea0.setEditable(false);
			
			panel0.add(textArea0);
			
			JTextArea textArea1 = new JTextArea();
			textArea1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			textArea1.setText(text);
			textArea1.setEditable(false);
			
			panel1.add(textArea1);

			ImageIcon empty = new ImageIcon("empty.png");
			Image image = empty.getImage();
			Image newimg = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			empty = new ImageIcon(newimg);
			JLabel labelEmpty = new JLabel(empty);
			
			ImageIcon solved = new ImageIcon("solved.png");
			image = solved.getImage();
			newimg = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			solved = new ImageIcon(newimg);
			JLabel labelSolved = new JLabel(solved);
			
			panel2.add(labelEmpty);
			panel2.add(labelSolved);
			
			JTextArea textArea2 = new JTextArea();
			textArea2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			textArea2.setText(" Boje polja predstavljaju visinu solitera."
					+ " Sto je boja tamnija, to je soliter visi.");
			textArea2.setEditable(false);
			
			panel3.add(textArea2);

			add(content, BorderLayout.CENTER);
			
			pack();
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			
			setVisible(true);
		}
	}
	
private class InstructionDialog extends Dialog{
		
		public InstructionDialog(Frame owner){
			super(owner);
			setTitle("Uputstvo");
			setBounds(100, 100, 900, 500);
			setResizable(false);
			
			JPanel content = new JPanel();
			content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); 
			
			JPanel panel0 = new JPanel(); 
			JPanel panel01 = new JPanel();   
			JPanel panel1 = new JPanel();   
			JPanel panel2 = new JPanel();
			JPanel panel03 = new JPanel(); 
			JPanel panel3 = new JPanel();
			JPanel panel4 = new JPanel();

			panel0.setBackground(Color.WHITE);
			panel01.setBackground(Color.WHITE);
			panel1.setBackground(Color.WHITE);
			panel2.setBackground(Color.WHITE);
			panel03.setBackground(Color.WHITE);
			panel3.setBackground(Color.WHITE);
			panel4.setBackground(Color.WHITE);

			content.add(panel0);
			content.add(panel01);
			content.add(panel1);
			content.add(panel2);
			content.add(panel03);
			content.add(panel3);
			content.add(panel4);
			
			JTextArea textArea0 = new JTextArea();
			textArea0.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			textArea0.setText(" BOJE \n");
			textArea0.setEditable(false);
			
			panel0.add(textArea0);
			
			JTextArea textArea01 = new JTextArea();
			textArea01.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			textArea01.setText("Crvena boja");
			textArea01.setEditable(false);
			
			panel01.add(textArea01);
			
			JTextArea textArea1 = new JTextArea();
			textArea1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			textArea1.setText("Oznacava gresku i pojavljuje se u slucaju da se u istom redu ili koloni"
					+ " nalazi vise solitera iste visine.");
			textArea1.setEditable(false);
			
			panel1.add(textArea1);

			ImageIcon red = new ImageIcon("red.png");
			Image image = red.getImage();
			Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			red = new ImageIcon(newimg);
			JLabel labelRed = new JLabel(red);
			
			panel2.add(labelRed);
			
			JTextArea textArea03 = new JTextArea();
			textArea03.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			textArea03.setText("Zuta boja");
			textArea03.setEditable(false);
			
			panel03.add(textArea03);
			
			JTextArea textArea2 = new JTextArea();
			textArea2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			textArea2.setText("Pojavljuje se kada se pritisne dugme 'Pomoc'. "
					+ "Oznacava polje koje nije reseno samostalno.");
			textArea2.setEditable(false);
			
			panel3.add(textArea2);
			
			ImageIcon yellow = new ImageIcon("yellow.png");
			image = yellow.getImage();
			newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
			yellow = new ImageIcon(newimg);
			JLabel labelYellow = new JLabel(yellow);
			
			panel4.add(labelYellow);

			add(content, BorderLayout.CENTER);
			
			pack();
			
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			
			setVisible(true);
		}
	}
}
