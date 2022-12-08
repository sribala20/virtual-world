import processing.core.PImage;

import java.util.List;

public class Obstacle implements AnimatingEntity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;
	private final double animationPeriod;

	//constructor
	public Obstacle(String id, Point position, double animationPeriod, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.animationPeriod = animationPeriod;
		this.images = images;
		this.imageIndex = 0;

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

	public int getImageIndex() {
		return imageIndex;
	}

	public void setImageIndex(int newImageIndex) {
		imageIndex = newImageIndex;
	}

	public List<PImage> getImages() {
		return images;
	}

	public Animation createAnimationAction(int repeatCount) {
		return new Animation(this, repeatCount);
	}

}
