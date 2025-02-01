/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database;

/**
 *
 * @author ADMIN
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthenticationController {
	public static boolean registerUser(String userName,String password, String securityQuestion
		)	
	{
		String hashedPassword = hashPassword(password);

		try (Connection conn = DatabaseConnection.getConnection()) {
			String query = "INSERT INTO users (userName,password,securityQuestion) VALUES (?, ?, ?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);

			pstmt.setString(1, userName);
			pstmt.setString(2,hashedPassword);
			pstmt.setString(3, securityQuestion);

			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Simple password hashing (Note: Use a more secure method in production)
	private static String hashPassword(String password) {
		return String.valueOf(password.hashCode());
	}

	// Login Method
	public static boolean loginUser(String username, String password) {
		String hashedPassword = hashPassword(password);
		System.out.println(hashedPassword);
		try (Connection conn = DatabaseConnection.getConnection()) {
			String query = "SELECT id, name  FROM users WHERE username = ? AND password = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, username);
			pstmt.setString(2, hashedPassword);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
}