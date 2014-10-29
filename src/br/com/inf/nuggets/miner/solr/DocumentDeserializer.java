package br.com.inf.nuggets.miner.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

public class DocumentDeserializer
{
  final ObjectMapper mapper = new ObjectMapper();
  
  public DocumentBase deserialize(String jsonDocument)
    throws IOException
  {
    return (DocumentBase)this.mapper.readValue(jsonDocument, DocumentBase.class);
  }
  
  public List<DocumentBase> deserialize(List<String> documents)
    throws IOException
  {
    List<DocumentBase> docList = new ArrayList();
    for (String d : documents) {
      docList.add(deserialize(d));
    }
    return docList;
  }
}
