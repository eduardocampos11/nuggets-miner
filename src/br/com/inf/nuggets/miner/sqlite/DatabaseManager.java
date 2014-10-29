package br.com.inf.nuggets.miner.sqlite;

import br.com.inf.nuggets.miner.Activator;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;
import br.com.inf.nuggets.miner.solr.DocumentBase;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.jface.preference.IPreferenceStore;

public class DatabaseManager
{
//  //sessao SSH com o servidor da FACOM
//  private static Session jschSession = null;
  private Connection conn;
  
  private static String tableName = "documents";
  private static DatabaseManager instance;
  public static final String SQLITE_CONN = "sqlite";
  public static final String MYSQL_CONN = "mysql";
  public static final String POSTGRESQL_CONN = "postgresql";
  
  private DatabaseManager()
    throws ClassNotFoundException, SQLException
  {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    String dbType = store.getString(NuggetsMinerPreferencesConstant.DB_TYPE);
    String clazzName = null;
    if (dbType == "sqlite") {
      clazzName = "org.sqlite.JDBC";
    } else if (dbType == "mysql") {
      clazzName = "com.mysql.jdbc.Driver";
    } else if (dbType == "postgresql") {
      clazzName = "org.postgresql.Driver";
    }
    String dbName = store.getString(NuggetsMinerPreferencesConstant.DB_NAME);
    boolean anonymous = store.getBoolean(NuggetsMinerPreferencesConstant.DB_ANONYMOUS_ACCESS);
    if (anonymous)
    {
      Class.forName(clazzName);	
      this.conn = DriverManager.getConnection(String.format("jdbc:%s:%s", new Object[] { dbType, dbName }));
    }
    else
    {
      String userName = store.getString(NuggetsMinerPreferencesConstant.DB_USER);
      String userPassword = store.getString(NuggetsMinerPreferencesConstant.DB_PASSWORD);
      String sshPort = store.getString(NuggetsMinerPreferencesConstant.SSH_PORT);
      
      /*Session sshSession = getSSHsession();
      if (sshSession != null && sshSession.isConnected()) { 
   
    	//conexao com o banco de dados mySQL 
    	 this.conn = null; 
    	 try {
    		//carrega classe de driver do banco de dados
			Class.forName(clazzName).newInstance();
			
			//estabelece conexao com o banco de dados
			this.conn = DriverManager.getConnection(String.format("jdbc:%s://localhost:%s/%s", 
		    		  new Object[] { dbType, sshPort, dbName }), userName, userPassword);
			
			System.out.println("Database connection established");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
      }*/
    }
    createTable();
  }
  
  /*private static Session getSSHsession() {
	  Properties config = new Properties();
	  config.put("StrictHostKeyChecking", "no");
	  
	  IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	  String sshIP = store.getString(SeahawkPreferencesConstant.SSH_IP);
	  String sshUser = store.getString(SeahawkPreferencesConstant.SSH_USER);
	  String sshPassword = store.getString(SeahawkPreferencesConstant.SSH_PASSWORD);
	  String sshPort = store.getString(SeahawkPreferencesConstant.SSH_PORT);
	  String dbPort = store.getString(SeahawkPreferencesConstant.DATABASE_PORT);
	  
	  Session jschSession = null;
	  if (jschSession == null || !jschSession.isConnected()) {			
		  JSch jsch = new JSch();
		  try {
			  jschSession = jsch.getSession(sshUser, sshIP, Integer.parseInt(sshPort));
			  jschSession.setPassword(sshPassword);
			  jschSession.setConfig(config);
			  jschSession.connect();	
			  setPortForwardingForSession(jschSession, Integer.parseInt(sshPort), 
					  sshIP, Integer.parseInt(dbPort));
		  } catch (JSchException e1) {
			  System.err.println("Erro ao tentar estabelecer conexao SSH");
			  e1.printStackTrace();
		  }
	  }
	  return jschSession;
  }*/
  
  /*private static void setPortForwardingForSession(Session jschSession, int sshPort, String sshIP, int dbPort) {	
		int assignedPort = 0;
		try {
			assignedPort = jschSession.setPortForwardingL(sshPort, sshIP, dbPort);
			System.out.println("localhost:" + assignedPort + " -> " + sshIP + ":" + dbPort);
			System.out.println("Port Forwarded");
		} catch (JSchException e) {
			System.err.println("Erro ao tentar encaminhar a porta: " + dbPort + " do servidor: " + sshIP);
			e.printStackTrace();
		}
  }*/
  
  private void createTable()
    throws SQLException
  {
    Statement st = this.conn.createStatement();
    
    st.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + 
      "file TEXT NOT NULL," + 
      "title TEXT NOT NULL," + 
      "docId TEXT NOT NULL," + 
      "comment TEXT, PRIMARY KEY(file,docId))");
  }
  
  public void deleteDocument(MrCrawleyDbEntry entry)
    throws Exception
  {
    PreparedStatement prep = this.conn.prepareStatement("DELETE FROM " + tableName + " WHERE file = ? AND docId = ?");
    prep.setString(1, entry.getFilePath());
    prep.setString(2, entry.getDocId());
    
    int res = prep.executeUpdate();
    if (res == -1)
    {
      System.err.println("Link DELETION failed");
      throw new Exception("Link DELETION failed. Unable to delete the linked document.");
    }
  }
  
  public void addDocument(String filePath, DocumentBase document, String comment)
    throws Exception
  {
    PreparedStatement update = this.conn.prepareStatement("UPDATE " + tableName + " SET title=?, comment=? WHERE file=? and docId=?");
    update.setString(1, document.getTitle());
    update.setString(2, comment);
    update.setString(3, filePath);
    update.setString(4, document.getId());
    if (update.executeUpdate() > 0) {
      return;
    }
    PreparedStatement prep = this.conn.prepareStatement("INSERT INTO " + tableName + " VALUES(?,?,?,?)");
    prep.setString(1, filePath);
    prep.setString(2, document.getTitle());
    prep.setString(3, document.getId());
    prep.setString(4, comment);
    
    int res = prep.executeUpdate();
    if (res == -1)
    {
      System.err.println("Link INSERTION failed");
      throw new Exception("Link INSERTION failed. Unable to create new link to document.");
    }
  }
  
  public List<MrCrawleyDbEntry> getDocuments(String fileName)
    throws SQLException
  {
    List<MrCrawleyDbEntry> entries = new ArrayList();
    PreparedStatement prep = this.conn.prepareStatement("SELECT * FROM " + tableName + " WHERE file=?");
    
    prep.setString(1, fileName);
    ResultSet res = prep.executeQuery();
    while (res.next()) {
      entries.add(new MrCrawleyDbEntry(res.getString("file"), 
        res.getString("docId"), res.getString("title"), res.getString("comment")));
    }
    return entries;
  }
  
  public static void reloadConnector()
    throws ClassNotFoundException, SQLException
  {
    instance = new DatabaseManager();
  }
  
  public static DatabaseManager getInstance()
    throws ClassNotFoundException, SQLException
  {
    if (instance == null) {
      instance = new DatabaseManager();
    }
    return instance;
  }
}
