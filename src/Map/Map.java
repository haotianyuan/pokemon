package Map;

import java.awt.Point;
import java.io.File;
import java.io.Serializable;

public abstract class Map implements Serializable{

	private static final long serialVersionUID = -2330121193394533506L;
	private static final String MapFolderPath = "images"+ File.separator + "Map" + File.separator + "MapText" + File.separator;
	
	private int[][] mapIndex;	// store the indexed map
	private MapBlock[][] map;	// store the map as grid
	private final static int MapSize = 129;
	protected final static int MapIndexSize_X = 40;
	protected final static int MapIndexSize_Y = 40;
	
	public Map(){
		initMap();
		mapGenerator();
	}
	
	private void initMap(){
		mapIndex = new int[MapSize][MapSize];
		map = new MapBlock[MapSize][MapSize];
		
		for (int i = 0; i < MapSize; i ++){
			for (int j = 0; j < MapSize; j ++){
				map[i][j] = new MapBlock(GroundType.GRASSLAND);
			}
		}
		
		for (int i = 0; i < MapSize; i ++){
			for (int j = 0; j < MapSize; j ++){
				mapIndex[i][j] = 0;
			}
		}
	}
	
	protected String getFolderPath(){
		return MapFolderPath;
	}
	
	// getter and setter
	public int getSize(){
		return this.MapSize;
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
	protected abstract void mapIndexReader();
	
	public abstract Map changeMap(Point portal);
	
	
	public void printMapInSymbol(){
		for (int i = 0; i < 129; i ++){
			for (int j = 0; j < 129; j++){
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
			}
			System.out.println("");
		}
	}

}

