import processing.core.PImage;

import java.util.List;

public class Sapling implements AnimatingEntity, PlantEntity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final double actionPeriod;
	private final double animationPeriod;
	private int health;
	private final int healthLimit;
	//constructor
	public Sapling(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.imageIndex = 0;
		this.animationPeriod = animationPeriod;
		this.actionPeriod = actionPeriod;
		this.health = health;
		this.healthLimit = healthLimit;
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

	public int getHealth() {
		return health;
	}

	public void setHealth(int newHealth) {
		health = newHealth;
	}

	public double getAnimationPeriod() {
		return this.animationPeriod;
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

	public double getActionPeriod() {
		return actionPeriod;
	}

	public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		this.health++;
		PlantEntity.super.executeActivity(world, imageStore, scheduler);
	}

	public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (this.health >= this.healthLimit) {
			Tree tree = Functions.createTree(Functions.TREE_KEY + "_" + this.id, this.position,
					Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN),
					Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

			world.removeEntity(scheduler, this);

			world.addEntity(tree);
			tree.scheduleActions(scheduler, world, imageStore);

			return true;
		}
		return PlantEntity.super.transform(world, scheduler,imageStore);

	}


}
