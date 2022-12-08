public interface PlantEntity extends ActionEntity {

	int getHealth();

	void setHealth(int newHealth);
	default void executeActivity (WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
		if (!transform(world, scheduler, imageStore)) {
			scheduler.scheduleEvent(this, createActivityAction(world, imageStore), this.getActionPeriod());
		}
	}
	default boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {

		if (this.getHealth() <= 0) {
			Entity stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.getId(), this.getPosition(),
					imageStore.getImageList(Functions.STUMP_KEY));

			world.removeEntity(scheduler, this);

			world.addEntity(stump);

			return true;
		}

		return false;
	}
}