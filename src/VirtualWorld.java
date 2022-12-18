import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    private void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);
		System.out.println(scheduler.getCurrentTime());

        Background outer_dam = new Background("outer_dam", imageStore.getImageList("outer_dam"));
		Background inner_dam1 = new Background("inner_dam1", imageStore.getImageList("inner_dam1"));
		Background inner_dam2 = new Background("inner_dam2", imageStore.getImageList("inner_dam2"));
		Background inner_dam3 = new Background("inner_dam3", imageStore.getImageList("inner_dam3"));
        buildEvent(pressed, outer_dam, 3);

		addBeaver(world, scheduler, imageStore, pressed);
        angerDudes(world, scheduler, imageStore, pressed);

		world.setBackgroundCell(new Point(pressed.x, pressed.y + 1), inner_dam1);
		world.setBackgroundCell(new Point(pressed.x + 1, pressed.y + 1), inner_dam2);
		world.setBackgroundCell(new Point(pressed.x + 2, pressed.y + 1), inner_dam3);

    }
	public void buildEvent(Point pressed, Background background, int limit) {
		for (int x = 0; x < limit; x++)
		{
			for (int y = 0; y < limit; y++)
			{
				world.setBackgroundCell(new Point(pressed.x + x, pressed.y + y), background);
			}
		}
	}

	public void addBeaver(WorldModel world, EventScheduler scheduler, ImageStore imageStore, Point pt) {
		Beaver beaver = Functions.createBeaver(Functions.BEAVER_KEY, pt, 1.3,1,
				imageStore.getImageList("beaver"));

		world.addEntity(beaver);
		beaver.scheduleActions(scheduler, world, imageStore);
	}

    public void angerDudes(WorldModel world, EventScheduler scheduler, ImageStore imageStore, Point pt) {
        Optional<Entity> dudeTarget = world.findNearest(pt, new ArrayList<>(List.of(DudeFull.class, DudeNotFull.class)));
        if (dudeTarget.isPresent()) {
            if (dudeTarget.get().getClass() == DudeNotFull.class) {
                ((DudeNotFull)dudeTarget.get()).transformAngry(world, scheduler, imageStore);
            }
            else {
                ((DudeFull)dudeTarget.get()).transformAngry(world, scheduler, imageStore);
            }
        }

    }


    private void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
        		if (entity instanceof Obstacle) {
					((AnimatingEntity) entity).scheduleActions(scheduler);
				}
				if (entity instanceof ActionEntity) {
				((ActionEntity) entity).scheduleActions(scheduler, world, imageStore);
			}
        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    private static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    private static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    private void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    private void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
