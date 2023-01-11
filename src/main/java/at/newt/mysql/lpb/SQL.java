package at.newt.mysql.lpb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public interface SQL {
	
	void connect() throws ClassNotFoundException, SQLException;
	 void disconnect() throws SQLException;
	 boolean isConnect() throws SQLException;
	 void update(String query) throws SQLException;
	 ResultSet getResult(String query) throws SQLException;
	 PreparedStatement getStatement(String query) throws SQLException;
	 ResultSet getPrepairedResult(String query) throws SQLException;
	 boolean isInDatabase(String table, Map<String, Object> column) throws SQLException;
	 void createTabele(String table, Map<String, String> column) throws SQLException;
	 void createTableIfNotExists(String table, Map<String, String> column) throws SQLException;
	 void insertInto(String table, Map<String, Object> column) throws SQLException;
	 void update(String table, String column, Object value, Map<String, Object> where) throws SQLException;
	 void delete(String table, Map<String, Object> column) throws SQLException;
	 void dropTable(String table) throws SQLException;
	 ResultSet select(String table, Map<String, Object> column) throws SQLException;
	 ResultSet selectSortTable(String table, String column) throws SQLException;
	 ResultSet selectSortTableLimit(String table, String column, int limit) throws SQLException;

	 void setLogging(boolean logging);
	 boolean isLogging();

}
