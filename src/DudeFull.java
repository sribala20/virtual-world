import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DudeFull implements AnimatingEntity, ActionEntity, MovingEntity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final int resourceLimit;
	private final int resourceCount;
	private final double actionPeriod;
	private final double animationPeriod;

	//constructor
	public DudeFull (String id, Point position, double actionPeriod, double animationPeriod, int resourceCount, int resourceLimit, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.imageIndex = 0;
		this.resourceLimit = resourceLimit;
		this.resourceCount = resourceCount;
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

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int newImageIndex) {
		imageIndex = newImageIndex;
	}

	public double getActionPeriod() {
		return actionPeriod;
	}

	public List<PImage> getImages() {
		return images;
	}

	public double getAnimationPeriod() {
		return this.animationPeriod;
	}

	public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> world.withinBounds(point) && (world.getOccupancyCell(point) == null || world.getOccupancyCell(point).getClass() == Stump.class);
	}

	public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

		if (fullTarget.isPresent() && moveTo(world, fullTarget.get(), scheduler)) {
			transformFull(world, scheduler, imageStore);
		} else {
			scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
		}
	}

	public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		DudeNotFull dude = Functions.createDudeNotFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

		world.removeEntity(scheduler, this);

		world.addEntity(dude);
		dude.scheduleActions(scheduler, world, imageStore);
	}

	public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
		if (this.getPosition().adjacent(target.getPosition())) {
			return true;
		} else {
			return MovingEntity.super.moveTo(world, target, scheduler);
		}
	}

	public boolean transformAngry (WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		AngryDude dude = Functions.createAngryDude(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, imageStore.getImageList(Functions.ANGRY_DUDE_KEY));

		world.removeEntity(scheduler, this);
		scheduler.unscheduleAllEvents(this);

		world.addEntity(dude);
		dude.scheduleActions(scheduler, world, imageStore);
		return true;
	}
}
