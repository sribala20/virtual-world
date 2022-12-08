public interface AnimatingEntity extends Entity {

	void setImageIndex(int imageIndex);
	int getImageIndex();
	double getAnimationPeriod();

	default void scheduleActions(EventScheduler scheduler) {
		scheduler.scheduleEvent(this, createAnimationAction(0), getAnimationPeriod());
	}

	default Animation createAnimationAction(int repeatCount) {
		return new Animation(this, repeatCount);
	}

	default void nextImage() {
		this.setImageIndex(this.getImageIndex() + 1);
	}
}
