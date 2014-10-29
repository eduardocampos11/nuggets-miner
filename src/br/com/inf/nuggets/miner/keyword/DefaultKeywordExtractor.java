package br.com.inf.nuggets.miner.keyword;

import br.com.inf.nuggets.miner.keyword.filter.CamelCaseFilter;
import br.com.inf.nuggets.miner.keyword.filter.DigitsFilter;
import br.com.inf.nuggets.miner.keyword.filter.IFilter;
import br.com.inf.nuggets.miner.keyword.filter.LowerCaseFilter;
import br.com.inf.nuggets.miner.keyword.filter.RemoveOnLengthFilter;
import br.com.inf.nuggets.miner.keyword.filter.StopWordFilter;
import br.com.inf.nuggets.miner.keyword.filter.SymbolsFilter;
import br.com.inf.nuggets.miner.keyword.filter.TrimFilter;
import br.com.inf.nuggets.miner.preferences.NuggetsMinerPreferencesConstant;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public final class DefaultKeywordExtractor
  extends CodeKeywordsExtractor
{
  public DefaultKeywordExtractor()
    throws JavaModelException, IOException, URISyntaxException
  {
    setFilters(new IFilter[] { new CamelCaseFilter(true), 
      new DigitsFilter(false), 
      new SymbolsFilter(false), 
      new TrimFilter(), 
      new LowerCaseFilter(), 
      new StopWordFilter(getStopWordsFile(), true, true), 
      new RemoveOnLengthFilter(2) });
  }
  
  private File getStopWordsFile()
    throws URISyntaxException, IOException
  {
    Bundle b = Platform.getBundle(NuggetsMinerPreferencesConstant.BUNDLE);
    URL url = b.getEntry("files/stopwords.txt");
    File resFile = new File(FileLocator.toFileURL(url).toURI());
    return resFile;
  }
}
