package Map;

import java.awt.Point;

public class Map_Test extends MapPokemon{
	
	public Map_Test() {
		super(129, 129);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 350193831245730397L;

	@Override
	public void mapGenerator() {
		// TODO: basic ground type
		for (int i = 0; i < this.getSize(); i ++){
			for (int j = 0; j < this.getSize(); j ++){
				// Obstacle around the map
				if (i == 0 || j == 0 || i == this.getSize() - 1 || j == this.getSize() - 1){
					getBlock(i, j).setGround(GroundType.SOIL);
					this.getBlock(i, j).setObstacle(ObstacleType.ROCK);
				}
				else if (Math.random() > 0.3){
					this.getBlock(i, j).setGround(GroundType.GRASSLAND);
				}
				else{
					this.getBlock(i, j).setGround(GroundType.SOIL);
				}
			}
		}
		
		// TODO: obstacle
		for (int i = 10; i < this.getSize() - 10; i ++){
			for (int j = 10; j < this.getSize() - 10; j ++){
				if (Math.random() > 0.95){
					this.getBlock(i, j).setObstacle(ObstacleType.ROCK);
				}
				else if (Math.random() < 0.1){
					this.getBlock(i, j).setObstacle(ObstacleType.TREE);
				}
				else{
					// TO Nothing
				}
			}
		}		
		
		// remove the obstacle around the centre
		for (int i = 55; i < this.getSize() - 55; i ++){
			for (int j = 55; j < this.getSize() - 55; j ++){
				this.getBlock(i, j).setObstacle(ObstacleType.NONE);
			}
		}
		
		// TODO: passable		
		for (int i = 0; i < this.getSize(); i ++){
			for (int j = 0; j < this.getSize(); j ++){
				// Obstacle around the map
				if (this.getBlock(i, j).getGround() == GroundType.SOIL && this.getBlock(i, j).getObstacle() == ObstacleType.NONE){
					this.getBlock(i, j).setInteract(InteractType.SAND);
				}
			}
		}
		
	}

	@Override
	public String getTeleportMap(Point portal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMapName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getTeleportPoint(Point portal) {
		// TODO Auto-generated method stub
		return null;
	}

}
