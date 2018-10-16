package Map;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class MapPokemon implements Serializable{

	private static final long serialVersionUID = -2330121193394533506L;
	private static final String MapFolderPath = "images"+ File.separator + "Map" + File.separator + "MapText" + File.separator;
	
	private int[][] mapIndex;	// store the indexed map
	private MapBlock[][] map;	// store the map as grid
	private final static int MapSize_Test = 129;
	protected final int MapSize_X;
	protected final int MapSize_Y;
	
	public MapPokemon(int height, int width){
		MapSize_X = width + 2;
		MapSize_Y = height + 2;
		initMap();
		mapGenerator();
	}
	
	private void initMap(){		
		mapIndex = new int[MapSize_X][MapSize_Y];
		map = new MapBlock[MapSize_X][MapSize_Y];
		
		// initiate the map block and index block
		for (int i = 0; i < MapSize_X; i ++){
			for (int j = 0; j < MapSize_Y; j ++){
				map[i][j] = new MapBlock(GroundType.GRASSLAND);
				mapIndex[i][j] = 0;
				// set the tag for border
				if (i == 0 || j == 0 || i == MapSize_X - 1 || j == MapSize_Y - 1){
					map[i][j].setObstacle(ObstacleType.BORDER);
					mapIndex[i][j] = 99;
				}
			}
		}
		
		
		for (int i = 0; i < MapSize_X; i ++){
			for (int j = 0; j < MapSize_Y; j ++){
				mapIndex[i][j] = 0;
			}
		}


		
	}
	
	protected String getFolderPath(){
		return MapFolderPath;
	}
	
	public int getMapSize_X(){
		return MapSize_X;
	}
	
	public int getMapSize_Y(){
		return MapSize_Y;
	}
	
	// getter and setter
	public int getSize(){
		return this.MapSize_Test;
	}
	
	
	// get the information of specific block
	public MapBlock getBlock(int x, int y){
		return map[y][x];
	}
		
	// get the information of specific block
	protected void setIndexBlock(int x, int y, int val){
		mapIndex[y][x] = val;
	}
	
	public int getIndexBlock(int x, int y){
		return mapIndex[y][x];
	}
	
	
	// TODO: GENERATE MAP
	protected abstract void mapGenerator();
	
	public abstract String getMapName();
	
	public abstract String getTeleportMap(Point portal);
	public abstract Point getTeleportPoint(Point portal);
	
	/*
	public double getVisionRadiusVertical(){
		return 5;
	}
	
	public double getVisionRadiusHorizontal(){
		return 7.5;
	}
	*/
	
	
	public void printMapInSymbol(){
		for (int i = 0; i < MapSize_X; i ++){
			for (int j = 0; j < MapSize_Y; j++){
				if (map[j][i].getObstacle() == ObstacleType.ROCK){
					System.out.print("R ");
				}
				else if (map[j][i].getInteractType() == InteractType.PORTAL){
					System.out.print("P ");
				}
				else if (map[j][i].getInteractType() == InteractType.SHORTGRASS){
					System.out.print("S ");
				}
				else if (map[j][i].getInteractType() == InteractType.SWAMP){
					System.out.print("W ");
				}
				else if (map[j][i].getObstacle() == ObstacleType.TREE){
					System.out.print("X ");
				}
				else if (map[j][i].getGround() == GroundType.GRASSLAND){
					System.out.print("O ");
				}
				else if (map[j][i].getGround() == GroundType.SOIL){
					System.out.print("D ");
				}
				else if (map[j][i].getObstacle() == ObstacleType.BORDER){
					System.out.print("B ");
				}
			}
			System.out.println("");
		}
	}
	
	protected void mapIndexReader(String mapFileName) {
		// read in the map file
		String filePath = getFolderPath() + mapFileName;
		try {
			Scanner inFile = new Scanner(new File(filePath));
			// initate the list
			ArrayList<String> lines = new ArrayList<String>();
			
			// loop to read in lines
			while(inFile.hasNextLine()){
				lines.add(inFile.nextLine());
			}
			
			inFile.close();
			
			// split lines into array
			int x = 0;	// index for loop
			for (String line: lines){
				int y = 0;	// index for loop
				String[] strArr = line.split("\\s+");
				for (String str: strArr){
					setIndexBlock(y + 1, x + 1, Integer.parseInt(str));
					y++;
				}
				x++;
			}
		} 
		// exception
		catch (FileNotFoundException e) {
			System.out.println("Cannot find file" + filePath);
			e.printStackTrace();
		}		
	}

}

