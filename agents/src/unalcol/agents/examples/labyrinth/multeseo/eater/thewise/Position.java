package unalcol.agents.examples.labyrinth.multeseo.eater.thewise;

public class Position
{
	private final byte x;
	private final byte y;

	public Position( int x, int y )
	{
		this.x = ( byte ) x;
		this.y = ( byte ) y;
	}
	
	
	
	public byte getX() {
		return x;
	}



	public byte getY() {
		return y;
	}



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		
		Position other = ( Position ) obj;
		if( x != other.x )
			return false;
		if( y != other.y )
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}
	
}