import java.util.Objects;

public class Node {

	private Point pos;
	private int g;
	private double h;
	private double f;
	private Node prior;


	public Node(Point pos) {
		this.pos = pos;
		this.g = 0;
		this.prior = null;
	}


	@Override //only checking location for equals
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return Objects.equals(pos, node.pos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos);
	}


	public Point getPos() {
		return pos;
	}

	public int getG() {
		return g;
	}

	public void setG(int g) {
		this.g = g;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public Node getPrior() {
		return prior;
	}

	public void setPrior(Node prior) {
		this.prior = prior;
	}
}
