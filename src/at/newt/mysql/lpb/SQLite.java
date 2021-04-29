package at.newt.mysql.lpb;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends SQLImpl{
	
	private File db;

    public SQLite(File db){
        this.db = db;
    }

    @Override
    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite://"+db.getAbsolutePath());

    }

}
