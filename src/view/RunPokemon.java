package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import GameModel.Direction;
import GameModel.GameModel;
import Inventory.ItemType;
import Mission.Mission;
import Mission.MissionType;

public class RunPokemon extends JFrame {
	
	private static final long serialVersionUID = 7487437405760007377L;

	private String SAVEFILENAME_GAME = "PokemonGame.ser";
	// declare the main window
	private final static int DefaultHeight = 1200;
	private final static int DefaultWidth = 800;
	private JLabel statusBar;
	private JLabel missionBoard;
	private JTable inventoryTable;
	private JTable pokemonTable;
	private JButton useItemButton;
	private JPanel currentView;
	
	// declare the main game view
	private static MainGameView mainGamePanel;
	private final static int DefaultGameHeight = 16 * 41;
	private final static int DefaultGameWidth = 16 * 41;
	
	// declare the battle view
	private static BattleView battlePanel;
	private final static int DefaultBattleHeight = 320;
	private final static int DefaultBattleWidth = 480;
	
	// declare timer detail
	public final static int delayInMillis = 25;
	public final static int framePerMove = 8;
	
	// declare game variable
	private GameModel gameModel;
	private boolean isOver;		// flag to check if the game is over
	private boolean isWin;		// flag to check if the game is win
	private boolean isLost;		// flag to check if the game is lost
	private boolean isBattle;	// flag to check if it was doing battle
	
	// main function
	public static void main(String[] args) {
		RunPokemon mainFrame = new RunPokemon();
		mainFrame.setVisible(true);
	}
	
	// constructor
	public RunPokemon(){		
		int userPrompt = JOptionPane.showConfirmDialog(null, "Do you want to start with presvious saved data?");
		// if the user choose yes, load the saved file
		if (userPrompt == JOptionPane.YES_OPTION) {
			// check if save file exist
			File saveFile = new File(SAVEFILENAME_GAME);
			if (saveFile.exists()){
				loadData();
			}
			else{
				JOptionPane.showMessageDialog(null, "You dont have a saved file",
					    "Inane error",
					    JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		
		// if the user choose no, use default
		else if (userPrompt == JOptionPane.NO_OPTION) {
			gameModel = new GameModel();
			Object[] options = {"25 Steps", "50 steps 5 pokemon"};
			userPrompt = JOptionPane.showOptionDialog(null, "Press Yes for 50 step limit 5 pokemon caught to win, no for 25 step to win",
													"Choose Mission",     
													JOptionPane.YES_NO_CANCEL_OPTION,
												    JOptionPane.QUESTION_MESSAGE,
												    null,	//Icon
												    options,
												    options[0]);
			if (userPrompt == 0){
				gameModel.setMission(new Mission(MissionType.STANDARDLADDER));
			}
			else{
				gameModel.setMission(new Mission(MissionType.TEST));
			}
		}
		// chosen cancel
		else {
			System.exit(0);
		}
		initiatePokemonGame();
		//timer = new Timer(delayInMillis, new MoveListener());
		//timer.start();
		// check if encounter a pokemon
		// start the battle
		if (gameModel.getTrainer().getCurEncounterPokemon() != null){
			mainGamePanel.setVisible(false);
		}
	}
		
	private void initiatePokemonGame(){
				setUpMainWindow();
				setUpBattleView();
				setUpGameView();		
				addEventListener();
				setUpInfoBoard();
				setUpMissionBoard();
				setUpInventory();
				setUpPokemonTable();
				setUpUseItemButton();
				addObservers();
				setViewTo(mainGamePanel);	// default starting view
	}
	
	private void setViewTo(JPanel newView) {
		if (currentView != null){
			remove(currentView);
		}
		currentView = newView;
		add(currentView);
		currentView.setVisible(true);
		gameModel.update();
		currentView.repaint();
		validate();
	}
	
	// user prompt to choose mission
	private void setUpMission(){
		// TODO: mission
	}
	
	public void setUpMainWindow(){
		// define the location of the main window
		this.setTitle("Pokemon Safari Zone - Alpha v0.5");
		this.setSize(DefaultHeight, DefaultWidth);
		this.setLocationRelativeTo(null); 
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setFocusable(true);
	}

	private void addObservers() {
		gameModel.addObserver(mainGamePanel);
		gameModel.addObserver(battlePanel);
	}
	
	public void setUpGameView(){
		// set up the main game model
		mainGamePanel = new MainGameView();
		mainGamePanel.setSize(DefaultGameHeight, DefaultGameWidth);
		mainGamePanel.setLocation(25, 25);
		mainGamePanel.setBackground(Color.WHITE);
		Border gameBorder = new LineBorder(Color.BLACK, 2, true);
		mainGamePanel.setBorder(gameBorder);
		mainGamePanel.addComponentListener(new viewChangeListener());
		mainGamePanel.setFocusable(true);
	}
	
	// setUpBattleView
	private void setUpBattleView(){
		// set up the main game model
		battlePanel = new BattleView();
		battlePanel.setSize(DefaultBattleWidth, DefaultBattleHeight);
		battlePanel.setLocation(25, 25);
		battlePanel.setBackground(Color.WHITE);
		Border gameBorder = new LineBorder(Color.BLACK, 2, true);
		battlePanel.setBorder(gameBorder);
		battlePanel.addComponentListener(new viewChangeListener());
		battlePanel.setFocusable(true);
	}
	
	public void addEventListener(){
		// add the key listener
		this.addKeyListener(new myKeyListener());
		// add the window listener
		this.addWindowListener(new windowsOnExit());
	}
	
	public void setUpInfoBoard(){
		statusBar = new JLabel("Trainer: " + gameModel.getTrainer().getID(), SwingConstants.LEFT);
		statusBar.setBounds(720, 25, 250, 30);
		statusBar.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(statusBar);
	}
	
	// show the mission board
	public void setUpMissionBoard(){
		missionBoard = new JLabel("<html>Mission Statistic:<br>" 
								+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
								+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>",SwingConstants.LEFT);
		missionBoard.setBounds(720, 100, 250, 80);
		missionBoard.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(missionBoard);
	}
	
	// show the inventory table
	public void setUpInventory(){
		inventoryTable = new JTable(gameModel.getTrainer().getInventory());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getTrainer().getInventory());
		inventoryTable.setRowSorter(sorter);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		inventoryTable.getColumn("Item Type").setCellRenderer( centerRenderer );		
		inventoryTable.getColumn("Quantity").setCellRenderer( centerRenderer );
		inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(40);

		JScrollPane pane = new JScrollPane(inventoryTable);
		pane.setBounds(720, 220, 270, 150);
		getContentPane().add(pane);
	}
	
	// add use item button
	public void setUpUseItemButton(){
		useItemButton = new JButton("Use Item");
		useItemButton.setBounds(720 + 305, 220, 100, 37);
		useItemButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
		getContentPane().add(useItemButton);
		useItemButton.addActionListener(new useItemButtonListener());
	}
	
	
	// show the inventory table
	public void setUpPokemonTable(){
		pokemonTable = new JTable(gameModel.getTrainer().getPokemonCollection());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getTrainer().getPokemonCollection());
		pokemonTable.setRowSorter(sorter);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		pokemonTable.getColumn("Pokedex Index").setCellRenderer( centerRenderer );		
		pokemonTable.getColumn("Captured Time").setCellRenderer( centerRenderer );
		pokemonTable.getColumn("Nickname").setCellRenderer( centerRenderer );
		pokemonTable.getColumn("Type").setCellRenderer( centerRenderer );
		pokemonTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		pokemonTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		pokemonTable.getColumnModel().getColumn(2).setPreferredWidth(40);
		
		JScrollPane pane = new JScrollPane(pokemonTable);
		pane.setBounds(720, 410, 405, 150);
		getContentPane().add(pane);
	}
		
	
	// saving the pokemon game data
	public void saveData(){
		try {
			FileOutputStream saveData_Pokemon = new FileOutputStream(SAVEFILENAME_GAME);
			ObjectOutputStream outFile_Pokemon = new ObjectOutputStream(saveData_Pokemon);
			outFile_Pokemon.writeObject(gameModel);
			outFile_Pokemon.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// loading saved JukeBox object from a file
	public void loadData() {
		try {
			FileInputStream prevData = new FileInputStream(SAVEFILENAME_GAME);
			ObjectInputStream inFile = new ObjectInputStream(prevData);
			
			gameModel = (GameModel) inFile.readObject();
			inFile.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
		
	// add the button listener for using the item button
	private class useItemButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable()){
				return;
			}
			String text = ((JButton) e.getSource()).getText();
			if (text.equals("Use Item") && currentView.getClass() == MainGameView.class){
				// check if there is any row selected
				if (inventoryTable.getSelectionModel().isSelectionEmpty()){
					return;
				}
				
				// interact with the selected row
				int index = inventoryTable.convertRowIndexToModel(inventoryTable.getSelectedRow());
				
				gameModel.getTrainer().useItem(index, gameModel.getTrainer());					
				
				// update the information table
				updateInfoBoard();
				
				// update the invertory table
				inventoryTable.repaint();
			}
			else if (text.equals("Use Item") && currentView.getClass() == BattleView.class){
				//System.out.println("Using Item during Battle");
				
				// check if there is any row selected
				if (inventoryTable.getSelectionModel().isSelectionEmpty()){
					return;
				}
				// interact with the selected row
				int index = inventoryTable.convertRowIndexToModel(inventoryTable.getSelectedRow());
				
				// use the item
				if (gameModel.getTrainer().getCurEncounterPokemon() != null ){					
					// notify the battle view if successfully use the item
					if (gameModel.getTrainer().checkItemUsable(index, gameModel.getTrainer().getCurEncounterPokemon())){
						// use the item
						gameModel.updateBattleView(gameModel.getTrainer().getInventory().getItem(index));
						gameModel.getTrainer().useItem(index, gameModel.getTrainer().getCurEncounterPokemon());
					}
				}
				// update the information table
				updateInfoBoard();
				
				// update the invertory table
				inventoryTable.repaint();
			}
		}
		
	}
	
	// update the information board
	private void updateInfoBoard(){
		// update infoboard
		missionBoard.setText("<html>Mission Statistic:<br>" 
					+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
					+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>");
		this.requestFocus();
	}
	
	// add the button listener for the item detail button
	private class checkItemButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}

	/***************************** Movement Control *********************************/
		
	// key board listener
	private class myKeyListener implements KeyListener {
		
		///////// MovementTimer /////////
		private Timer moveTimer;
		private int moveCounter = 0;
		
		private void startTimer() {
			moveTimer = new Timer(delayInMillis, new movementTimerListener());
			moveTimer.start();
		}
		
		private boolean isPressing = false;
		private boolean isActive = true;	// flag for the continue of the while loop

		@Override
		public void keyPressed(KeyEvent key) {
			isPressing = true;
			if (isPressing && currentView.getClass() == MainGameView.class){
				// loop to check if the key was loose or the game is over or the timer stop
				while (isPressing && !isOver && isActive && currentView.getClass() == MainGameView.class){
					isActive = false;
					if (key.getKeyCode() == KeyEvent.VK_UP) {
						gameModel.moveTrainer(Direction.NORTH);
					}
					if (key.getKeyCode() == KeyEvent.VK_DOWN) {
						gameModel.moveTrainer(Direction.SOUTH);
					}
					if (key.getKeyCode() == KeyEvent.VK_LEFT) {
						gameModel.moveTrainer(Direction.WEST);
					}
					if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
						gameModel.moveTrainer(Direction.EAST);
					}
					gameModel.setLocation(gameModel.getLocation().x, gameModel.getLocation().y);
					
					// update infoboard
					updateInfoBoard();
					// check win/lost
					checkGameResult();
					
					startTimer();
				}
			}


		}

		@Override
		public void keyReleased(KeyEvent key) {
			isPressing = false;
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}		
		
		// timer listener for the key listener
		private class movementTimerListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (moveCounter < framePerMove ){
					moveCounter++;
				}
				else{
					moveTimer.stop();
					isActive = true;
					moveCounter = 0;
					
					// check if encounter pokemon
					if (gameModel.getTrainer().getCurEncounterPokemon() != null){
						isPressing = false;
						mainGamePanel.setVisible(false);
					}
				}
			}
		}
	}
	
		
	// set up for the window on exit
	public class windowsOnExit implements WindowListener {

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			if (!isOver){
				int userPrompt = JOptionPane.showConfirmDialog(null, "Do you want to save the data?");
				// if the user choose yes, then save the data
				if (userPrompt == JOptionPane.YES_OPTION) {
					saveData();
					System.exit(0);
				}
				// if the user choose no, exit the program
				else if(userPrompt == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
				else{
					return;
				}
			}
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void checkGameResult(){
		if (gameModel.isWin()){
			missionBoard.setText("<html>YOU WIN<br>&nbsp;&nbsp;&nbsp;THE GAME IS OVER</html>");
			isWin = true;
			isOver = true;
		}
		
		if (gameModel.isLost()){
			missionBoard.setText("<html>YOU LOST<br>&nbsp;&nbsp;&nbsp;THE GAME IS OVER</html>");
			isLost = true;
			isOver = true;
		}
	}
	
	// detect the change in view
	public class viewChangeListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent e) {
			if (e.getComponent().getClass() == BattleView.class){
				setViewTo(mainGamePanel);
			}
			else if (e.getComponent().getClass() == MainGameView.class){
				// save the game before going into battle
				saveData();
				battlePanel.startBattle();
				setViewTo(battlePanel);
			}
			else{
				// TODO: 
			}
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentShown(ComponentEvent e) {
						
		}
		
	}
	
}
