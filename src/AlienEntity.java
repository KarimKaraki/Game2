import java.util.Random;

/**
 * An entity which represents one of our space invader aliens.
 * 
 * @author Karim Karaki 
 * @author Hekmat Kassir
 */
public class AlienEntity extends Entity {
	/** The speed at which the alient moves horizontally */
	private double moveSpeed = 75+NewJFrame.target_speed;
	/** The game in which the entity exists */
	private Game game;
	private int block_count = 0;
	public boolean direction;
	public int speed_coef;
	public int angle;
	public boolean auto = false;
	
	/**
	 * Create a new alien entity
	 * 
	 * @param game The game in which this entity is being created
	 * @param ref The sprite which should be displayed for this alien
	 * @param x The intial x location of this alien
	 * @param y The intial y location of this alient
	 */
	public AlienEntity(Game game,String ref,int x,int y,boolean dir,int ang,int speed) {
		super(ref,x,y);
		this.direction = dir;
		this.game = game;
		this.angle = ang;
		dx = -moveSpeed;
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
		Random random = new Random();
	    int ran_y = random.nextInt(150) + 50;
		if(speed_coef == 4) {
			speed_coef = 2;
			auto = true;
		}
		if(angle == 0) {
			if(direction == false) { //if direction is false the target goes left
			
				x -= 1*speed_coef;
				y -= 0;
			} else {
				x+= 1*speed_coef;
				y-= 0;
			}
			if(auto) {
				if((x<100 && x>500) && block_count == 0) {
					block_count++;
					System.out.println("wwwww");

					FlareEntity flare = new FlareEntity(game, "flare1.gif", (int)x,(int)y+30,2); 
					game.entities.add(flare);
					block_count--;
				}
			}
//			
			
		} else if(angle == 1) {
			if(direction == false) {
				x -= 1*speed_coef;
				y -= 1*speed_coef;
			} else {
				x += 1*speed_coef;
				y -= 1*speed_coef;
			}
			if(auto) {
				if((y<100 && y>100-2) && block_count == 0) {
					block_count++;
					System.out.println("wwwww");

					FlareEntity flare = new FlareEntity(game, "flare1.gif", (int)x,(int)y+30,2); 
					game.entities.add(flare);
					block_count--;
				}
			}
			
		} else if (angle == 2) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 0;
				y -= 1*speed_coef;
			} else {
				x+= 0;
				y-= 1*speed_coef;
			}
			if(auto) {
				if((y<ran_y && y<ran_y-(2*speed_coef)) && block_count == 0) {
					System.out.println("wwwww");

					FlareEntity flare = new FlareEntity(game, "flare1.gif", (int)x,(int)y+30,2); 
					game.entities.add(flare);
					block_count--;
				}
			}
		
		} else if (angle == 3) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 3;
				y -= 2*Math.cos((x/55)+4) ;
				//System.out.println(y);
			} else {
				x+= 3;
				y-= 2*Math.cos((x/55)+4) ;
			}
//			if((y<100 && y>98) && block_count > 0) {
//				FlareEntity flare = new FlareEntity(game, "gun.gif", (int)x,(int)y,2); 
//				game.entities.add(flare);
//				block_count--;
//			}
		} else if (angle == 4) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 1*speed_coef;
				y -= 3*speed_coef;
				//System.out.println(y);
			} else {
				x += 1*speed_coef;
				y -= 3*speed_coef;
			}
//			if((y<100 /*&& y>95*/) && block_count > 0) {
//				FlareEntity flare = new FlareEntity(game, "gun.gif", (int)x,(int)y,2); 
//				game.entities.add(flare);
//				block_count--;
//			}
		} else if (angle == 5) {
			if(direction == false) { //if direction is false the target goes left
				
				x -= 1*speed_coef;
				y -= (0.3333333333)*speed_coef;
				//System.out.println(y);
			} else {
				x += 1*speed_coef;
				y -= (0.3333333333)*speed_coef;
			}
//			if((y<450 /*&& y>440*/) && block_count > 0) {
//				FlareEntity flare = new FlareEntity(game, "gun.gif", (int)x,(int)y,2); 
//				game.entities.add(flare);
//				block_count--;
//			}
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
//	public boolean collidesWith(Entity other) {
//		// TODO Auto-generated method stub
//		return false;
//	}
	



	
}