package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import GameModel.Direction;
import GameModel.GameModel;
import Inventory.ItemType;
import Map.GroundType;
import Pokemon.Pokedex;
import Pokemon.Pokemon;

public class BattleView extends JPanel implements Observer{
	
	private static final long serialVersionUID = 784618488834701873L;
	
	// declare image sheet
	private BufferedImage pokemonSheet;
	private BufferedImage battleField;
	private BufferedImage effectSheet;
	private BufferedImage menuSheet;
	private BufferedImage barSheet;
	private BufferedImage trainerSheet;
	
	// game model
	private GameModel model;
	private int curTurn;
	
	// combat pokemon information
	private Pokemon curPokemon;	// current encountering pokemon

	
	// declare variables for animation
	private boolean battleEnd = true;	// flag to show that the battle is end
	
	private final static int delayInMillis = 15;
	private final static double PixelPerFrame = 4;
	private final static int MoveInPixel = 300;		// store the pixel that trainer and pokemon move in
		
	// location for the trainer on the battlefield
	private final static int TrainerStartLocation_X = 429;
	private int trainerMidLocation_X;
	private int trainerMidLocation_Y;
	private int trainerCurWidth;
	private int trainerCurHeight;
	
	// location for the pokemon on the battlefield
	private final static int PokemonStartLocation_X = 53;
	private int pokemonMidLocation_X;
	private int pokemonMidLocation_Y;
	private int pokemonCurWidth;
	private int pokemonCurHeight;
	
	private ItemType usingItemType = null;
		
	
	// constructor
	public BattleView(){
		loadImages();
		initData();
		repaint();
		this.addMouseListener(new ClickListener());
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (battleEnd == false){
			model = (GameModel) o;
			
			// play opening animation
			if (!openingStarted){
				initData();
				playOpeningAnimation();
			}
			
			// play use item animation
			if(arg != null && !itemUsingStarted){
				// raise the item using flag				
				usingItemType = (ItemType) arg;
				playItemAnimation(usingItemType);
				System.out.println(usingItemType.getClass().getName());
				// TODO: need item animation
			}
			
			
			repaint();
		}
		
	}
					
	// initiate the data when the battle begin
	private void initData(){
		// initiate the data when construct it
		if (model == null){
			curPokemon = null;
			return;
		}
		
		resetData();
		
		// get the encountered pokemon
		setCurPokemon(model.getTrainer().getCurEncounterPokemon());
		
		// calcualte and set the data for pokemon basing on the trainer data
		double bonusRunChance = model.getTrainer().getBonusRun();
		double bonusCapRate = model.getTrainer().getBonusCapture();
		int bonusTurn = model.getTrainer().getBonusTurn();
				
		curPokemon.incrementCapRate(bonusCapRate);
		curPokemon.decrementRunChance(bonusRunChance);
		curPokemon.incrementMaxTurn(bonusTurn);
	}
	
	// this will be called in initiation
	private void resetData(){
		usingItemType = null;	// reset the using item
		curTurn = 0;	// reset the turn count

		// Date for the opening animation
		// location for the trainer on the battlefield
		trainerMidLocation_X = TrainerStartLocation_X; 
		trainerMidLocation_Y = BattleField_Height;
		// location for the pokemon on the battlefield
		pokemonMidLocation_X = PokemonStartLocation_X;
		pokemonMidLocation_Y = BattleField_Height - 60;
	}
		
	public void setCurPokemon(Pokemon pokemon){
		this.curPokemon = pokemon;
	}
	
	public void startBattle(){
		battleEnd = false;
		openingStarted = false;		
	}
	
	private void endBattle(){
		battleEnd = true;
		model.getTrainer().setCurEncounterPokemon(null);
		model.setEncounteredThisBlock(false);
	}
	
	
		
	// click on run set the visibility of the view and raise the end flag
	private class ClickListener implements MouseListener {
		private final static int RunButtonHeight = 30;
		private final static int RunButtonWidth = 45;
		private final static int RunButtonHeight_OFFSET = 10;
		private final static int RunButtonWidth_OFFSET = 15;

		@Override
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			System.out.println("Click on: " + x + ", " + y);
			
			if (!battleEnd){
				//int x = e.getX();
				//int y = e.getY();
				if (x >= 480 - RunButtonWidth - RunButtonWidth_OFFSET && x < 480 - RunButtonWidth_OFFSET 
						&& y >= 320 - RunButtonHeight - RunButtonHeight_OFFSET && y < 320 - RunButtonHeight_OFFSET){
					//System.out.println("Click on: " + x + ", " + y);
					endBattle();
					setVisible(false);
				}
			}			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	

	/*
	 * *************************************** *
	 *  Painting and Animation Creator Below   *
	 * *************************************** *
	 */
	
	//////////////////////////// Draw Move In Animation /////////////////////////////
	
	public boolean InteractEnable(){
		return openingEnd && itemUsingEnd;
	}
	
	/******** Opening of the Battle *******/
	private boolean openingStarted = false;	// flag to check if the openning animation has been played
	private boolean openingEnd = true;	// flag to check if the moving animation is over and enable the listener
	private void playOpeningAnimation(){
		openingStarted = true;
		openingEnd = false;		
		startMoveInTimer();
	}
	
	/************* Move In Timer at the opening*************/
	private Timer moveInTimer;
	private int moveInCounter;
	
	private void startMoveInTimer() {
		moveInCounter = 0;
		moveInTimer = new Timer(delayInMillis, new moveInTimerListener());
		moveInTimer.start();
	}
	
	private class moveInTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//System.out.println(moveInCounter + "");
			
			if (moveInCounter < MoveInPixel/PixelPerFrame){
				moveInCounter++;
				// set the location of the trainer and the pokemon
				trainerMidLocation_X -= PixelPerFrame;
				
				// pokemon move in later
				if (moveInCounter < 50){
					pokemonMidLocation_X += (int)(PixelPerFrame * 1.5);
				}
				repaint();
			}
			else{
				openingEnd = true;
				moveInTimer.stop();
				moveInCounter = 0;
			}
			
		}
	}
	
	/******** Using an Item and play effect *******/
	private boolean itemUsingStarted = false;	// flag to check if using item animation started
	private boolean itemUsingEnd = true;
	private void playItemAnimation(ItemType type){
		itemUsingStarted = true;
		itemUsingEnd = false;
	}
	
	/************* Item Timer *************/
	private Timer itemTimer;
	private int itemCounter;
	
	private void startItemTimer() {
		itemCounter = 0;
		itemTimer = new Timer(delayInMillis, new itemTimerListener());
		itemTimer.start();
	}
	
	private class itemTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//System.out.println(moveInCounter + "");
			
			if (itemCounter < MoveInPixel/PixelPerFrame){
				// TODO: algorithm to draw the curve

				repaint();
			}
			else{
				openingEnd = true;
				moveInTimer.stop();
				moveInCounter = 0;
			}
			
		}
	}
	
	/////////////////////////// Define sprite sheet name ///////////////////////////
	private static final String pokemonTextureFileName = "pokemon_pokemons.png";
	private static final String trainerTextureFileName = "pokemon_trainer.png";
	private static final String effectTextureFileName = "pokemon_effect.png";
	private static final String battleMenuFileName = "pokemon_battle_menu.png";
	private static final String hpBarFileName = "pokemon_hpbar.png";
	private static final String battleFieldFileName = "pokemon_battle_field.png";
	
	private final void loadImages(){
		loadPokemonTexture();
		loadTrainerTexture();
		loadEffectTexture();
		loadMenuTexture();
		loadHpBarTexture();
		loadBattleField();
		generateOFFSETList();
	}
	
	// function to load images sheet
	
	private void loadPokemonTexture() {
		// try to open the file of pokemon textures
		try{
			File pokemonTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					pokemonTextureFileName);
			pokemonSheet = ImageIO.read(pokemonTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + pokemonTextureFileName);
		}
		
	}
		
	private void loadTrainerTexture() {
		// try to open the file of trainer texture
		try{
			File trainerTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					trainerTextureFileName);
			trainerSheet = ImageIO.read(trainerTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + trainerTextureFileName);
		}	
	}
	
	
	private void loadEffectTexture() {
		// try to open the file of effect texture
		try{
			File effectTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					effectTextureFileName);
			effectSheet = ImageIO.read(effectTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + effectTextureFileName);
		}	
	}
	
	private void loadBattleField() {
		// try to open the file of the background
		try{
			File battleFieldTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					battleFieldFileName);
			battleField = ImageIO.read(battleFieldTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + battleFieldFileName);
		}	
	}
	
	private void loadMenuTexture() {
		// try to open the file of the menu texture
		try{
			File menuTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					battleMenuFileName);
			menuSheet = ImageIO.read(menuTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + battleMenuFileName);
		}	
	}
	
	private void loadHpBarTexture() {
		// try to open the file of the hp bar texture
		try{
			File barTextureFile = new File("images" + File.separator + 
					"Texture" + File.separator + 
					hpBarFileName);
			barSheet = ImageIO.read(barTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + hpBarFileName);
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
	
	
	////////////////////// Draw Battle Field /////////////////////////////
	private final static int BattleField_Height = 224;
	private final static int BattleField_Width = 480;
	
	private final static int BattleField_Grass_OFFSET_X = 497;
	private final static int BattleField_Grass_OFFSET_Y = 12;
	
	private final static int BattleField_Sand_OFFSET_X = 11;
	private final static int BattleField_Sand_OFFSET_Y = 242;
	
	private final static int BattleField_Magic_OFFSET_X = 11;
	private final static int BattleField_Magic_OFFSET_Y = 242;
	
	private BufferedImage drawBattleField(GroundType gndType){
		if (gndType == GroundType.GRASSLAND){
			return battleField.getSubimage(BattleField_Grass_OFFSET_X, BattleField_Grass_OFFSET_Y, 
					BattleField_Width, BattleField_Height);
		}
		else if (gndType == GroundType.SOIL){
			return battleField.getSubimage(BattleField_Sand_OFFSET_X, BattleField_Sand_OFFSET_Y, 
					BattleField_Width, BattleField_Height);
		}
		else{
			return battleField.getSubimage(BattleField_Magic_OFFSET_X, BattleField_Magic_OFFSET_Y, 
					BattleField_Width, BattleField_Height);
		}
	}
	
	
	
	
	////////////////////// Draw Battle Menu /////////////////////////////
	private final static int BattleMenu_Height = 96;
	private final static int BattleMenu_Width = 480;
	
	private final static int BattleMenu_OFFSET_X = 594;
	private final static int BattleMenu_OFFSET_Y = 112;
	
	private BufferedImage drawBattleMenu(){
		return menuSheet.getSubimage(BattleMenu_OFFSET_X, BattleMenu_OFFSET_Y, 
				BattleMenu_Width, BattleMenu_Height);

	}
	
	
	
	////////////////////// Draw Trainer /////////////////////////////
	private final static int Trainer_Height = 98;
	
	private final static int Trainer_Steady_Width = 76;
	private final static int Trainer_Throw01_Width = 128;
	private final static int Trainer_Throw02_Width = 106;
	private final static int Trainer_Throw03_Width = 132;
	private final static int Trainer_Throw04_Width = 112;
	
	private final static int Trainer_OFFSET_Y = 365;
	
	private final static int Trainer_Steady_OFFSET_X = 51;
	private final static int Trainer_Throw01_OFFSET_X = 143;
	private final static int Trainer_Throw02_OFFSET_X = 271;
	private final static int Trainer_Throw03_OFFSET_X = 398;
	private final static int Trainer_Throw04_OFFSET_X = 529;
	
	private BufferedImage drawTrainer(){
		// set the corresponding height
		trainerCurHeight = Trainer_Height;
		
		// trainer remain steady during the movie in
		if (!openingEnd){
			// set the corresponding width
			trainerCurWidth = Trainer_Steady_Width;
			return trainerSheet.getSubimage(Trainer_Steady_OFFSET_X, Trainer_OFFSET_Y, 
					Trainer_Steady_Width, Trainer_Height);
		}
		else{
			return trainerSheet.getSubimage(Trainer_Steady_OFFSET_X, Trainer_OFFSET_Y, 
					Trainer_Steady_Width, Trainer_Height);
		}
	}
	
	// return the upper left point of the trainer
	private Point getTrainerUpperLeft(){
		Point p = new Point();
		p.setLocation(trainerMidLocation_X - trainerCurWidth/2, trainerMidLocation_Y - trainerCurHeight);
		return p;
	}
	
	// return the upper left point of the pokemon
	private Point getPokemonUpperLeft(){
		Point p = new Point();
		p.setLocation(pokemonMidLocation_X - pokemonCurWidth/2, pokemonMidLocation_Y - pokemonCurHeight);
		return p;
	}
	
	
	////////////////////// Draw Pokemon /////////////////////////////
	private final static int Pokemon_Height = 126;
	private final static int Pokemon_Width = 126;
	
	private BufferedImage drawPokemon(Pokedex type){
		pokemonCurHeight = Pokemon_Height;
		pokemonCurWidth = Pokemon_Width;
		
		// pokemon remain steady during the movie in
		// and scream at you at the end
		if (!openingEnd){
			if (moveInCounter > 50){
				if (moveInCounter < 73){
					return drawPokemonB(type);
				}
				else{
					return drawPokemonA(type);
				}
			}
			return drawPokemonA(type);
		}
		else{
			return null;
		}
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
	
			
	// draw the battle
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// check the existence of model
		if (model == null){
			return;
		}
		
		// draw the back ground
		g2.drawImage(drawBattleField(GroundType.GRASSLAND), 0, 0, null);
		g2.drawImage(drawBattleMenu(), 0, BattleField_Height, null);
		
		// check if the openning animation has end
		// draw the move in trainer and pokemon
		if (openingStarted && !openingEnd){
			//System.out.println("Counter: " + moveInCounter + ",  Trainer Drawing at: " + getTrainerUpperLeft().x + ", " + getTrainerUpperLeft().y);
			
			// pokemon move in later
			if (moveInCounter > 5){
				g2.drawImage(drawPokemon(curPokemon.getSpecy()), getPokemonUpperLeft().x, getPokemonUpperLeft().y, null);
				//System.out.println("Counter: " + moveInCounter + ",  Pokemon Drawing at: " + getPokemonUpperLeft().x + ", " + getPokemonUpperLeft().y +
						//",  " + curPokemon.getSpecy().getName());
			}
			g2.drawImage(drawTrainer(), getTrainerUpperLeft().x, getTrainerUpperLeft().y, null);
			
		}
		// draw the item using animation
		else if (itemUsingStarted && !itemUsingEnd){
			g2.drawImage(drawTrainer(), getTrainerUpperLeft().x, getTrainerUpperLeft().y, null);
			g2.drawImage(drawPokemonA(curPokemon.getSpecy()), getPokemonUpperLeft().x, getPokemonUpperLeft().y, null);
		}
		// draw stand-by status
		else{
			g2.drawImage(drawTrainer(), getTrainerUpperLeft().x, getTrainerUpperLeft().y, null);
			g2.drawImage(drawPokemonA(curPokemon.getSpecy()), getPokemonUpperLeft().x, getPokemonUpperLeft().y, null);
		}
	}

}
