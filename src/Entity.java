import processing.core.PImage;

import java.util.List;

public interface Entity {
	String getId();
	Point getPosition();

	void setPosition(Point newPosition);

	int getImageIndex();

	List<PImage> getImages();

	default PImage getCurrentImage() {
		return this.getImages().get(this.getImageIndex() % this.getImages().size());
	}

	default String log() {
		return this.getId().isEmpty() ? null :
				String.format("%s %d %d %d", this.getId(), this.getPosition().x, this.getPosition().y, this.getImageIndex());
	}

}
