package org.auriferous.webautomator.shared.data.history;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.auriferous.webautomator.shared.data.DataEntry;

public class HistoryConfig {
	private Connection database = null;
	
	public HistoryConfig() {
		try {
			database = DriverManager.getConnection(
		            "jdbc:derby:config/historydb;create=true");
			
			DatabaseMetaData md = database.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			
			boolean historyTableExists = false;
			while (rs.next()) {
				if (rs.getString(3).equals("HISTORY")) {
					historyTableExists = true;
					break;
				}
			}
			
			if (!historyTableExists) {
				Statement stmt = database.createStatement();
				
				String sql = "CREATE TABLE HISTORY ("+
							 "Timestamp bigint,"+
							 "URL varchar(10000),"+
							 "Title varchar(10000),"+
							 "Favicon varchar(10000))";
				
				int result = stmt.executeUpdate(sql);
				stmt.close();
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void clear() {
		String sql = "DELETE FROM HISTORY";
		try {
			Statement stmt = database.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addEntry(HistoryEntry entry) {
		String sql = "INSERT INTO HISTORY VALUES ("+
					 entry.getTimeStamp()+","+
					 "'"+entry.getURL()+"',"+
					 "'"+entry.getTitle()+"',"+
					 "'"+entry.getFaviconPath()+"')";
		try {
			Statement stmt = database.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeEntry(HistoryEntry entry) {
	}
	
	public List<HistoryEntry> getEntries() {
		List<HistoryEntry> entries = new ArrayList<HistoryEntry>();
		
		String sql = "SELECT * FROM HISTORY";
		
		try {
			Statement stmt = database.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				entries.add(new HistoryEntry(rs.getLong(1), rs.getString(4), rs.getString(3), rs.getString(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entries;
	}
}
