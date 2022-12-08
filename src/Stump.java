import processing.core.PImage;

import java.util.List;

public class Stump implements Entity {
	private final String id;
	private Point position;
	private final List<PImage> images;
	private int imageIndex;

	//constructor
	public Stump(String id, Point position, List<PImage> images) {
		this.id = id;
		this.position = position;
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

	public int getImageIndex() {
		return imageIndex;
	}

	public List<PImage> getImages() {
		return images;
	}

}
