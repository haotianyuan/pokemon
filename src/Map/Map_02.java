package Map;

import java.awt.Point;

public class Map_02 extends Map{
	
	private static final long serialVersionUID = -4335937365325101997L;
	
	private static final String MapTextFileName = "Map_2612.txt";
	private final static String MapTagName = "02";
	
	public Map_02() {
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
	public Map changeMap(Point portal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMapName() {
		return MapTagName;
	}	
}
