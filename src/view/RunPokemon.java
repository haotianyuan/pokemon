package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.TextArea;
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
import java.util.ArrayList;
import java.util.Calendar;

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
import javax.swing.JTextArea;
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
import Inventory.Item;
import Inventory.ItemType;
import Mission.Difficulty;
import Mission.Mission;
import Mission.MissionType;
import Pokemon.Pokedex;
import Pokemon.Pokemon;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class RunPokemon extends JFrame {
	
	private static final long serialVersionUID = 7487437405760007377L;

	private String SAVEFILENAME_GAME = "PokemonGame.ser";
		
	// declare the main window
	private final static int DefaultHeight = 720;
	private final static int DefaultWidth = 600;
	
	//private JLabel statusBar;
	//private JLabel missionBoard;
	
	// declare the main game view
	private JDialog dialog;
	private JPanel currentView;
	private MissionType SelectedMissionType = MissionType.STANDARDLADDER;	// default mission type
	
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
	private String trainerName = "Ash";	// default name
	
	// main function
	public static void main(String[] args) {
		RunPokemon mainFrame = new RunPokemon();
		mainFrame.setVisible(true);
	}
	
	// constructor
	public RunPokemon(){	
		loadImage();
		int userPrompt = JOptionPane.showConfirmDialog(null, "Do you want to start with presvious saved data?");
		// if the user choose yes, load the saved file
		if (userPrompt == JOptionPane.YES_OPTION) {
			// check if save file exist
			File saveFile = new File(SAVEFILENAME_GAME);
			if (saveFile.exists()){
				loadData();
				initiatePokemonGame();
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
			startNewGame();
			//setUpMission();
			if (gameModel == null){
				gameModel = new GameModel(trainerName, SelectedMissionType);
			}
		}
		// chosen cancel
		else {
			System.exit(0);
		}
		// check if encounter a pokemon
		// start the battle
		if (gameModel.getTrainer().getCurEncounterPokemon() != null){
			mainGamePanel.playTransitionAnimation();
			while (!mainGamePanel.InteractEnable());
			mainGamePanel.setVisible(false);
		}
	}
		
	private void initiatePokemonGame(){
				gameModel.getMission().setTrainer(gameModel.getTrainer());
				setUpMainWindow();
				setUpBattleView();
				setUpGameView();		
				addEventListener();
				//setUpInfoBoard();
				setUpMissionTable();
				setUpInventoryTable();
				setUpPokemonTable();
				setUpButtons();

				addObservers();
				mainGamePanel.startGeneralTimer();
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
	
	/*****************  Game Starter *****************/
	
	/****************** Create Trainer ******************/
	private void startNewGame(){
		startLogginTimer();
		dialog = new JDialog(this, "Trainer Creator", true);
		dialog.addWindowListener(new WindowListener(){

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
				//stopLogginTimer();
				System.exit(0);
				
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
			
		});
		JPanel TrainerCreatorPanel = new JPanel(new BorderLayout()){
	
			private static final long serialVersionUID = -8811191521258893558L;

			//this.setPreferredSize(new Dimension(100, 600));
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(getLogginScreenTexture(), 0, 0, null);
            }
		};
		TrainerCreatorPanel.setPreferredSize(new Dimension(480, 320));
		//TrainerCreatorPanel.setLayout(new BorderLayout());
		TrainerCreatorPanel.setBorder(new EmptyBorder(140, 40, 80, 10));
		
		JButton inputButton = new JButton("Create");
		inputButton.setPreferredSize(new Dimension(80, 20));
		JTextArea inputNameArea = new JTextArea("Trainer Name");
		inputNameArea.setPreferredSize(new Dimension(80, 20));
		inputNameArea.setBackground(Color.WHITE);
		inputNameArea.selectAll();
		
		inputButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						if (inputNameArea.getText() != null || inputNameArea.getText() != ""){
							trainerName = inputNameArea.getText();
						}
						//dialog.dispose();
						dialog.setVisible(false);
						setUpMission();
					}
				});
		
		JPanel LeftBox = new JPanel(new BorderLayout());
		
		LeftBox.setBorder(new EmptyBorder(20, 5, 20, 5));
		LeftBox.setOpaque(false);
		LeftBox.add(inputNameArea,BorderLayout.NORTH);
		LeftBox.add(inputButton,BorderLayout.SOUTH);
		LeftBox.setPreferredSize(new Dimension(100, 20));
		
		TrainerCreatorPanel.add(LeftBox, BorderLayout.WEST);
		
		dialog.setContentPane(TrainerCreatorPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

	}
	
	/***************** Mission Selector *******************/
	
	private final void setUpMission(){		
		dialog = new JDialog(this, "Mission Selector", true);
		dialog.addWindowListener(new WindowListener(){

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
				//stopLogginTimer();
				System.exit(0);
				
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
			
		});
		JPanel LogginPanel = new JPanel(new BorderLayout()){

			private static final long serialVersionUID = 6739252419993318909L;

			//this.setPreferredSize(new Dimension(100, 600));
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(getLogginScreenTexture(), 0, 0, null);
            }
		};
		//LogginPanel.setBounds(8, 15, LogginScreen_Width, LogginScreen_Height);
		LogginPanel.setPreferredSize(new Dimension(480, 320));
		LogginPanel.setLayout(new BorderLayout());
		LogginPanel.setBorder(new EmptyBorder(15, 40, 130, 10));
		
		// set up the label for the loggin information
		JLabel logginInfo = new JLabel("", SwingConstants.LEFT);
		logginInfo.setFont(new Font("Times New Roman", Font.BOLD, 14));
		logginInfo.setForeground(Color.WHITE);
		//logginInfo.setBounds(5, 15, 200, 80);
		logginInfo.setText("<html>Please Select a Mission to Start<pre> You Can Move Mouse Over the Buttons<br>   To See Details</pre></html>");
		LogginPanel.add(logginInfo, BorderLayout.NORTH);
		
		// first line of button
		JPanel buttonPanel_0 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		// Define button for choose mission
		JButton m0 = new JButton(Difficulty.TEST.name());
		m0.setToolTipText("A TEST MISSION");
		m0.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.TEST;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		JButton m1 = new JButton(Difficulty.CASUAL.name());
		m1.setToolTipText("1000 STEPS LIMIT, FREE ROLLING");
		m1.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.STANDARDLADDER;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		JButton m2 = new JButton(Difficulty.EASY.name());
		m2.setToolTipText("TWENTY POKEMON REQUIRED WITHIN 500 STEPS");
		m2.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.TWENTYPOKEMON;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		JButton m3 = new JButton(Difficulty.NORMAL.name());
		m3.setToolTipText("THIRTY POKEMON REQUIRED WITHIN 500 STEPS");
		m3.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.THIRTYPOKEMON;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		buttonPanel_0.add(m0);
		buttonPanel_0.add(m1);
		buttonPanel_0.add(m2);
		buttonPanel_0.add(m3);
					
		// second line of button
		JPanel buttonPanel_1 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		JButton m4 = new JButton(Difficulty.HARD.name());
		m4.setToolTipText("FIFTY POKEMON REQUIRED WITHIN 500 STEPS");
		m4.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.FIFTYPOKEMON;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		JButton m5 = new JButton(Difficulty.VERYHARD.name());
		m5.setToolTipText("FIVE EPIC POKEMON REQUIRED WITHIN 500 STEPS");
		m5.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.FIVEEPIC;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		JButton m6 = new JButton(Difficulty.HELL.name());
		m6.setToolTipText("TRY TO CATCH A LENGENDARY POKEMON");
		m6.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent ae){
						SelectedMissionType = MissionType.FINDLENGEND;
						gameModel.setMission(new Mission(SelectedMissionType));
						dialog.setVisible(false);
						stopLogginTimer();
					}
				});
		
		buttonPanel_1.add(m4);
		buttonPanel_1.add(m5);
		buttonPanel_1.add(m6);

		buttonPanel_0.setOpaque(false);
		buttonPanel_1.setOpaque(false);
		LogginPanel.add(buttonPanel_0, BorderLayout.CENTER);
		LogginPanel.add(buttonPanel_1, BorderLayout.SOUTH);
	
		
		dialog.setContentPane(LogginPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
	}
	
	/********************* Add Component Into Pane ***********************/
	
	public void setUpMainWindow(){
		// define the location of the main window
		this.setTitle("Pokemon Safari Zone - Beta v0.9");
		this.setSize(DefaultWidth, DefaultHeight);
		//this.setPreferredSize(new Dimension(DefaultWidth, DefaultHeight));
		this.setResizable(false);
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
	
	/*
	public void setUpInfoBoard(){
		statusBar = new JLabel("Trainer: " + gameModel.getTrainer().getID(), SwingConstants.LEFT);
		statusBar.setBounds(720, 25, 250, 30);
		statusBar.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(statusBar);
	}
	*/
	
	// show the mission board
	public JScrollPane setUpMissionTable(){
		/*
		missionBoard = new JLabel("<html>Mission Statistic:<br>" 
								+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getTrainer().getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
								+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>",SwingConstants.LEFT);
		
		missionBoard.setBounds(520, 100, 250, 80);
		missionBoard.setFont(new Font("Times New Roman", Font.BOLD, 18));
		getContentPane().add(missionBoard);
		*/
		missionTable = new JTable(gameModel.getMission());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getMission());
		missionTable.setRowSorter(sorter);
		missionTable.setOpaque(false);
		missionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {{
            setOpaque(false);
            setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        }});
		//DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		//centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		//missionTable.getColumn("Requirement").setCellRenderer( centerRenderer );		
		//missionTable.getColumn("Progressing").setCellRenderer( centerRenderer );
		missionTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		missionTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		missionTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(missionTable);
		pane.setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setBounds(View_OFFSET_X, View_OFFSET_Y, TableWidth, TableHeight);
		//getContentPane().add(pane);
		return pane;
	}
	
	
	//////////////////// Add Buttons ////////////////////
	public void setUpButtons(){
		setUpTrainerInfoButton();
		setUpInventoryButton();
		setUpPokedexButton();
		
		setUpUseItemButton();
		setUpInspectItemButton();
		setUpInspectPokedexButton();
		setUpInspectMissionButton();
		setUpInspectPokemonEncounterButton();
	}
						
	/***************** Save/Load ******************/
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
	
	// loading saved object from a file
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
	
			
	// update the information board
	private void updateInfoBoard(){
		if (curTablePane != null){
			curTablePane.repaint();
		}
		/*
		// update infoboard
		missionBoard.setText("<html>Mission Statistic:<br>" 
					+ "&nbsp;&nbsp;&nbsp;Step Count: " + gameModel.getTrainer().getStepCount() + " / " + gameModel.getMission().getStepCap() + "<br>"
					+ "&nbsp;&nbsp;&nbsp;Total Pokemon Count: " + gameModel.getTrainer().getPokemonCollection().getSize() + " / " + gameModel.getMission().getTotalRequirement() + "</html>");
		*/
		this.requestFocus();
	}
		
	/*
	 * *************************************** *
	 *    		  Table Creator Below          *
	 * *************************************** *
	 */
	private final static int TableWidth = 460;
	private final static int TableHeight = 250;
	
	private JTable inventoryTable;
	private JTable missionTable;
	private JTable pokemonTable;
	private JTable trainerTable;
	private JTable pokedexTable;
	

	private JButton trainerInfoButton;
	private JButton inventoryButton;	
	private JButton pokedexButton;
		
	private JScrollPane trainerPane;
	private JScrollPane podexPane;
	private JScrollPane inventoryPane;
	private JScrollPane curTablePane;
	
	/*************** Trainer Info Button ****************/
	// add user info button
	public void setUpTrainerInfoButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getTrainerInfoIcon());
		trainerInfoButton = new JButton(icon);
		trainerInfoButton.setToolTipText("CHECK THE TRAINER INFORMATION");
		trainerInfoButton.setBounds(View_OFFSET_X, DefaultGameHeight + View_OFFSET_Y + 5, Trainer_Info_Width, Trainer_Info_Height);
		trainerInfoButton.setOpaque(false);
		trainerInfoButton.setContentAreaFilled(false);
		trainerInfoButton.setBorderPainted(false);
		trainerInfoButton.setFocusPainted(false);
		trainerInfoButton.addActionListener(new checkTrainerButtonListener());
		getContentPane().add(trainerInfoButton);
	}
		
	// show the inventory table
	public JScrollPane setUpTrainerInfoTable(){
		trainerTable = new JTable(gameModel.getTrainer());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getTrainer());
		trainerTable.setRowSorter(sorter);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		trainerTable.getColumn("Name").setCellRenderer( centerRenderer );		
		trainerTable.getColumn("Value").setCellRenderer( centerRenderer );
		trainerTable.getColumnModel().getColumn(0).setPreferredWidth(80);
		trainerTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		trainerTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(trainerTable);
		pane.setBounds(View_OFFSET_X, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, TableWidth, TableHeight);
		//getContentPane().add(pane);
		return pane;
	}
	
	private class checkTrainerButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable()){
				//return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == trainerInfoButton){
				if ( curTablePane != null){
					curTablePane.setVisible(false);
				}

				pokemonEncounterInspectButton.setVisible(false);
				useItemButton.setVisible(false);
				itemInspectButton.setVisible(false);
				missionInspectButton.setVisible(true);
				pokedexInspectButton.setVisible(false);
				curTablePane = setUpTrainerInfoTable();
				getContentPane().add(curTablePane);				
			}
			
		}
	}
	
		
	/*************** Pokedex Button ****************/
	// add pokemon info button
	public void setUpPokedexButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getPokedexIcon());
		pokedexButton = new JButton(icon);
		pokedexButton.setToolTipText("CHECK THE POKEDEX INFORMATION");
		pokedexButton.setBounds(View_OFFSET_X + 20 + 2 * Pokedex_Width, DefaultGameHeight + View_OFFSET_Y + 5, Pokedex_Width, Pokedex_Height);
		pokedexButton.setOpaque(false);
		pokedexButton.setContentAreaFilled(false);
		pokedexButton.setBorderPainted(false);
		pokedexButton.setFocusPainted(false);
		pokedexButton.addActionListener(new checkPokedexButtonListener());
		getContentPane().add(pokedexButton);
	}
	
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
		pane.setBounds(View_OFFSET_X, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, TableWidth, TableHeight);
		//getContentPane().add(pane);
		
		return pane;
	}
	
	// show the mission board
	public JScrollPane setUpPokedexTable(Pokemon p){	
		if (p == null){
			return null;
		}	

		pokedexTable = new JTable(p);
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(p);
		pokedexTable.setRowSorter(sorter);
		pokedexTable.setOpaque(false);
		pokedexTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 321653868345372859L;

		{
            setOpaque(false);
            setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        }});
		//DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		//centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		//missionTable.getColumn("Requirement").setCellRenderer( centerRenderer );		
		//missionTable.getColumn("Progressing").setCellRenderer( centerRenderer );
		pokedexTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		pokedexTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		//pokedexTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(pokedexTable);
		pane.setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setBounds(View_OFFSET_X, View_OFFSET_Y, TableWidth / 2, TableHeight - 100);
		//getContentPane().add(pane);
		return pane;
	}
	
	private class checkPokedexButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				//return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == pokedexButton){
				if ( curTablePane != null){
					curTablePane.setVisible(false);
				}
				
				useItemButton.setVisible(false);
				itemInspectButton.setVisible(false);
				missionInspectButton.setVisible(false);
				pokedexInspectButton.setVisible(true);
				pokemonEncounterInspectButton.setVisible(true);
				
				curTablePane = setUpPokemonTable();
				getContentPane().add(curTablePane);				
			}
			
		}
	}

	
	
	/*************** Inventory Button ****************/
	// add inventory button
	public void setUpInventoryButton(){
		// get the info icon image
		ImageIcon icon = new ImageIcon(getBagInfoIcon());
		inventoryButton = new JButton(icon);
		inventoryButton.setToolTipText("CHECK THE INVENTORY INFORMATION");
		inventoryButton.setBounds(View_OFFSET_X + 10 + Bag_Info_Width, DefaultGameHeight + View_OFFSET_Y + 5, Bag_Info_Width, Bag_Info_Height);
		inventoryButton.setOpaque(false);
		inventoryButton.setContentAreaFilled(false);		
		inventoryButton.setBorderPainted(false);
		inventoryButton.setFocusPainted(false);
		inventoryButton.addActionListener(new checkInventoryButtonListener());
		getContentPane().add(inventoryButton);
	}
	
	// show the inventory table
	public JScrollPane setUpInventoryTable(){
		inventoryTable = new JTable(gameModel.getTrainer().getInventory());
		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(gameModel.getTrainer().getInventory());
		inventoryTable.setRowSorter(sorter);
		inventoryTable.setOpaque(false);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		inventoryTable.getColumn("Item Type").setCellRenderer( centerRenderer );		
		inventoryTable.getColumn("Quantity").setCellRenderer( centerRenderer );
		inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(40);
		inventoryTable.setAutoCreateColumnsFromModel(true);
		
		JScrollPane pane = new JScrollPane(inventoryTable);
		pane.setOpaque(false);
		pane.setBounds(View_OFFSET_X, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, TableWidth, TableHeight);
		//getContentPane().add(pane);
		return pane;
		
	}
	
	private class checkInventoryButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable()){
				//return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == inventoryButton){
				if ( curTablePane != null){
					curTablePane.setVisible(false);
				}
				useItemButton.setVisible(true);
				itemInspectButton.setVisible(true);
				missionInspectButton.setVisible(false);
				pokedexInspectButton.setVisible(false);
				pokemonEncounterInspectButton.setVisible(false);
				curTablePane = setUpInventoryTable();
				getContentPane().add(curTablePane);				
			}
			
		}
	}
	
	/*
	// add the button listener for the item detail button
	private class checkItemButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				//return;
			}
			Object obj = ((JButton) e.getSource());
			if (obj == inventoryButton){
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
	*/
	
	/*
	 * *************************************** *
	 *    		 Button Creator Below          *
	 * *************************************** *
	 */
	
	private JButton useItemButton;
	private JButton itemInspectButton;
	private JButton missionInspectButton;
	private JButton pokedexInspectButton;
	private JButton pokemonEncounterInspectButton;
	
	//////////////// Check Item Button /////////////////////
	
	public void setUpInspectItemButton(){
		ImageIcon icon = new ImageIcon(TextureFolderPath + "inspect.png");
		itemInspectButton = new JButton(icon);
		itemInspectButton.setToolTipText("CHECK THE SELECTED ITEM DESCRIPTION");
		itemInspectButton.setBounds(View_OFFSET_X + TableWidth + 6, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, 
								TableButton_Width, TableButton_Height);
		itemInspectButton.setOpaque(false);
		itemInspectButton.setContentAreaFilled(false);
		itemInspectButton.setBorderPainted(false);
		itemInspectButton.setFocusPainted(false);
		itemInspectButton.setVisible(false);
		itemInspectButton.addActionListener(new itemInspectButtonListener());
		getContentPane().add(itemInspectButton);
	}
	
	private class itemInspectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (inventoryTable.getSelectionModel().isSelectionEmpty()){
				return;
			}
			
			// interact with the selected row
			int index = inventoryTable.convertRowIndexToModel(inventoryTable.getSelectedRow());
			
			Item item = gameModel.getTrainer().getInventory().getItem(index);
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(null, "<html>" + item.getName() + ":<br><pre>  " + item.getInfo() + "</pre></html>");
		}
		
	}
	
	
	//////////////// Check Mission Button /////////////////////
	
	public void setUpInspectMissionButton(){
		ImageIcon icon = new ImageIcon(TextureFolderPath + "inspect.png");
		missionInspectButton = new JButton(icon);
		missionInspectButton.setToolTipText("CHECK MISSION PROGRESS");
		missionInspectButton.setBounds(View_OFFSET_X + TableWidth + 6, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, 
				TableButton_Width, TableButton_Height);
		missionInspectButton.setOpaque(false);
		missionInspectButton.setContentAreaFilled(false);
		missionInspectButton.setBorderPainted(false);
		missionInspectButton.setFocusPainted(false);
		missionInspectButton.setVisible(false);
		missionInspectButton.addActionListener(new missionInspectButtonListener());
		getContentPane().add(missionInspectButton);
	}
	
	private class missionInspectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			popMissionInfoBoard();
		}
		
	}
	
	private void popMissionInfoBoard(){
		dialog = new JDialog(this, "Mission Information", true);
		dialog.addWindowListener(new WindowListener(){

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
				//stopLogginTimer();
				//requestFocus();
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
			
		});
 
		JPanel Panel = new JPanel(new BorderLayout()){
	
			private static final long serialVersionUID = -8811191521258893558L;

			//this.setPreferredSize(new Dimension(100, 600));
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(getMissionBackgroundTexture(), 0, 0, null);
            }
		};
		Panel.setPreferredSize(new Dimension(400, 217));
	    JScrollPane newPane = setUpMissionTable();
	    Panel.setBorder(new EmptyBorder(20, 10, 20, 10 ));
		Panel.add(newPane, BorderLayout.CENTER);
		
		dialog.setContentPane(Panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

	}
	
	
	//////////////// Check Pokemon Encounter Button /////////////////////
	
	public void setUpInspectPokemonEncounterButton(){
		ImageIcon icon = new ImageIcon(TextureFolderPath + "inspect2.png");
		pokemonEncounterInspectButton = new JButton(icon);
		pokemonEncounterInspectButton.setToolTipText("CHECK THE BATTLED POKEMON");
		pokemonEncounterInspectButton.setBounds(520, 100, TableButton_Width, TableButton_Height);
		pokemonEncounterInspectButton.setOpaque(false);
		pokemonEncounterInspectButton.setContentAreaFilled(false);
		pokemonEncounterInspectButton.setBorderPainted(false);
		pokemonEncounterInspectButton.setFocusPainted(false);
		pokemonEncounterInspectButton.setVisible(false);
		pokemonEncounterInspectButton.addActionListener(new pokedexInspectButtonListener());
		getContentPane().add(pokemonEncounterInspectButton);
	}
	
	private class pokedexInspectButtonListener implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				//return;
			}
			if (currentView != battlePanel){
				return;
			}
			
			if (gameModel.getTrainer().getCurEncounterPokemon() != null && currentView == battlePanel){
				popPokedexBoard(gameModel.getTrainer().getCurEncounterPokemon());
			}
		}
		
	}
	
	
	//////////////// Check Current Pokemon Button /////////////////////
	
	public void setUpInspectPokedexButton(){
		ImageIcon icon = new ImageIcon(TextureFolderPath + "inspect.png");
		pokedexInspectButton = new JButton(icon);
		pokedexInspectButton.setToolTipText("CHECK THE SELECTED POKEMON");
		pokedexInspectButton.setBounds(View_OFFSET_X + TableWidth + 6, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 10, 
				TableButton_Width, TableButton_Height);
		pokedexInspectButton.setOpaque(false);
		pokedexInspectButton.setContentAreaFilled(false);
		pokedexInspectButton.setBorderPainted(false);
		pokedexInspectButton.setFocusPainted(false);
		pokedexInspectButton.setVisible(false);
		pokedexInspectButton.addActionListener(new pokemonEncounterInspectButtonListener());
		getContentPane().add(pokedexInspectButton);
	}
	
	private class pokemonEncounterInspectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable() && !mainGamePanel.InteractEnable()){
				//return;
			}
			
			// check if there is any row selected
			if (pokemonTable.getSelectionModel().isSelectionEmpty()){
				return;
			}			
			// interact with the selected row
			int index = pokemonTable.convertRowIndexToModel(pokemonTable.getSelectedRow());
			
			Pokemon p = gameModel.getTrainer().getPokemonCollection().getPokemon(index);	
			popPokedexBoard(p);
			
		}
	}
		
	private void popPokedexBoard(Pokemon p){
		dialog = new JDialog(this, "Pokemon Information", true);
		dialog.addWindowListener(new WindowListener(){

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
				//stopLogginTimer();
				//requestFocus();
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
			
		});
 
		JPanel Panel = new JPanel(new BorderLayout()){
	
			private static final long serialVersionUID = -8811191521258893558L;

			//this.setPreferredSize(new Dimension(100, 600));
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(getPokedexBackgroundTexture(), 0, 0, null);
                g.drawImage(drawPokemonA(p.getSpecy()), 80, 160, null);
                g.drawImage(drawPokemonB(p.getSpecy()), 240, 160, null);
            }
		};
		Panel.setPreferredSize(new Dimension(PokedexBackground_Width, PokedexBackground_Height));
	    JScrollPane newPane = setUpPokedexTable(p);
	    Panel.setBorder(new EmptyBorder(5, 10, 5, 10 ));
		Panel.add(newPane, BorderLayout.EAST);
		JTextArea textArea = new JTextArea(p.getIntro());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setOpaque(false);
		
		Panel.add(textArea, BorderLayout.SOUTH);
		
		dialog.setContentPane(Panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
	}
	
	
	/////////////////// Use Item Button /////////////////////
	// add use item button
	private final static int TableButton_Width = 40;
	private final static int TableButton_Height = 40;
	
	public void setUpUseItemButton(){
		ImageIcon icon = new ImageIcon(TextureFolderPath + "useicon.png");
		useItemButton = new JButton(icon);
		useItemButton.setBounds(View_OFFSET_X + TableWidth + 6, DefaultGameHeight + View_OFFSET_Y + Pokedex_Height + 60, 
								TableButton_Width, TableButton_Height);
		useItemButton.setOpaque(false);
		useItemButton.setContentAreaFilled(false);
		useItemButton.setBorderPainted(false);
		useItemButton.setFocusPainted(false);
		useItemButton.setVisible(false);
		useItemButton.addActionListener(new useItemButtonListener());
		getContentPane().add(useItemButton);
	}
	
	private class useItemButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!battlePanel.InteractEnable()){
				return;
			}
			//String text = ((JButton) e.getSource()).getText();
			if (currentView.getClass() == MainGameView.class){
				// check if there is any row selected
				if (inventoryTable.getSelectionModel().isSelectionEmpty()){
					return;
				}
				
				// interact with the selected row
				int index = inventoryTable.convertRowIndexToModel(inventoryTable.getSelectedRow());
				
				gameModel.getTrainer().useItem(index, gameModel.getTrainer());					
				
			}
			else if (currentView.getClass() == BattleView.class){
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
	
	// TODO: Victory Summary
	public void checkGameResult(){
		if (gameModel.isWin()){
			//missionBoard.setText("<html>YOU WIN<br>&nbsp;&nbsp;&nbsp;THE GAME IS OVER</html>");
			isWin = true;
			isOver = true;
			mainGamePanel.stopGeneralTimer();
			mainGamePanel.stopPlayCurSound();
			//this.setVisible(false);
			JOptionPane.showMessageDialog(this, "GG ! YOU WON THIS. CLOSE THE DIALOG TO CHECK YOUR STATUS. YOU GOT 15s");
			//curBackMusicFileName = "end_victory.mp3";
			//this.playBackgroundMusic();
			startEndTimer();
		}
		
		if (gameModel.isLost()){
			//missionBoard.setText("<html>YOU LOST<br>&nbsp;&nbsp;&nbsp;THE GAME IS OVER</html>");
			isLost = true;
			isOver = true;
			//this.setVisible(false);
			//this.popMissionInfoBoard();
			JOptionPane.showMessageDialog(this, "SIGH ! YOU DID NOT MAKE THIS. CLOSE THE DIALOG TO CHECK YOUR STATUS. YOU GOT 15s");
			startEndTimer();
		}
	}
	
	public void itemLootAfterBattle(){
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		ItemType item = gameModel.generateLoot( hour % 6 * 0.08 + 0.4);
		if (item != null){
			if (gameModel.getTrainer().justCaught){
				playItemLootMusic();
			}
			gameModel.getTrainer().addItem(item);
			
			// show loot notification
			JOptionPane.showMessageDialog(this, "YOU FOUND '" + item.name() + "' FROM THE BATTLEFIELD");
			if (curTablePane != null){
				curTablePane.repaint();
			}
		}
		

		
		// another loot chance if just caught
		if (gameModel.getTrainer().justCaught){
			gameModel.getTrainer().justCaught = false;
			itemLootAfterBattle();
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
				
				// TODO: loot pop up
				itemLootAfterBattle();
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
				//currentView.requestFocus();
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
	private BufferedImage mainMenuSheet;
	
	private final static String TextureFolderPath = "images" + File.separator + "Texture" + File.separator;
	private static final String TrainerTextureFileName = "pokemon_trainer.png";
	private static final String BagTextureFileName = "pokemon_bag_window.png";
	private static final String PokedexTextureFileName = "pokemon_pokemons.png";	
	private static final String MainMenuTextureFileName = "pokemon_main_menu.png";
	
	
	private void loadImage(){
		loadTrainerTexture();
		loadBagTexture();
		loadPokedexTexture();
		loadLogScreen();
		loadMainMenuTexture();
		loadPokemonTexture();
		loadItemTexture();
		loadMissionBackground();
		loadPokedexBackground();
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
	
	private void loadMainMenuTexture() {
		String filePath = TextureFolderPath + MainMenuTextureFileName;
		
		// try to open the file of the trainer
		try{
			File mainTextureFile = new File(filePath);
			mainMenuSheet = ImageIO.read(mainTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	
	/******************* Draw Trainer Info Icon ********************/
	
	private static final int Trainer_Info_OFFSET_X = 173;
	private static final int Trainer_Info_OFFSET_Y = 76;
	private static final int Trainer_Info_Width = 162;
	private static final int Trainer_Info_Height = 54;
	
	private BufferedImage getTrainerInfoIcon(){
		return mainMenuSheet.getSubimage(Trainer_Info_OFFSET_X , Trainer_Info_OFFSET_Y,
				Trainer_Info_Width, Trainer_Info_Height);
	}
	
	
	/******************* Draw Bag Info Icon ********************/
	
	private static final int Bag_Info_OFFSET_X = 3;
	private static final int Bag_Info_OFFSET_Y = 76;
	private static final int Bag_Info_Width = 162;
	private static final int Bag_Info_Height = 54;
	
	private BufferedImage getBagInfoIcon(){
		return mainMenuSheet.getSubimage(Bag_Info_OFFSET_X , Bag_Info_OFFSET_Y,
				Bag_Info_Width, Bag_Info_Height);
	}
	
	
	/******************* Draw Pokedex Info Icon ********************/
	
	private static final int Pokedex_OFFSET_X = 3;
	private static final int Pokedex_OFFSET_Y = 10;
	private static final int Pokedex_Width = 162;
	private static final int Pokedex_Height = 54;
	
	private BufferedImage getPokedexIcon(){
		return mainMenuSheet.getSubimage(Pokedex_OFFSET_X , Pokedex_OFFSET_Y,
				Pokedex_Width, Pokedex_Height);
	}	
	
	/******************* Draw Loggin Screen ********************/
	
	private static BufferedImage LogginScreen;
	private final static String LogginScreenFileName = "LogginScreen_2.png";
	private final static int LogginScreen_Width = 480;
	private final static int LogginScreen_Height = 320;
	
	private void loadLogScreen(){
		String filePath = TextureFolderPath + LogginScreenFileName;
		
		// try to open the file of the trainer
		try{
			File logFile = new File(filePath);
			LogginScreen = ImageIO.read(logFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private BufferedImage getLogginScreenTexture(){
		return LogginScreen.getSubimage(0 , 0, LogginScreen_Width, LogginScreen_Height);
	}
	
	/******************* Draw Mission Background ********************/
	private static BufferedImage MissionBackgroundSheet;
	private final static String MissionBackgroundFileName = "mission_background.png";
	private final static int MissionBackground_Width = 400;
	private final static int MissionBackground_Height = 217;
	
	private void loadMissionBackground(){
		String filePath = TextureFolderPath + MissionBackgroundFileName;
		
		// try to open the file of the trainer
		try{
			File logFile = new File(filePath);
			MissionBackgroundSheet = ImageIO.read(logFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private BufferedImage getMissionBackgroundTexture(){
		return MissionBackgroundSheet.getSubimage(0 , 0, MissionBackground_Width, MissionBackground_Height);
	}
	
	
	/******************* Draw Pokedex Background ********************/
	private static BufferedImage PokedexBackgroundSheet;
	private final static String PokedexBackgroundFileName = "pokedex_background.png";
	private final static int PokedexBackground_Width = 480;
	private final static int PokedexBackground_Height = 360;
	
	private void loadPokedexBackground(){
		String filePath = TextureFolderPath + PokedexBackgroundFileName;
		
		// try to open the file of the trainer
		try{
			File logFile = new File(filePath);
			PokedexBackgroundSheet = ImageIO.read(logFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private BufferedImage getPokedexBackgroundTexture(){
		return PokedexBackgroundSheet.getSubimage(0 , 0, PokedexBackground_Width, PokedexBackground_Height);
	}
	
	
	/***************** Loggin Timer ******************/
	////////////Item Timer ////////////
	private Timer logginTimer;
	private int logginCounter;

	public void startLogginTimer() {
		playBackgroundMusic();
		logginCounter = 0;
		logginTimer = new Timer(delayInMillis * 20, new generalTimerListener());
		logginTimer.start();
	}
	
	public void stopLogginTimer(){
		logginCounter = 0;
		logginTimer.stop();
		this.initiatePokemonGame();
		stopPlayCurSound();
		
	}

	private class generalTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// reset the counter when go beyond 50
			if (MyBackgroundMusicPlayer.getStatus()  == 2){
				playBackgroundMusic();
			}		
			logginCounter++;
		}
	}
	/*
	 * *************************************** *
	 *  	        Paint/Draw Below           *
	 * *************************************** *
	 */
	
	/******************* Overall Background Screen ********************/
	
	private static BufferedImage BackgroundScreen;
	private final static String BackgroundScreenFileName = "LogginScreen_2.png";
	private final static int BackgroundScreen_Width = 480;
	private final static int BackgroundScreen_Height = 320;
	
	private void loadBackground(){
		String filePath = TextureFolderPath + BackgroundScreenFileName;
		
		// try to open the file of the trainer
		try{
			File logFile = new File(filePath);
			BackgroundScreen = ImageIO.read(logFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private BufferedImage getBackgroundScreenTexture(){
		return BackgroundScreen.getSubimage(0 , 0, BackgroundScreen_Width, BackgroundScreen_Height);
	}
	
	
	/********************** Draw Pokemon **********************/
	// declare image sheet
	private static BufferedImage pokemonSheet;
	private static final String pokemonTextureFileName = "pokemon_pokemons.png";
	
	private final static int Pokemon_Height = 126;
	private final static int Pokemon_Width = 126;
	
	private void loadPokemonTexture() {
		// try to open the file of pokemon textures
		try{
			File pokemonTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					pokemonTextureFileName);
			pokemonSheet = ImageIO.read(pokemonTextureFile);
			generateOFFSETList();
		}
		catch (IOException e){
			System.out.println("Could not find: " + pokemonTextureFileName);
		}
		
	}
	
	// define the upper left point for all pokemon on the sheet
	private static ArrayList<Point> PokemonOFFSET_A;	// OFFSET list for pokemon first image
	private static ArrayList<Point> PokemonOFFSET_B;	// OFFSET list for pokemon second image
	
	private final void generateOFFSETList(){
		PokemonOFFSET_A = new ArrayList<Point>();
		PokemonOFFSET_B = new ArrayList<Point>();
		for (int i = 1; i <= 151; i++){
			// get row number and col number for offset
			int row = i / 5;
			int col = i % 5;
			if (col == 0){
				col = 5;
				row = row - 1;
			}			
			// calculate for offset 01
			// create a new point object
			Point p1 = new Point();
			int x1 = 11 + 138 * (col * 2 - 2);
			int y1 = 11 + 138 * row;
			p1.setLocation(x1,  y1);
			PokemonOFFSET_A.add(p1);
			
			// calculate for offset 02
			// create a new point object
			Point p2 = new Point();
			int x2 = 11 + 138 * (col * 2 - 1);
			int y2 = 11 + 138 * row;
			p2.setLocation(x2,  y2);
			PokemonOFFSET_B.add(p2);
		}
	}
	
	private Point getPokemonOFFSET_A(Pokedex type){
		return PokemonOFFSET_A.get(type.getIndex() - 1);
		
	}
	
	private Point getPokemonOFFSET_B(Pokedex type){
		return PokemonOFFSET_B.get(type.getIndex() - 1);
	}
	
	
	private BufferedImage drawPokemonA(Pokedex type){
		// get the offset point
		Point p = this.getPokemonOFFSET_A(type);
		return pokemonSheet.getSubimage(p.x, p.y, 
				Pokemon_Width, Pokemon_Height);
	}
	
	private BufferedImage drawPokemonB(Pokedex type){
		// get the offset point
		Point p = this.getPokemonOFFSET_B(type);
		return pokemonSheet.getSubimage(p.x, p.y, 
				Pokemon_Width, Pokemon_Height);
	}
		
	
	/******************* Draw Icon *******************/
	private static final String itemTextureFileName = "pokemon_item.png";
	private static BufferedImage itemSheet;
	
	private void loadItemTexture() {
		// try to open the file of the item texture
		try{
			File itemTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					itemTextureFileName);
			itemSheet = ImageIO.read(itemTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + itemTextureFileName);
		}	
	}
	
	/*
	 * *************************************** *
	 *  	  Dialog/OptionPanel Below         *
	 * *************************************** *
	 */
	
	
	/*
	 * *************************************** *
	 *  	  SoundTrack Creator Below         *
	 * *************************************** *
	 */
	private BasicPlayer MyBackgroundMusicPlayer;
	private Thread backgroundPlayerThread;
	private static String curBackMusicFileName = "loggin_music.mp3";
	private final static String soundtrackFolder = "soundtrack" + File.separator;
	
	public void playBackgroundMusic() {
	    try {
	    	stopPlayCurSound();
	    	File file = new File(soundtrackFolder + curBackMusicFileName);
	    	MyBackgroundMusicPlayer = new BasicPlayer();
	    	MyBackgroundMusicPlayer.open(file);
	    } 
	    catch (Exception e) {
	        System.err.printf("%s\n", e.getMessage());
	    }

	    backgroundPlayerThread = new Thread() {
	    	@Override
	    	public void run() {
	    		try {
	    			MyBackgroundMusicPlayer.play();
	    		} 
	    		catch (Exception e) {
	    			System.err.printf("%s\n", e.getMessage());
	    		}
	    	}
	    };
	    
	    backgroundPlayerThread.start();
	}
	
	public void stopPlayCurSound(){
		if (MyBackgroundMusicPlayer != null) {
			try {
				MyBackgroundMusicPlayer.stop();
			} catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
		if (backgroundPlayerThread != null){
			backgroundPlayerThread.interrupt();
		}
	}
	
	/************* Play Item Get ***************/
	private BasicPlayer MyItemLootPlayer;
	private Thread ItemLootPlayerThread;
	private static String ItemLootFileName = "obtain_item.mp3";
	
	public void playItemLootMusic() {
	    try {
	    	stopPlayCurItemLoot();
	    	File file = new File(soundtrackFolder + ItemLootFileName);
	    	MyItemLootPlayer = new BasicPlayer();
	    	MyItemLootPlayer.open(file);
	    } 
	    catch (Exception e) {
	        System.err.printf("%s\n", e.getMessage());
	    }

	    ItemLootPlayerThread = new Thread() {
	    	@Override
	    	public void run() {
	    		try {
	    			MyItemLootPlayer.play();
	    		} 
	    		catch (Exception e) {
	    			System.err.printf("%s\n", e.getMessage());
	    		}
	    	}
	    };
	    
	    ItemLootPlayerThread.start();
	}
	
	public void stopPlayCurItemLoot(){
		if (MyItemLootPlayer != null) {
			try {
				MyItemLootPlayer.stop();
			} catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
		if (ItemLootPlayerThread != null){
			ItemLootPlayerThread.interrupt();
		}
	}	
	
	
	
	/***************** End Timer ******************/
	////////////Item Timer ////////////
	private Timer endTimer;
	private int endCounter;

	public void startEndTimer() {
		if (isLost = true){
			curBackMusicFileName = "lose_dva.mp3";
		}
		else{
			curBackMusicFileName = "gg.mp3";
		}
		playBackgroundMusic();
		endCounter = 0;
		endTimer = new Timer(delayInMillis * 20, new endTimerListener());
		endTimer.start();
	}
	

	private class endTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// reset the counter when go beyond 50	
			endCounter++;
			if (endCounter > 15){
				endTimer.stop();
				System.exit(0);
			}
		}
	}
	
}

