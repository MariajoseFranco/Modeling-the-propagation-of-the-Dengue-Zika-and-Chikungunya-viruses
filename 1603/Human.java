package humans_1603;

import java.util.List;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;

/**
 * @author rafaelmateus
 *
 */
public class Human {
	
	private ContinuousSpace <Object> space; 
	private Grid <Object> grid;
	private int energy, startingEnergy;
	
	public Human (ContinuousSpace <Object> space, Grid <Object> grid,
			int energy) {
		this.space = space;
		this.grid = grid;
		this.energy = energy;
	}
	
	@Watch (watcheeClassName = "humans_1603.Mosquito",
			watcheeFieldNames = "moved",
			query = "within_moore 1",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run () {
		//get the grid location of this Human
		GridPoint pt = grid.getLocation(this);
		//use the GridCellNg class to create GridCells for the 
		//surrounding neighborhood
		GridCellNgh <Mosquito> nghCreator = new GridCellNgh <Mosquito> (grid,
				pt, Mosquito.class, 1, 1); 
		List <GridCell<Mosquito>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithLeastZombies = null;
		int minCount = Integer.MAX_VALUE;
		for (GridCell <Mosquito> cell : gridCells) {
			if (cell.size() < minCount) {
				pointWithLeastZombies = cell.getPoint();
				minCount = cell.size();
			}
		}
		if (energy > 0) {
			moveTowards(pointWithLeastZombies);
		} else {
			energy = startingEnergy; 
		}
	}
		
	public void moveTowards (GridPoint pt) {
		//only move if we are not already in this grid location
		if (!pt.equals(grid.getLocation(this))) {
			NdPoint myPoint = space.getLocation(this);
			NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
			double angle = SpatialMath.calcAngleFor2DMovement(space, myPoint, 
					otherPoint); 
			space.moveByVector(this, 2, angle, 0); 
			myPoint = space.getLocation(this);
			grid.moveTo(this, (int)myPoint.getX(), (int)myPoint.getY());
			energy--;
		}
	}
	
}
