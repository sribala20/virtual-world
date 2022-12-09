import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DudeNotFull implements AnimatingEntity, ActionEntity, MovingEntity{
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final int resourceLimit;
	private int resourceCount;
	private final double actionPeriod;
	private final double animationPeriod;


	//constructor
	public DudeNotFull (String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.imageIndex = 0;
		this.resourceLimit = resourceLimit;
		this.resourceCount = 0;
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

	public List<PImage> getImages() {
		return images;
	}

	public double getAnimationPeriod() {
		return this.animationPeriod;
	}

	public double getActionPeriod() {
		return actionPeriod;
	}
	public Predicate<Point> nextPositionPassThrough(WorldModel world) {
		return point -> world.withinBounds(point) && (world.getOccupancyCell(point) == null || world.getOccupancyCell(point).getClass() == Stump.class);
	}

	public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

		if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !transformNotFull(world, scheduler, imageStore)) {
			scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.actionPeriod);
		}
	}

	public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
		if (this.position.adjacent(target.getPosition())) {
			this.resourceCount += 1;
			((PlantEntity)target).setHealth(((PlantEntity)target).getHealth() - 1);
			return true;
		} else {
			return MovingEntity.super.moveTo(world, target, scheduler);
		}
	}

	private boolean transformNotFull (WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (this.resourceCount >= this.resourceLimit) {
			DudeFull dude = Functions.createDudeFull(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceCount, this.resourceLimit, this.images);

			world.removeEntity(scheduler, this);
			scheduler.unscheduleAllEvents(this);

			world.addEntity(dude);
			dude.scheduleActions(scheduler, world, imageStore);
			return true;
		}
		return false;
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
