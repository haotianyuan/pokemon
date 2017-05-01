package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import GameModel.GameModel;
import Inventory.*;
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
	private BufferedImage itemSheet;
	
	// game model
	private GameModel model;
	private int curTurn;
	
	// combat pokemon information
	private Pokemon curPokemon;	// current encountering pokemon
	private JLabel battleInfo;

	
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
		
	
	// constructor
	public BattleView(){
		loadImages();
		initData();
		repaint();
		this.addMouseListener(new ClickListener());
	}
	
	/**************** observer ****************/
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
			if(arg != null){
				curTurn++;
				// raise the item using flag				
				usingItemClass = ((Item) arg).getClass();
				playItemAnimation();
			}			
			repaint();
		}
		
	}

	/**************** Initiate Data ****************/
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
	}
	
	// this will be called in initiation
	private void resetData(){
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
	
	/**************** Tool ****************/
	public void setCurPokemon(Pokemon pokemon){
		this.curPokemon = pokemon;
	}
	
	public void startBattle(){
		battleEnd = false;
		openingStarted = false;		
	}
	
	private void endBattle(){
		battleEnd = true;
		curPokemon = null;
		model.getTrainer().setCurEncounterPokemon(null);
		model.setEncounteredThisBlock(false);
		generalTimer.stop();
		generalCounter = 0;
	}
	
	public boolean InteractEnable(){
		return openingEnd && itemUsingEnd && itemFlyEnd && itemEffectEnd;
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
	
		
	/**************** Define sprite sheet name ****************/
	private static final String pokemonTextureFileName = "pokemon_pokemons.png";
	private static final String trainerTextureFileName = "pokemon_trainer.png";
	private static final String effectTextureFileName = "pokemon_effect.png";
	private static final String battleMenuFileName = "pokemon_battle_menu.png";
	private static final String hpBarFileName = "pokemon_hpbar.png";
	private static final String battleFieldFileName = "pokemon_battle_field.png";
	private static final String itemTextureFileName = "pokemon_item.png";
	private static final String menuFrameFileName = "pokemon_menu_frame.png";
 		
	/********************** Load Image **********************/
	private final void loadImages(){
		loadPokemonTexture();
		loadTrainerTexture();
		loadEffectTexture();
		loadMenuTexture();
		loadHpBarTexture();
		loadBattleField();
		loadItemTexture();
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
		
		// check if the openning animation has end
		// draw the pokemon
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
		else{
			if (generalCounter >= 21 && generalCounter <= 23){
				return drawPokemonB(type);
			}
			else if (generalCounter >= 25 && generalCounter <= 27){
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
	private int effect_Center_OFFSET_X = 0;
	private int effect_Center_OFFSET_Y = 0;
	private int effectRoationRadian = 0;
	private final static int EffectFrame = 10;
	
	private final static int Effect_Punch_Heavy_Width = 40;
	private final static int Effect_Punch_Heavy_Height = 36;
	private final static int Effect_Punch_Heavy_OFFSET_X = 960;
	private final static int Effect_Punch_Heavy_OFFSET_Y = 449;
	
	private final static int Effect_Punch_Light_Width = 22;
	private final static int Effect_Punch_Light_Height = 24;
	private final static int Effect_Punch_Light_OFFSET_X = 962;
	private final static int Effect_Punch_Light_OFFSET_Y = 500;
	
	
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
				startGeneralTimer();
				
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
				
				// check the item use result
				
			}
			
		}
	}
	
	/******************* Draw Item Effect Animation ******************/
	private boolean itemEffectStarted = false;	// flag to check if using item animation started
	private boolean itemEffectEnd = true;
	private void playItemEffectAnimation(){
		itemEffectStarted = true;
		itemEffectEnd = false;
		startItemEffectTimer();
	}
	
	//////////// Item Timer ////////////
	private Timer itemEffectTimer;
	private int itemEffectCounter;
	
	private void startItemEffectTimer() {
		itemEffectCounter = 0;
		itemEffectTimer = new Timer(delayInMillis * 9, new itemEffectTimerListener());
		itemEffectTimer.start();
	}
	
	private class itemEffectTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// play flying track and play effect
			if (itemEffectCounter < 10){
				//System.out.println(itemEffectCounter + "");
				repaint();
				itemFlyCounter ++;
			}
			else{
				itemEffectEnd = true;
				itemEffectStarted = false;
				itemEffectTimer.stop();
				itemEffectCounter = 0;
				
				// check the item use result
				
			}
			
		}
	}
	
	
	/**************** Overall Timer *****************/
	////////////Item Timer ////////////
	private Timer generalTimer;
	private int generalCounter;

	private void startGeneralTimer() {
		generalCounter = 0;
		generalTimer = new Timer(delayInMillis * 10, new generalTimerListener());
		generalTimer.start();
	}

	private class generalTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			generalCounter ++;
			// reset the counter when go beyond 50
			if (generalCounter > 50){
				generalCounter = 0;
			}		
			
			if (InteractEnable()){
				repaint();
			}
		
		}
	
	}
	
			
	/**************** Paint function ****************/
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// check the existence of model
		if (model == null){
			return;
		}
		
		//System.out.println("Pokemon Mid Location: " + pokemonMidLocation_X + ", " + pokemonMidLocation_Y);
		
		// draw the back ground
		g2.drawImage(drawBattleField(GroundType.GRASSLAND), 0, 0, null);
		g2.drawImage(drawBattleMenu(), 0, BattleField_Height, null);
		

		// draw the trainer and pokemon
		g2.drawImage(drawPokemon(curPokemon.getSpecy()), pokemonUpperLeft.x, pokemonUpperLeft.y, null);
		g2.drawImage(drawTrainer(), trainerUpperLeft.x, trainerUpperLeft.y, null);
		
		// draw item and effect
		// set the transform
		if (itemFlyStarted && !itemFlyEnd){
			// rotate the item
			BufferedImage img = drawItemFly(usingItemClass);
			AffineTransform trans = AffineTransform.getTranslateInstance(itemUpperLeft.x, itemUpperLeft.y);
			trans.rotate(itemRotationRadian, itemCurWidth/2, itemCurHeight/2);
			g2.drawImage(img, trans, null);
			System.out.println("Item Mid Location: " + itemUpperLeft.x + ", " + itemUpperLeft.y);
		}
		//g2.drawImage(drawItemFly(usingItemType), itemUpperLeft.x, itemUpperLeft.y, null);
	}

}
