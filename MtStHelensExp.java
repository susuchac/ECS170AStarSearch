import java.awt.Point;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.HashMap;

public class MtStHelensExp implements AIModule 
{

	public class Node
	{
		Point curr;
		Point prev;
		double fn;
		double gn;
		double hn;

		public Node(Point p1, Point p2, double g, double h)
		{
			curr = p1;
			prev = p2;
			gn = g;
			hn = h;
			fn = gn + hn;
		}
	}

	public void tracePath(HashMap<Point, Point> closed, ArrayList<Point> path, Point curr, Point start)
	{
		if(curr.x == start.x && curr.y == start.y)
		{
			path.add(curr);
			return;
		}
		tracePath(closed, path, closed.get(curr), start);
		path.add(curr);
	}

	public List<Point> createPath(final TerrainMap map)
	{
		Point start = map.getStartPoint();
		Point goal = map.getEndPoint();

		ArrayList<Point> path = new ArrayList<Point>();
		HashMap<Point, Point> closed = new HashMap<Point, Point>();
		// PQ orders nodes by min cost first, max cost last
		PriorityQueue<Node> open = new PriorityQueue<Node>(10, new Comparator<Node>()
		{

			public int compare(Node n1, Node n2)
			{
				if(n1.fn < n2.fn)
					return -1;
				if(n1.fn > n2.fn)
					return 1;
				return 0;
			}
		});
 
		open.add(new Node(start, new Point(-1,-1), 0.0, getHeuristic(map, start, goal)));

		while(!open.isEmpty())
		{
			Node n = open.poll();
			Point curr = n.curr;

			if(curr.x == goal.x && curr.y == goal.y)
			{
				closed.put(curr, n.prev);
				break;
			}

			if(!closed.containsKey(curr)) {
				closed.put(curr, n.prev);
				Point[] neighbors = map.getNeighbors(curr);

				for(int i=0; i < neighbors.length; i++)
				{
					open.add(new Node(neighbors[i], curr, n.gn + map.getCost(curr, neighbors[i]), getHeuristic(map, neighbors[i], goal)));
				}
			}
		}

		tracePath(closed, path, goal, start);
		return path;

	}

	private double getHeuristic(final TerrainMap map, final Point p1, final Point p2)
	{
		double diagonal = Math.max(Math.abs(p2.y-p1.y), Math.abs(p2.x-p1.x));
		double heightDiff = map.getTile(p2) - map.getTile(p1);
		double heuristic = 0;

		if(heightDiff >= diagonal) 
		{
			heuristic = heightDiff*2;
		} else
		{
			heuristic = Math.pow(2, heightDiff/diagonal)*diagonal;
		}

		return heuristic;
	}

}