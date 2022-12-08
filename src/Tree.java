import processing.core.PImage;

import java.util.List;

public class Tree implements AnimatingEntity, PlantEntity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final double actionPeriod;
	private final double animationPeriod;
	private int health;

	//constructor
	public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
		this.id = id;
		this.position = position;
		this.images = images;
		this.imageIndex = 0;
		this.actionPeriod = actionPeriod;
		this.animationPeriod = animationPeriod;
		this.health = health;
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

	public boolean transformToHole(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
		if (health <= 0) {
			Hole hole = Functions.createHole(Functions.HOLE_KEY + "_" + getId(), getPosition(), imageStore.getImageList(Functions.HOLE_KEY));

			world.removeEntity(scheduler, this);

			world.addEntity(hole);
			return true;
		}
		return false;
	}

}
