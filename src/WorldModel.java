import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {
    }

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	/**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
	public boolean isOccupied(Point pos) {
		return withinBounds(pos) && getOccupancyCell(pos) != null;
	}
	public Entity getOccupancyCell(Point pos) {
		return this.occupancy[pos.y][pos.x];
	}

	public boolean withinBounds(Point pos) {
		return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
	}

	public void addEntity(Entity entity) {
		if (withinBounds(entity.getPosition())) {
			setOccupancyCell(entity.getPosition(), entity);
			this.entities.add(entity);
		}
	}

	public void removeEntity(EventScheduler scheduler, Entity entity) {
		scheduler.unscheduleAllEvents(entity);
		removeEntityAt(entity.getPosition());
	}

	public void removeEntityAt(Point pos) {
		if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
			Entity entity = this.getOccupancyCell(pos);

			/* This moves the entity just outside of the grid for
			 * debugging purposes. */
			entity.setPosition(new Point(-1, -1));
			this.entities.remove(entity);
			setOccupancyCell(pos, null);
		}
	}
	private void setOccupancyCell(Point pos, Entity entity) {
		this.occupancy[pos.y][pos.x] = entity;
	}

	public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
		List<Entity> ofType = new LinkedList<>();
		for (Class kind : kinds) {
			for (Entity entity : this.entities) {
				if (entity.getClass() == kind) {
					ofType.add(entity);
				}
			}
		}
		return Functions.nearestEntity(ofType, pos);
	}

	public void moveEntity(EventScheduler scheduler, MovingEntity entity, Point pos) {
		Point oldPos = entity.getPosition();
		if (withinBounds(pos) && !pos.equals(oldPos)) {
			setOccupancyCell(oldPos, null);
			Optional<Entity> occupant = getOccupant(pos);
			occupant.ifPresent(target -> removeEntity(scheduler, target));
			setOccupancyCell(pos, entity);
			entity.setPosition(pos);
		}
	}

	public Optional<Entity> getOccupant(Point pos) {
		if (isOccupied(pos)) {
			return Optional.of(getOccupancyCell(pos));
		} else {
			return Optional.empty();
		} //might need plantentity??
	}

	public void tryAddEntity(Entity entity) {
		if (isOccupied(entity.getPosition())) {
			// arguably the wrong type of exception, but we are not
			// defining our own exceptions yet
			throw new IllegalArgumentException("position occupied");
		}
		addEntity(entity);
	}

	public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
		parseSaveFile(saveFile, imageStore, defaultBackground);
		if(this.background == null){
			this.background = new Background[this.numRows][this.numCols];
			for (Background[] row : this.background)
				Arrays.fill(row, defaultBackground);
		}
		if(this.occupancy == null){
			this.occupancy = new Entity[this.numRows][this.numCols];
			this.entities = new HashSet<>();
		}
	}

	private void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
		String lastHeader = "";
		int headerLine = 0;
		int lineCounter = 0;
		while(saveFile.hasNextLine()){
			lineCounter++;
			String line = saveFile.nextLine().strip();
			if(line.endsWith(":")){
				headerLine = lineCounter;
				lastHeader = line;
				switch (line){
					case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
					case "Entities:" -> {
						this.occupancy = new Entity[this.numRows][this.numCols];
						this.entities = new HashSet<>();
					}
				}
			}else{
				switch (lastHeader){
					case "Rows:" -> this.numRows = Integer.parseInt(line);
					case "Cols:" -> this.numCols = Integer.parseInt(line);
					case "Backgrounds:" -> parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
					case "Entities:" -> Functions.parseEntity(this, line, imageStore);
				}
			}
		}
	}

	private void parseBackgroundRow(String line, int row, ImageStore imageStore) {
		String[] cells = line.split(" ");
		if(row < this.numRows){
			int rows = Math.min(cells.length, this.numCols);
			for (int col = 0; col < rows; col++){
				this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
			}
		}
	}

	private Background getBackgroundCell(Point pos) {
		return this.background[pos.y][pos.x];
	}

	public void setBackgroundCell(Point pos, Background background) {
		this.background[pos.y][pos.x] = background;
	}

	public Optional<PImage> getBackgroundImage(Point pos) {
		if (withinBounds(pos)) {
			return Optional.of(getBackgroundCell(pos).getCurrentImage());
		} else {
			return Optional.empty();
		}
	}
}


