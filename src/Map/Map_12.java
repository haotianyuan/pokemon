package Map;

import java.awt.Point;

public class Map_12 extends Map{

	private static final long serialVersionUID = -6202641066827591644L;
	
	private static final String MapTextFileName = "Map_2613.txt";
	private final static String MapTagName = "12";
	
	public Map_12() {
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
			return "11";
		}
		else if (portal.y == 1){
			return "02";
		}
		else if (portal.x == 20 && portal.y == 13){
			return "12";
		}
		else if (portal.x == 24 && portal.y == 13){
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
		else if (portal.x == 20 && portal.y == 13){
			return (new Point(24, 13));
		}
		else if (portal.x == 24 && portal.y == 13){
			return (new Point(24, 13));
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
