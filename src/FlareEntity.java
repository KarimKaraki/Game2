
public class FlareEntity extends Entity {
	/** The speed at which the alient moves horizontally */
	private double moveSpeed = 75;
	/** The game in which the entity exists */
	private Game game;
	//public AlienEntity droper;
	public int speed_coef;

	
	/**
	 * Create a new alien entity
	 * 
	 * @param game The game in which this entity is being created
	 * @param ref The sprite which should be displayed for this alien
	 * @param x The intial x location of this flare
	 * @param y The intial y location of this flare
	 */
	public FlareEntity(Game game,String ref,int x,int y,int speed) {
		super(ref,x,y);
		this.game = game;
		dx = -moveSpeed;
		//this.droper = drop;
		this.speed_coef = speed;
	}
	
	

	/**
	 * Request that this alien moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta,boolean dir) {
		// if we have reached the left hand side of the screen and
		// are moving left then request a logic update 
		
			
		y+=1*speed_coef;
		
		
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
		if (y < 0) {
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
		// prevents double kills, if we've already hit something,
		// don't collide
		
		// if we've hit an alien, kill it!
		
		/*if (other instanceof ShotEntity) {
			// remove the affected entities
			game.removeEntity(this);
			game.removeEntity(other);
			
			// notify the game that the alien has been killed
			//game.notifyAlienKilled();			
		} */
	}



	@Override
	public boolean collidesWith(Entity other) {
		// TODO Auto-generated method stub
		return false;
	}
}
