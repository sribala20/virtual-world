public interface ActionEntity extends AnimatingEntity {

	void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

	double getActionPeriod();
	default Activity createActivityAction(WorldModel world, ImageStore imageStore) {
		return new Activity(this, world, imageStore);
	}

	default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
		AnimatingEntity.super.scheduleActions(scheduler);
		scheduler.scheduleEvent(this, createActivityAction(world, imageStore), this.getActionPeriod());
	}

}
