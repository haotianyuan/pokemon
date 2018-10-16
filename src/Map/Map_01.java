package Map;

import java.awt.Point;

public class Map_01 extends MapPokemon{
	

	private static final long serialVersionUID = -7762644513628487590L;
	
	private static final String MapTextFileName = "Map_261.txt";
	private final static String MapTagName = "01";
	
	public Map_01() {
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
			return "11";
		}
		else if (portal.x == 41 && portal.y == 4){
			return "02";
		}
		else{
			return null;
		}
	}
	
	@Override
	public Point getTeleportPoint(Point portal) {
		if (portal.y == MapSize_Y - 2){
			Point p = new Point();
			p.setLocation(portal.x, 2);
			return p;
		}
		else if (portal.x == MapSize_X - 2 && portal.y == 4){
			Point p = new Point();
			p.setLocation(2, 4);
			return p;
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
