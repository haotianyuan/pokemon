package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import GameModel.Direction;
import GameModel.GameModel;
import Inventory.ItemType;
import Mission.Difficulty;
import Mission.Mission;
import Mission.MissionType;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

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
	private JButton trainerInfoButton;
	private JButton inventoryButton;
	private JButton pokedexButton;
	
	
	// declare the main game view
	private JPanel currentView;
	private MissionType SelectedMissionType;
	
	private final static int View_OFFSET_X = 25; 
	private final static int View_OFFSET_Y = 25; 
	private static MainGameView mainGamePanel;
	private final static int DefaultGameHeight = 320;
	private final static int DefaultGameWidth = 480;
	
	// declare the battle view
	private static BattleView battlePanel;
	private final static int DefaultBattleHeight = 320;
	private final static int DefaultBattleWidth = 480;
	
	
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
			setUpMission();
			/*
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
			*/
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
			mainGamePanel.playTransitionAnimation();
			while (!mainGamePanel.InteractEnable());
			mainGamePanel.setVisible(false);
		}
	}
		
	private void initiatePokemonGame(){
				loadImage();
				setUpMainWindow();
				setUpBattleView();
				setUpGameView();		
				addEventListener();
				setUpInfoBoard();
				setUpMissionBoard();
				setUpInventoryTable();
				setUpPokemonTable();
				setUpButtons();

				addObservers();
				setViewTo(mainGamePanel);	// default starting view
				mainGamePanel.startGeneralTimer();
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
	
	/***************** Mission Selector *******************/
	private final void setUpMission(){		
		JDialog dialog = new JDialog(this, "Mission Selector", true);
		JPanel MissionSelector = new JPanel(new BorderLayout());
		MissionSelector.setLayout(new BorderLayout());
		MissionSelector.setBorder(new EmptyBorder(10, 10, 10, 10));
		MissionSelector.add(new JLabel("Please Select a Mission to Start. You Can Move Mouse Over the Button To See Details"), BorderLayout.NORTH);
		
		JPanel buttonPanel_0 = new JPanel(new FlowLayout());
		
		// Define button for choose mission
		JButton m0 = new JButton(Difficulty.TEST.name());
		m0.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.TEST;
						dialog.setVisible(false);
					}
				});
		
		JButton m1 = new JButton(Difficulty.CASUAL.name());
		m1.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.STANDARDLADDER;
						dialog.setVisible(false);
					}
				});
		
		JButton m2 = new JButton(Difficulty.EASY.name());
		m2.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.TWENTYPOKEMON;
						dialog.setVisible(false);
					}
				});
		
		JButton m3 = new JButton(Difficulty.NORMAL.name());
		m3.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.THIRTYPOKEMON;
						dialog.setVisible(false);
					}
				});
		
		buttonPanel_0.add(m0);
		buttonPanel_0.add(m1);
		buttonPanel_0.add(m2);
		buttonPanel_0.add(m3);
					
		// second line of button
		JPanel buttonPanel_1 = new JPanel(new FlowLayout());
		
		JButton m4 = new JButton(Difficulty.HARD.name());
		m4.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.FIFTYPOKEMON;
						dialog.setVisible(false);
					}
				});
		
		JButton m5 = new JButton(Difficulty.VERYHARD.name());
		m5.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.FIVEEPIC;
						dialog.setVisible(false);
					}
				});
		
		JButton m6 = new JButton(Difficulty.HELL.name());
		m6.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.STANDARDLADDER;
						dialog.setVisible(false);
					}
				});
		
		buttonPanel_1.add(m4);
		buttonPanel_1.add(m5);
		buttonPanel_1.add(m6);

		
		MissionSelector.add(buttonPanel_0, BorderLayout.CENTER);
		MissionSelector.add(buttonPanel_1, BorderLayout.SOUTH);
	
		
		dialog.setContentPane(MissionSelector);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        gameModel.setMission(new Mission(SelectedMissionType));
	}
	
	/********************* Add Component Into Pane ***********************/
	
	public void setUpMainWindow(){
		// define the location of the main window
		this.setTitle("Pokemon Safari Zone - Beta v0.9");
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
		mainGamePanel.setSize(DefaultGameWidth, DefaultGameHeight);
		mainGamePanel.setLocation(View_OFFSET_X, View_OFFSET_Y);
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
		battlePanel.setLocation(View_OFFSET_X, View_OFFSET_Y);
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
		// add regainfocus listener
		this.addMouseListener(new gainFocusClickListener());
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
								+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getTrainer().getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
								+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>",SwingConstants.LEFT);
		missionBoard.setBounds(720, 100, 250, 80);
		missionBoard.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(missionBoard);
	}
	
	// show the inventory table
	public JScrollPane setUpInventoryTable(){
		inventoryTable = new JTable(gameModel.getTrainer().getInventory());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getTrainer().getInventory());
		inventoryTable.setRowSorter(sorter);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		inventoryTable.getColumn("Item Type").setCellRenderer( centerRenderer );		
		inventoryTable.getColumn("Quantity").setCellRenderer( centerRenderer );
		inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		inventoryTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(inventoryTable);
		pane.setBounds(720, 220, 270, 150);
		getContentPane().add(pane);
		return pane;
	}
	
	//////////////////// Add Buttons ////////////////////
	public void setUpButtons(){
		setUpUseItemButton();
		setUpTrainerInfoButton();
		setUpInventoryButton();
		setUpPokedexButton();
	}
	
	// add use item button
	public void setUpUseItemButton(){
		useItemButton = new JButton("Use Item");
		useItemButton.setBounds(720 + 305, 220, 100, 37);
		useItemButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
		getContentPane().add(useItemButton);
		useItemButton.addActionListener(new useItemButtonListener());
	}
	
	// add user info button
	public void setUpTrainerInfoButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getTrainerInfoIcon());
		trainerInfoButton = new JButton(icon);
		trainerInfoButton.setBounds(25, 420, Trainer_Info_Width, Trainer_Info_Height);
		trainerInfoButton.setOpaque(false);
		trainerInfoButton.setContentAreaFilled(false);
		trainerInfoButton.setBorderPainted(false);
		trainerInfoButton.setFocusPainted(false);
		getContentPane().add(trainerInfoButton);
	}
		
	// add user info button
	public void setUpInventoryButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getBagInfoIcon());
		inventoryButton = new JButton(icon);
		inventoryButton.setBounds(150, 420, Bag_Info_Width, Bag_Info_Height);
		inventoryButton.setOpaque(false);
		inventoryButton.setContentAreaFilled(false);
		
		inventoryButton.setBorderPainted(false);
		inventoryButton.setFocusPainted(false);
		inventoryButton.addActionListener(new checkInventoryButtonListener());
		getContentPane().add(inventoryButton);
	}
	
	// add pokemon info button
	public void setUpPokedexButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getPokedexIcon());
		pokedexButton = new JButton(icon);
		pokedexButton.setBounds(290, 432, Pokedex_Width, Pokedex_Height);
		pokedexButton.setOpaque(false);
		pokedexButton.setContentAreaFilled(false);
		
		pokedexButton.setBorderPainted(false);
		pokedexButton.setFocusPainted(false);
		pokedexButton.addActionListener(new checkPokedexButtonListener());
		getContentPane().add(pokedexButton);
	}
	
	// show the inventory table
	public JScrollPane setUpPokemonTable(){
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
		pokemonTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(pokemonTable);
		pane.setBounds(720, 410, 405, 150);
		getContentPane().add(pane);
		
		return pane;
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
		
	/*
	 * *************************************** *
	 *  		Listener Creator Below         *
	 * *************************************** *
	 */
	/*************** Use Item Button ****************/
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
			}
			// update the information table
			updateInfoBoard();
			
			// update the invertory table
			inventoryTable.repaint();
			pokemonTable.repaint();
		}
		
	}
	
	/*************** Pokedex Button ****************/
	private class checkPokedexButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == pokedexButton && currentView.getClass() == MainGameView.class){
			    JFrame frame = new JFrame();
			    frame.setLayout(new BorderLayout());
			    JScrollPane newPane = setUpPokemonTable();
			    frame.add(newPane);
			    frame.pack();
			    frame.setLocationRelativeTo(null);
			    frame.setVisible(true);
			}
			
		}
		
	}
	
	// update the information board
	private void updateInfoBoard(){
		// update infoboard
		missionBoard.setText("<html>Mission Statistic:<br>" 
					+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getTrainer().getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
					+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>");
		this.requestFocus();
	}
	
	// add the button listener for the item detail button
	private class checkInventoryButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == inventoryButton && currentView.getClass() == MainGameView.class){
			    JFrame frame = new JFrame();
			    frame.setLayout(new BorderLayout());
			    JScrollPane newPane = setUpInventoryTable();
			    frame.add(newPane);
			    frame.pack();
			    frame.setLocationRelativeTo(null);
			    frame.setVisible(true);
			}
			
		}
		
	}

	/***************************** Movement Control *********************************/
	// declare timer detail
	public final static int delayInMillis = 50;
	public final static int framePerMove = 8;
		
	// key board listener
	private class myKeyListener implements KeyListener {
							
		private boolean isActive = false;

		// timer listener for the key listener
	

		@Override
		public void keyPressed(KeyEvent key) {
			isActive = true;
			if (!isOver && isActive && currentView.getClass() == MainGameView.class && mainGamePanel.InteractEnable()){
				// loop to check if the key was loose or the game is over or the timer stop					
					// after the moving done
						if (key.getKeyCode() == KeyEvent.VK_UP) {
							//System.out.println("Move to NORTH");
							gameModel.moveTrainer(Direction.NORTH);
						}
						if (key.getKeyCode() == KeyEvent.VK_DOWN) {
							//System.out.println("Move to SOUTH");
							gameModel.moveTrainer(Direction.SOUTH);
						}
						if (key.getKeyCode() == KeyEvent.VK_LEFT) {
							//System.out.println("Move to WEST");
							gameModel.moveTrainer(Direction.WEST);
						}
						if (key.getKeyCode() == KeyEvent.VK_RIGHT) {
							//System.out.println("Move to EAST");
							gameModel.moveTrainer(Direction.EAST);
						}						
						// update infoboard
						updateInfoBoard();
						// check win/lost
						checkGameResult();	
			}
			else{
				isActive = false;
			}


		}

		@Override
		public void keyReleased(KeyEvent key) {
			isActive = false;
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
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
				battlePanel.stopAllSoundTrack();
				mainGamePanel.startGeneralTimer();
				setViewTo(mainGamePanel);
			}
			else if (e.getComponent().getClass() == MainGameView.class){
				// save the game before going into battle
				saveData();
				mainGamePanel.stopPlayCurSound();
				// start general timer
				battlePanel.startGeneralTimer();
				battlePanel.startBattle();
				setViewTo(battlePanel);
			}
			else{
				// TODO: 
			}
			//inventoryTable.requestFocus();
			inventoryTable.repaint();
			
			//pokemonTable.requestFocus();
			pokemonTable.repaint();
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
	
	// regain focus when click on the game view
	private class gainFocusClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (x >= 25 && x <= 25 + DefaultGameWidth && y >= 25 && y <= 25 + DefaultGameHeight){
				System.out.println("Click on: " + x + ", " + y);
				requestFocus();
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	/*
	 * *************************************** *
	 *  		Painting Creator Below         *
	 * *************************************** *
	 */
	
	/******************* Define Sprite Sheet ********************/
	private BufferedImage trainerSheet;
	private BufferedImage bagSheet;
	private BufferedImage pokedexSheet;
	
	private final static String TextureFolderPath = "images" + File.separator + "Texture" + File.separator;
	private static final String TrainerTextureFileName = "pokemon_trainer.png";
	private static final String BagTextureFileName = "pokemon_bag_window.png";
	private static final String PokedexTextureFileName = "pokedex.png";
	
	
	
	private void loadImage(){
		loadTrainerTexture();
		loadBagTexture();
		loadPokedexTexture();
	}
	
	private void loadTrainerTexture() {
		String filePath = TextureFolderPath + TrainerTextureFileName;
		
		// try to open the file of the trainer
		try{
			File trainerTextureFile = new File(filePath);
			trainerSheet = ImageIO.read(trainerTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadBagTexture() {
		String filePath = TextureFolderPath + BagTextureFileName;
		
		// try to open the file of the trainer
		try{
			File bagTextureFile = new File(filePath);
			bagSheet = ImageIO.read(bagTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadPokedexTexture() {
		String filePath = TextureFolderPath + PokedexTextureFileName;
		
		// try to open the file of the trainer
		try{
			File pokdexTextureFile = new File(filePath);
			pokedexSheet = ImageIO.read(pokdexTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	
	/******************* Draw Trainer Info Icon ********************/
	
	private static final int Trainer_Info_OFFSET_X = 1015;
	private static final int Trainer_Info_OFFSET_Y = 272;
	private static final int Trainer_Info_Width = 71;
	private static final int Trainer_Info_Height = 163;
	
	private BufferedImage getTrainerInfoIcon(){
		return trainerSheet.getSubimage(Trainer_Info_OFFSET_X , Trainer_Info_OFFSET_Y,
				Trainer_Info_Width, Trainer_Info_Height);
	}
	
	
	/******************* Draw Bag Info Icon ********************/
	
	private static final int Bag_Info_OFFSET_X = 660;
	private static final int Bag_Info_OFFSET_Y = 5;
	private static final int Bag_Info_Width = 122;
	private static final int Bag_Info_Height = 163;
	
	private BufferedImage getBagInfoIcon(){
		return bagSheet.getSubimage(Bag_Info_OFFSET_X , Bag_Info_OFFSET_Y,
				Bag_Info_Width, Bag_Info_Height);
	}
	
	
	/******************* Draw Pokedex Info Icon ********************/
	
	private static final int Pokedex_OFFSET_X = 0;
	private static final int Pokedex_OFFSET_Y = 0;
	private static final int Pokedex_Width = 250;
	private static final int Pokedex_Height = 151;
	
	private BufferedImage getPokedexIcon(){
		return pokedexSheet.getSubimage(Pokedex_OFFSET_X , Pokedex_OFFSET_Y,
				Pokedex_Width, Pokedex_Height);
	}	
	
	
	/*
	 * *************************************** *
	 *  	  Dialog/OptionPanel Below         *
	 * *************************************** *
	 */
	

	
}

