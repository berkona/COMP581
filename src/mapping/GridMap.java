package mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import robot.Vector2;

public class GridMap {
	public static Vector2[] DEFAULT_ANGLES = new Vector2[] {
		// +X 
		new Vector2(1, 0),
		// -X
		new Vector2(-1, 0),
		// +Y
		new Vector2(0, 1),
		// -Y
		new Vector2(0, -1),
	};
	
	private Map<String, List<Vector2>> cells = new HashMap<String, List<Vector2>>();
	private Vector2[] connectingAngles;
	private float cellSize;
	
	public GridMap(float cellSize) {
		this(cellSize, DEFAULT_ANGLES);
	}
	
	public GridMap(float cellSize, Vector2[] connectingAngles) {
		this.cellSize = cellSize;
		this.connectingAngles = connectingAngles;
	}
	
	public void addPoint(Vector2 point) {
		String cell = getCell(point);
		List<Vector2> points = cells.get(cell);
		if (points == null) {
			points = new LinkedList<Vector2>();
			cells.put(cell, points);
		}
		points.add(point);
	}
	
	public List<Vector2> getPoints(Vector2 cell) {
		return cells.get(getCell(cell));
	}
	
	public Vector2[] neighbors(Vector2 point) {
		int n = connectingAngles.length;
		Vector2[] neighbors = new Vector2[n];
		for (int i = 0; i < n; i++) {
			neighbors[i] = Vector2.add(point, Vector2.mult(connectingAngles[i], cellSize));
		}
		return neighbors;
	}
	
	public String getCell(Vector2 point) {
		int xCell = Math.round(point.x / cellSize);
		int yCell = Math.round(point.y / cellSize);
		return xCell + "," + yCell;
	}
	
	public Vector2 getPoint(String cell) {
		String[] coords = cell.split(",");
		return new Vector2(Integer.parseInt(coords[0]) * cellSize, Integer.parseInt(coords[1]) * cellSize);
	}
}
