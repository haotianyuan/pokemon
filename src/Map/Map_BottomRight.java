package Map;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Map_BottomRight extends Map{

	private static final long serialVersionUID = -2250637403160453799L;
	private static final String MapTextFileName = "Map_260.txt";
	
	private Point topPortal;
	private Point leftPortal;

	@Override
	// generator the map according to the index map
	public void mapGenerator() {
		mapIndexReader();	// create the index map
		
		// loop the index map
		for (int i = 0; i < MapIndexSize_X; i ++){
			for (int j = 0; j < MapIndexSize_Y; j++){
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
	protected void mapIndexReader() {
		// read in the map file
		String filePath = getFolderPath() + MapTextFileName;
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
					setIndexBlock(x, y, Integer.parseInt(str));
					y++;
				}
				x++;
			}
			
			/*
			// print out the map in text
			for (int i = 0; i < 40; i++){
				for (int j = 0; j < 40; j++){
					System.out.print(getIndexBlock(i, j) + "");
				}
				System.out.println("");
			}
			*/
		} 
		// exception
		catch (FileNotFoundException e) {
			System.out.println("Cannot find file" + filePath);
			e.printStackTrace();
		}		
	}
	
	// TODO: we gonna use them in iterator 2
	/*
	public Point getTopPortal(){
		return this.topPortal;
	}
	
	public Point getLeftPortal(){
		return this.leftPortal;
	}
	*/
	
	

}
