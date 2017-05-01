package Map;

import java.awt.Point;

public class Map_11 extends Map{
	
	private static final long serialVersionUID = 4963642230804791365L;
	
	private static final String MapTextFileName = "Map_263.txt";
	private final static String MapTagName = "11";
	
	public Map_11() {
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
		if (portal.x == 1){
			return "10";
		}
		else if (portal.y == 1){
			return "01";
		}
		else if (portal.x == MapSize_X - 2){
			return "12";
		}
		else{
			return null;
		}
	}
	
	@Override
	public Point getTeleportPoint(Point portal) {
		if (portal.x == 1){
			return (new Point(MapSize_X - 2, portal.y));
		}
		else if (portal.y == 1){
			return (new Point(portal.x, MapSize_Y - 2));
		}
		else if (portal.x == MapSize_X - 2){
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
