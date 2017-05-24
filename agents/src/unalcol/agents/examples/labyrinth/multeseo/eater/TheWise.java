package unalcol.agents.examples.labyrinth.multeseo.eater;

import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TheWise implements AgentProgram
{
	private SimpleLanguage language;
	private Vector<String> commands;
	private Integer orientation;
	private Integer previousEnergy;
	private ArrayList<Node> queue;
	private Node root;
	private Node current;
	private boolean senseChildrens;
	private HashMap<Position, Byte> visiteds;
	
	private HashMap<Code, Integer > food;
	private boolean isEating;
	private boolean[] currentCode;

	public TheWise( SimpleLanguage language )
	{
		this.language = language;
		setInitialValues();
	}

	private void setInitialValues()
	{
		commands = new Vector<>();
		orientation = 0;
		queue = new ArrayList<>();
		root = new Node( new Position( 0, 0 ), -1, 0, null );
		current = root;
		senseChildrens = true;
		visiteds = new HashMap<>();
		visiteds.put( root.getPosition(), ( byte ) 1 );
		
		food = new HashMap<>();
		isEating = false;
	}

	private boolean[] getPerceptionsAccordingOrientation( Percept p )
	{
		//Perceptions according to orientation front
		//"front", "right", "back", "left", "treasure", "fail", "afront", "aright", "aback", "aleft"
		boolean perceptions[] = new boolean[10];
		for( int i = 0; i < 10; i++ )
			if( i < 4 )
				perceptions[( i + orientation ) % 4] = ( ( boolean ) p.getAttribute( language.getPercept( i ) ) );
			else if( i > 5 && i < 10 )
				perceptions[( ( i + orientation - 6 ) % 4 ) + 6] = ( ( boolean ) p.getAttribute( language.getPercept( i ) ) );
			else
				perceptions[i] = ( ( boolean ) p.getAttribute( language.getPercept( i ) ) );

		return perceptions;
	}

	private int getActionAccordingSide( int side )
	{
		if( orientation % 2 == 0 )
			return ( orientation + side ) % 4;

		return ( orientation + side + 2 ) % 4;
	}

	private int getActionAccordingCoordinates( Node current )
	{
		byte[] parentCoordinates = current.getParent().getPosition().getCoordinates();
		byte[] currentCoordinates = current.getPosition().getCoordinates();

		if( currentCoordinates[0] == parentCoordinates[0] )
			if( currentCoordinates[1] > parentCoordinates[1] )
				return ( 6 - orientation ) % 4;
			else
				return ( 4 - orientation ) % 4;
		else if( currentCoordinates[1] == parentCoordinates[1] )
			if( currentCoordinates[0] < parentCoordinates[0] )
				return ( 5 - orientation ) % 4;

		return 3 - orientation;
	}

	private int moveToChildren( Node parent, Position newPosition, int side, boolean add )
	{
		int action = getActionAccordingSide( side );
		orientation = ( orientation + action ) % 4;
		if( add )
		{
			visiteds.put( newPosition, ( byte ) 1 );
			queue.add( new Node( newPosition, side, parent.getDepth() + 1 , parent ) );
		}

		return action;
	}

	private int moveToParent( Node current )
	{
		int action = getActionAccordingCoordinates( current );
		orientation = ( orientation + action ) % 4;

		return action;
	}

	private ArrayList<Integer> actions( boolean[] perceptions, int energy )
	{
		//Perceptions according to orientation front
		//"front", "right", "back", "left", "treasure", "fail", "afront", "aright", "aback", "aleft"
		ArrayList<Integer> actions = new ArrayList<>();
		if( perceptions[4] )
		{
			actions.add( -1 );
			return actions;
		}

		//If the agent must sense the childrens
		if( senseChildrens )
		{
			int action;
			Node parent;
			//0 - If the front is no wall
			//1 - If the rigth is no wall
			//2 - If the back is no wall
			//3 - If the left is no wall
			for( int i = 0; i < 4; i++ )
			{
				Position position = null;
				switch( i )
				{
					case 0:
						position = new Position( current.getPosition().getCoordinates()[0], current.getPosition().getCoordinates()[1] + 1 );
						break;
					case 1:
						position = new Position( current.getPosition().getCoordinates()[0] + 1, current.getPosition().getCoordinates()[1] );
						break;
					case 2:
						position = new Position( current.getPosition().getCoordinates()[0], current.getPosition().getCoordinates()[1] - 1 );
						break;
					case 3:
						position = new Position( current.getPosition().getCoordinates()[0] - 1, current.getPosition().getCoordinates()[1] );
						break;
				}
				if( !perceptions[i] && !visiteds.containsKey( position ) )
				{
					action = moveToChildren( current, position, i, true );
					actions.add( action );
					current = queue.get( queue.size() - 1 );
					action = moveToParent( current );
					actions.add( action );
					parent = current.getParent();
					current = parent;
				}
			}

			//Move to root node
			for( int i = current.getDepth() - 1; i > -1; i-- )
			{
				parent = current.getParent();
				action = moveToParent( current );
				actions.add( action );
				current = parent;
			}
		}
		else
		{
			//Move to target node (first in queue)
			Node target = queue.remove( 0 );
			ArrayList<Node> parents = new ArrayList<>();
			Node aux = target;
			int action;
			for( int i = 0; i < target.getDepth(); i++ )
			{
				aux = aux.getParent();
				parents.add( aux );
			}
			for( int i = target.getDepth() - 1; i > 0; i-- )
			{
				action = moveToChildren( parents.get( i ), null, parents.get( i - 1 ).getSide(), false );
				actions.add( action );
				current = parents.get( i - 1 );
			}
			action = moveToChildren( current, null, target.getSide(), false );
			actions.add( action );
			current = target;
		}

		senseChildrens = !senseChildrens;

		return actions;
	}

	@Override
	public Action compute( Percept p )
	{
//		
//		if (isEating) {
//			int currentEnergy = ( ( int ) p.getAttribute( "energy_level" ) );
//			food.put(new Code(currentCode), Math.abs(currentEnergy - previousEnergy ));
//			System.out.println(food);
//			isEating = false;
//		}
		
		//Perceptions according to orientation front
		//"front", "right", "back", "left", "treasure", "fail", "afront", "aright", "aback", "aleft"
		if( commands.size() == 0 )
		{
			//Get perceptions according to orientation
			boolean perceptions[] = getPerceptionsAccordingOrientation( p );
			
			
			int currentEnergy = ( ( int ) p.getAttribute( "energy_level" ) );
			
			//Get ArrayList of actions of course to BFS
			ArrayList<Integer> actions = actions( perceptions, currentEnergy );
			for( int i = 0; i < actions.size(); i++ )
			{
				int action = actions.get( i );
				if( action > -1 && action < 4 )
				{
					for( int j = 0; j < action; j++ )
						commands.add( language.getAction( 3 ) ); //rotate
					commands.add( language.getAction( 2 ) ); // advance

				}
				else
					commands.add( language.getAction( 0 ) ); // die
			}
		}

		String command = commands.get( 0 );
		
		
		if (command.equals("advance")) {
			boolean eat = ( ( Boolean ) p.getAttribute( language.getPercept( 10 ) ) ).
					booleanValue();
			System.out.println("eat: " + eat);
			if (eat) {
				boolean eat1 = ( ( Boolean ) p.getAttribute( language.getPercept( 11 ) ) ).
						booleanValue();
				boolean eat2 = ( ( Boolean ) p.getAttribute( language.getPercept( 12 ) ) ).
						booleanValue();
				boolean eat3 = ( ( Boolean ) p.getAttribute( language.getPercept( 13 ) ) ).
						booleanValue();
				boolean eat4 = ( ( Boolean ) p.getAttribute( language.getPercept( 14 ) ) ).
						booleanValue();
				
				int currentEnergy = ( ( int ) p.getAttribute( "energy_level" ) );
				previousEnergy = currentEnergy;
				currentCode = new boolean[]{eat1,eat2,eat3,eat4};
				
				if (food.containsKey(currentCode) && food.get(currentCode) >= 0 || !food.containsKey(currentCode)) {
					System.out.println("entrorrr");
					commands.add(0, language.getAction( 4 ) );
					isEating = true;
				}
				
			}
		}
		
		commands.remove( 0 );

		return new Action( command );
	}
	
	@Override
	public void init()
	{

	}
}

class Node
{
	private Position position;
	private byte side;
	private byte depth;
	private Node parent;
	

	public Node( Position position, int side, int depth, Node parent )
	{
		this.position = position;
		this.side = ( byte ) side;
		this.depth = ( byte ) depth;
		this.parent = parent;
	}

	public Position getPosition()
	{
		return this.position;
	}

	public byte getSide()
	{
		return side;
	}

	public byte getDepth()
	{
		return depth;
	}

	public Node getParent()
	{
		return parent;
	}
}

class Position
{
	private byte x;
	private byte y;

	public Position( int x, int y )
	{
		this.x = ( byte ) x;
		this.y = ( byte ) y;
	}

	public byte[] getCoordinates()
	{
		return new byte[]{ x, y };
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
	public boolean equals( Object object )
	{
		if( this == object )
			return true;
		if( object == null )
			return false;
		if( getClass() != object.getClass() )
			return false;

		Position other = ( Position ) object;

		return x == other.x && y == other.y;
	}
}

class Code{
	private boolean[] code;
	
	public Code(boolean[] code ){
		this.code = code;
	}

	public boolean[] getCode() {
		return code;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(code);
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
		Code other = (Code) obj;
		if (!Arrays.equals(code, other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Code [code=" + Arrays.toString(code) + "]";
	}
	
}