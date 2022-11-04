
/**
 * An entity which represents one of our space invader aliens.
 * 
 * @author Kevin Glass
 */
public class AlienEntity extends Entity {
	/** The speed at which the alient moves horizontally */
	private double moveSpeed = 75;
	/** The game in which the entity exists */
	private Game game;
	
	public boolean direction;
	
	public int angle;
	
	/**
	 * Create a new alien entity
	 * 
	 * @param game The game in which this entity is being created
	 * @param ref The sprite which should be displayed for this alien
	 * @param x The intial x location of this alien
	 * @param y The intial y location of this alient
	 */
	public AlienEntity(Game game,String ref,int x,int y,boolean dir,int ang) {
		super(ref,x,y);
		this.direction = dir;
		this.game = game;
		this.angle = ang;
		dx = -moveSpeed;
	}

	/**
	 * Request that this alien moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta,boolean dir) {
		// if we have reached the left hand side of the screen and
		// are moving left then request a logic update 
		if(angle == 0) {
			if(direction == false) { //if direction is false the target goes left
			
				x -= 2;
				y -= 0;
			} else {
				x+= 2;
				y-= 0;
			}
		} else if (angle == 1) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 2;
				y -= 2*Math.cos((x/55)+4) ;
				//System.out.println(y);
			} else {
				x+= 2;
				y-= 2*Math.cos((x/55)+4) ;
			}
			
		} else if (angle == 2) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 0;
				y -= 2;
			} else {
				x+= 0;
				y-= 2;
			}
		} else if(angle == 3) {
			if(direction == false) {
				x -= 2;
				y -= (16/3);
			} else {
				x += 2;
				y -= (16/3);
			}
			
		}
		
		
		// proceed with normal move
		//super.move(delta);
	}
	
	/**
	 * Update the game logic related to aliens
	 */
	public void doLogic() {
		// swap over horizontal movement and move down the
		// screen a bit
		dx = -dx;
		y += 10;
		
		// if we've reached the bottom of the screen then the player
		// dies
		if (y > 570) {
			game.notifyDeath();
		}
	}
	
	/**
	 * Notification that this alien has collided with another entity
	 * 
	 * @param other The other entity
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled elsewhere
	}
}