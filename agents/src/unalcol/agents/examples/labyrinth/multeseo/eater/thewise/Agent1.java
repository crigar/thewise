package unalcol.agents.examples.labyrinth.multeseo.eater.isi2017.thewise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import unalcol.agents.Action;
import unalcol.agents.AgentProgram;
import unalcol.agents.Percept;
import unalcol.agents.simulate.util.SimpleLanguage;
import unalcol.types.collection.vector.Vector;

public class Agent1 implements AgentProgram
{
	private SimpleLanguage language;
	private Vector<String> cmds = new Vector<String>();
	private int orient = 0;
	private HashMap<Position, HashMap<Node, Byte>> tree = new HashMap<>();
	private HashMap<Position, Integer> positionsAdded = new HashMap<>();
	private HashMap<Position, Integer> positionsNoExit = new HashMap<>();
	private Stack<Node> stack = new Stack<Node>();
	
	private int maxLevel = 32;
	private Node current,goal,newCurrent;
	private Queue<Node> moves = new LinkedList();
	private int d;
	private Node root;
	private boolean changeLevel = false;
	private int currentEnergy = 0;
	private int partialEnergy = 0;
	private boolean eat = false;
	private HashMap<Position, BarCode> goodFood = new HashMap<>();
	private HashMap<Position, BarCode> badFood = new HashMap<>();
	private Position lastGoodFood;
	private boolean slowEnergy = false;
	
	private Stack<Node> pathFoodMostNearly = new Stack<Node>();
	private Stack<Node> comeBack = new Stack<Node>();
	private boolean goodEat = false;
	private boolean flagComeBack = false;
	
	private boolean eat1= false;
	private boolean eat2= false;
	private boolean eat3= false;
	private boolean eat4= false;
	private HashMap<Position, BarCode> codesEat = new HashMap<>();
	private int minLevelEnergy = 15;
	private boolean ate = false;
	private boolean searchingFood = false;
	private Node home = null;
	private boolean findingHome = false;
	private HashSet<BarCode> badFoodCode = new HashSet<>();
	private HashSet<BarCode> goodFoodCode = new HashSet<>();
	private boolean eatingGood = false;
	private int maxEnergy = 0;
	private boolean maxEnergyFound = false;
	private int numberOfFeed = 6;
	
	public Agent1( SimpleLanguage language )
	{
		this.language = language;
		Node first = new Node( new Position(0, 0),null, 0,true );
		current = first;
		root = first;
		
		maxLevel = 25;
		
	}
	public boolean chechPositionsAdded(Position position){
		//si la posision no ha sido agregada y esa posicion tiene salida retorna falso
		//porque no se encuentra en ninguna de las dos estructuras
		if (positionsAdded.get(position) == null && positionsNoExit.get(position) == null) return false;
		return true;
	}
	public boolean[] changeWall(boolean PF, boolean PD, boolean PA, boolean PI){		
		if (orient == 1) return new boolean[]{PI,PF,PD,PA};
		if (orient == 2) return new boolean[]{PA,PI,PF,PD};
		if (orient == 3) return new boolean[]{PD,PA,PI,PF};
		return new boolean[]{PF,PD,PA,PI};
	}
		
	public boolean getChildren(boolean PF, boolean PD, boolean PA, boolean PI){
		if (tree.get(current.getPosition()) != null)return true;
		Node parent = current;
		Node son;
		HashMap<Node, Byte> children = new HashMap<>();
		boolean[] walls = changeWall(PF, PD, PA, PI);
		
		
		if (!walls[3]) {
			Position position = new Position(parent.getPosition().getX() - 1 ,parent.getPosition().getY());
			if (!chechPositionsAdded(position)) {
				son = new Node(position, parent,parent.getlevel()+1,true);
				children.put(son, (byte)1);
				if (parent.getlevel() < maxLevel) {
					positionsAdded.put(son.getPosition(), 1);
					stack.push(son);
				}
				
			}
			
		}
		if (!walls[2]) {
			Position position = new Position(parent.getPosition().getX(),parent.getPosition().getY() - 1 );
			if (!chechPositionsAdded(position)) {
				son = new Node(position,parent, parent.getlevel()+1,true);
				children.put(son, (byte)1);
				if (parent.getlevel() < maxLevel) {
					positionsAdded.put(son.getPosition(), 1);
					stack.push(son);
				}
			}
		}
		if (!walls[1]) {
			Position position = new Position(parent.getPosition().getX() + 1,parent.getPosition().getY() );
			if (!chechPositionsAdded(position)) {
				son = new Node(position,parent, parent.getlevel()+1,true);
				children.put(son, (byte)1);
				if (parent.getlevel() < maxLevel) {
					positionsAdded.put(son.getPosition(), 1);
					stack.push(son);
				}
			}
		}
		if (!walls[0]) {
			Position position = new Position(parent.getPosition().getX(),parent.getPosition().getY() + 1 );
			if (!chechPositionsAdded(position)) {
				son = new Node(position,parent, parent.getlevel()+1,true);
				children.put(son, (byte)1);
				if (parent.getlevel() < maxLevel) {
					positionsAdded.put(son.getPosition(), 1);
					stack.push(son);
				}
			}
		}
		if (children.isEmpty()) {
			parent.setExit(false);
			positionsNoExit.put(parent.getPosition(), 1);
			return false;
		}
		tree.put(parent.getPosition(),children);
		positionsAdded.put(parent.getPosition(), 1);
		return true;
		
	}
	public void testExit(Node node){
		boolean haveExit = false;
		for ( Node child : tree.get(node.getPosition()).keySet()) {
			if (child.getExit()) {
				haveExit = true;
				break;
			}
		}
		if (!haveExit){
			node.setExit(false);
			positionsNoExit.put(node.getPosition(), 1);
		}
	}
	public int move(Position initial, Position goal){
		int move = -1;
		int action = 0;
		if ((initial.getX() == goal.getX() && initial.getY() + 1 == goal.getY() ) )move = 0;
		if ((initial.getX() + 1  == goal.getX() && initial.getY() == goal.getY()) )move = 1;
		if ((initial.getX() == goal.getX() && initial.getY() - 1 == goal.getY()) ) move = 2;
		if ((initial.getX() - 1 == goal.getX() && initial.getY() == goal.getY()) ) move = 3;
		if (move == -1) return -1;
		if (orient == 0){
			orient = move;
			return move;
		}
		if (orient == 1) {
			switch (move) {
			case 0:
				action = 1 + 2; // 3
				break;
			case 1:
				action = 1 - 1; // 0
				break;
			case 2:
				action = 1; //1
				break;
			case 3:
				action = 1 + 1; // 2
				break;
			default:
				break;
			}
		}
		if (orient == 2) {
			switch (move) {
			case 0:
				action = 2;//2
				break;
			case 1:
				action = 2 + 1;//3
				break;
			case 2:
				action = 2 - 2;//0
				break;
			case 3:
				action = 2 - 1;//1
				break;
			default:
				break;
			}
		}
		if (orient == 3) {
			switch (move) {
			case 0:
				action = 3 -2 ;//1
				break;
			case 1:
				action = 3 - 1; //2
				break;
			case 2:
				action = 3;//3
				break;
			case 3:
				action = 3 - 3;//0
				break;
			default:
				break;
			}
		}
		orient = move;
		return action;
	}
	public int givePath(Node initial, Node goal,  boolean PF, boolean PD, boolean PA, boolean PI){
		boolean[] walls = {PF,PD,PA,PI};
		//Si es un movimiento de padre a hijo
		if (initial.equals(goal.getParent())){
			moves.add(goal);
			return 0;
		}
		//si es un movimiento entre hermanos
		if (initial.getParent().equals(goal.getParent())) {
			moves.add(initial.getParent());
			moves.add(goal);
			return 1;
		}
		//si es un movimiento de sobrino a tio
		if (initial.getParent().getParent().equals(goal.getParent())) {
			int partialOrient = orient;
			int proximityTest = move(initial.getPosition(), goal.getPosition());
			if (proximityTest != -1 && !walls[proximityTest] ) {
				orient = partialOrient;
				moves.add(goal);
			}else{
				orient = partialOrient;
				Node parent = initial.getParent();
				Node grandParent = parent.getParent();
				moves.add(parent);
				moves.add(grandParent);
				moves.add(goal);
			}
			testExit(initial.getParent());
			return 2;
		}
		//en caso de que no encuentre ninguna relacion se va al padre
		//verifica si hay salida por los hijos del padre de initial
		Node parent = initial.getParent();
		testExit(parent);
		moves.add(parent);
		stack.push(goal);
		return -1;
	}
	public void changeMaxLevel(){
		if (current.equals(root)){
			maxLevel = maxLevel + 35;
			current = root;
			positionsAdded.clear();
			tree.clear();
			changeLevel = false;
		}
	}
	
	public int findGoodFood(){
		searchingFood = true;
		
		
		Node next = pathFoodMostNearly.pop();
		comeBack.push(next);
		int d = move(current.getPosition(), next.getPosition());
		
		current = next;
				
		return d;
	}
	
	public int findHome(){
		
		
		
		if (current.equals(home))
			return -1;
		
		Node next = comeBack.pop();
		
		int d = move(current.getPosition(), next.getPosition());
		
		current = next;
		
		
		return d;
		
		
	}
	
	public int accion( boolean PF, boolean PD, boolean PA, boolean PI, boolean MT, boolean FAIL){
		if (MT) return -1;
		int move = -2;
		
		//en caso de que la energia sea muy baja
		if ((slowEnergy && !goodFoodCode.isEmpty() && pathFoodMostNearly.size() >= 2) || searchingFood) {	
			if (!searchingFood){
				home = pathFoodMostNearly.pop();
				comeBack.push(home);
				
			}
			int val = findGoodFood();
			if ( val == -1 ){
				searchingFood = false;
				findingHome = true;
				comeBack.pop();
				comeBack.pop();
			}
			else return val;
		}
		
		if (findingHome)	
		{
			int d = findHome();
			
			if (d == -1)
				findingHome = false;
			else
				return d;
		}
		
		
		Node next;
		//Si hay que cambiar el nivel maximo del arbol de busqueda
		if (changeLevel) changeMaxLevel();
		//mientras no haya llegado a su objetivo se sigue moviendo
		if (!moves.isEmpty()){
			next = moves.remove();
			move = move(current.getPosition(), next.getPosition());
			current = next;
			return move;
		}else{
			//Si tiene hijos los agrega al stack
			if (current.getlevel() <= maxLevel) {
				getChildren(PF, PD, PA, PI);
			}
			//Pregunta el camino del agente desde la celda actual hacia la celda objetivo
			if (!stack.isEmpty()) {
				goal = stack.pop();
				givePath(current, goal, PF, PD, PA, PI);
				next = moves.remove();
				move = move(current.getPosition(), next.getPosition());
				current = next;
				return move;
			}
		}
		
		move = move(current.getPosition(), current.getParent().getPosition());
		current = current.getParent();
		changeLevel = true;
		return move;		
	}

	
	@Override
	public Action compute( Percept p )
	{	
			
			boolean PF = ( ( Boolean ) p.getAttribute( language.getPercept( 0 ) ) ).
				booleanValue();
		    boolean PD = ( ( Boolean ) p.getAttribute( language.getPercept( 1 ) ) ).
				booleanValue();
		    boolean PA = ( ( Boolean ) p.getAttribute( language.getPercept( 2 ) ) ).
				booleanValue();
		    boolean PI = ( ( Boolean ) p.getAttribute( language.getPercept( 3 ) ) ).
				booleanValue();
		    boolean MT = ( ( Boolean ) p.getAttribute( language.getPercept( 4 ) ) ).
				booleanValue();
		    boolean FAIL = ( ( Boolean ) p.getAttribute( language.getPercept( 5 ) ) ).
				booleanValue();
		    boolean food = ( ( Boolean ) p.getAttribute( language.getPercept( 10 ) ) ).
					booleanValue();
		    
		boolean AF = false, AD = false, AA = false, AI = false;
		boolean agente = false;
		AF = ( ( Boolean ) p.getAttribute( language.getPercept( 6 ) ) ).
				booleanValue();;
	    AD = ( ( Boolean ) p.getAttribute( language.getPercept( 7 ) ) ).
				booleanValue();
	    AA = ( ( Boolean ) p.getAttribute( language.getPercept( 8 ) ) ).
				booleanValue();
	    AI = ( ( Boolean ) p.getAttribute( language.getPercept( 9 ) ) ).
				booleanValue();
		    
	    currentEnergy = ( int ) p.getAttribute( "energy_level" );	    
	    /*
	     
		if (eatingGood)
		{
			if (partialEnergy >= currentEnergy){
				maxEnergy = partialEnergy;
				maxEnergyFound = true;
			}
			
		}
		
		
		if (maxEnergyFound)
		{
			if (Math.abs(currentEnergy - maxEnergy) <= 10)
			{
				
				int n = 0;
			
				for (int j = 0; j < cmds.size(); ++j)
				{
					if ( cmds.get(j).equals( language.getAction( 4 ) )) 
					{
						n++;
					}
				}
				
				numberOfFeed -= n;
				
			}
				
		}
		    */
		if( cmds.size() == 0 )
		{
			
			
			if (!searchingFood){
				//!pathFoodMostNearly.contains(current) && 
				
				if (pathFoodMostNearly.contains(current))
				{
					while(!pathFoodMostNearly.peek().equals(current))
						pathFoodMostNearly.pop();
				}
				else 
					pathFoodMostNearly.push(current);
				
			}
				
			;
			if (ate) {
				
				
		    	if (currentEnergy - partialEnergy >= 0) {
		    		pathFoodMostNearly.clear();
		    		pathFoodMostNearly.push(newCurrent);
		    		
		    		goodFoodCode.add(new BarCode(eat1, eat2, eat3, eat4));
					goodFood.put(newCurrent.getPosition(), new BarCode(eat1, eat2, eat3, eat4));
				}else{
					badFoodCode.add(new BarCode(eat1, eat2, eat3, eat4));
					badFood.put(newCurrent.getPosition(),  new BarCode(eat1, eat2, eat3, eat4));
				}
			}
			
			
			
			if (food)
			{
				eat1 = ( ( Boolean ) p.getAttribute( language.getPercept( 11 ) ) ).
						booleanValue();
				eat2 = ( ( Boolean ) p.getAttribute( language.getPercept( 12 ) ) ).
						booleanValue();
				eat3 = ( ( Boolean ) p.getAttribute( language.getPercept( 13 ) ) ).
						booleanValue();
				eat4 = ( ( Boolean ) p.getAttribute( language.getPercept( 14 ) ) ).
						booleanValue();
			}
			
			
			
			if (food && !ate && !badFoodCode.contains(new BarCode(eat1, eat2, eat3, eat4))) {
				
				
				partialEnergy = ( int ) p.getAttribute( "energy_level" );
				
				cmds.add( language.getAction( 4 ) );
				
				if (goodFoodCode.contains(new BarCode(eat1, eat2, eat3, eat4)))
				{
					eatingGood = true;
					///if (!maxEnergyFound)
					
//					for (int i = 0; i < numberOfFeed; i++)
//						cmds.add( language.getAction( 4 ) );
					cmds.add( language.getAction( 4 ) );
					cmds.add( language.getAction( 4 ) );
					cmds.add( language.getAction( 4 ) );
					
					
				}
				ate = true;
				newCurrent = current;
			}else{
				ate = false;
				
				
				
				
				slowEnergy = currentEnergy <= minLevelEnergy;
				
				d = accion( PF, PD, PA, PI, MT, FAIL );
			    if( 0 <= d && d < 4 )
			    {
			    	for( int i = 1; i <= d; i++ ){
			    		cmds.add( language.getAction( 3 ) ); //rotate
			    	}
			    	
			    	cmds.add( language.getAction( 2 ) ); // advance
			    }
			    else
			    	cmds.add( language.getAction( 0 ) ); // die
			    
			    if (d == 0 && AF) agente = true;
		    	if (d == 1 && AD) agente = true;
		    	if (d == 2 && AA) agente = true;
		    	if (d == 3 && AI) agente = true;
			}
		}
		
    	String x = cmds.get( 0 );
		
    	
    	
    	if (x.equals("advance") && agente) {
			for (int i = 0; i < 4; i++) {
				cmds.add(0, language.getAction( 3 ) ); //rotate
			}
			x = cmds.get(0);
		}
    	
    	
    	cmds.remove( 0 );
		return new Action( x );
	}

	@Override
	public void init()
	{
		cmds.clear();
	}
}



class BarCode
{
	boolean eat1;
	boolean eat2;
	boolean eat3;
	boolean eat4;
	
	public BarCode(boolean eat1, boolean eat2, boolean eat3, boolean eat4)
	{
		this.eat1 = eat1;
		this.eat2 = eat2;
		this.eat3 = eat3;
		this.eat4 = eat4;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (eat1 ? 1231 : 1237);
		result = prime * result + (eat2 ? 1231 : 1237);
		result = prime * result + (eat3 ? 1231 : 1237);
		result = prime * result + (eat4 ? 1231 : 1237);
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
		BarCode other = (BarCode) obj;
		if (eat1 != other.eat1)
			return false;
		if (eat2 != other.eat2)
			return false;
		if (eat3 != other.eat3)
			return false;
		if (eat4 != other.eat4)
			return false;
		return true;
	}
	
	
	
	
	
}


