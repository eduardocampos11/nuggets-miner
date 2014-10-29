package br.com.inf.nuggets.miner.dnd;

import br.com.inf.nuggets.miner.solr.DocumentBase;
import br.com.inf.nuggets.miner.solr.DocumentDeserializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public class DocumentTransfer
  extends ByteArrayTransfer
{
  private static final DocumentTransfer instance = new DocumentTransfer();
  private static final String TYPE_NAME = "document-transfer-format:" + System.currentTimeMillis() + ":" + instance.hashCode();
  private static final int TYPE_ID = registerType(TYPE_NAME);
  
  protected void javaToNative(Object data, TransferData transferData)
  {
    Object[] temp = (Object[])data;
    
    List<DocumentBase> docs = new ArrayList();
    for (int i = 0; i < temp.length; i++)
    {
      if (!(temp[i] instanceof DocumentBase)) {
        return;
      }
      docs.add((DocumentBase)temp[i]);
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(out);
    try
    {
      dataOut.writeInt(docs.size());
      for (DocumentBase doc : docs)
      {
        String json = doc.toJson();
        dataOut.writeInt(json.length());
        dataOut.writeBytes(json);
      }
      dataOut.close();
      out.close();
      super.javaToNative(out.toByteArray(), transferData);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  protected Object nativeToJava(TransferData transferData)
  {
    byte[] bytes = (byte[])super.nativeToJava(transferData);
    if (bytes == null) {
      return null;
    }
    DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
    List<String> docs = new ArrayList();
    try
    {
      int count = in.readInt();
      for (int i = 0; i < count; i++)
      {
        int length = in.readInt();
        byte[] jsonBuff = new byte[length];
        in.read(jsonBuff);
        docs.add(new String(jsonBuff));
      }
      return new DocumentDeserializer().deserialize(docs).toArray(new DocumentBase[docs.size()]);
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public static DocumentTransfer getInstance()
  {
    return instance;
  }
  
  protected int[] getTypeIds()
  {
    return new int[] { TYPE_ID };
  }
  
  protected String[] getTypeNames()
  {
    return new String[] { TYPE_NAME };
  }
}
