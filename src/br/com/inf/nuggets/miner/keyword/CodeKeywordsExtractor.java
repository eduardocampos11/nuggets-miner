package br.com.inf.nuggets.miner.keyword;

import br.com.inf.nuggets.miner.keyword.filter.IFilter;
import br.com.inf.nuggets.miner.keyword.tokenizer.ITokenizer;
import br.com.inf.nuggets.miner.keyword.tokenizer.WhiteSpaceTokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class CodeKeywordsExtractor
{
  private ITokenizer tokenizer;
  private final List<IFilter> filters;
  
  public CodeKeywordsExtractor()
    throws JavaModelException
  {
    this.tokenizer = new WhiteSpaceTokenizer();
    this.filters = new ArrayList();
  }
  
  public CodeKeywordsExtractor(ITokenizer tokenizer)
    throws JavaModelException
  {
    this();
    this.tokenizer = tokenizer;
  }
  
  public List<String> extractKeywords(IJavaElement element)
    throws JavaModelException
  {
    Set<String> keywords = new HashSet();
    Stack<IJavaElement> toVisit = new Stack();
    toVisit.add(element);
    while (toVisit.size() > 0)
    {
      IJavaElement e = (IJavaElement)toVisit.pop();
      switch (e.getElementType())
      {
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        IParent parent = (IParent)e;
        for (IJavaElement child : parent.getChildren()) {
          toVisit.push(child);
        }
        break;
      case 7: 
      case 9: 
        keywords.addAll(extractEntityKeywords(e));
      }
    }
    return new ArrayList(keywords);
  }
  
  protected List<String> extractEntityKeywords(IJavaElement entity)
    throws JavaModelException
  {
    if (!(entity instanceof IMember)) {
      return new ArrayList();
    }
    IMember member = (IMember)entity;
    
    Set<String> keywords = new HashSet();
    ICompilationUnit unit = member.getCompilationUnit();
    if (unit == null) {
      return new ArrayList();
    }
    List<IImportDeclaration> imports = Arrays.asList(unit.getImports());
    List<String> importKeywords = getKeywordsFromImports(member, imports);
    List<String> methodKeywords = getTopNKeywords(member.getSource(), 10);
    keywords.addAll(importKeywords);
    keywords.addAll(methodKeywords);
    keywords.add(entity.getElementName());
    
    return new ArrayList(keywords);
  }
  
  protected List<String> getKeywordsFromImports(IJavaElement element, List<IImportDeclaration> imports)
    throws JavaModelException
  {
    Set<String> keywords = new HashSet();
    IJavaProject project = element.getJavaProject();
    for (IImportDeclaration i : imports) {
      if ((!isProjectImport(project, i)) && (isImportUsed(element, i)))
      {
        String name = i.getElementName().replace("*", "");
        keywords.addAll(Arrays.asList(name.split("\\.")));
      }
    }
    return new ArrayList(keywords);
  }
  
  private boolean isImportUsed(IJavaElement element, IImportDeclaration importDeclaration)
    throws JavaModelException
  {
    String source = ((ISourceReference)element).getSource();
    int index = importDeclaration.getElementName().lastIndexOf(".");
    String typeName = importDeclaration.getElementName().substring(index + 1);
    
    return source.contains(typeName);
  }
  
  private boolean isProjectImport(IJavaProject project, IImportDeclaration importDeclaration)
    throws JavaModelException
  {
    int index = importDeclaration.getElementName().lastIndexOf(".");
    String typeName = importDeclaration.getElementName().substring(index + 1);
    String pkgName = importDeclaration.getElementName().substring(0, index);
    IType type = project.findType(pkgName, typeName, new NullProgressMonitor());
    if (type == null) {
      return false;
    }
    ICompilationUnit unit = type.getCompilationUnit();
    if (unit == null) {
      return false;
    }
    String path = unit.getResource().getFullPath().toOSString();
    String prjPath = project.getResource().getFullPath().toString();
    
    return path.contains(prjPath);
  }
  
  protected List<String> getTopNKeywords(String content, int n)
  {
    List<String> tokens = this.tokenizer.tokenize(content);
    List<String> filteredTokens = new ArrayList();
    for (String t : tokens)
    {
      List<String> filtered = applyFilters(t);
      filteredTokens.addAll(filtered);
    }
    List<String> top = new ArrayList();
    Object itf = new TreeMap(new Comparator<Integer>()
    {
      public int compare(Integer i, Integer j)
      {
        return j.compareTo(i);
      }
    });
    Map<String, Integer> tf = new HashMap();
    for (String k : filteredTokens)
    {
      if (!tf.containsKey(k)) {
        tf.put(k, Integer.valueOf(0));
      }
      tf.put(k, Integer.valueOf(((Integer)tf.get(k)).intValue() + 1));
    }
    for (Map.Entry<String, Integer> e : tf.entrySet())
    {
      if (!((Map)itf).containsKey(e.getValue())) {
        ((Map)itf).put((Integer)e.getValue(), new ArrayList());
      }
      ((List)((Map)itf).get(e.getValue())).add((String)e.getKey());
    }
    List<Integer> ids = new ArrayList(((Map)itf).keySet());
    for (Integer i : ids)
    {
      List<String> vals = (List)((Map)itf).get(i);
      top.addAll(vals);
      if (top.size() >= n) {
        break;
      }
    }
    return top;
  }
  
  private List<String> applyFilters(String token)
  {
    List<String> tokens = new ArrayList();
    tokens.add(token);
    if (this.filters.size() <= 0) {
      return tokens;
    }
    for (IFilter f : this.filters) {
      tokens = f.filter(tokens);
    }
    return tokens;
  }
  
  public void setFilters(IFilter... filters)
  {
    for (IFilter f : filters) {
      this.filters.add(f);
    }
  }
  
  public void addFilter(IFilter filter)
  {
    this.filters.add(filter);
  }
  
  public void removeFilter(IFilter filter)
  {
    this.filters.add(filter);
  }
  
  public void setTokenizer(ITokenizer tokenizer)
  {
    this.tokenizer = tokenizer;
  }
  
  public ITokenizer getTokenizer()
  {
    return this.tokenizer;
  }
}
