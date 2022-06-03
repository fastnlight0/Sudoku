import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class Sudoku extends JFrame implements KeyListener, ActionListener {
	ArrayList<ArrayList<Integer>> nums = new ArrayList<>();
	ArrayList<ArrayList<Integer>> answer = new ArrayList<>();
	int numberOfEmptyCells = 20;

	public static void print(Object t) {
		System.out.println(t);
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.application.name", "Sudoku");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Sudoku();
	}

	public Sudoku() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		setLocationRelativeTo(null);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setTitle("Sudoku");
		setLayout(new GridLayout(9, 9, 1, 1));
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGameItem = new JMenuItem("New Game");
		JMenuItem showAnswerItem = new JMenuItem("Show Answer");
		newGameItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateGameBoard();
			}
		});
		showAnswerItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAnswer();
			}
		});
		gameMenu.add(newGameItem);
		gameMenu.add(showAnswerItem);
		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem changeNumberOfEmptyCellsItem = new JMenuItem("Change Number of Empty Cells");
		changeNumberOfEmptyCellsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(null, "Enter the number of empty cells:",
						"Change Number of Empty Cells", JOptionPane.QUESTION_MESSAGE);
				if (input != null) {
					try {
						int num = Integer.parseInt(input);
						if (num >= 1 && num <= 81) {
							numberOfEmptyCells = num;
							generateGameBoard();
						} else {
							JOptionPane.showMessageDialog(null,
									"Invalid input. Please enter a number between 1 and 81.", "Invalid Input",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number between 1 and 81.",
								"Invalid Input", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		settingsMenu.add(changeNumberOfEmptyCellsItem);
		menuBar.add(gameMenu);
		menuBar.add(settingsMenu);
		setJMenuBar(menuBar);
		ArrayList<Integer> greens = new ArrayList<>();
		ArrayList<Integer> reds = new ArrayList<>();
		ArrayList<Integer> blues = new ArrayList<>();
		ArrayList<Integer> firstPointer = greens;
		ArrayList<Integer> secondPointer = reds;
		ArrayList<Integer> thirdPointer = blues;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (j % 9 == 0 || j % 9 == 1 || j % 9 == 2){
					firstPointer.add(i * 9 + j);
				} else if (j % 9 == 3 || j % 9 == 4 || j % 9 == 5){
					secondPointer.add(i * 9 + j);
				} else if (j % 9 == 6 || j % 9 == 7 || j % 9 == 8){
					thirdPointer.add(i * 9 + j);
				}
			}
			if (i != 0 && i % 3 == 2) {
				ArrayList<Integer> temp = firstPointer;
				firstPointer = secondPointer;
				secondPointer = thirdPointer;
				thirdPointer = temp;
			}
		}
		for (int i = 0; i < 81; i++) {
			int row = (int) Math.floor(i / 9);
			int col = i % 9;
			if (col == 0) {
				nums.add(row, new ArrayList<Integer>());
			}
			nums.get(row).add(col, 0);
			JPanel jPanel = new JPanel();
			JButton button = new JButton();
			button.setName(String.valueOf(i));
			button.setPreferredSize(new Dimension(60, 60));
			button.setVerticalAlignment(SwingConstants.CENTER);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			jPanel.setName(Integer.toString(i));
			if (greens.contains(i)) {
				jPanel.setBackground(Color.GREEN);
			} else if (reds.contains(i)) {
				jPanel.setBackground(Color.ORANGE);
			} else if (blues.contains(i)) {
				jPanel.setBackground(Color.CYAN);
			}
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String input = JOptionPane.showInputDialog(Sudoku.getFrames()[0],
							"Enter a number for spot: " + jPanel.getName(), nums.get(row).get(col));
					if (input != null) {
						try {
							int num = Integer.parseInt(input);
							if (num > 0 && num < 10) {
								if (checkIfValid(row, col, num, true, false)) {
									nums.get(row).set(col, num);
									button.setText(input);
									checkForWin();
								}
							} else if (num == 0) {
								button.setText("");
								nums.get(row).set(col, num);
							} else {
								JOptionPane.showMessageDialog(Sudoku.getFrames()[0],
										"Please enter a number between 1 and 9", "Invalid Input",
										JOptionPane.ERROR_MESSAGE);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
			jPanel.add(button);
			add(jPanel, i);
		}
		generateGameBoard();
		SwingUtilities.updateComponentTreeUI(this);
	}

	public boolean checkIfValid(int row, int col, int num, boolean showError, boolean isNew) {
		String clear = "";
		if (!isNew) {
			if (nums.get(row).contains(num)) {
				clear = "row";
			}
		} else {
			if (answer.get(row).contains(num)) {
				clear = "row";
			}
		}
		for (int l = 0; l < nums.size(); l++) {
			if (!isNew) {
				if (nums.get(l).get(col) == num) {
					clear = "column";
				}
			} else {
				if (answer.get(l).get(col) == num) {
					clear = "column";
				}
			}
		}
		int boxRow = (int) Math.floor(row / 3);
		int boxCol = (int) Math.floor(col / 3);
		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 3; m++) {
				if (!isNew) {
					if (nums.get(boxRow * 3 + l).get(boxCol * 3 + m) == num) {
						clear = "box";
					}
				} else {
					if (answer.get(boxRow * 3 + l).get(boxCol * 3 + m) == num) {
						clear = "box";
					}
				}
			}
		}
		if (clear == "") {
			return true;
		} else {
			if (showError) {
				JOptionPane.showMessageDialog(Sudoku.getFrames()[0],
						"That number is already in the same " + clear + ".");
			}
			return false;
		}
	}

	public void updateView() {
		SwingUtilities.updateComponentTreeUI(this);
	}

	public void showAnswer() {
		for (int i = 0; i < 81; i++) {
			nums.get(i / 9).set(i % 9, answer.get(i / 9).get(i % 9));
		}
		setTextOnAllButtons();
		disableAll();
	}

	public void generateGameBoard() {
		disableAll();
		answer.clear();
		for (int i = 0; i < 81; i++) {
			int row = (int) Math.floor(i / 9);
			int col = i % 9;
			if (col == 0) {
				answer.add(row, new ArrayList<Integer>());
			}
			answer.get(row).add(col, 0);
		}
		nums.clear();
		for (int i = 0; i < 81; i++) {
			int row = (int) Math.floor(i / 9);
			int col = i % 9;
			if (col == 0) {
				nums.add(row, new ArrayList<Integer>());
			}
			nums.get(row).add(col, 0);
		}
		fillDiagonal();
		if (!fillRemaining(answer, 0)){
			generateGameBoard();
			return;
		}
		for (int i = 0; i < 81; i++) {
			nums.get(i / 9).set(i % 9, answer.get(i / 9).get(i % 9));
		}
		addBlankSpots();
		setTextOnAllButtons();
		enableBlank();
	}

	public void fillDiagonal() {
		for (int j = 0; j < 3; j++) {
			ArrayList<Integer> rand = new ArrayList<>();
			for (int i = 0; i < 9; i++) {
				rand.add(i);
			}
			Collections.shuffle(rand);
			for (int i = 0; i < 9; i++) {
				int row = j * 3 + rand.get(i) / 3;
				int col = j * 3 + rand.get(i) % 3;
				answer.get(row).set(col, i + 1);
			}
		}
	}

	public boolean fillRemaining(ArrayList<ArrayList<Integer>> pastAnswer, int index) {
		if (index >= 80) {
			this.answer = pastAnswer;
			return true;
		}
		int row = (int) Math.floor(index / 9);
		int col = index % 9;
		ArrayList<Integer> rand = new ArrayList<>();
		for (int j = 1; j < 10; j++) {
			rand.add(j);
		}
		Collections.shuffle(rand);
		if (pastAnswer.get(row).get(col) == 0) {
			for (Integer j : rand) {
				if (checkIfValid(row, col, j, false, true)) {
					ArrayList<ArrayList<Integer>> newAnswer = pastAnswer;
					newAnswer.get(row).set(col, j);
					if (fillRemaining(newAnswer, index + 1)) {
						return true;
					}
				}
			}
		} else {
			return fillRemaining(pastAnswer, index + 1);
		}
		return false;
	}
	public void addBlankSpots(){
		ArrayList<Integer> rand = new ArrayList<>();
		for (int i = 0; i < 81; i++) {
			rand.add(i);
		}
		Collections.shuffle(rand);
		for (int i = 0; i < numberOfEmptyCells; i++) {
			int row = (int) Math.floor(rand.get(i) / 9);
			int col = rand.get(i) % 9;
			nums.get(row).set(col, 0);
		}
	}
	public void checkForWin() {
		for (ArrayList<Integer> i : nums) {
			if (i.contains(0)) {
				return;
			}
		}
		disableAll();
		JOptionPane.showMessageDialog(Sudoku.getFrames()[0], "You win!");
	}

	public void disableAll() {
		for (int i = 0; i < 81; i++) {
			JButton button = (JButton) ((JPanel) ((JFrame) Sudoku.getFrames()[0]).getContentPane().getComponents()[i])
					.getComponents()[0];
			button.setEnabled(false);
		}
		updateView();
	}
	public void enableBlank(){
		for (int i = 0; i < 81; i++) {
			if (nums.get(i / 9).get(i % 9) == 0) {
				JButton button = (JButton) ((JPanel) ((JFrame) Sudoku.getFrames()[0]).getContentPane().getComponents()[i])
						.getComponents()[0];
				button.setEnabled(true);
			}
		}
		updateView();
	}

	public void setTextOnAllButtons() {
		for (int i = 0; i < 81; i++) {
			JButton button = (JButton) ((JPanel) ((JFrame) Sudoku.getFrames()[0]).getContentPane().getComponents()[i])
					.getComponents()[0];
			if (nums.get(i / 9).get(i % 9) != 0) {
				button.setText(nums.get(i / 9).get(i % 9).toString());
			} else {
				button.setText("");
			}
		}
		updateView();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
