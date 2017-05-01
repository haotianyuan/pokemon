package Map;

import java.awt.Point;

public class Map_10 extends Map{
		
	private static final long serialVersionUID = 7994764713547686893L;

	private static final String MapTextFileName = "Map_262.txt";
	private final static String MapTagName = "10";
	
	public Map_10() {
		super(40, 40);
	}
	
	@Override
	// generator the map according to the index map
	public void mapGenerator() {
		mapIndexReader(MapTextFileName);	// create the index map
		
		// loop the index map
		for (int i = 1; i < MapSize_X - 1; i ++){
			for (int j = 1; j < MapSize_Y - 1; j++){
				// set the ground type/ obstacle type/ interactableType
				getBlock(i, j).setBlockFromIndex(getIndexBlock(i, j));
			}
		}
		
		// print
		//printMapInSymbol();
	}

	@Override
	public String getTeleportMap(Point portal) {
		if (portal.y == 1){
			return "00";
		}
		else if (portal.x == MapSize_X - 2){
			return "11";
		}
		else{
			return null;
		}
	}
	
	@Override
	public Point getTeleportPoint(Point portal) {
		if (portal.y == 1){
			return (new Point(portal.x, MapSize_Y - 1));
		}
		else if (portal.x == MapSize_X -2){
			return (new Point(2, portal.y));
		}
		else{
			return null;
		}
	}	

	@Override
	public String getMapName() {
		return MapTagName;
	}

}