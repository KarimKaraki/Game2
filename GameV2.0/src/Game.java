
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.lang.System.Logger.Level;
import java.nio.channels.IllegalBlockingModeException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet.ColorAttribute;
import javax.swing.JLabel;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Karim Karaki 
 * @author Hekmat Kassir
 */
public class Game extends Canvas {
	/** The stragey that allows us to use accelerate page flipping */
	public BufferStrategy strategy;
	/** True if the game is currently "running", i.e. the game loop is looping */
	public boolean gameRunning = true;
	/** The list of all the entities that exist in our game */
	public ArrayList entities = new ArrayList();
	/** The list of entities that need to be removed from the game this loop */
	public ArrayList removeList = new ArrayList();
	/** The entity representing the player */
	public Entity ship ;
	/** The speed at which the player's ship should move (pixels/sec) */
	public double moveSpeed = 300;
	/** The time at which last fired a shot */
	public long lastFire = 0;
	/** The interval between our players shot (ms) */
	public long firingInterval = 300;
	/** The number of aliens left on the screen */
	public int alienCount;
	
	public double speed = 1.02;
	
	/** The message to display which waiting for a key press */
	public String message = "";
	/** True if we're holding up game play until a key has been pressed */
	public boolean waitingForKeyPress = true;
	/** True if the left cursor key is currently pressed */
	public boolean leftPressed = false;
	/** True if the right cursor key is currently pressed */
	public boolean rightPressed = false;
	/** True if we are firing */
	public boolean firePressed = false;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	public boolean logicRequiredThisLoop = false;
	public JLabel scoreLabel ;
	public int score = 0;
	public static int level = 0;
	public static boolean game_Start = true;
	public static String username;
	public static Connection conn;
	public static int shots = 0;
	public JLabel shotsLabel;
	Timer tim;
	
	/**
	 * Construct our game and set it running.
	 */
	public Game(String username, int level) {
		shots = 0;
		this.level = level;
		// create a frame to contain our game
		JFrame container = new JFrame("Space Invaders 101");
		
		// get hold the content of the frame and set up the resolution of the game
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);
		
		// setup our canvas size and put it into the content of the frame
		scoreLabel= new JLabel("Score=0");
		scoreLabel.setBackground(Color.black);
		scoreLabel.setBounds(10, 10, 70, 30);
		shotsLabel = new JLabel("Shots=30");
		shotsLabel.setBackground(Color.black);
		shotsLabel.setBounds(100,10,70,30);
		container.add(shotsLabel);
		container.add(scoreLabel);
		setBounds(0,0,800,600);
		JButton btnNewButton_2 = new JButton("Return");
		btnNewButton_2.setBackground(Color.green);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String usr = "'" + username + "'";
				String sel = " SELECT HIGHSCORE FROM Players WHERE USERNAME = " + usr;
				int temp = 0;
				java.sql.Statement stm;
				try {
					stm = conn.createStatement();
					ResultSet rSet = stm.executeQuery(sel);
					int oldscore = rSet.getInt("HIGHSCORE");
					temp = oldscore;
					if(score > oldscore) {
						int t = stm.executeUpdate("UPDATE Players SET HIGHSCORE = " + Integer.toString(score)+" WHERE USERNAME = " + usr);
					}

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
						
				game_Start = false;
				
				container.setVisible(false);
				container.dispose();
				if(score>temp) {
					Home newHome = new Home(username, score);
					newHome.setVisible(true);
				} else {
					Home newHome = new Home(username, temp);
					newHome.setVisible(true);
				}
			}
		});
		btnNewButton_2.setBounds(700, 10, 85, 21);
		container.add(btnNewButton_2);
		panel.add(this);
		
		
		tim  = new Timer();

		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);
		
		
		
		
		// finally make the window visible 
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		
		
		
		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit the game
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(new KeyInputHandler());
		
		// request the focus so key events come to us
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		// initialise the entities in our game so there's something
		// to see at startup
		initEntities();
	}
	
	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	public void startGame() {
		// clear out any existing entities and intialise a new set
		entities.clear();
		initEntities();
		
		// blank out any keyboard settings we might currently have
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
	}
	
	/**
	 * Initialise the starting state of the entities (ship and aliens). Each
	 * entitiy will be added to the overall list of entities in the game.
	 */
	public void initEntities() {
		// create the player ship and place it roughly in the center of the screen
		ship = new ShipEntity(this,"gun.gif",400,550);
		entities.add(ship);
		
		Random rd = new Random(); // creating Random object
		boolean dir = rd.nextBoolean();
		int angle ;  			
		if(level == 1) {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else if(level == 2) {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else if ( level == 3){
			angle = rd.nextInt(4);
			//angle2 = rd.nextInt(4);
			//Entity alien2 = new AlienEntity(this,"plot.gif",400,300,dir,1,level);
			//entities.add(alien2);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);

			ShotEntity shot = new ShotEntity(this,"shot.gif",ship.getX()+10,ship.getY()-30,angle,dir);
			entities.add(shot);
		}
			
		
	}
	
	public void initAliens() throws InterruptedException {
		alienCount = 0;
		Random rd = new Random(); // creating Random object
		boolean dir = rd.nextBoolean();
		
		int angle ;  			
		if(level == 1) {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else if(level == 2) {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else if ( level == 3){
			angle = rd.nextInt(4);
			//angle2 = rd.nextInt(4);
			//Entity alien2 = new AlienEntity(this,"plot.gif",400,300,dir,1,level);
			//entities.add(alien2);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);
		} else {
			angle = rd.nextInt(3);
			Entity alien = new AlienEntity(this,"plot.gif",400,400,dir,angle,level);
			entities.add(alien);

			ShotEntity shot = new ShotEntity(this,"shot.gif",ship.getX()+10,ship.getY()-30,angle,dir);
			entities.add(shot);
		}
	
	}
	
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}
		if(shots<=30) {
			// if we waited long enough, create the shot entity, and record the time.
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this,"shot.gif",ship.getX()+10,ship.getY()-30,level);
			entities.add(shot);
			shots++;
		}
		
	}
	
	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}
	
	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}
	
	/**
	 * Notification that the player has died. 
	 */
	public void notifyDeath() {
		message = "Oh no! They got you, try again?";
		waitingForKeyPress = true;
	}
	
	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	//public void notifyWin() {
		//message = "Well done! You Win!";
	//	waitingForKeyPress = true;
	//}
	
	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		// reduce the alient count, if there are none left, the player has won!
		
		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * speed);
			}
		}
	}
	
	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this 
	 * point, i.e. has he/she waited long enough between shots
	 */
	
	
	/**
	 * The main game loop. This loop is running during all game
	 * play as is responsible for the following activities:
	 * <p>
	 * - Working out the speed of the game loop to update moves
	 * - Moving the game entities
	 * - Drawing the screen contents (entities, text)
	 * - Updating game events
	 * - Checking Input
	 * <p>
	 * @throws InterruptedException 
	 */
	public void gameLoop() throws InterruptedException {
		long lastLoopTime = System.currentTimeMillis();
		// keep looping round til the game ends
		while (gameRunning) {
			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			
			// Get hold of a graphics context for the accelerated 
			// surface and blank it out
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.blue);
			g.fillRect(0,0,800,600);
			
			Random rd = new Random(); // creating Random object
			boolean dir = rd.nextBoolean();
			
			// cycle round asking each entity to move itself
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					if(entity instanceof AlienEntity) {
						if(entity.getX() <= 10 || entity.getY() <= 0 || entity.getX() >= 800) {
							removeEntity(entity);
							initAliens();
						}
					}
					entity.move(delta,dir);
					
					
				}
			}
			
			// cycle round drawing all the entities we have in the game
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				
				entity.draw(g);
			}
			
			// brute force collisions, compare every entity against
			// every other entity. If any of them collide notify 
			// both entities that the collision has occured
			for (int p=0;p<entities.size();p++) {
				for (int s=p+1;s<entities.size();s++) {
					Entity me = (Entity) entities.get(p);
					Entity him = (Entity) entities.get(s);
					
					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
						initAliens();
						score++;
						scoreLabel.setText("Score="+Integer.toString(score));
						
					}
				}
			}
			
			// remove any entity that has been marked for clear up
			entities.removeAll(removeList);
			removeList.clear();

			// if a game event has indicated that game logic should
			// be resolved, cycle round every entity requesting that
			// their personal logic should be considered.
			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				}
				
				logicRequiredThisLoop = false;
			}
			
			// if we're waiting for an "any key" press then draw the 
			// current message 
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("Press any key to start, and click X to go back to main menu",(800-g.getFontMetrics().stringWidth("Press any key to start, and click X to go back to main menu"))/2,300);
			}
			
			// finally, we've completed drawing so clear up the graphics
			// and flip the buffer over
			g.dispose();
			strategy.show();
			
			// resolve the movement of the ship. First assume the ship 
			// isn't moving. If either cursor key is pressed then
			// update the movement appropraitely
			ship.setHorizontalMovement(0);
			
			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);
			}
			
			// if we're pressing fire, attempt to fire
			if (firePressed) {
				tryToFire();
				if(shots<=30){
					int temp = 30 - shots;
					shotsLabel.setText("Shots = " +Integer.toString(temp));
				}
			}
			
			//finally pause for a bit. Note: this should run us at about
			//100 fps but on windows this might vary each loop due to
			//a bad implementation of timer
			try { Thread.sleep(10); } catch (Exception e) {}
		}
	}
	
	/**
	 * A class to handle keyboard input from the user. The class
	 * handles both dynamic input during game play, i.e. left/right 
	 * and shoot, and more static type input (i.e. press any key to
	 * continue)
	 * 
	 * This has been implemented as an inner class more through 
	 * habbit then anything else. Its perfectly normal to implement
	 * this as seperate class if slight less convienient.
	 * 
	 * @author Kevin Glass
	 */
	public class KeyInputHandler extends KeyAdapter {
		/** The number of key presses we've had while waiting for an "any key" press */
		public int pressCount = 1;
		
		/**
		 * Notification from AWT that a key has been pressed. Note that
		 * a key being pressed is equal to being pushed down but *NOT*
		 * released. Thats where keyTyped() comes in.
		 *
		 * @param e The details of the key that was pressed 
		 */
		public void keyPressed(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "press"
			if (waitingForKeyPress) {
				return;
			}
			
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if(level <4) {
					leftPressed = true;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if(level <4) {
					rightPressed = true;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if(level <4) {
					firePressed = true;
				}
			}
		} 
		
		/**
		 * Notification from AWT that a key has been released.
		 *
		 * @param e The details of the key that was released 
		 */
		public void keyReleased(KeyEvent e) {
			// if we're waiting for an "any key" typed then we don't 
			// want to do anything with just a "released"
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
		}

		/**
		 * Notification from AWT that a key has been typed. Note that
		 * typing a key means to both press and then release it.
		 *
		 * @param e The details of the key that was typed. 
		 */
		public void keyTyped(KeyEvent e) {
			// if we're waiting for a "any key" type then
			// check if we've recieved any recently. We may
			// have had a keyType() event from the user releasing
			// the shoot or move keys, hence the use of the "pressCount"
			// counter.
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					// since we've now recieved our key typed
					// event we can mark it as such and start 
					// our new game
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			// if we hit escape, then quit the game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			}
		}
	}
	
	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String username, Game g) throws ClassNotFoundException, InterruptedException {
		Class.forName("org.sqlite.JDBC");
		try {
			try {
				conn = DriverManager.getConnection("jdbc:sqlite:test2.db");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Game g =new Game(username);

		// Start the main game loop, note: this method will not
		// return until the game has finished running. Hence we are
		// using the actual main thread to run the game.
		while(true) {
			g.gameLoop();
		}
	}
}
