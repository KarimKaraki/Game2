import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.JLabel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



/*
 * @author Karim Karaki 
 * @author Hekmat Kassir
 */
public class Home extends JFrame {

	private JPanel contentPane;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home("ddd",1);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	/**
	 * Create the frame.
	 */
	public Home(String username, int highscore) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JButton btnNewButton = new JButton("Level 1");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker() {
					
					@Override
					protected String doInBackground() throws Exception {
						setVisible(false);
						Game game = new Game(username,1);
						game.main(username, game);
						
						return "";
					}
					
				}.execute();
				dispose();
			}
		});
		btnNewButton.setFont(new Font("Times New Roman", Font.BOLD, 17));
		btnNewButton.setBounds(42, 119, 111, 55);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel(username);
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblNewLabel.setBounds(140, 11, 196, 36);
		contentPane.add(lblNewLabel);
		
		
		
		JLabel lblNewLabel_1 = new JLabel("HighScore = " + Integer.toString(highscore));
		lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblNewLabel_1.setBounds(10, 10, 120, 36);
		contentPane.add(lblNewLabel_1);
		
		JButton btnNewButton_2 = new JButton("Quit");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnNewButton_2.setBounds(341, 232, 85, 21);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_1 = new JButton("Level 2");
		btnNewButton_1.setFont(new Font("Times New Roman", Font.BOLD, 17));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				new SwingWorker() {
					
					@Override
					protected String doInBackground() throws Exception {
						Game game = new Game(username,2);
						game.main(username, game);
						
						return "";
					}
					
				}.execute();
			}
		});
		btnNewButton_1.setBounds(163, 119, 111, 55);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_3 = new JButton("Level 3");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker() {
					
					@Override
					protected String doInBackground() throws Exception {
						Game game = new Game(username,3);
						game.main(username, game);
						return "";
					}
					
				}.execute();
			}
		});
		btnNewButton_3.setFont(new Font("Times New Roman", Font.BOLD, 17));
		btnNewButton_3.setBounds(284, 119, 111, 55);
		contentPane.add(btnNewButton_3);
	}
}
