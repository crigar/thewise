package unalcol.agents.examples.labyrinth.multeseo.eater.thewise;

public class Node
{
	private Position position;
	private Node parent;
	private byte level;
	private boolean exit;
	 
	
	//constructor cristian
	public Node(Position position, Node parent, int level, boolean exit){
		this.position = position;
		this.level = (byte)level;
		this.parent = parent;
		this.exit = exit;
	}
	public Position getPosition()
	{
		return position;
	}
	
	public boolean getExit() {
		return exit;
	}
	public void setExit(boolean visited) {
		this.exit = visited;
	}
	public Node getParent()
	{
		return parent;
	}
	public byte getlevel() {
		return level;
	}
	public void setlevel(byte level) {
		this.level = level;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Node [position=" + position + ", exit=" + exit  + "]\n";
	}
	
	
}