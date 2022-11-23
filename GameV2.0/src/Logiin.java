import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;


/*
 * @author Karim Karaki 
 * @author Hekmat Kassir
 */
public class Logiin extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	public static Connection conn;
	public int id_fr;

	/**
	 * Launch the application.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:test2.db");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Logiin frame = new Logiin();
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
	public Logiin() {
		/*try {
			java.sql.Statement stm = conn.createStatement();
			ResultSet rSet = stm.executeQuery("SELECT ID FROM Players");
			while(rSet.next()) {				
				id_fr = rSet.getInt("ID");
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(52, 106, 67, 29);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(172, 111, 96, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(52, 159, 86, 29);
		contentPane.add(lblNewLabel_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(172, 164, 96, 19);
		contentPane.add(passwordField);
		
		JLabel lblNewLabel_2 = new JLabel("Login");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 21));
		lblNewLabel_2.setBounds(172, 29, 86, 44);
		contentPane.add(lblNewLabel_2);
		
		JButton btnNewButton = new JButton("Login");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = textField.getText();
				String pass = passwordField.getText();
				int hs =0;
				try {
					java.sql.Statement stm = conn.createStatement();
					ResultSet rSet = stm.executeQuery("SELECT USERNAME,PASSWORD,HIGHSCORE FROM Players");
					while(rSet.next()) {
						String temp1 = rSet.getString("USERNAME");
						String temp2 = rSet.getString("PASSWORD");
						hs = rSet.getInt("HIGHSCORE");
						if(temp1.equals(username) && temp2.equals(pass)) {
							JFrame home = new Home(temp1,hs);
							home.setVisible(true);
							setVisible(false);
						}
						
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		btnNewButton.setBounds(194, 232, 85, 21);
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel_3 = new JLabel("");
		lblNewLabel_3.setBounds(10, 236, 96, 13);
		contentPane.add(lblNewLabel_3);
		
		JButton btnNewButton_1 = new JButton("Register");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sql = "INSERT INTO Players(USERNAME,PASSWORD) VALUES(?,?)";

		        PreparedStatement pstmt;
				try {
					pstmt = conn.prepareStatement(sql);
					String username = textField.getText();
					String pass = passwordField.getText();
		            pstmt.setString(1, username);  
		            pstmt.setString(2, pass);
		            pstmt.executeUpdate();
		            lblNewLabel_3.setText("Registration successfull");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(322, 232, 85, 21);
		contentPane.add(btnNewButton_1);
		
		JTextPane txtpnIfYouDo = new JTextPane();
		txtpnIfYouDo.setText("If you do not have an account create a username and password in the provided fields and click the register button your account will then be created");
		txtpnIfYouDo.setBounds(313, 82, 113, 110);
		contentPane.add(txtpnIfYouDo);
		
		
	}
	
	
}
