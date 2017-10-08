import java.util.HashMap;
import java.util.Map;

public class GridMap {
	
	private class GridNode {
		
		private GridNode[] neighbors;
		private boolean accessable = true;
		
		public GridNode(GridNode[] neighbors) {
			this.neighbors = neighbors;
		}
		
		public boolean isAccessable() {
			return accessable;
		}
		
		public void setAccessable(boolean accessable) {
			this.accessable = accessable;
		}
	}
	
	private Map<String, GridNode> gridNodes = new HashMap<String, GridNode>();
	
	private int degree;
	
	public GridMap(int degree) {
		this.degree = degree;
	}
	
	public GridNode GetGridNode(int x, int y) {
		return gridNodes.get(getKey(x, y));
	}
	
	public void AddNode(int x, int y) {
		String key = getKey(x, y);
		if (gridNodes.containsKey(key))
			throw new IllegalArgumentException("Node already added");
		GridNode[] nodes = new GridNode[degree];
		gridNodes.put(key, new GridNode(nodes));
	}
	
	String getKey(int x, int y) {
		return String.valueOf(x) + "," + String.valueOf(y);
	}
}
