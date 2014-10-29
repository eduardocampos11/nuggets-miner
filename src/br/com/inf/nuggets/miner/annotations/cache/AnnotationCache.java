package br.com.inf.nuggets.miner.annotations.cache;

import br.com.inf.nuggets.miner.annotations.NuggetsMinerAnnotation;
import br.com.inf.nuggets.miner.annotations.NuggetsMinerAnnotationParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;

public class AnnotationCache
{
  private static AnnotationCache instance;
  private static final String dbName = ".seahawk.cache";
  private static final String tableName = "cache";
  private static final String newAnnotations = "newAnnotations";
  private final Connection conn;
  
  private AnnotationCache()
    throws SQLException, ClassNotFoundException
  {
    Class.forName("org.sqlite.JDBC");
    this.conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", new Object[] { ".seahawk.cache" }));
    createsTable();
  }
  
  private void createsTable()
    throws SQLException
  {
    Statement st = this.conn.createStatement();
    


    st.executeUpdate("CREATE TABLE IF NOT EXISTS cache (resourcePath TEXT NOT NULL,documentId TEXT NOT NULL,title TEXT NOT NULL,comment TEXT NOT NULL,author TEXT NOT NULL,date TEXT NOT NULL)");
    







    st.executeUpdate("CREATE TABLE IF NOT EXISTS newAnnotations (resourcePath TEXT NOT NULL,count INTEGER NOT NULL,PRIMARY KEY (resourcePath))");
  }
  
  public List<NuggetsMinerAnnotation> getAnnotationsForResource(IResource resource)
  {
    try
    {
      String path = resource.getFullPath().toPortableString();
      return getIdsList(path);
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return new ArrayList();
  }
  
  public void deleteAnnotationsForResource(IResource resource)
    throws SQLException
  {
    String path = resource.getFullPath().toPortableString();
    PreparedStatement prep = this.conn.prepareStatement("DELETE FROM cache  WHERE resourcePath = ?");
    prep.setString(1, path);
    
    int res = prep.executeUpdate();
    if (res <= 0) {
      System.err.println("Cache DELETION failed for resource " + path);
    }
  }
  
  public void addAnnotationForResource(IResource resource, NuggetsMinerAnnotation annotation)
    throws SQLException
  {
    String path = resource.getFullPath().toPortableString();
    PreparedStatement prep = this.conn.prepareStatement("INSERT INTO cache VALUES(?,?,?,?,?,?)");
    prep.setString(1, path);
    prep.setString(2, (String)annotation.getFieldValue(NuggetsMinerAnnotation.DOCUMENT_ID_FIELD));
    prep.setString(3, (String)annotation.getFieldValue(NuggetsMinerAnnotation.DOCUMENT_TITLE_FIELD));
    prep.setString(4, (String)annotation.getFieldValue(NuggetsMinerAnnotation.COMMENT_FIELD));
    prep.setString(5, (String)annotation.getFieldValue(NuggetsMinerAnnotation.AUTHOR_FIELD));
    prep.setString(6, (String)annotation.getFieldValue(NuggetsMinerAnnotation.CREATION_TIME_FIELD));
    

    int res = prep.executeUpdate();
    if (res <= 0) {
      System.err.println("Cache ADDITION failed for resource " + path);
    }
  }
  
  public void updateCacheForResource(IResource resource, List<NuggetsMinerAnnotation> annotations)
  {
    try
    {
      deleteAnnotationsForResource(resource);
      deleteNewAnnotationsCountForResource(resource);
      for (NuggetsMinerAnnotation a : annotations) {
        addAnnotationForResource(resource, a);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public void updateForMovedResource(IResourceDelta delta)
  {
    try
    {
      String from = delta.getMovedFromPath().toPortableString();
      String to = delta.getFullPath().toPortableString();
      PreparedStatement prep = this.conn.prepareStatement("UPDATE cache SET resourcePath=? WHERE resourcePath=?");
      prep.setString(1, to);
      prep.setString(2, from);
      
      int res = prep.executeUpdate();
      if (res <= 0) {
        System.err.println("Cache UPDATE failed for resource " + from);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  public int getNewAnnotationsCount(IResource resource)
  {
    String path = resource.getFullPath().toPortableString();
    try
    {
      PreparedStatement prep = this.conn.prepareStatement(
        "SELECT count FROM newAnnotations WHERE resourcePath like '" + path + "%'");
      
      ResultSet res = prep.executeQuery();
      int total = 0;
      while (res.next()) {
        total += res.getInt(1);
      }
      return total;
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    return 0;
  }
  
  public void deleteNewAnnotationsCountForResource(IResource resource)
    throws SQLException
  {
    String path = resource.getFullPath().toPortableString();
    PreparedStatement s = this.conn.prepareStatement("SELECT * FROM newAnnotations  WHERE resourcePath=?");
    s.setString(1, path);
    
    ResultSet set = s.executeQuery();
    while (set.next()) {
      System.out.println(set.getString("resourcePath"));
    }
    PreparedStatement prep = this.conn.prepareStatement("DELETE FROM newAnnotations  WHERE resourcePath=?");
    prep.setString(1, path);
    

    int res = prep.executeUpdate();
    if (res <= 0) {
      System.err.println("Cache DELETION failed for resource " + path);
    }
  }
  
  public void storeNewAnnotationsCountForResource(IResource resource)
  {
    try
    {
      IPath location = resource.getRawLocation();
      if (location == null) {
        return;
      }
      File file = new File(location.toOSString());
      if (!file.isFile()) {
        return;
      }
      deleteNewAnnotationsCountForResource(resource);
      List<NuggetsMinerAnnotation> annotations = NuggetsMinerAnnotationParser.parse(file);
      List<NuggetsMinerAnnotation> current = getAnnotationsForResource(resource);
      annotations.removeAll(current);
      int newCount = annotations.size();
      String path = resource.getFullPath().toPortableString();
      
      PreparedStatement prep = this.conn.prepareStatement("INSERT INTO newAnnotations VALUES(?,?)");
      prep.setString(1, path);
      prep.setInt(2, newCount);
      
      int res = prep.executeUpdate();
      if (res <= 0) {
        System.err.println("New annotation count cache ADDITION failed for resource " + path);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
  
  private List<NuggetsMinerAnnotation> getIdsList(String resourcePath)
    throws SQLException
  {
    List<NuggetsMinerAnnotation> annotations = new ArrayList();
    PreparedStatement prep = this.conn.prepareStatement("SELECT * FROM cache WHERE resourcePath=?");
    
    prep.setString(1, resourcePath);
    
    ResultSet res = prep.executeQuery();
    while (res.next())
    {
      NuggetsMinerAnnotation a = new NuggetsMinerAnnotation();
      a.setFieldValue(NuggetsMinerAnnotation.DOCUMENT_ID_FIELD, res.getString("documentId"));
      a.setFieldValue(NuggetsMinerAnnotation.COMMENT_FIELD, res.getString("comment"));
      a.setFieldValue(NuggetsMinerAnnotation.AUTHOR_FIELD, res.getString("author"));
      a.setFieldValue(NuggetsMinerAnnotation.DOCUMENT_TITLE_FIELD, res.getString("title"));
      a.setFieldValue(NuggetsMinerAnnotation.CREATION_TIME_FIELD, res.getString("date"));
      annotations.add(a);
    }
    return annotations;
  }
  
  public static AnnotationCache getInstance()
  {
    if (instance == null) {
      try
      {
        instance = new AnnotationCache();
      }
      catch (SQLException e)
      {
        e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
        e.printStackTrace();
      }
    }
    return instance;
  }
}
