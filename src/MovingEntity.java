import java.util.List;
import java.util.function.Predicate;

public interface MovingEntity extends Entity {

	default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
			Point nextPos = this.nextPosition(world, target.getPosition());

			if (!this.getPosition().equals(nextPos)) {
				world.moveEntity(scheduler, this, nextPos);
			}
			return false;
		}

	default Point nextPosition(WorldModel world, Point destPos) {
		//PathingStrategy strat = new SingleStepPathingStrategy();
		PathingStrategy strat = new AStarPathingStrategy();
		List<Point> path = strat.computePath(this.getPosition(), destPos,
				nextPositionPassThrough(world) , Point::adjacent, PathingStrategy.CARDINAL_NEIGHBORS);

		if (path.size() == 0) {
			return this.getPosition(); // if there are no valid spaces to move to
		}

		return path.get(0); // first in path for nextPos
	}

	Predicate<Point> nextPositionPassThrough(WorldModel world);

/*
	--FAIRY--
	public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> (world.withinBounds(point) && world.getOccupancyCell(point) == null);
	}

	--DUDE--
		public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> world.withinBounds(point) && (world.getOccupancyCell(point) == null || world.getOccupancyCell(point).getClass() == Stump.class);
	}
*/

}
