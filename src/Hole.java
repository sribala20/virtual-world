import processing.core.PImage;

import java.util.List;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public class Hole implements Entity {
	private String id;
	private Point position;
	private List<PImage> images;
	private int imageIndex;

	public Hole(String id, Point position, List<PImage> images) {
		this.id = id;
		this.position = position;
		this.images = images;
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
	public int getImageIndex() {
		return imageIndex;
	}
}