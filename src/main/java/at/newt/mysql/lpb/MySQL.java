package at.newt.mysql.lpb;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends SQLImpl{
	
	private String host;
	private int port;
	private String db;
	private String user;
	private String pw;
	
	public MySQL(String host, int port, String db, String user, String pw) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.pw = pw;
	}
	
	public void connect() throws SQLException{
		con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&serverTimezone=UTC", user, pw);
	}

}
