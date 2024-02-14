package gui;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomeWindow extends JDialog{
	
	private PuzzleWindow game = null;
	
	private int n = 4;
	private String color = "Plava";
	
	public int getN() {
		return n;
	}

	public String getColor() {
		return color;
	}

	private void populateWindow() {
		// WEST
		JPanel westPanel = new JPanel();
		westPanel.setPreferredSize(new Dimension(10, 10));
		add(westPanel, BorderLayout.WEST);
						
		// EAST
		JPanel eastPanel = new JPanel();
		eastPanel.setPreferredSize(new Dimension(10, 10));
		add(eastPanel, BorderLayout.EAST);
						
		// NOTRH
		JPanel northPanel = new JPanel();
		northPanel.setPreferredSize(new Dimension(5, 5));
		add(northPanel, BorderLayout.NORTH);
						
		// SOUTH
		Panel southPanel = new Panel(new GridLayout(2, 1, 0, 0));
		Panel southSubPanel = new Panel();
		southSubPanel.setPreferredSize(new Dimension(5, 5));
		southPanel.add(southSubPanel);
		add(southPanel, BorderLayout.SOUTH);
		
		// CENTER
		JPanel content = new JPanel(new GridLayout(0, 1, 0, 0));
		
		// matrix size
		JPanel sizePanel = new JPanel(new GridLayout(2, 1, 0, 0));
		JLabel sizeLabel = new JLabel("Izaberite dimenzije matrice");
		sizePanel.add(sizeLabel);
		Choice sizeChoice = new Choice();
		sizeChoice.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		sizeChoice.add("4x4");
		sizeChoice.add("5x5");
		sizeChoice.add("6x6");
		sizeChoice.select(0);
		sizePanel.add(sizeChoice);
		content.add(sizePanel);
		
		// size action
		sizeChoice.addItemListener((ie) -> {
			String name = sizeChoice.getSelectedItem();
			if(name.equals("4x4")) {
				n = 4;
			} else if(name.equals("5x5")) {
				n = 5;
			} else if(name.equals("6x6")) {
				n = 6;
			}
		});
		
		// skyscrapers color
		JPanel colorPanel = new JPanel(new GridLayout(2, 1, 0, 0));
		JLabel colorLabel = new JLabel("Izaberite boju solitera");
		colorPanel.add(colorLabel);
		Choice colorChoice = new Choice();
		colorChoice.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		colorChoice.add("Plava");
		colorChoice.add("Zelena");
		colorChoice.add("Ljubicasta");
		colorChoice.select(0);
		colorPanel.add(colorChoice);
		content.add(colorPanel);
		
		// color action
		colorChoice.addItemListener((ie) -> {
			color = colorChoice.getSelectedItem();
		});
		
		// button
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JButton startButton = new JButton("Zapocni igru");
		startButton.setBackground(Color.LIGHT_GRAY);
		startButton.setPreferredSize(new Dimension(140, 20));
		startButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		buttonPanel.add(startButton);
		content.add(buttonPanel);
		
		// button action
		startButton.addActionListener((ae) -> {
			this.setVisible(false);
			if(game != null)
				game.dispose();
			game = new PuzzleWindow(this);
		});
		
		add(content, BorderLayout.CENTER);
	}
	
	public HomeWindow() {
		setTitle("Soliteri");
		setBounds(600, 200, 200, 300);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		
		populateWindow();
		
		pack();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new HomeWindow();
	}

}
