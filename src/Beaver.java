import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Beaver implements ActionEntity, MovingEntity {
	private String id;
	private Point position;
	private List<PImage> images;
	private double actionPeriod;
	private double animationPeriod;
	private int imageIndex;

	public Beaver(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.actionPeriod = actionPeriod;
		this.animationPeriod = animationPeriod;
		this.imageIndex = 0;
	}

	public String getId() {
		return id;
	}
	public Point getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position = position;
	}
	public List<PImage> getImages() {
		return images;
	}

	public void setImageIndex(int imageIndex) {
		this.imageIndex = imageIndex;
	}

	public int getImageIndex() {
		return imageIndex;
	}
	public double getAnimationPeriod() {
		return animationPeriod;
	}
	public double getActionPeriod() {
		return actionPeriod;
	}
	public void nextImage() { imageIndex = imageIndex + 1; }

	public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
		//if (scheduler.getCurrentTime() % 4 <= 1) {
			if (this.getPosition().adjacent(target.getPosition())) {
				world.removeEntity(scheduler, target);
				return true;
			//}
		} return MovingEntity.super.moveTo(world, target, scheduler);
	}


	@Override
	public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> (world.withinBounds(point) && world.getOccupancyCell(point) == null);
	}


	public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		Optional<Entity> beaverTarget = world.findNearest(position, new ArrayList<>(List.of(Tree.class)));

		if (beaverTarget.isPresent()) {
			Point tgtPos = beaverTarget.get().getPosition();

			if (this.moveTo(world, beaverTarget.get(), scheduler)) {
				Hole hole = Functions.createHole(Functions.HOLE_KEY + "_" + getId(), tgtPos, imageStore.getImageList(Functions.HOLE_KEY));
				world.addEntity(hole);
			}
		}

		scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), actionPeriod);
	}
}

/*
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

	    public boolean transformToHole(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (health <= 0) {
            Hole hole = WorldModel.createHole(Functions.HOLE_KEY + "_" + getId(), getPosition(), imageStore.getImageList(Functions.HOLE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(hole);
            return true;
        }
        return false;
    }
 */