package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import GameModel.GameModel;
import Inventory.*;
import Map.GroundType;
import Pokemon.Pokedex;
import Pokemon.Pokemon;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class BattleView extends JPanel implements Observer{
	
	private static final long serialVersionUID = 784618488834701873L;
		
	// game model
	private GameModel gameModel;
	private int curTurn;
	
	// combat pokemon information
	private Pokemon curPokemon;	// current encountering pokemon
	private JLabel battleInfoBoard;

	
	// declare variables for animation
	private boolean battleEnd = true;	// flag to show that the battle is end
	
	private final static int delayInMillis = 10;
	private final static double PixelPerFrame = 4;
	private final static int MoveInPixel = 300;		// store the pixel that trainer and pokemon move in
		
	// location for the trainer on the battlefield
	private final static int TrainerStartLocation_X = 429;
	private int trainerMidLocation_X;
	private int trainerMidLocation_Y;
	private int trainerCurWidth;
	private int trainerCurHeight;
	private Point trainerUpperLeft = new Point();
	
	// location for the pokemon on the battlefield
	private final static int PokemonStartLocation_X = 53;
	private int pokemonMidLocation_X;
	private int pokemonMidLocation_Y;
	private int pokemonCurWidth;
	private int pokemonCurHeight;
	private Point pokemonUpperLeft = new Point();
	private boolean isCaught = false;
	
	// location for item on the battlefield
	private final static int ItemStartCenterLocation_X = 173;
	private final static int ItemStartCenterLocation_Y = 115;
	private final static int ItemFinalCenterLocation_X = 353;
	private final static int ItemFinalCenterLocation_Y = 100;
	private Point itemUpperLeft = new Point();
	private int itemCurWidth;
	private int itemCurHeight;
		
	// status and location for effect of the pokemon
	private boolean isBaited = false;
	private boolean isRocked = false;
	
	private Class<?> usingItemClass = null;
	private Item dummyItem = null;
		
	
	// constructor
	public BattleView(){
		setLayout(null);
		loadImages();
		initData();
		initBattleInfoBoard();
		repaint();
		this.addMouseListener(new ClickListener());
	}
	
	/**************** observer ****************/
	@Override
	public void update(Observable o, Object arg) {
		if (battleEnd == false){
			gameModel = (GameModel) o;
			
			// play opening animation
			if (!openingStarted){
				
				initData();
				playOpeningAnimation();
			}
			
			// play use item animation
			if(arg != null){
				curTurn++;
				// set pokemon turn count
				gameModel.getTrainer().getCurEncounterPokemon().recordCapTurn(curTurn);
				
				// raise the item using flag				
				usingItemClass = ((Item) arg).getClass();
				
				createDummyItem();
				
				// play the item animation
				playItemAnimation();
			}			
			repaint();
		}
		
	}

	/**************** Initiate Data ****************/
	// initiate the data when the battle begin
	private void initData(){
		// initiate the data when construct it
		if (gameModel == null){
			curPokemon = null;
			return;
		}
		
		resetData();
		
		// get the encountered pokemon
		setCurPokemon(gameModel.getTrainer().getCurEncounterPokemon());
	}
	
	// this will be called in initiation
	private void resetData(){
		isCaught = false;
		usingItemClass = null;	// reset the using item class
		curTurn = 0;	// reset the turn count

		// Date for the opening animation
		// location for the trainer on the battlefield
		trainerMidLocation_X = TrainerStartLocation_X; 
		trainerMidLocation_Y = BattleField_Height;
		// location for the pokemon on the battlefield
		pokemonMidLocation_X = PokemonStartLocation_X;
		pokemonMidLocation_Y = BattleField_Height - 60;
		
		// reset upperleft point
		trainerUpperLeft = new Point();
		pokemonUpperLeft = new Point();
		itemUpperLeft = new Point();
	}
	
	// create a dummy item according to the item class
	private void createDummyItem(){
		// initiate the a new item with the class
		try {
			Constructor<?> cons = usingItemClass.getConstructors()[0];
			dummyItem = (Item) cons.newInstance();
			
			//System.out.println("Item created");
			//System.out.println("Item: " + dummyItem.getName());
		} 
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	// initate Jlabel
	private void initBattleInfoBoard(){
		battleInfoBoard = new JLabel();
		battleInfoBoard.setBounds(20, 241, 380, 59);
		battleInfoBoard.setFont(new Font("Times New Roman", Font.BOLD, 12));
		battleInfoBoard.setForeground(Color.white);
		add(battleInfoBoard);
	}
	
	/**************** Tool ****************/
	public void setCurPokemon(Pokemon pokemon){
		this.curPokemon = pokemon;
	}
	
	public void startBattle(){
		battleInfoBoard.setVisible(true);
		battleEnd = false;
		openingStarted = false;		
	}
	
	private void endBattle(){
		if (caughtTimer != null){
			caughtTimer.stop();
			
			caughtEnd = true;
			caughtStarted = false;
			caughtCounter = 0;
		}
		
		battleInfoBoard.setVisible(false);
		battleEnd = true;
		curPokemon = null;
		gameModel.getTrainer().setCurEncounterPokemon(null);
		gameModel.setEncounteredThisBlock(false);
		generalTimer.stop();
		generalCounter = 0;
		this.stopAllSoundTrack();
		
	}
	
	public boolean InteractEnable(){
		return openingEnd && itemUsingEnd && itemFlyEnd && itemEffectEnd && transEnd && caughtEnd;
	}
	
	
	
	/**************** Click Listener ****************/
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
					System.out.println("Click on: " + x + ", " + y);
					playTransitionAnimation();
				}
			}			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {			
		}
		
	}
	

	/*
	 * *************************************** *
	 *  Painting and Animation Creator Below   *
	 * *************************************** *
	 */
	
		
	/**************** Define sprite sheet name ****************/
	
	// declare image sheet
	private static BufferedImage pokemonSheet;
	private static BufferedImage battleField;
	private static BufferedImage effectSheet;
	private static BufferedImage menuSheet;
	private static BufferedImage barSheet;
	private static BufferedImage trainerSheet;
	private static BufferedImage itemSheet;
	private static BufferedImage transSheet;
	private static BufferedImage victorySheet;
	
	private static final String TextureFolderPath = "images"+ File.separator + "Texture" + File.separator;
	
	private static final String pokemonTextureFileName = "pokemon_pokemons.png";
	private static final String trainerTextureFileName = "pokemon_trainer.png";
	private static final String effectTextureFileName = "pokemon_effect.png";
	private static final String battleMenuFileName = "pokemon_battle_menu.png";
	private static final String hpBarFileName = "pokemon_hpbar.png";
	private static final String battleFieldFileName = "pokemon_battle_field.png";
	private static final String itemTextureFileName = "pokemon_item.png";
	private static final String menuFrameFileName = "pokemon_menu_frame.png";
	private static final String victoryFieldFileName = "pokemon_backgrounds.png";
	private static final String TransitionTextureFileName = "TransitionBall" + File.separator + "pokeball_transition_00.png";
 		
	/********************** Load Image **********************/
	private final void loadImages(){
		loadTransitionImage();
		loadPokemonTexture();
		loadTrainerTexture();
		loadEffectTexture();
		loadMenuTexture();
		loadHpBarTexture();
		loadBattleField();
		loadItemTexture();
		loadVictoryFieldTexture();
		generateOFFSETList();
	}
	
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
	
	private void loadTransitionImage(){
		String filePath = TextureFolderPath + TransitionTextureFileName;
		
		// try to open the file of the trainer
		try{
			File transitionTextureFile = new File(filePath);
			transSheet = ImageIO.read(transitionTextureFile);
			TransBallImage = transSheet.getSubimage(TransBall_OFFSET_X, TransBall_OFFSET_Y, TransBall_Width, TransBall_Height);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadVictoryFieldTexture(){
		String filePath = TextureFolderPath + victoryFieldFileName;
		
		// try to open the file of the trainer
		try{
			File victoryTextureFile = new File(filePath);
			victorySheet = ImageIO.read(victoryTextureFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
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
	
	
	/********************** Draw Battle Field **********************/
	
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
	
	
	/********************** Draw Victory Field **********************/
	
	private final static int VictoryField_Height = 360;
	private final static int VictoryField_Width = 480;
	
	private final static int VictoryField_Grass_OFFSET_X = 3;
	private final static int VictoryField_Grass_OFFSET_Y = 1459;
		
	private final static int VictoryField_Sand_OFFSET_X = 3;
	private final static int VictoryField_Sand_OFFSET_Y = 1096;
	
	private final static int VictoryField_Magic_OFFSET_X = 3;
	private final static int VictoryField_Magic_OFFSET_Y = 6178;
	
	private BufferedImage drawVictoryField(GroundType gndType){
		if (gndType == GroundType.GRASSLAND){
			return victorySheet.getSubimage(VictoryField_Grass_OFFSET_X, VictoryField_Grass_OFFSET_Y, 
					VictoryField_Width, VictoryField_Height);
		}
		else if (gndType == GroundType.SOIL){
			return victorySheet.getSubimage(VictoryField_Sand_OFFSET_X, VictoryField_Sand_OFFSET_Y, 
					VictoryField_Width, VictoryField_Height);
		}
		else{
			return victorySheet.getSubimage(VictoryField_Magic_OFFSET_X, VictoryField_Magic_OFFSET_Y, 
					VictoryField_Width, VictoryField_Height);
		}
	}
		
	/********************** Draw Battle Menu **********************/
	private final static int BattleMenu_Height = 96;
	private final static int BattleMenu_Width = 480;
	
	private final static int BattleMenu_OFFSET_X = 594;
	private final static int BattleMenu_OFFSET_Y = 112;
	
	private BufferedImage drawBattleMenu(){
		return menuSheet.getSubimage(BattleMenu_OFFSET_X, BattleMenu_OFFSET_Y, 
				BattleMenu_Width, BattleMenu_Height);

	}
	
	
	
	/********************** Draw Trainer **********************/
	private final static int Trainer_Height = 98;

	private final static int Trainer_Steady_Width = 75;
	private final static int Trainer_Throw01_Width = 128;
	private final static int Trainer_Throw02_Width = 106;
	private final static int Trainer_Throw03_Width = 130;
	private final static int Trainer_Throw04_Width = 112;
	
	private final static int Trainer_OFFSET_Y = 365;
	
	private final static int Trainer_Steady_OFFSET_X = 44;
	private final static int Trainer_Throw01_OFFSET_X = 134;
	private final static int Trainer_Throw02_OFFSET_X = 263;
	private final static int Trainer_Throw03_OFFSET_X = 390;
	private final static int Trainer_Throw04_OFFSET_X = 521;
	
	private final static int Trainer_Victory_Width = 83;
	private final static int Trainer_Victory_Height = 115;	
	private final static int Trainer_Victory_OFFSET_X = 676;	
	private final static int Trainer_Victory_OFFSET_Y = 350;
	
	private BufferedImage drawTrainer(){
		// set the corresponding height
		trainerCurHeight = Trainer_Height;
		trainerUpperLeft = getTrainerUpperLeft();
		
		// trainer remain steady during the movie in
		if (openingStarted && !openingEnd){
			// set the corresponding width
			trainerCurWidth = Trainer_Steady_Width;
			return trainerSheet.getSubimage(Trainer_Steady_OFFSET_X, Trainer_OFFSET_Y, 
					Trainer_Steady_Width, Trainer_Height);
		}
		// draw the animation during using item
		else if (itemUsingStarted && !itemUsingEnd){			
			// play throw frame 01
			if (itemUsingCounter  == 1){
				trainerCurWidth = Trainer_Throw01_Width;
				return trainerSheet.getSubimage(Trainer_Throw01_OFFSET_X, Trainer_OFFSET_Y, 
						Trainer_Throw01_Width, Trainer_Height);				
			}
			// play throw frame 02
			else if (itemUsingCounter == 2){
				trainerCurWidth = Trainer_Throw02_Width;
				return trainerSheet.getSubimage(Trainer_Throw02_OFFSET_X, Trainer_OFFSET_Y, 
						Trainer_Throw02_Width, Trainer_Height);				
			}
			// play throw frame 03
			else if (itemUsingCounter >= 3 && itemUsingCounter <= 5 ){
				trainerCurWidth = Trainer_Steady_Width;
				return trainerSheet.getSubimage(Trainer_Throw03_OFFSET_X, Trainer_OFFSET_Y, 
						Trainer_Throw03_Width, Trainer_Height);				
			}
			// play throw frame 04
			else if (itemUsingCounter >= 5 && itemUsingCounter <= 6){
				trainerCurWidth = Trainer_Steady_Width;
				return trainerSheet.getSubimage(Trainer_Throw04_OFFSET_X, Trainer_OFFSET_Y, 
						Trainer_Throw04_Width, Trainer_Height);				
			}
			// remain steady at the beginning and the end
			else{
				trainerCurWidth = Trainer_Steady_Width;
				return trainerSheet.getSubimage(Trainer_Steady_OFFSET_X, Trainer_OFFSET_Y, 
						Trainer_Steady_Width, Trainer_Height);
			}
		}
		// remain steady the rest of the time
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
	
	// draw pose of the trainer
	private BufferedImage drawTrainerPose(){
		return trainerSheet.getSubimage(Trainer_Victory_OFFSET_X, Trainer_Victory_OFFSET_Y, 
				Trainer_Victory_Width, Trainer_Victory_Height);
	}
		
	
	/********************** Draw Pokemon **********************/
	private final static int Pokemon_Height = 126;
	private final static int Pokemon_Width = 126;
	
	// return the upper left point of the pokemon
	private Point getPokemonUpperLeft(){
		Point p = new Point();
		p.setLocation(pokemonMidLocation_X - pokemonCurWidth/2, pokemonMidLocation_Y - pokemonCurHeight);
		return p;
	}
	
	private BufferedImage drawPokemon(Pokedex type){
		pokemonCurHeight = Pokemon_Height;
		pokemonCurWidth = Pokemon_Width;
		pokemonUpperLeft = getPokemonUpperLeft();
		if (isCaught){
			pokemonUpperLeft.setLocation(pokemonMidLocation_X - Ball_Regular_Width/2, pokemonMidLocation_Y - Ball_Regular_Height);
			return itemSheet.getSubimage(Ball_Regular_OFFSET_X, Ball_Regular_OFFSET_Y, 
					Ball_Regular_Width, Ball_Regular_Height);	
		}
		
		// check if the openning animation has end draw the pokemon
		// pokemon remain steady during the movie in
		if (openingStarted && !openingEnd){
			// the pokemon move in later
			if (moveInCounter <= 5){
				return null;
			}
			// the pokemon play animation 
			else if (moveInCounter > 50){
				if (moveInCounter < 73){
					return drawPokemonB(type);
				}
				else{
					return drawPokemonA(type);
				}
			}
			// remain steady reset of the time
			else{
				return drawPokemonA(type);
			}
		}
		
		// use the general timer to make the pokemon more alive
		else{
			if (generalCounter % 50 >= 21 && generalCounter <= 23 % 50){
				return drawPokemonB(type);
			}
			else if (generalCounter % 50 >= 25 && generalCounter % 50 <= 27){
				return drawPokemonB(type);
			}
			else{
				return drawPokemonA(type);
			}
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
	
	/********************** Draw Item Flying **********************/
	//////////////// Overall Item ///////////////
	// define the rotation angle of the item and the offset of its center
	private double itemRotationRadian = 0;	
	private int item_Center_OFFSET_X = 0;
	private int item_Center_OFFSET_Y = 0;
	private final static int ItemFlyingFrame = 21;
	
	private Point getItemUpperLeft(){
		if (itemFlyCounter < ItemFlyingFrame){
			Point p = new Point();
			Point temp = calculateItemFlyingPath(itemFlyCounter);
			p.setLocation(temp.x - itemCurWidth/2, temp.y - itemCurHeight/2);
			return p;
		}
		else{
			Point p = new Point();
			p.setLocation(ItemFinalCenterLocation_X - itemCurWidth/2, ItemFinalCenterLocation_Y - itemCurHeight/2);
			return p;
		}
	}
	
	// draw the image according to the item class
	private BufferedImage drawItemFly(Class<?> itemClass){
		if (itemClass.isAssignableFrom(SafariBall.class)){
			return drawItem_Ball();
		}
		else if (itemClass.isAssignableFrom(Bait.class)){
			return drawItem_Bait();
		}
		else if (itemClass.isAssignableFrom(Rock.class)){
			return drawItem_Rock();
		}
		else{
			return null;
		}
	}
	
	// calculate the location of the item on the path
	private Point calculateItemFlyingPath(int counter){
		int x = 180 / (ItemFlyingFrame - 1) * counter + ItemStartCenterLocation_X;
		double dx = (double) (x - ItemStartCenterLocation_X);
		//double y = -1 * 79 * dx * dx / 14742 + 1853 * dx / 1638;
		double y = 499 * dx / 468 - 23 * dx * dx / 4212;
		
		//System.out.println("Ball Location: " + x + ", " + y);
		//System.out.println("	Equation: " + a + ", " + b);
		
		double y_real = ItemStartCenterLocation_Y - y;
		
		// cast y to int
		Point p = new Point();
		p.setLocation(x, (int) y_real);
		return p;
	}
	
	//////////////// Ball ////////////////
	private final static int Ball_Regular_Height = 28;
	private final static int Ball_Regular_Width = 28;
	
	private final static int Ball_Regular_OFFSET_X = 124;
	private final static int Ball_Regular_OFFSET_Y = 40;
		
	private BufferedImage drawItem_Ball(){
		if (itemFlyStarted && !itemFlyEnd){
			itemUpperLeft = getItemUpperLeft();
			
			// set the width and height
			itemCurHeight = Ball_Regular_Height;
			itemCurWidth = Ball_Regular_Width;
						
			// calculate the ball position
			if (itemFlyStarted && !itemFlyEnd){				
				// set the angle
				itemRotationRadian = Math.toRadians(itemFlyCounter * 54);
				
				/*
				BufferedImage image = trainerSheet.getSubimage(Ball_Regular_OFFSET_X, Ball_Regular_OFFSET_Y, 
						Ball_Regular_Width, Ball_Regular_Height);
				return op.filter(image, null);
				*/
			}		
			return itemSheet.getSubimage(Ball_Regular_OFFSET_X, Ball_Regular_OFFSET_Y, 
					Ball_Regular_Width, Ball_Regular_Height);	
		}
		else{
			return null;
		}
	}
	
	//////////////// BAIT ////////////////
	private final static int Bait_Regular_Height = 34;
	private final static int Bait_Regular_Width = 34;
	
	private final static int Bait_Regular_OFFSET_X = 217;
	private final static int Bait_Regular_OFFSET_Y = 590;
		
	private BufferedImage drawItem_Bait(){
		if (itemFlyStarted && !itemFlyEnd){
			itemUpperLeft = getItemUpperLeft();
			
			// set the width and height
			itemCurHeight = Bait_Regular_Height;
			itemCurWidth = Bait_Regular_Width;
						
			// calculate the ball position
			if (itemFlyStarted && !itemFlyEnd){				
				// set the angle
				itemRotationRadian = Math.toRadians(itemFlyCounter * 108);
			}		
			return effectSheet.getSubimage(Bait_Regular_OFFSET_X, Bait_Regular_OFFSET_Y, 
					Bait_Regular_Width, Bait_Regular_Height);	
		}
		else{
			return null;
		}
	}
	
	
	//////////////// ROCK ////////////////
	private final static int Rock_Regular_Height = 36;
	private final static int Rock_Regular_Width = 36;
	
	private final static int Rock_Regular_OFFSET_X = 1030;
	private final static int Rock_Regular_OFFSET_Y = 125;
		
	private BufferedImage drawItem_Rock(){
		if (itemFlyStarted && !itemFlyEnd){
			itemUpperLeft = getItemUpperLeft();
			
			// set the width and height
			itemCurHeight = Rock_Regular_Height;
			itemCurWidth = Rock_Regular_Width;
						
			// calculate the ball position
			if (itemFlyStarted && !itemFlyEnd){				
				// set the angle
				itemRotationRadian = Math.toRadians(itemFlyCounter * 108);
			}		
			return effectSheet.getSubimage(Rock_Regular_OFFSET_X, Rock_Regular_OFFSET_Y, 
					Rock_Regular_Width, Rock_Regular_Height);	
		}
		else{
			return null;
		}
	}
	
	
	/**************** Draw Item Effect ****************/
	private final static int EffectBlockSize_X = 120;
	private final static int EffectBlockSize_Y = 100;
	private final static int EffectBlockCenter_OFFSET_X = 20;
	private final static int EffectBlockCenter_OFFSET_Y = 20;
	//private int effect_Center_OFFSET_X = 0;
	//private int effect_Center_OFFSET_Y = 0;
	//private int effectRoationRadian = 0;
		
	////////////// ROCK HIT ///////////////
	private final static int Effect_Punch_Heavy_Width = 40;
	private final static int Effect_Punch_Heavy_Height = 36;
	private final static int Effect_Punch_Heavy_OFFSET_X = 960;
	private final static int Effect_Punch_Heavy_OFFSET_Y = 449;
	
	private final static int Effect_Punch_Light_Width = 22;
	private final static int Effect_Punch_Light_Height = 24;
	private final static int Effect_Punch_Light_OFFSET_X = 962;
	private final static int Effect_Punch_Light_OFFSET_Y = 500;
		
	private BufferedImage drawEffect_Punch_Heavy(){
		return effectSheet.getSubimage(Effect_Punch_Heavy_OFFSET_X, Effect_Punch_Heavy_OFFSET_Y, Effect_Punch_Heavy_Width, Effect_Punch_Heavy_Height);
	}
	
	private BufferedImage drawEffect_Punch_Light(){
		return effectSheet.getSubimage(Effect_Punch_Light_OFFSET_X, Effect_Punch_Light_OFFSET_Y, Effect_Punch_Light_Width, Effect_Punch_Light_Height);
	}
		
	////////////// BAIT CHARM ///////////////
	private final static int Effect_Heart_Full_Width = 29;
	private final static int Effect_Heart_Full_Height = 24;
	private final static int Effect_Heart_Full_OFFSET_X = 548;
	private final static int Effect_Heart_Full_OFFSET_Y = 765;
	
	private final static int Effect_Heart_Large_Width = 23;
	private final static int Effect_Heart_Large_Height = 20;
	private final static int Effect_Heart_Large_OFFSET_X = 551;
	private final static int Effect_Heart_Large_OFFSET_Y = 725;
	
	private final static int Effect_Heart_Medium_Width = 19;
	private final static int Effect_Heart_Medium_Height = 15;
	private final static int Effect_Heart_Medium_OFFSET_X = 553;
	private final static int Effect_Heart_Medium_OFFSET_Y = 685;
	
	private final static int Effect_Heart_Small_Width = 11;
	private final static int Effect_Heart_Small_Height = 10;
	private final static int Effect_Heart_Small_OFFSET_X = 537;
	private final static int Effect_Heart_Small_OFFSET_Y = 646;
	
	private final static int Effect_Heart_Tiny_Width = 8;
	private final static int Effect_Heart_Tiny_Height = 8;
	private final static int Effect_Heart_Tiny_OFFSET_X = 558;
	private final static int Effect_Heart_Tiny_OFFSET_Y = 605;
	
	private Image drawEffect_Heart_Spiral(){
		BufferedImage origin = effectSheet.getSubimage(Effect_Heart_Full_OFFSET_X, Effect_Heart_Full_OFFSET_Y, Effect_Heart_Full_Width, Effect_Heart_Full_Height);
		double ratio = ((double)(itemEffectCounter + 1) / (EffectFrame/1.5));
		int newWidth = (int) (Effect_Heart_Full_Width * ratio);
		int newHeight = (int) (Effect_Heart_Full_Height * ratio);
		Image img =  origin.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return img;
	}
	
	// TODO: not going to use these part but i would like to save them
	/*
	private Image drawEffect_Heart_Spiral_Sub(){
		if (itemEffectCounter >= 8){
			return drawEffect_Heart_Full();
		}
		else if (itemEffectCounter >= 6 && itemEffectCounter < 8) {
			return drawEffect_Heart_Large();
		}
		else if (itemEffectCounter >= 4 && itemEffectCounter < 6) {
			return drawEffect_Heart_Medium();
		}
		else if (itemEffectCounter >= 2 && itemEffectCounter < 4) {
			return drawEffect_Heart_Small();
		}
		else if (itemEffectCounter >= 0 && itemEffectCounter < 2) {
			return drawEffect_Heart_Tiny();
		}
		else{
			return null;
		}
	}
	
	private BufferedImage drawEffect_Heart_Full(){
		if (itemEffectCounter >= 8 * 2){
			return effectSheet.getSubimage(Effect_Heart_Full_OFFSET_X, Effect_Heart_Full_OFFSET_Y, Effect_Heart_Full_Width, Effect_Heart_Full_Height);
		}
		else{
			return null;
		}
	}
	
	private BufferedImage drawEffect_Heart_Large(){
		if (itemEffectCounter >= 6 * 2){
			return effectSheet.getSubimage(Effect_Heart_Large_OFFSET_X, Effect_Heart_Large_OFFSET_Y, Effect_Heart_Large_Width, Effect_Heart_Large_Height);
		}
		else{
			return null;
		}
	}
	
	private BufferedImage drawEffect_Heart_Medium(){
		if (itemEffectCounter >= 4 * 2){
			return effectSheet.getSubimage(Effect_Heart_Medium_OFFSET_X, Effect_Heart_Medium_OFFSET_Y, Effect_Heart_Medium_Width, Effect_Heart_Medium_Height);
		}
		else{
			return null;
		}
	}
	
	private BufferedImage drawEffect_Heart_Small(){
		if (itemEffectCounter >= 2 * 2){
			return effectSheet.getSubimage(Effect_Heart_Small_OFFSET_X, Effect_Heart_Small_OFFSET_Y, Effect_Heart_Small_Width, Effect_Heart_Small_Height);
		}
		else{
			return null;
		}
	}
	
	private BufferedImage drawEffect_Heart_Tiny(){
		return effectSheet.getSubimage(Effect_Heart_Tiny_OFFSET_X, Effect_Heart_Tiny_OFFSET_Y, Effect_Heart_Tiny_Width, Effect_Heart_Tiny_Height);
	}
	*/
	
	
	
	/******************* Draw Move In Animation ******************/
	
	///////// Opening of the Battle /////////
	private boolean openingStarted = false;	// flag to check if the openning animation has been played
	private boolean openingEnd = true;	// flag to check if the moving animation is over and enable the listener
	private void playOpeningAnimation(){
		openingStarted = true;
		openingEnd = false;		
		startMoveInTimer();
	}
	
	///////// Move In Timer at the opening /////////
	private Timer moveInTimer;
	private int moveInCounter;
	
	private void startMoveInTimer() {
		moveInCounter = 0;
		moveInTimer = new Timer(delayInMillis, new moveInTimerListener());
		moveInTimer.start();
		
		// set board info
		battleInfoBoard.setText("<html>YOU ENCOUNTER A WILD " + "<font color='#33f70c' ><u>" + gameModel.getTrainer().getCurEncounterPokemon().getName().toUpperCase() + "</u></font>" + 
								"<br>" +
								"IT IS A " + "<font color='yellow'><u>" + gameModel.getTrainer().getCurEncounterPokemon().getQuality().name() + "</u></font>" +
								" POKEMON" + "</html>");
	}
	
	private class moveInTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//System.out.println(moveInCounter + "");
			
			if (moveInCounter < MoveInPixel/PixelPerFrame){
				// set the location of the trainer and the pokemon
				trainerMidLocation_X -= PixelPerFrame;
				
				// pokemon move in later
				if (moveInCounter < 50){
					pokemonMidLocation_X += (int)(PixelPerFrame * 1.5);
				}
				repaint();
				moveInCounter++;
			}
			else{
				openingEnd = true;
				moveInTimer.stop();
				moveInCounter = 0;
				
				// start general timer
				//startGeneralTimer();
				
			}
			
		}
	}
	
	/******************* Draw Using Item Animation ******************/
	private boolean itemUsingStarted = false;	// flag to check if using item animation started
	private boolean itemUsingEnd = true;
	private void playItemAnimation(){
		itemUsingStarted = true;
		itemUsingEnd = false;
		startItemUsingTimer();
	}
	
	//////////// Item Timer ////////////
	private Timer itemUsingTimer;
	private int itemUsingCounter;
	
	private void startItemUsingTimer() {
		itemUsingCounter = 0;
		itemUsingTimer = new Timer(delayInMillis * 11, new itemUsingTimerListener());
		itemUsingTimer.start();
		
		// update the board
		battleInfoBoard.setText("<html>YOU USE A " + "<font color='#ffff26' ><u>" + dummyItem.getName().toUpperCase() + "</u></font>" + 
				"<br>" +
				"THE POKEMON SEEMS TO BE " + "<font color='#ff38e7' ><u>" + dummyItem.getEffectMessage().toUpperCase() + "</u></font>" + "</html>");
	}
	
	private class itemUsingTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (itemUsingCounter < 10){
				
				//System.out.println(itemUsingCounter + "");
				if (itemUsingCounter < 4){
					repaint();
				}
				itemUsingCounter ++;
				
				// start to draw the flying item
				if (itemUsingCounter == 4){
					// draw the item effect
					playItemFlyAnimation();
				}
			}
			else{
				itemUsingEnd = true;
				itemUsingStarted = false;
				itemUsingTimer.stop();
				itemUsingCounter = 0;
			}
			
		}
	}
	
	
	/******************* Draw Item Fly Animation ******************/
	private boolean itemFlyStarted = false;	// flag to check if using item animation started
	private boolean itemFlyEnd = true;
	private void playItemFlyAnimation(){
		itemFlyStarted = true;
		itemFlyEnd = false;
		startItemFlyTimer();
	}
	
	//////////// Item Timer ////////////
	private Timer itemFlyTimer;
	private int itemFlyCounter;
	
	private void startItemFlyTimer() {
		itemFlyCounter = 0;
		itemFlyTimer = new Timer(delayInMillis * 4, new itemFlyTimerListener());
		if (usingItemClass == SafariBall.class){
			this.playEffectSound(UltimateSoundEffectFileName);
		}
		itemFlyTimer.start();
	}
	
	private class itemFlyTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// play flying track and play effect
			if (itemFlyCounter < ItemFlyingFrame){
				//System.out.println(itemEffectCounter + "");
				repaint();
				itemFlyCounter ++;
			}

			else{
				itemFlyEnd = true;
				itemFlyStarted = false;
				itemFlyTimer.stop();
				itemFlyCounter = 0;
				
				// play item effect animation
				playItemEffectAnimation();
				
			}
			
		}
	}
	
	/******************* Draw Item Effect Animation ******************/
	private boolean itemEffectStarted = false;	// flag to check if using item animation started
	private boolean itemEffectEnd = true;
	private final static int EffectFrame = 30;
	private boolean BallShakeStarted = false;
	private boolean BallShakeEnd = true;
	
	private void playItemEffectAnimation(){
		RollingBallRadian = 0;
		itemEffectStarted = true;
		itemEffectEnd = false;
		startItemEffectTimer();
	}
	
	//////////// Item Timer ////////////
	private Timer itemEffectTimer;
	private int itemEffectCounter;
	
	private void startItemEffectTimer() {
		itemEffectCounter = 0;
		itemEffectTimer = new Timer((int) (delayInMillis * 4), new itemEffectTimerListener());
		
		// mark the disappear of pokemon
		if (usingItemClass == SafariBall.class){
			BallShakeStarted = true;
			BallShakeEnd = false;
		}
		else if (usingItemClass == Bait.class){
			System.out.println("play bait sound");
			playEffectSound(BaitSoundEffectFileName);
		}
		else if (usingItemClass == Rock.class){
			System.out.println("play rock sound");
			playEffectSound(RockSoundEffectFileName);
		}
		itemEffectTimer.start();
	}
	
	private class itemEffectTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// play effect
			if (itemEffectCounter < EffectFrame){
				repaint();
				itemEffectCounter ++;
			}
			else{
				BallShakeStarted = false;
				BallShakeEnd = true;
				itemEffectCounter = 0;
				itemEffectEnd = true;
				itemEffectStarted = false;
				itemEffectTimer.stop();
								
				repaint();				
				// TODO: check the item use result
				checkItemUseResult();
			}
			
		}
	}
	
	private void checkItemUseResult(){
		// ball result
		if (usingItemClass.isAssignableFrom(SafariBall.class)){
			/////////////// Check if Caught for Ball Use ///////////////
			if (gameModel.checkIfCaughtPokemon(gameModel.getTrainer().getCurEncounterPokemon())){
				isCaught = true;
				battleInfoBoard.setText("<html>CONGRATULATIONS!!!<br>"
						+ "YOU CAUGHT A " + "<font color='#33f70c' ><u>" + gameModel.getTrainer().getCurEncounterPokemon().getName().toUpperCase() + "</u></font>" + "</html>");
				playCaughtAnimation();
			}
			else{
				// TODO: playEscapeAnimation();
			}
			
		}
		// other result
		else{
			if (gameModel.checkIfRunPokemon(gameModel.getTrainer().getCurEncounterPokemon())){
				// TODO: playRunAwayAnimation();
			}
			else{
				// set board info
				battleInfoBoard.setText("<html>THE WILD POKEMON IS STILL THERE<br>KEEP IT UP</html>");
				repaint();
			}
		}
		
		usingItemClass = null;
	}
	
	
	/***************************** Transition Timer *************************************/
	private final static int VisionRadius_X = 240;	// define the vision of the trainer on the map
	private final static int VisionRadius_Y = 160;	// define the vision of the trainer on the map
	private Timer transTimer;
	private int transMoveCounter = 0;
	public final static int FramePerTrans = 35;
	private static final double PixelPerTransFrame = 16;
	private boolean transStarted = false;
	private boolean transEnd = true;
	//private double transRotationRadian = 0;
	
	private void startTransTimer() {
		transTimer = new Timer(delayInMillis * 2, new transTimerListener());
		transStarted = true;
		transEnd = false;
		transTimer.start();
		
	}
	
	public void playTransitionAnimation() {		
		System.out.println("start transition timer");
		startTransTimer();

	}
	
	private class transTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (transMoveCounter < FramePerTrans){
				if (transMoveCounter == 20){
					battleInfoBoard.setVisible(false);
				}
				repaint();
				transMoveCounter++;
				//System.out.println("counter: " + transMoveCounter);
			}
			else{
				transTimer.stop();
				transMoveCounter = 0;
				transStarted = false;
				transEnd = true;
				endBattle();
				if (gameModel.getTrainer().getCurEncounterPokemon() == null){
					setVisible(false);
					return;
				}
			}
		}
	}	
	
	/**************** Draw Transition ****************/
	private static final int TransBall_Height = 80;
	private static final int TransBall_Width = 80;
	private static final int TransBall_OFFSET_X = 0;
	private static final int TransBall_OFFSET_Y = 0;
	
	private static BufferedImage TransBallImage;
	
	private BufferedImage drawTransitionBall_01(){
		// get the subimage of the ball
		if (transMoveCounter > (FramePerTrans - 2)){
			return null;
		}
		
		int startX = 0;
		int startY = 0;
		int width = TransBall_Width;
		int height = TransBall_Height;
		
		if (transMoveCounter < 5 ){
			startX = (int) (TransBall_Width - (transMoveCounter + 1) * PixelPerTransFrame);
			startY = 0;
			width = (int) ((transMoveCounter + 1) * PixelPerTransFrame);
			height = TransBall_Height;
		}
		else if ( transMoveCounter > FramePerTrans - 6){
			startX = 0;
			startY = 0;
			width = (int) ((FramePerTrans - transMoveCounter - 1) * PixelPerTransFrame);
			height = TransBall_Height;
		}
		
		//System.out.println("Ball 1:");
		//System.out.println("	" + transMoveCounter + ": " + startX + ", " + startY + ", " + width + ", " + height);
		
		return TransBallImage.getSubimage(startX, startY, width, height);
	}
	
	private BufferedImage drawTransitionBall_02(){
		// get the subimage of the ball
		if (transMoveCounter > (FramePerTrans - 2)){
			return null;
		}
		
		int startX = 0;
		int startY = 0;
		int width = TransBall_Width;
		int height = TransBall_Height;
		
		if (transMoveCounter < 5 ){
			startX = 0;
			startY = 0;
			width = (int) ((transMoveCounter + 1) * PixelPerTransFrame);
			height = TransBall_Height;
		}
		else if ( transMoveCounter > FramePerTrans - 6){
			startX = (int) (TransBall_Width - (FramePerTrans - transMoveCounter - 1) * PixelPerTransFrame);
			startY = 0;
			width = (int) ((FramePerTrans - transMoveCounter - 1) * PixelPerTransFrame);
			height = TransBall_Height;
		}
		
		//System.out.println("Ball 2:");
		//System.out.println("	" + transMoveCounter + ": " + startX + ", " + startY + ", " + width + ", " + height);
		
		return TransBallImage.getSubimage(startX, startY, width, height);
	}
	
	
	
	/***************************** Caught Timer *************************************/
	private Timer caughtTimer;
	private int caughtCounter = 0;
	public final static int FramePerCaught = 10;
	//private static final double PixelPerCaughtFrame = 16;
	private boolean caughtStarted = false;
	private boolean caughtEnd = true;
	//private double transRotationRadian = 0;
	
	private void startCaughtTimer() {
		caughtTimer = new Timer(delayInMillis * 100, new caughtTimerListener());
		caughtStarted = true;
		caughtEnd = false;
		
		stopPlayBackgroundSound();
		playEffectSound(CatchMusicFileName);
		
		playBackgroundMusic(VictoryMusicFileName);
		caughtTimer.start();
		callEndOptionPane();
		
		
	}
	
	public void playCaughtAnimation() {		
		System.out.println("start caught timer");
		startCaughtTimer();

	}
	
	private class caughtTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			/*
			if (caughtEnd){
				caughtStarted = false;
				caughtTimer.stop();
				caughtCounter = 0;
				repaint();
				playTransitionAnimation();
				
			}
			*/
			repaint();
			caughtCounter++;
		

		}
	}	
	
	/**************** Overall Timer *****************/
	////////////Item Timer ////////////
	private Timer generalTimer;
	private int generalCounter;

	protected void startGeneralTimer() {
		generalCounter = 0;
		generalTimer = new Timer(delayInMillis * 10, new generalTimerListener());
		generalTimer.start();
	}

	private class generalTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// reset the counter when go beyond 50
			if (generalCounter == 0){
				if (isCaught){
					playBackgroundMusic(VictoryMusicFileName);
				}
				else{
					playBackgroundMusic(BattleMusicFileName);
				}
			}
			else if (curBackgroundPlayer.getStatus() == 2 && !isCaught){
				playBackgroundMusic(BattleMusicFileName);
			}		
			else if (curBackgroundPlayer.getStatus() == 2 && isCaught){
				playBackgroundMusic(VictoryMusicFileName);
			}

			
			if (InteractEnable()){
				repaint();
			}
			
			//if (backgroundPlayer.)
			generalCounter ++;
		
		}
	
	}
	
	
	
			
	/**************** Paint function ****************/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// check the existence of model
		if (gameModel == null){
			return;
		}
		
		/*
		// play background music
		if (!isCaught && (backgroundPlayer == null || backgroundPlayer.isComplete())){
			playBackgroundMusic(BattleMusicFileName);
			System.out.println("play music spam");
		}
		else{
			playBackgroundMusic(VictoryMusicFileName);
		}
		*/
		
		//System.out.println("Pokemon Mid Location: " + pokemonMidLocation_X + ", " + pokemonMidLocation_Y);
		
		// draw the back ground
		if ((transStarted && !transEnd  && isCaught) || caughtCounter >= 2){
			g2.drawImage(drawVictoryField(GroundType.GRASSLAND), 0, 0, null);
		}
		else{
			g2.drawImage(drawBattleField(GroundType.GRASSLAND), 0, 0, null);
		}
		g2.drawImage(drawBattleMenu(), 0, BattleField_Height, null);
		

		// draw the trainer and pokemon
		// dont display the pokemon when ball shaking
		if (!transStarted && transEnd){
			if (caughtStarted && !caughtEnd && caughtCounter < 2){			
				g2.drawImage(drawCaughtPokemon(), ItemFinalCenterLocation_X, ItemFinalCenterLocation_Y - Ball_Regular_Height/2, null);
			}
			else if (!BallShakeStarted && BallShakeEnd && caughtCounter == 0){
				g2.drawImage(drawPokemon(curPokemon.getSpecy()), pokemonUpperLeft.x, pokemonUpperLeft.y, null);
			}
		}
		
		// draw trainer
		if ((transStarted && !transEnd && isCaught) || caughtCounter >= 2){
			g2.drawImage(drawTrainerPose(), 240 - Trainer_Victory_Width/2, 160 - Trainer_Victory_Height + 30, null);
		}
		else{
			g2.drawImage(drawTrainer(), trainerUpperLeft.x, trainerUpperLeft.y, null);
		}
		
		// draw item and effect
		// set the transform
		if (itemFlyStarted && !itemFlyEnd){
			// rotate the item
			BufferedImage img = drawItemFly(usingItemClass);
			AffineTransform trans = AffineTransform.getTranslateInstance(itemUpperLeft.x, itemUpperLeft.y);
			trans.rotate(itemRotationRadian, itemCurWidth/2, itemCurHeight/2);
			g2.drawImage(img, trans, null);
			//System.out.println("Item Mid Location: " + itemUpperLeft.x + ", " + itemUpperLeft.y);
		}
		//g2.drawImage(drawItemFly(usingItemType), itemUpperLeft.x, itemUpperLeft.y, null);
				
		if (itemEffectStarted && !itemEffectEnd){
			paintItemEffectImage(g2);
		}
		
		
		if (transStarted && !transEnd){
			paintTransitionImage(g2);
		}
		
		
	}
		
	//////////////// Paint Item Effect /////////////////
	private double RollingBallRadian = 0;
	
	private void paintItemEffectImage(Graphics2D g2){
		// paint ball effect
		if (usingItemClass.isAssignableFrom(SafariBall.class)){
			// get the upper left point of the ball
			Point p = generateRollingBallUpperLeft(itemEffectCounter);
			
			//System.out.println("Location: " + p.x + ", " + p.y);
			
			// rotate the ball
			BufferedImage img = itemSheet.getSubimage(Ball_Regular_OFFSET_X, Ball_Regular_OFFSET_Y, 
					Ball_Regular_Width, Ball_Regular_Height);	
			AffineTransform trans = AffineTransform.getTranslateInstance(p.x, p.y);
			trans.rotate(RollingBallRadian, Ball_Regular_Width/2, Ball_Regular_Height/2);
			g2.drawImage(img, trans, null);
		}
		// paint bait effect
		else if (usingItemClass.isAssignableFrom(Bait.class)){
			Point p = generateArchSpiralPoint(itemEffectCounter);
			g2.drawImage(drawEffect_Heart_Spiral(), p.x - 20, p.y - 20, null);
		}
		// paint rock effect
		else if (usingItemClass.isAssignableFrom(Rock.class)){
			int x = (int) (ItemFinalCenterLocation_X + (Math.random()  - 0.5 )* EffectBlockSize_X - EffectBlockCenter_OFFSET_X);
			int y = (int) (ItemFinalCenterLocation_Y + (Math.random() - 0.5 ) * EffectBlockSize_Y - EffectBlockCenter_OFFSET_Y);
			
			// choose which to show
			if (Math.random() < 0.5){
				g2.drawImage(drawEffect_Punch_Heavy(), x, y, null);
			}
			else{
				g2.drawImage(drawEffect_Punch_Light(), x, y, null);
			}
		}
		else{
			return;
		}
	}
	
	//////////////// Paint Caught Scene /////////////////
	private BufferedImage drawCaughtPokemon(){
		return itemSheet.getSubimage(Ball_Regular_OFFSET_X, Ball_Regular_OFFSET_Y, 
				Ball_Regular_Width, Ball_Regular_Height);	
	}
	
	
	//////////////// Transition /////////////////
	private void paintTransitionImage(Graphics2D g2){
		// draw black rectange
		if (transMoveCounter > 1 && transMoveCounter < 32){
			g2.setColor(Color.BLACK);
			//int startX = 0;
			//int length = (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame);
			//System.out.println("Rect_1:		StratX: " + startX + "	length: " + length);
			
			g2.fillRect(0, 0, (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 80);
			
			
			//startX = (int) (480 - ( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame);
			//length = (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame);
			//System.out.println("Rect_2:		StratX: " + startX + "	length: " + length);
			
			g2.fillRect((int) (480 - ( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 80, (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 80);
			
			g2.fillRect(0, 160, (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 80);
			
			g2.fillRect((int) (480 - ( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 240, (int) (( transMoveCounter - 2 + 0.5 ) * PixelPerTransFrame), 80);
		}
		else if (transMoveCounter >= 32){
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, 480, 80);
			
			g2.fillRect(0, 80, 480, 80);
			
			g2.fillRect(0, 160, 480, 80);
			
			g2.fillRect(0, 240, 480, 80);
		}
		// draw ball
		if (transMoveCounter < 5){
			g2.drawImage(drawTransitionBall_01(), 0, 0, null);
			g2.drawImage(drawTransitionBall_02(), (int) (VisionRadius_X * 2 - ( transMoveCounter + 1 ) * PixelPerTransFrame), VisionRadius_Y / 2, null);
			g2.drawImage(drawTransitionBall_01(), (int) 0, VisionRadius_Y, null);
			g2.drawImage(drawTransitionBall_02(), (int) (VisionRadius_X * 2 - ( transMoveCounter + 1 ) * PixelPerTransFrame), (int) (VisionRadius_Y * 1.5), null);
		}
		else if (transMoveCounter < FramePerTrans && transMoveCounter > FramePerTrans- 6){				
			g2.drawImage(drawTransitionBall_01(), (int) (2 * VisionRadius_X - (FramePerTrans - transMoveCounter - 1) * PixelPerTransFrame), 0, null);
			g2.drawImage(drawTransitionBall_02(), 0, VisionRadius_Y / 2, null);
			g2.drawImage(drawTransitionBall_01(), (int) (2 * VisionRadius_X - (FramePerTrans - transMoveCounter - 1) * PixelPerTransFrame), VisionRadius_Y, null);
			g2.drawImage(drawTransitionBall_02(), 0, (int) (VisionRadius_Y * 1.5), null);
		}
		else{				
			g2.drawImage(drawTransitionBall_01(), (int) (0 + ( transMoveCounter - 4 ) * PixelPerTransFrame), 0, null);
			g2.drawImage(drawTransitionBall_02(), (int) (VisionRadius_X * 2 - (transMoveCounter + 1 ) * PixelPerTransFrame), VisionRadius_Y / 2, null);
			g2.drawImage(drawTransitionBall_01(), (int) (0 + ( transMoveCounter - 4 ) * PixelPerTransFrame), VisionRadius_Y, null);
			g2.drawImage(drawTransitionBall_02(), (int) (VisionRadius_X * 2 - (transMoveCounter + 1) * PixelPerTransFrame), (int) (VisionRadius_Y * 1.5), null);
		}
	}
	
	/********************** Archimedean spiral **************************/
		
	private Point generateArchSpiralPoint(int counter){
	        double angle = Math.toRadians(60 * counter);
	        int a = 2;
	        int b = 2;
	        int x = (int) (ItemFinalCenterLocation_X + (a + b * angle) * Math.cos(angle));
	        int y = (int) (ItemFinalCenterLocation_Y + (a + b * angle) * Math.sin(angle) + EffectBlockCenter_OFFSET_Y);
	        
	        return (new Point(x, y));
	}
	
	
	/********************** Rolling Pokeball **************************/
	private Point generateRollingBallUpperLeft(int counter){
		if (counter >= 25 && counter <= 29){
			RollingBallRadian = Math.pow(-1, (double)(counter - 25));
		}
		else if (counter >= 16 && counter <= 24){
			if (counter >= 16 && counter <= 18){
				RollingBallRadian--;
			}
			else if (counter > 18 && counter <= 22){
				RollingBallRadian++;
			}
			else{
				RollingBallRadian--;
			}
		}
		else{
			if (counter >= 0 && counter <= 4){
				RollingBallRadian--;
			}
			else if (counter > 4 && counter <= 12){
				RollingBallRadian++;
			}
			else{
				RollingBallRadian--;
			}
		}
		
		int x = (int) (RollingBallRadian * Ball_Regular_Width/2 + ItemFinalCenterLocation_X) ;
		int y = ItemFinalCenterLocation_Y - Ball_Regular_Height/2;
		
		return (new Point(x, y)); 
	}
	
	/*
	 * *************************************** *
	 *  	  SoundTrack Creator Below         *
	 * *************************************** *
	 */
	/************* Overall Control **************/
	public void stopAllSoundTrack(){
		this.stopPlayBackgroundSound();
		this.stopPlayEffectSound();
	}
	
	private final static String SoundTrackFolder = "soundtrack" + File.separator;
	
	////////////////// Background Sound /////////////////
	private static String curBackgroundMusicFileName = "battle_wild_01.mp3";
	
	private static String BattleMusicFileName = "battle_wild_01.mp3";
	private static String VictoryMusicFileName = "victory.mp3";
	private BasicPlayer curBackgroundPlayer;
	//private FileInputStream fis_BackgroundMusic;
	//private BufferedInputStream bis_BackgroundMusic;
	private Thread backgroundThread;
			
	private void playBackgroundMusic(String fileName) {
	    try {
			stopPlayBackgroundSound();
			curBackgroundMusicFileName = fileName;
	    	File fis = new File(SoundTrackFolder + fileName);
	    	//BufferedInputStream bis = new BufferedInputStream(fis);
	    	curBackgroundPlayer = new BasicPlayer();
	    	curBackgroundPlayer.open(fis);
	    	curBackgroundPlayer.setGain(0.1);
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
			//System.err.printf("1. cannot open: " + fileName, e.getMessage());
	    }
	    
	    
	    backgroundThread = new Thread() {
	    	@Override
	    	public void run() {
	    		try {
	    			curBackgroundPlayer.play();
	    		} 
	    		catch (Exception e) {
	    			System.err.printf("2. cannot open: " + fileName, e.getMessage());
	    		}
	    	}
	    };
	    
	    backgroundThread.start();
	    
	}
	
	private void stopPlayBackgroundSound(){
		if (curBackgroundPlayer != null) {
			try {
				curBackgroundPlayer.stop();
			}
			catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		if (backgroundThread != null){
			backgroundThread.interrupt();
		}
		
	}
	
	////////////////// Vcitory Sound /////////////////
	private final static String CatchMusicFileName = "catch.mp3";
	private final static String BaitSoundEffectFileName = "Bait_Effect.wav";
	private final static String RockSoundEffectFileName = "Rock_Effect.wav";
	private final static String UltimateSoundEffectFileName = "Ultimate.mp3";
	private BasicPlayer curEffectPlayer;
	private Thread effectThread;
	
	
	private void playEffectSound(String fileName) {
	    try {
	    	stopPlayEffectSound();
	    	File fis = new File(SoundTrackFolder + fileName);
	    	//BufferedInputStream bis = new BufferedInputStream(fis);
	    	curEffectPlayer = new BasicPlayer();
	    	curEffectPlayer.open(fis);
	    	curEffectPlayer.setGain(1);
		    //effectPlayer.play();
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	        //System.err.printf("1. cannot open: " + fileName, e.getMessage());
	    }

	    
	    effectThread = new Thread() {
	    	@Override
	    	public void run() {
	    		try {
	    			curEffectPlayer.play();
	    		} 
	    		catch (Exception e) {
	    			System.err.printf("2. cannot open: " + fileName, e.getMessage());
	    		}
	    	}
	    };
	    
	    effectThread.start();
	    
	}
	
	private void stopPlayEffectSound(){
		if (effectThread != null){
			effectThread.interrupt();
		}
		
		if (curEffectPlayer != null) {
			try {
				curEffectPlayer.stop();
			} 
			catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	/*
	 * *************************************** *
	 *  	  SoundTrack Creator Below         *
	 * *************************************** *
	 */
	
	private boolean endGameOptionPaneStarted = false;
	JOptionPane endGameOptionPane = new JOptionPane();
	
	private void callEndOptionPane(){
		endGameOptionPaneStarted = true;
		String newName = endGameOptionPane.showInputDialog("Do you want a new name for it?");
		if (newName == null){
			gameModel.getTrainer().catchPokemon(gameModel.getTrainer().getCurEncounterPokemon());
			
			System.out.println("no new name");
		}
		else{
			gameModel.getTrainer().getCurEncounterPokemon().setName(newName);
			gameModel.getTrainer().catchPokemon(gameModel.getTrainer().getCurEncounterPokemon());
			
		}
		endGameOptionPaneStarted = false;
		//System.out.println("" + gameModel.getTrainer().getPokemonCollection().getSize());
		
		// end the battle
		playTransitionAnimation();
	}

}
