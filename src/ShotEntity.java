
/**
 * An entity representing a shot fired by the player's ship
 * 
 *  @author Karim Karaki 
 * @author Hekmat Kassir
 */
public class ShotEntity extends Entity {
	/** The vertical speed at which the players shot moves */
	private double moveSpeed = -500-(NewJFrame.shot_speed
			
			
			
			);
	/** The game in which this entity exists */
	private Game game;
	/** True if this shot has been "used", i.e. its hit something */
	private boolean used = false;
	public int angle;
	public boolean direction;
	public int level = 4;
	
	
	/**
	 * Create a new shot from the player
	 * 
	 * @param game The game in which the shot has been created
	 * @param sprite The sprite representing this shot
	 * @param x The initial x location of the shot
	 * @param y The initial y location of the shot
	 */
	public ShotEntity(Game game,String sprite,int x,int y,int level) {
		super(sprite,x,y);
		this.level = level;
		this.game = game;
		
		dy = moveSpeed;
	}
	
	public ShotEntity(Game game,String sprite,int x,int y,int angle,boolean direction) {
		super(sprite,x,y);
		this.angle = angle;
		this.direction = direction;
		this.game = game;
		
		dy = -100;
	}

	/**
	 * Request that this shot moved based on time elapsed
	 * 
	 * @param delta The time that has elapsed since last move
	 */
	public void move(long delta,boolean dir) {
		// proceed with normal move
		if (level <= 3) {
			super.move(delta,false);
		} else {
			if(angle == 0) {//horizontal
				if(direction == false) { //if direction is false the target goes left
				
					x -= 2.15;
					y -= 1;
				} else {
					x+= 2.25;
					y-= 1;
				}
				
			} else if (angle == 2) {//vertical
				if(direction == false) { //if direction is false the target goes left
					
					x -= 0;
					y -= 2.5;
				} else {
					x+= 0;
					y-= 2.5;
				}
			} else if(angle == 1 || angle == 3) {//oblique
				if(direction == false) {
					x -= 2;
					y -= 4;
				} else {
					x += 2;
					y -= 4;
				}
				
			} else if(angle == 4) {
				if(direction == false) {
					x -= 2.5;
					y -= 8.5;
				} else {
					x += 2.5;
					y -= 8;
				}	
			} else  {
				if(direction == false) {
					x -= 2;
					y -= 1.5;
				} else {
					x += 2;
					y -= 1.5;
				}
				
			}
		}
		
		// if we shot off the screen, remove ourselfs
		if (y < -100) {
			game.removeEntity(this);
		}
	}
	
	/**
	 * Notification that this shot has collided with another
	 * entity
	 * 
	 * @parma other The other entity with which we've collided
	 */
	public void collidedWith(Entity other) {
		// prevents double kills, if we've already hit something,
		// don't collide
		if (used) {
			return;
		}
		
		// if we've hit an alien, kill it!
		if (other instanceof AlienEntity)  {
			// remove the affected entities
			game.removeEntity(this);
			game.removeEntity(other);
			// notify the game that the alien has been killed
			//1game.notifyAlienKilled();
			used = true;
			
		}
		
	}

	//@Override
//	public boolean collidesWith(Entity other) {
//		// TODO Auto-generated method stub
//		
//		return false;
//	}
}