import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Fairy implements AnimatingEntity, ActionEntity, MovingEntity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final double actionPeriod;
	private final double animationPeriod;

	//constructor
	public Fairy (String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.imageIndex = 0;
		this.actionPeriod = actionPeriod;
		this.animationPeriod = animationPeriod;

	}
	//getters

	public String getId() {
		return id;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point newPosition) {
		position = newPosition;
	}

	public double getAnimationPeriod() {
		return this.animationPeriod;
	}

	public double getActionPeriod() {
		return actionPeriod;
	}


	public void setImageIndex(int newImageIndex) {
		imageIndex = newImageIndex;
	}

	public int getImageIndex() {
		return imageIndex;
	}

	public List<PImage> getImages() {
		return images;
	}

	/**
	 * Helper method for testing. Preserve this functionality while refactoring.
	 */

	public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> (world.withinBounds(point) && world.getOccupancyCell(point) == null);
	}

	public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class)));

		if (fairyTarget.isPresent()) {
			Point tgtPos = fairyTarget.get().getPosition();

			if (moveTo(world, fairyTarget.get(), scheduler)) {

				Sapling sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(Functions.SAPLING_KEY), 0);

				world.addEntity(sapling);
				sapling.scheduleActions(scheduler, world, imageStore);
			}
		}

		scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
	}

	public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
		if (this.position.adjacent(target.getPosition())) {
			world.removeEntity(scheduler, target);
			return true;
		} else {
			return MovingEntity.super.moveTo(world, target, scheduler);
		}
	}

}
