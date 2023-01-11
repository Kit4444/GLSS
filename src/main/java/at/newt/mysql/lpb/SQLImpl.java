package at.newt.mysql.lpb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public abstract class SQLImpl implements SQL{
	
	protected Connection con;

    private boolean logging = false;

    @Override
    public void disconnect() throws SQLException {
        if (con != null) {
                con.close();
        }
    }

    @Override
    public boolean isConnect() throws SQLException {
         if (con.isClosed() || con == null) {
             return false;
         }
        return true;
    }

    @Override
    public void update(String query) throws SQLException {
        Statement stm = con.createStatement();
        if(logging)
            System.out.println("Ausgefuehrtes Abfrage: " + query);
        stm.executeUpdate(query);
        stm.close();
    }

    @Override
    public ResultSet getResult(String query) throws SQLException {
        ResultSet rs = null;
        Statement stm = con.createStatement();
        if(logging)
            System.out.println("Ausgefuehrtes Abfrage: " + query);
        rs = stm.executeQuery(query);
        return rs;
    }

    @Override
    public PreparedStatement getStatement(String query) throws SQLException {
        if(logging)
            System.out.println("Ausgefuehrtes Abfrage: " + query);
        return con.prepareStatement(query);
    }

    public ResultSet getPrepairedResult(String query) throws SQLException {
        return getStatement(query).executeQuery();
    }

    @Override
    public boolean isInDatabase(String table, Map<String, Object> column) throws SQLException {
        ResultSet rs = select(table,column);
        try {
            return rs.next();
        }finally {
            rs.close();
        }
    }

    @Override
    public void createTabele(String table, Map<String, String> column) throws SQLException {
        String query = "CREATE TABLE "+table+" (";
        String[] keys = column.keySet().toArray(new String[column.keySet().size()]);
        query += String.join(" ?, ", keys);
        query += " ?)";
        PreparedStatement ps = getStatement(query);
        for(int i = 0; i < column.size();i++)
            ps.setObject(i+1,column.get(keys[i]));
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void createTableIfNotExists(String table, Map<String, String> column) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS "+table+" (";
        String[] keys = column.keySet().toArray(new String[column.keySet().size()]);
        query += String.join(" ?, ", keys);
        query += " ?)";
        PreparedStatement ps = getStatement(query);
        for(int i = 0; i < column.size();i++)
            ps.setObject(i+1,column.get(keys[i]));
        ps.executeUpdate();
        ps.close();
    }

    @SuppressWarnings("unused")
	@Override
    public void insertInto(String table, Map<String, Object> column) throws SQLException {
        String query = "INSERT INTO "+ table+" (";
        String[] keys = column.keySet().toArray(new String[column.keySet().size()]);
        query += String.join(", ", keys);
        query += ") VALUES (";
        for(String s : keys)
            query += "?, ";
        query = query.substring(0,query.length()-2);
        query += " )";
        PreparedStatement ps = getStatement(query);
        for(int i = 0;i<column.size();i++)
            ps.setObject(i+1,column.get(keys[i]));
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void update(String table, String column, Object value, Map<String, Object> where) throws SQLException {
        String query = "UPDATE "+table+" SET " + column+ "=" + value +" WHERE ";
        String[] keys = where.keySet().toArray(new String[where.keySet().size()]);
        query += String.join("=? AND ", keys)+ "=?";
        PreparedStatement ps = getStatement(query);
        for(int i = 0; i < where.size(); i++)
            ps.setObject(i+1,where.get(keys[i]));
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void delete(String table, Map<String, Object> column) throws SQLException {
        String query = "DELETE FROM " + table + " WHERE ";
        String[] keys = column.keySet().toArray(new String[column.keySet().size()]);
        query += String.join("=? AND ", keys) + "=?";
        PreparedStatement ps = getStatement(query);
        for(int i = 0; i<column.size();i++)
            ps.setObject(i+1,column.get(keys[i]));
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void dropTable(String table) throws SQLException {
        String query = "DROP TABLE " + table + "";
        update(query);
    }

    @Override
    public ResultSet select(String table, Map<String, Object> column) throws SQLException {
        String query = "SELECT * FROM "+table+" WHERE ";
        String[] keys = column.keySet().toArray(new String[column.keySet().size()]);
        query += String.join("=? AND ", keys)+"=?";
        PreparedStatement ps = getStatement(query);
        for(int i = 0; i<column.size();i++)
            ps.setObject(i+1,column.get(keys[i]));
        return ps.executeQuery();
    }

    @Override
    public ResultSet selectSortTable(String table, String column) throws SQLException {
        return getPrepairedResult("SELECT * FROM " + table + " ORDER BY " + column + " DESC");
    }

    @Override
    public ResultSet selectSortTableLimit(String table, String column, int limit) throws SQLException {
        return getPrepairedResult("SELECT * FROM " + table + " ORDER BY " + column + " DESC LIMIT "+ limit);
    }

    @Override
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    @Override
    public boolean isLogging() {
        return logging;
    }

}
