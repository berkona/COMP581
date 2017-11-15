package mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import robot.Vector2;

public class AStarPathfinder implements IPathfinder {

	private GridMap map;

	public AStarPathfinder(GridMap map) {
		this.map = map;
	}
	
	@Override
	public Vector2[] findPath(Vector2 start, Vector2 goal) {
		String startIdx = map.getCell(start);
		String goalIdx = map.getCell(goal);
		
		Set<String> openSet = new HashSet<String>();
		Set<String> closedSet = new HashSet<String>();

		Map<String, String> cameFrom = new HashMap<String, String>();
		Map<String, Float> gScore = new HashMap<String, Float>();
		gScore.put(startIdx, 0f);
		
		Map<String, Float> fScore = new HashMap<String, Float>();
		fScore.put(startIdx, cost(start, goal));
		
		while (!openSet.isEmpty()) {
			//  current := the node in openSet having the lowest fScore[] value
			String curr = null;
			float minF = Float.POSITIVE_INFINITY;
			for (String s : openSet) {
				float f = fScore.get(s);
				if (f < minF) {
					curr = s;
					minF = f;
				}
			}
			
			if (curr == goalIdx) {
				return makePath(cameFrom, curr);
			}
			
			openSet.remove(curr);
			closedSet.add(curr);
			
			float currDist = gScore.getOrDefault(curr, Float.POSITIVE_INFINITY);
			
			Vector2 currPt = map.getPoint(curr);
			
			for (Vector2 neighbor : map.neighbors(currPt)) {
				String nIdx = map.getCell(neighbor);
				if (closedSet.contains(nIdx)) continue;
				
				if (!openSet.contains(nIdx))
					openSet.add(nIdx);
				
				float newGScore = currDist + cost(currPt, neighbor);
				if (newGScore >= gScore.getOrDefault(nIdx, Float.POSITIVE_INFINITY))
						continue;
				
				cameFrom.put(nIdx, curr);
				gScore.put(nIdx, newGScore);
				fScore.put(nIdx, newGScore + cost(neighbor, goal));
			}
		}
		// no path
		return null;
	}
	
	private Vector2[] makePath(Map<String, String> cameFrom, String curr) {
		List<Vector2> path = new LinkedList<Vector2>();
		path.add(map.getPoint(curr));
		while (cameFrom.containsKey(curr)) {
			curr = cameFrom.get(curr);
			path.add(0, map.getPoint(curr));
		}
		return path.toArray(new Vector2[path.size()]);
	}

	private float cost(Vector2 from, Vector2 to) {
		return Vector2.sub(from, to).magnitude();
	}

}
