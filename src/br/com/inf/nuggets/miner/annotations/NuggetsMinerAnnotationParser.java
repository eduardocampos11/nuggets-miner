package br.com.inf.nuggets.miner.annotations;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

public class NuggetsMinerAnnotationParser
{
  private static NuggetsMinerAnnotation getAnnotationData(String comment)
  {
    String cleanedComment = comment.replaceAll("[\\s]*\\*\\s?", "\n");
    Pattern p = Pattern.compile("\\s*\\@(\\w+[\\w|\\d|-|_]*)\\s");
    Matcher m = p.matcher(cleanedComment);
    NuggetsMinerAnnotation annotation = new NuggetsMinerAnnotation();
    List<String> tags = new ArrayList();
    List<Integer> startIndex = new ArrayList();
    List<Integer> endIndex = new ArrayList();
    while (m.find())
    {
      tags.add(m.group(1));
      endIndex.add(Integer.valueOf(m.end()));
      startIndex.add(Integer.valueOf(m.start()));
    }
    for (int i = 0; i < tags.size(); i++)
    {
      String tag = (String)tags.get(i);
      Integer end = (Integer)endIndex.get(i);
      if (i + 1 < tags.size()) {
        annotation.setFieldValue(tag, cleanedComment.substring(end.intValue(), ((Integer)startIndex.get(i + 1)).intValue()).trim());
      } else {
        annotation.setFieldValue(tag, cleanedComment.substring(end.intValue(), cleanedComment.length() - 1).trim());
      }
    }
    if (!annotation.isValid()) {
      return null;
    }
    return annotation;
  }
  
  public static NuggetsMinerAnnotation parse(IDocument document, ITypedRegion region)
  {
    try
    {
      String content = document.get(region.getOffset(), region.getLength());
      
      String[] delimiters = NuggetsMinerPartitionScanner.getDelimiters(document);
      if (delimiters == null) {
        return null;
      }
      List<NuggetsMinerAnnotation> annotations = parse(content, delimiters[0], delimiters[1]);
      if ((annotations == null) || (annotations.size() <= 0)) {
        return null;
      }
      ((NuggetsMinerAnnotation)annotations.get(0)).setFieldValue(NuggetsMinerAnnotation.OFFSET_FIELD, new Integer(region.getOffset()));
      ((NuggetsMinerAnnotation)annotations.get(0)).setFieldValue(NuggetsMinerAnnotation.LENGTH_FIELD, new Integer(region.getLength()));
      return (NuggetsMinerAnnotation)annotations.get(0);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private static String escapeForRegex(String str)
  {
    return 
    











      str.replaceAll("\\*", "\\\\*").replaceAll("\\^", "\\\\^").replaceAll("\\$", "\\\\$").replaceAll("\\+", "\\\\+").replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]").replaceAll("\\#", "\\\\#").replaceAll("\\|", "\\\\|").replaceAll("\\?", "\\\\?");
  }
  
  public static List<NuggetsMinerAnnotation> parse(String source, String fileExtension)
  {
    String[] delimiters = NuggetsMinerPartitionScanner.getDelimiters(fileExtension);
    if (delimiters == null) {
      return new ArrayList();
    }
    return parse(source, delimiters[0], delimiters[1]);
  }
  
  public static List<NuggetsMinerAnnotation> parse(String content, String openDelimiter, String closeDelimiter)
  {
    List<NuggetsMinerAnnotation> annotations = new ArrayList();
    String[] split = content.split(escapeForRegex(openDelimiter));
    for (String s : split)
    {
      int index = s.indexOf(closeDelimiter);
      if (index > 0)
      {
        String commentContent = s.substring(0, index);
        NuggetsMinerAnnotation annotation = getAnnotationData(commentContent.replaceAll("\n\\*", "\n"));
        if (annotation != null) {
          annotations.add(annotation);
        }
      }
    }
    return annotations;
  }
  
  public static List<NuggetsMinerAnnotation> parse(File input)
    throws IOException
  {
    String path = input.getCanonicalPath();
    int index = path.lastIndexOf(".");
    if (index < 0) {
      return new ArrayList();
    }
    String extension = path.substring(index + 1);
    String content = loadFile(input);
    return parse(content, extension);
  }
  
  private static String loadFile(File file)
    throws IOException
  {
    char[] chr = new char[4096];
    StringBuffer buffer = new StringBuffer();
    FileReader reader = new FileReader(file);
    int len;
    try
    {
      while ((len = reader.read(chr)) > 0)
      {
        buffer.append(chr, 0, len);
      }
    }
    finally
    {
      reader.close();
    }
    return buffer.toString();
  }
  
  public static boolean isAnnotation(String content)
  {
    return getAnnotationData(content) != null;
  }
}
