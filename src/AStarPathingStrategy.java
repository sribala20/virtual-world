import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
		implements PathingStrategy
{


	public List<Point> computePath(Point start, Point end,
								   Predicate<Point> canPassThrough,
								   BiPredicate<Point, Point> withinReach,
								   Function<Point, Stream<Point>> potentialNeighbors)
	{
		List<Point> path = new LinkedList<>();

		PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getF).thenComparing(Node::getH)); //break ties w H
		HashMap<Point, Node> openListMap = new HashMap<>();
		HashMap<Point, Node> closedList = new HashMap<>();

		//1. choose start
		Node S = new Node(start);
		double start_val = Math.abs(end.x - start.x) + Math.abs(end.y - start.y);
		S.setH(start_val);
		S.setF(start_val);

		//2. add start to open list
		openList.add(S);
		openListMap.put(start, S);

		//repeat until WITHIN REACH or no longer searching
		while (openList.size() != 0) {

			//2 & 5. mark start as curr/ curr now node with smallest f-value
			Node curr = openList.remove();

			if (withinReach.test(curr.getPos(), end)) {
				while (curr.getPrior() != null) { // doesn't add start node
					path.add(curr.getPos());
					curr = curr.getPrior();
				}
				Collections.reverse(path);
				return path;
			}

			//get valid neighbors, return lst of points
			List<Point> neighbors = potentialNeighbors.apply(curr.getPos())
					.filter(canPassThrough)
					.filter(pt -> !pt.equals(start) && !pt.equals(end))
					.filter(pt -> !closedList.containsKey(pt))
					.collect(Collectors.toList());

			//3. for each valid neighborNode
			for (Point neighbor: neighbors) {
				Node neighborNode = new Node(neighbor);
				//a. determine g value
				int new_g = curr.getG() + 1;
				neighborNode.setG(new_g);
				//b. if node on open list:
				if (openListMap.containsKey(neighbor)) {
					Node prev_neighbor = openListMap.get(neighbor);
					if (neighborNode.getG() < prev_neighbor.getG()) {
						prev_neighbor.setG(new_g);
					} else {
						continue; //b[i]. skip to a) for next node
					}
				}
				//c. estimate h (manhattan distance)
				double new_h = Math.abs(end.x - neighbor.x) + Math.abs(end.y - neighbor.y);
				neighborNode.setH(new_h);
				//d. add g and h to get f value
				double new_f = new_g + new_h;
				neighborNode.setF(new_f);
				//e. save prior node of this neighbor
				neighborNode.setPrior(curr);

				//f. add node to open list, replacing if there was one
				if (openListMap.containsKey(neighbor)) {
					openList.remove(openListMap.get(neighbor));
					openListMap.replace(neighbor, openListMap.get(neighbor), neighborNode);
				} else {
					openListMap.put(neighbor, neighborNode);
				}
				openList.add(neighborNode);

			}

			//4. move curr to closed
			//System.out.println(openList.stream().map(Node::getPos).toList()); //testing open lst
			closedList.put(curr.getPos(), curr);
		}
		return path; //empty list if found nothing
	}
}
