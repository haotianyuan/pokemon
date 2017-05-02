package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import GameModel.Direction;
import GameModel.GameModel;
import Map.GroundType;
import Map.Map_00;
import Map.ObstacleType;

public class MainGameView extends JPanel implements Observer{

	private static final long serialVersionUID = 7713222421276164624L;
		
	// declare drawing coords
	private static Point onScreenTrainerMid = new Point();
	private static Point onMapTrainerMid = new Point();
	private static Point onMapCenterOfView = new Point();
	private GameModel gameModel;
	private boolean needToChangeView = false;
	
	private int curX;
	private int curY;
	
	// declare timer detail and animation detail

	private final static int VisionRadius_X = 240;	// define the vision of the trainer on the map
	private final static int VisionRadius_Y = 160;	// define the vision of the trainer on the map
	
	// constructor
	public MainGameView(){
		setLayout(null);
		loadImages();
		repaint();
	}
	
	public boolean InteractEnable(){
		return endMoving && transEnd;
	}
		

	/**************** Calculator for Location ****************/
	
	private boolean trainerMove_Vertical = false;
	private boolean trainerMove_Horizontal = false;
	
	private Point getOnMapCenterOfView(){		
		Point p = new Point();
				
		// declare the center of the view of the default
		double viewCenterOnMapX = onMapTrainerMid.x;
		double viewCenterOnMapY = onMapTrainerMid.y;
		
		// check if the vision cover the corner/sides
		// collide left side or right side
		if (viewCenterOnMapX <= 272 ){
			viewCenterOnMapX = 8.5 * MapBlockSize;
		}
		else if (viewCenterOnMapX >= 1072){
			viewCenterOnMapX = 33.5 * MapBlockSize;
		}
		
		// collide top or bottom
		if (viewCenterOnMapY <= 208 ){
			viewCenterOnMapY = 6.5 * MapBlockSize;
		}
		else if (viewCenterOnMapY >= 1152){
			viewCenterOnMapY = 36 * MapBlockSize;
		}
		
		p.setLocation((int) viewCenterOnMapX, (int) viewCenterOnMapY);
		onMapCenterOfView.setLocation(p);
		return p;		
	}
	
	private Point getOnMapTrainerMid(){
		if (startMoving && !endMoving){
			if (onMapTrainerMid.y >= 1152){
				trainerMove_Vertical = true;
			}
		}
		Point p = new Point();
		
		int pX = (int) ((curX + 0.5 ) * MapBlockSize);
		int pY = (int) ((curY + 0.5) * MapBlockSize);
		
		p.setLocation(pX, pY);
		onMapTrainerMid.setLocation(p);
		return p;
	}
	
	private Point getOnScreenTrainerMid(){
		if (startMoving && !endMoving){
			if (onMapTrainerMid.y >= 1152 || onMapTrainerMid.y <= 208){
				trainerMove_Vertical = true;
			}
			else{
				trainerMove_Vertical = false;
			}
			
			if (onMapTrainerMid.x <= 272 || onMapTrainerMid.x >= 1072){
				trainerMove_Horizontal = true;
			}
			else{
				trainerMove_Horizontal = false;
			}
			return null;
		}
		
		Point p = new Point();
		
		// set the default location
		double trainerMidOnScreenX = VisionRadius_X;
		double trainerMidOnScreenY = VisionRadius_Y;
		
		// check if the vision cover the corner/sides
		
		// collide left side or right side
		if (curX < 8){
			trainerMidOnScreenX = (curX - 0.5) * MapBlockSize;
			trainerMove_Horizontal = true;
		}
		else if (curX > 33){
			trainerMidOnScreenX = 2 * VisionRadius_X - (gameModel.getCurMap().getMapSize_X() - curX - 1.5) * MapBlockSize;
		}
		else{
			trainerMove_Horizontal = false;
		}
		
		// collide top or bottom
		if (curY <= 6){
			trainerMidOnScreenY = (curY - 1) * MapBlockSize;
		}
		else if (onMapTrainerMid.y >= 1152){
			System.out.println("mark here");
			trainerMidOnScreenY = 2 * VisionRadius_Y - (gameModel.getCurMap().getMapSize_Y() - curY - 1.5) * MapBlockSize;
		}
		else{
			trainerMove_Vertical = false;
		}
		
		p.setLocation((int) trainerMidOnScreenX, (int) trainerMidOnScreenY);
		
		onScreenTrainerMid.setLocation(p);
		return p;
	}
	

	
	/***************************** Movement Timer *************************************/
	private Timer moveTimer;
	private int trainerMoveCounter;
	public final static int FramePerMove = 16;
	public final static int delayInMillis = 20;
	private static final double PixelPerFrame = 2;
	private boolean startMoving = false;
	private boolean endMoving = true;
	
	private void startMoveTimer() {
		moveTimer = new Timer(delayInMillis, new moveTimerListener());
		startMoving = true;
		endMoving = false;
		moveTimer.start();
		
	}
	
	private void drawTrainerWithAnimation() {
		curX = gameModel.getPrevLocation().x;
		curY = gameModel.getPrevLocation().y;
		
		getOnMapTrainerMid();
		getOnScreenTrainerMid();
		getOnMapCenterOfView();
		
		startMoveTimer();
	}
	
	private class moveTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (trainerMoveCounter < FramePerMove ){
				
				//getOnMapCenterOfView();
				//System.out.println("Counter: " + trainerMoveCounter);		
				
				// update map location
				if (gameModel.getDir() == Direction.EAST){
					onMapTrainerMid.setLocation(onMapTrainerMid.x + PixelPerFrame, onMapTrainerMid.y);
				}
				else if (gameModel.getDir() == Direction.WEST){
					onMapTrainerMid.setLocation(onMapTrainerMid.x - PixelPerFrame, onMapTrainerMid.y);
				}
				else if (gameModel.getDir() == Direction.SOUTH){
					onMapTrainerMid.setLocation(onMapTrainerMid.x, onMapTrainerMid.y + PixelPerFrame);
				}
				else if (gameModel.getDir() == Direction.NORTH){
					onMapTrainerMid.setLocation(onMapTrainerMid.x, onMapTrainerMid.y - PixelPerFrame);
				}	
				
				// update current location 
				// Check direction
				getOnScreenTrainerMid();
				if (gameModel.getDir() == Direction.EAST && trainerMove_Horizontal == true){
					onScreenTrainerMid.setLocation(onScreenTrainerMid.x + PixelPerFrame, onScreenTrainerMid.y);
				}
				else if (gameModel.getDir() == Direction.WEST && trainerMove_Horizontal == true){
					onScreenTrainerMid.setLocation(onScreenTrainerMid.x - PixelPerFrame, onScreenTrainerMid.y);
				}
				else if (gameModel.getDir() == Direction.SOUTH && trainerMove_Vertical == true){
					onScreenTrainerMid.setLocation(onScreenTrainerMid.x, onScreenTrainerMid.y + PixelPerFrame);
				}
				else if (gameModel.getDir() == Direction.NORTH && trainerMove_Vertical == true){
					onScreenTrainerMid.setLocation(onScreenTrainerMid.x, onScreenTrainerMid.y - PixelPerFrame);
				}	
				
				//System.out.println("Counter: " + trainerMoveCounter);
				//printTrack();
				
				getOnMapCenterOfView();
				trainerMoveCounter++;
				repaint();
			}
			else{
				moveTimer.stop();
				trainerMoveCounter = 0;
				startMoving = false;
				endMoving = true;
				gameModel.setLocation(gameModel.getCurLocation().x, gameModel.getCurLocation().y);
				
				curX = gameModel.getCurLocation().x;
				curY = gameModel.getCurLocation().y;
				
				getOnMapTrainerMid();
				getOnScreenTrainerMid();
				getOnMapCenterOfView();
				
				repaint();
				
				//printTrack();
				
				
				//System.out.println("Final On Map Trainer Location: " + onMapTrainerMid.x + ", " + onMapTrainerMid.y);
				
				// call the pokemon encounter
				// avoid repeat encounter and set up the encountered pokemon
				if (!gameModel.hasEncounteredThisBlock()){
					gameModel.pokemonEncounter();
				}
				// check if encounter pokemon
				if (gameModel.getTrainer().getCurEncounterPokemon() != null){
					playTransitionAnimation();
				}
			}
		}

		
	}
	
	
	
	/***************************** Transition Timer *************************************/
	private Timer transTimer;
	private int transMoveCounter = 0;
	public final static int FramePerTrans = 35;
	private static final double PixelPerTransFrame = 16;
	private boolean transStarted = false;
	private boolean transEnd = true;
	//private double transRotationRadian = 0;
	
	private void startTransTimer() {
		transTimer = new Timer(delayInMillis, new transTimerListener());
		transStarted = true;
		transEnd = false;
		transTimer.start();
		
	}
	
	public void playTransitionAnimation() {		
		//System.out.println("start transition timer");
		startTransTimer();
	}
	
	private class transTimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (transMoveCounter < FramePerTrans){
				repaint();
				transMoveCounter++;
				//System.out.println("counter: " + transMoveCounter);
			}
			else{
				transTimer.stop();
				transMoveCounter = 0;
				transStarted = false;
				transEnd = true;
				if (gameModel.getTrainer().getCurEncounterPokemon() != null){
					setVisible(false);
					return;
				}
				
				if (gameModel.isTeleporting()){
					gameModel.doneTeleporting();
				}
			}
		}
	}	
	
	
	@Override
	public void update(Observable o, Object arg) {
		gameModel = (GameModel) o;
		
		// check if teleporting play the transition animation
		if (gameModel.isTeleporting()){
			playTransitionAnimation();
		}		
		// if the user did not move, dont play the moving animation
		else if (InteractEnable() && !gameModel.getPrevLocation().equals(gameModel.getCurLocation())){
			
			//System.out.println("call the drawing function");
			
			drawTrainerWithAnimation();
		}
		else{
			curX = gameModel.getCurLocation().x;
			curY = gameModel.getCurLocation().y;
			
			getOnMapTrainerMid();
			getOnScreenTrainerMid();
			getOnMapCenterOfView();
		}
		
		repaint();
		
		//printTrack();
	}
	
	private void printTrack(){
		System.out.println("Current Coords of Trainer On Map: " + gameModel.getCurLocation().x + ", " + gameModel.getCurLocation().y );
		System.out.println("Current Center of Trainer On Screen: " + onScreenTrainerMid.x + ", " + onScreenTrainerMid.y);
		System.out.println("Current Center of Trainer On Map: " + onMapTrainerMid.x + ", " + onMapTrainerMid.y);
		System.out.println("Current Center of View On Map: " + onMapCenterOfView.x + ", " + onMapCenterOfView.y);
	}
	
		
	/*
	 * *************************************** *
	 *  Painting and Animation Creator Below   *
	 * *************************************** *
	 */
	
	/**************** Define the Sprite Sheet ****************/
	
	// declare image sheet
	private static BufferedImage trainerSheet;
	private static BufferedImage transSheet;
	private static BufferedImage map_00;
	private static BufferedImage map_01;
	private static BufferedImage map_02;
	private static BufferedImage map_10;
	private static BufferedImage map_11;
	private static BufferedImage map_12;
	
	private static final String TextureFolderPath = "images"+ File.separator + "Texture" + File.separator;
	private static final String MapFolderPath = "images"+ File.separator + "Map" + File.separator;
	
	// define sprite sheet name
	private static final String TrainerTextureFileName = "pokemon_trainer.png";
	private static final String TransitionTextureFileName = "TransitionBall" + File.separator + "pokeball_transition_00.png";
	
	private static final String MapTextureFileName_00 = "SAFARI ZONE_260.png";
	private static final String MapTextureFileName_01 = "SAFARI ZONE_261.png";
	private static final String MapTextureFileName_02 = "SAFARI ZONE_2612.png";
	private static final String MapTextureFileName_10 = "SAFARI ZONE_262.png";
	private static final String MapTextureFileName_11 = "SAFARI ZONE_263.png";
	private static final String MapTextureFileName_12 = "SAFARI ZONE_2613.png";
	
	// define the size of one block
	public static final int MapBlockSize = 32;
		
	// function to load images sheet
	private void loadImages() {		
		loadTrainerTexture();
		loadTransitionImage();
		// load maps
		loadMap_00();
		loadMap_01();
		loadMap_02();
		loadMap_10();
		loadMap_11();
		loadMap_12();
		
	}
	
	private void loadTrainerTexture(){
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
	
	private void loadMap_00(){
		String filePath = MapFolderPath + MapTextureFileName_00;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_00 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadMap_01(){
		String filePath = MapFolderPath + MapTextureFileName_01;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_01 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadMap_02(){
		String filePath = MapFolderPath + MapTextureFileName_02;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_02 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadMap_10(){
		String filePath = MapFolderPath + MapTextureFileName_10;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_10 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadMap_11(){
		String filePath = MapFolderPath + MapTextureFileName_11;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_11 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	private void loadMap_12(){
		String filePath = MapFolderPath + MapTextureFileName_12;
		
		// try to open the file of the trainer
		try{
			File mapFile = new File(filePath);
			map_12 = ImageIO.read(mapFile);
		}
		catch (IOException e){
			System.out.println("Could not find: " + filePath);
		}
	}
	
	
	/**************** Draw Trainer ****************/
	
	private static final int Trainer_Height = 40;
	private static final int Trainer_Width = 30;
	
	private static final int Trainer_South_OFFSET_Y = 78;
	private static final int Trainer_South_Steady_OFFSET_X = 41;
	private static final int Trainer_South_1_OFFSET_X = 7;
	private static final int Trainer_South_2_OFFSET_X = 73;
	
	private static final int Trainer_North_OFFSET_Y = 143;
	private static final int Trainer_North_Steady_OFFSET_X = 41;
	private static final int Trainer_North_1_OFFSET_X = 8;
	private static final int Trainer_North_2_OFFSET_X = 73;

	private static final int Trainer_West_OFFSET_Y = 206;
	private static final int Trainer_West_Steady_OFFSET_X = 38;
	private static final int Trainer_West_1_OFFSET_X = 7;
	private static final int Trainer_West_2_OFFSET_X = 71;

	private static final int Trainer_East_OFFSET_Y = 246;
	private static final int Trainer_East_Steady_OFFSET_X = 154;
	private static final int Trainer_East_1_OFFSET_X = 122;
	private static final int Trainer_East_2_OFFSET_X = 186;


	// function to draw the tree obstacle according to the model
	private BufferedImage drawTrainer(){
		// check the direction of the trainer
		// walking towards south
		if (gameModel.getDir() == Direction.SOUTH){
			// during moving
			if (startMoving && !endMoving){
				if (trainerMoveCounter >= 1  && trainerMoveCounter < 8){
					return trainerSheet.getSubimage(Trainer_South_1_OFFSET_X, Trainer_South_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else if (trainerMoveCounter >= 10  && trainerMoveCounter < 16){
					return trainerSheet.getSubimage(Trainer_South_2_OFFSET_X, Trainer_South_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else{
					return trainerSheet.getSubimage(Trainer_South_Steady_OFFSET_X, Trainer_South_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
			}
			// Idle state
			else{
				return trainerSheet.getSubimage(Trainer_South_Steady_OFFSET_X, Trainer_South_OFFSET_Y, 
						Trainer_Width, Trainer_Height);
			}
		}
		// walking towards north
		else if (gameModel.getDir() == Direction.NORTH){
			// during moving
			if (startMoving && !endMoving){
				if (trainerMoveCounter >= 1  && trainerMoveCounter < 8){
					return trainerSheet.getSubimage(Trainer_North_1_OFFSET_X, Trainer_North_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else if (trainerMoveCounter >= 10  && trainerMoveCounter < 16){
					return trainerSheet.getSubimage(Trainer_North_2_OFFSET_X, Trainer_North_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else{
					return trainerSheet.getSubimage(Trainer_North_Steady_OFFSET_X, Trainer_North_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
			}
			// Idle state
			else{
				return trainerSheet.getSubimage(Trainer_North_Steady_OFFSET_X, Trainer_North_OFFSET_Y, 
						Trainer_Width, Trainer_Height);
			}

		}
		// walking towards west
		else if (gameModel.getDir() == Direction.WEST){
			// during moving
			if (startMoving && !endMoving){
				if (trainerMoveCounter >= 1  && trainerMoveCounter < 8){
					return trainerSheet.getSubimage(Trainer_West_1_OFFSET_X, Trainer_West_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else if (trainerMoveCounter >= 10 && trainerMoveCounter < 16){
					return trainerSheet.getSubimage(Trainer_West_2_OFFSET_X, Trainer_West_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else{
					return trainerSheet.getSubimage(Trainer_West_Steady_OFFSET_X, Trainer_West_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}				
			}
			// Idle state
			else{
				return trainerSheet.getSubimage(Trainer_West_Steady_OFFSET_X, Trainer_West_OFFSET_Y, 
						Trainer_Width, Trainer_Height);
			}
		}
		// walking towards east
		else{
			// during moving
			if (startMoving && !endMoving){
				if (trainerMoveCounter >= 1  && trainerMoveCounter < 8){
					return trainerSheet.getSubimage(Trainer_East_1_OFFSET_X, Trainer_East_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else if (trainerMoveCounter >= 10 && trainerMoveCounter < 16){
					return trainerSheet.getSubimage(Trainer_East_2_OFFSET_X, Trainer_East_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}
				else{
					return trainerSheet.getSubimage(Trainer_East_Steady_OFFSET_X, Trainer_East_OFFSET_Y, 
							Trainer_Width, Trainer_Height);
				}				
			}
			// Idle state
			else{
				return trainerSheet.getSubimage(Trainer_East_Steady_OFFSET_X, Trainer_East_OFFSET_Y, 
						Trainer_Width, Trainer_Height);
			}	
		}
	}
	
	
	/**************** Draw Map ****************/
	private static final int Map_Height = 320;
	private static final int Map_Width = 480;
	
	// function to draw the tree obstacle according to the model
	private BufferedImage drawMapView(){
		int Map_OFFSET_X = onMapCenterOfView.x - VisionRadius_X - 32;
		int Map_OFFSET_Y = onMapCenterOfView.y - VisionRadius_Y - 32;
		
		//System.out.println("Map OFFSET_x: " + Map_OFFSET_X + ", Map OFFSET_x: " + Map_OFFSET_Y);
		// check the current map
		if (gameModel.getCurMap().getMapName().equals("00")){
			return map_00.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else if (gameModel.getCurMap().getMapName().equals("01")){
			return map_01.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else if (gameModel.getCurMap().getMapName().equals("02")){
			return map_02.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else if (gameModel.getCurMap().getMapName().equals("10")){
			return map_10.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else if (gameModel.getCurMap().getMapName().equals("11")){
			return map_11.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else if (gameModel.getCurMap().getMapName().equals("12")){
			return map_12.getSubimage(Map_OFFSET_X, Map_OFFSET_Y, 
					Map_Width, Map_Height);
		}
		else{
			return null;
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
	
	/*
	private BufferedImage drawTransitionBall_03(){
		return transSheet.getSubimage(TransBall_OFFSET_X, TransBall_OFFSET_Y, TransBall_Width, TransBall_Height);
	}
	
	private BufferedImage drawTransitionBall_04(){
		return transSheet.getSubimage(Trans_BallOFFSET_X, TransBall_OFFSET_Y, TransBall_Width, TransBall_Height);
	}
	*/
	
	
	/**************** Paint function ****************/
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		// check the existence of model
		if (gameModel == null){
			return;
		}
		
		// Check if play moving animation
		if ((!startMoving && endMoving && !gameModel.getCurLocation().equals(gameModel.getPrevLocation())) || gameModel.isTeleporting()){
			// calculate the position of the trainer during moving time
			getOnMapTrainerMid();
			getOnScreenTrainerMid();
			getOnMapCenterOfView();
		}		
				
		// draw view
		g2.drawImage(drawMapView(), 0, 0, null);
		// draw trainer
		g2.drawImage(drawTrainer(), onScreenTrainerMid.x - Trainer_Width/2, onScreenTrainerMid.y - Trainer_Height + 10, null);
		
		//System.out.println("After Move:   CurX: " + curX + ", CurY: " + curY);
		//printTrack();
		// draw transition ball
		if (transStarted && !transEnd){
			drawTransitionImage(g2);			
		}
			
	}
	
	private void drawTransitionImage(Graphics g2){
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
	
	
	
}



