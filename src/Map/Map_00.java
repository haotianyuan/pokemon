package Map;

import java.awt.Point;

public class Map_00 extends Map{

	private static final long serialVersionUID = -2250637403160453799L;
	private static final String MapTextFileName = "Map_260.txt";
	private final static String MapTagName = "00";
	
	public Map_00() {
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
		if (portal.y == MapSize_Y - 2){
			return "10";
		}
		else{
			return null;
		}
	}

	@Override
	public Point getTeleportPoint(Point portal) {
		Point p = new Point();
		p.setLocation(portal.x, 2);
		return p;
	}
	
	@Override
	public String getMapName() {
		return MapTagName;
	}

}