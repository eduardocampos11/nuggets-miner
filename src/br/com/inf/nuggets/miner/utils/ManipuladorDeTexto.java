/* 
 * Classe responsavel por fazer transformacoes no texto, como remover stop words, aplicar stemming e remover tags. 
 */
package br.com.inf.nuggets.miner.utils;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class ManipuladorDeTexto extends Analyzer {  
    //Set com os stop words stemmizados
	private Set stopWords;  
    //private final boolean usePorterStemming;  
//	private Tokenizer tokenizer;
//	private POSTagger posTagger;
//	private ChunkerME chunkerME;
//	private SentenceDetectorME sdetector;
  
    public ManipuladorDeTexto() {  
       try{
    	 //Ate o memento tenho 4 opcoes de lista de stop words
           
           //http://snowball.tartarus.org/algorithms/english/stop.txt
           //String stopWordsVet[] = {"i","me","my","myself","we","us","our","ours","ourselves","you","your","yours","yourself","yourselves","he","him","his","himself","she","her","hers","herself","it","its","itself","they","them","their","theirs","themselves","what","which","who","whom","this","that","these","those","am","is","are","was","were","be","been","being","have","has","had","having","do","does","did","doing","would","should","could","ought","i'm","you're","he's","she's","it's","we're","they're","i've","you've","we've","they've","i'd","you'd","he'd","she'd","we'd","they'd","i'll","you'll","he'll","she'll","we'll","they'll","isn't","aren't","wasn't","weren't","hasn't","haven't","hadn't","doesn't","don't","didn't","won't","wouldn't","shan't","shouldn't","can't","cannot","couldn't","mustn't","let's","that's","who's","what's","here's","there's","when's","where's","why's","how's","a","an","the","and","but","if","or","because","as","until","while","of","at","by","for","with","about","against","between","into","through","during","before","after","above","below","to","from","up","down","in","out","on","off","over","under","again","further","then","once","here","there","when","where","why","how","all","any","both","each","few","more","most","other","some","such","no","nor","not","only","own","same","so","than","too","very"};
           
           //R-language
           //String stopWordsVet[] = {"a","about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","am","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","aren't","around","as","ask","asked","asking","asks","at","away","b","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","below","best","better","between","big","both","but","by","c","came","can","cannot","can't","case","cases","certain","certainly","clear","clearly","come","could","couldn't","d","did","didn't","differ","different","differently","do","does","doesn't","doing","done","don't","down","downed","downing","downs","during","e","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","h","had","hadn't","has","hasn't","have","haven't","having","he","he'd","he'll","her","here","here's","hers","herself","he's","high","higher","highest","him","himself","his","how","however","how's","i","i'd","if","i'll","i'm","important","in","interest","interested","interesting","interests","into","is","isn't","it","its","it's","itself","i've","j","just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest","least","less","let","lets","let's","like","likely","long","longer","longest","m","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","mustn't","my","myself","n","necessary","need","needed","needing","needs","never","new","newer","newest","next","no","nobody","non","noone","nor","not","nothing","now","nowhere","number","numbers","o","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","ought","our","ours","ourselves","out","over","own","p","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","shan't","she","she'd","she'll","she's","should","shouldn't","show","showed","showing","shows","side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere","state","states","still","such","sure","t","take","taken","than","that","that's","the","their","theirs","them","themselves","then","there","therefore","there's","these","they","they'd","they'll","they're","they've","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants","was","wasn't","way","ways","we","we'd","well","we'll","wells","went","were","we're","weren't","we've","what","what's","when","when's","where","where's","whether","which","while","who","whole","whom","who's","whose","why","why's","will","with","within","without","won't","work","worked","working","works","would","wouldn't","x","y","year","years","yes","yet","you","you'd","you'll","young","younger","youngest","your","you're","yours","yourself","yourselves","you've","z"};
           
           //Poshyvanic
           //String stopWordsVet[] = {"a","about","above","accordingly","across","after","afterwards","again","against","all","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anywhere","apart","appear","appropriate","are","around","as","aside","associated","at","available","away","awfully","b","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cause","causes","certain","changes","co","come","consequently","contain","containing","contains","corresponding","could","currently","d","day","described","did","different","do","does","doing","done","down","downwards","during","e","each","eg","eight","either","else","elsewhere","enough","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","example","except","f","far","few","fifth","first","five","followed","following","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","given","gives","go","gone","good","got","great","h","had","hardly","has","have","having","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","him","himself","his","hither","how","howbeit","however","i","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","it","its","itself","j","just","k","keep","kept","know","l","last","latter","latterly","least","less","lest","life","like","little","long","ltd","m","made","make","man","many","may","me","meanwhile","men","might","more","moreover","most","mostly","mr","much","must","my","myself","n","name","namely","near","necessary","neither","never","nevertheless","new","next","nine","no","nobody","none","noone","nor","normally","not","nothing","novel","now","nowhere","o","of","off","often","oh","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","people","per","perhaps","placed","please","plus","possible","probably","provides","q","que","quite","r","rather","really","relatively","respectively","right","s","said","same","second","secondly","see","seem","seemed","seeming","seems","self","selves","sensible","sent","serious","seven","several","shall","she","should","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","specified","specify","specifying","state","still","sub","such","sup","t","take","taken","than","that","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","time","to","together","too","toward","towards","twice","two","u","under","unless","until","unto","up","upon","us","use","used","useful","uses","using","usually","v","value","various","very","via","viz","vs","w","was","way","we","well","went","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","with","within","without","work","world","would","x","y","year","years","yet","you","your","yours","yourself","yourselves","z","zero"};
           
           //Poshyvanic + palavras que contem ` do snowball
           String stopWordsVet[] = {"a","about","above","accordingly","across","after","afterwards","again","against","all","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anywhere","apart","appear","appropriate","are","around","as","aside","associated","at","available","away","awfully","b","back","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cause","causes","certain",/*"changes",*/"co","come","consequently","contain","containing","contains","corresponding","could","currently","d","day","described","did","different","do","does","doing","done","down","downwards","during","e","each","eg","eight","either","else","elsewhere","enough","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","example","except","f","far","few","fifth","first","five","followed","following","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","given","gives","go","gone","good","got","great","h","had","hardly","has","have","having","he","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","him","himself","his","hither","how","howbeit","however","i","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","it","its","itself","j","just","k","keep","kept","know","l","last","latter","latterly","least","less","lest","life","like","little","long","ltd","m","made","make","man","many","may","me","meanwhile","men","might","more","moreover","most","mostly","mr","much","must","my","myself","n","name","namely","near","necessary","neither","never","nevertheless","new","next","nine","no","nobody","none","noone","nor","normally","not","nothing","novel","now","nowhere","o","of","off","often","oh","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","people","per","perhaps","placed","please","plus","possible","probably","provides","q","que","quite","r","rather","really","relatively","respectively","right","s","said","same","second","secondly","see","seem","seemed","seeming","seems","self","selves","sensible","sent","serious","seven","several","shall","she","should","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","specified","specify","specifying","state","still","sub","such","sup","t","take","taken","than","that","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","thereupon","these","they","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","time","to","together","too","toward","towards","twice","two","u","under","unless","until","unto","up","upon","us","use","used","useful","uses","using","usually","v","value","various","very","via","viz","vs","w","was","way","we","well","went","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","with","within","without","work","world","would","x","y","year","years","yet","you","your","yours","yourself","yourselves","z","zero","i'm","you're","he's","she's","it's","we're","they're","i've","you've","we've","they've","i'd","you'd","he'd","she'd","we'd","they'd","i'll","you'll","he'll","she'll","we'll","they'll","isn't","aren't","wasn't","weren't","hasn't","haven't","hadn't","doesn't","don't","didn't","won't","wouldn't","shan't","shouldn't","can't","couldn't","mustn't","let's","that's","who's","what's","here's","there's","when's","where's","why's","how's"};
           
           List<String> words = new ArrayList();
           for(int i=0; i< stopWordsVet.length; i++){
           	if(!stopWordsVet[i].equals(""))
           		words.add(stopWordsVet[i]);
       	}
           
           //Vamos fazer o stemming de todos os termos da lista de stop words
           words = stemmizaListaDePalavras(words);
           
        //vamos adiacionar no conjunto de stop words, os stems que foram considerados genericos a partir da analise dos topicos dos cookbooks 
        String stopStemsFromCookbookAnalysis[] = {"actual","answer","app","applic","approach","code","develop","help","implement","need","program","project","question","requir","respons","solut","sourc","suggest","sure","system","thank","thing","think","understand","user","want","algorithm","class","execut","function","main","method","object","problem","result","set","return","run","note"};
        for(int i=0; i< stopStemsFromCookbookAnalysis.length; i++){
        	words.add(stopStemsFromCookbookAnalysis[i]);
        }
        
       	stopWords = StopFilter.makeStopSet(Version.LUCENE_36, words, true);
       	
//       	TokenizerModel tokenModel = new TokenizerModel(new FileInputStream("/media/HDii-2x3Tb/Lucas/lucas2/workspace2/ProjetoMestrado/libs/opennlp/models/en-token.bin"));
//   		tokenizer = new TokenizerME(tokenModel);
//   		
//   		POSModel posModel = new POSModel(new FileInputStream("/media/HDii-2x3Tb/Lucas/lucas2/workspace2/ProjetoMestrado/libs/opennlp/models/en-pos-maxent.bin"));
//		posTagger = new POSTaggerME(posModel);
//		
//		ChunkerModel cModel = new ChunkerModel(new FileInputStream("/media/HDii-2x3Tb/Lucas/lucas2/workspace2/ProjetoMestrado/libs/opennlp/models/en-chunker.bin"));
//		chunkerME = new ChunkerME(cModel);
//		
//		SentenceModel model = new SentenceModel(new FileInputStream("/media/HDii-2x3Tb/Lucas/lucas2/workspace2/ProjetoMestrado/libs/opennlp/models/en-sent.bin"));
//		sdetector = new SentenceDetectorME(model);
   	
       }catch(Exception e){
    	   e.printStackTrace();
       }
        
    		
		
    }  
  
    //Metodo para stemmizar os termos da lista de stop words
    public List<String> stemmizaListaDePalavras(List<String> words) {
    	List<String> listaStemmizada = new ArrayList<String>();

    	for(int i=0; i <  words.size(); i++)
    		listaStemmizada.add(stemmizaPalavra(words.get(i)));
    	return listaStemmizada;
    }

	public TokenStream tokenStream(String fieldName, Reader reader) {  
    	
    	TokenStream result = new StandardTokenizer(Version.LUCENE_36, reader);
    	result = new StandardFilter(Version.LUCENE_36, result);
    	result = new LowerCaseFilter(Version.LUCENE_36, result); 
    	result = new PorterStemFilter(result);   
        //Vamos remover stop words depois do stemming, pois o set de stowords contem o stemming das stop words
    	result = new StopFilter(Version.LUCENE_36, result, stopWords);
        return result;  
    }  
    
    public String removeStopWordsEFazStemming(String texto){
    	try{
    		StringReader tReader = new StringReader(texto);
    		TokenStream tStream = tokenStream("contents", tReader);
    		
    		CharTermAttribute charTermAttribute = tStream.addAttribute(CharTermAttribute.class);
		    String saida = "";
    		while(tStream.incrementToken()){
    			//Vamos desconsiderar tokens que contem apenas numeros
    			//:TODO por enquanto estemos removendo numeros desta forma, mas a forma correta eh modificar a gramtica (jfex) para nao gerar esses numeros 
    			String token = charTermAttribute.toString().toString();
    			
    			if(!contemApenasNumeros(token)){
    				
    				//:TODO isto nao esta da melhor forma possivel (o ideal eh modificar o tokenizer)
    				//Caso o termo termine com ' (ex. eclipse'), vamos remover o ', stemmizar o termo resultante
    				//e caso ele nao seja stop word, ele sera considerado na saida
    				//ex: work` -> work -> como eh stop word, nao eh considerado
    				if(token.lastIndexOf('\'') ==  token.length()-1){
    					token = token.substring(0, token.length()-1);
    					token = stemmizaPalavra(token);
    					if(stopWords.contains(token))
    						continue;
    				}
    					
    				saida += token + "\n";
    			}
    		}
    		return saida;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
  }
    
    //Metodo responsavel por stemmizar apenas um termo
    private String stemmizaPalavra(String palavra) {
		try{
			StringReader tReader = new StringReader(palavra);
			
			TokenStream result = new StandardTokenizer(Version.LUCENE_36, tReader);
			result = new StandardFilter(Version.LUCENE_36, result);
	    	result = new LowerCaseFilter(Version.LUCENE_36, result); 
	    	result = new PorterStemFilter(result);   
	    	
	    	CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);
		    String saida = "";
			while(result.incrementToken()){
				//Vamos desconsiderar tokens que contem apenas numeros
				String token = charTermAttribute.toString().toString();
				return  token;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
    }

	public boolean contemApenasNumeros(String str) {

    	//It can't contain only numbers if it's null or empty...
    	if (str == null || str.length() == 0)
    		return false;

    	for (int i = 0; i < str.length(); i++) {

    		//If we find a non-digit character we return false.
    		if (!Character.isDigit(str.charAt(i)))
    			return false;
    	}

    	return true;
    }
    
	//metodo resposavel por elimnar do texto tags indesejadas e seu conteudo (ex code, links), e retornar apenas o conteudo do post (sem tags) 
	public String removeTagsIndesejadas(String textodoc, Boolean consideraCodigo) {
		
		try{
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean( textodoc );
			  
			//System.out.println(root.getText());
			  
			//Remove as tags "code" do documento HTML
			if(!consideraCodigo)  {
				Object[] codeNodes = root.evaluateXPath( "//code" );
				  for(int i=0; i<codeNodes.length; i++){
					  ((TagNode)codeNodes[i]).removeFromTree();
				}
			}
			
			//Por enquanto, as tags referentes a links sempre NAO serao removidas 
			//Remove as tags "a" (links) do documento HTML
			/*Object[] linkNodes = root.evaluateXPath( "//a" );
			for(int i=0; i<linkNodes.length; i++){
			 ((TagNode)linkNodes[i]).removeFromTree();
			}*/
			
			return root.getText().toString();
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		 
		 return null;
	}
	
	//Metodo responsavel que a partir de uma string, monta uma lista com os tokens existentes na string
	public List<String> recuperaTokens(String texto){
		
		List<String> listaTokens = new ArrayList<String>();
		try{
			StringReader reader = new StringReader(texto);
			
			//Nao sera chamado o metodo tokenStream, pois nesse metodo queremos apenas pegar os tokens, nao
			//precisamos fazer remocao de stop words
			//TokenStream tStream = tokenStream("contents", tReader);
			
			TokenStream result = new StandardTokenizer(Version.LUCENE_36, reader);
	    	result = new StandardFilter(Version.LUCENE_36, result);
	    	result = new LowerCaseFilter(Version.LUCENE_36, result); 
	    	result = new PorterStemFilter(result);   
			
			CharTermAttribute charTermAttribute = result.addAttribute(CharTermAttribute.class);
		    while(result.incrementToken()){
				//Vamos desconsiderar tokens que contem apenas numeros
				//:TODO por enquanto estemos removendo numeros desta forma, mas a forma correta eh modificar a gramtica (jfex) para nao gerar esses numeros 
				listaTokens.add(charTermAttribute.toString().toString());
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return listaTokens;
	}
	
	//metodo responsavel por pegar o centeudo de todas as tags "code" do corpo de um post
	public String getCodigo(String texto){
		String textoCodigos = "";
		try{
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean( texto );
			
			Object[] codeNodes = root.evaluateXPath( "//code" );
			for(int i=0; i<codeNodes.length; i++){

				textoCodigos += ((TagNode)codeNodes[i]).getText() + "\n";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return textoCodigos;
	}
	
	//metodo responsavel por pegar o conteudo de todas as tags "pre/code" do corpo de um post
	public String getCodigoPreCode(String texto) {
		String textoCodigos = "";
		try{
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean( texto );
			
			Object[] codeNodes = root.evaluateXPath( "//pre/code" );
			for(int i=0; i<codeNodes.length; i++){

				textoCodigos += ((TagNode)codeNodes[i]).getText();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return textoCodigos;
	}
	
	//metodo responsavel por descobir o numero de trechos de codigo existente num post
	public int getNumeroDeTrechosDeCodigo(String texto){
		try{
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode root = cleaner.clean( texto );
			
			Object[] codeNodes = root.evaluateXPath( "//pre/code" );
			return codeNodes.length;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//return textoCodigos;
		return 0;
	}

	//metodo responsavel por aplicar chunking sobre o texto da thread e devolver um texto contendo os chunks que nao sao NP (Noun Phrase)
	public String aplicaChunking(String texto) {
		String sentencas[] = DetectaSetencas(texto);
		String strResultado = "";
		try{
			for(int w=0; w< sentencas.length; w++){
				String input = sentencas[w];
				String tokens[] = null;
				String tags[] = null;
				
//				tokens = tokenizer.tokenize(input);
				
				for(int p=0; p< tokens.length; p++){
					if(tokens[p].equals("n't"))
						tokens[p] = "not";
					else if(tokens[p].equals("'s"))
						tokens[p] = "is";
					else if(tokens[p].equals("'re"))
						tokens[p] = "are";
					else if(tokens[p].equals("'d"))
						tokens[p] = "would";
					else if(tokens[p].equals("'ve"))
						tokens[p] = "have";
					else if(tokens[p].equals("'m"))
						tokens[p] = "am";
					
				}
				
				
//				tags = posTagger.tag(tokens);
//				String tag[] = chunkerME.chunk(tokens, tags);
//				Span[] span = (Span[]) chunkerME.chunkAsSpans(tokens, tags);
//				double probabilidades[] = chunkerME.probs();
				
				/*for(int i=0; i<tokens.length; i++){
					if(tokens[i].equals("(")&& !tags[i].equals("-LRB-")){
						int a =0;
						int b=a;
					}
						
				}*/
				
				//System.out.println();
				
//				for (Span s : span){
//					if(s.getType().equals("VP") /*|| s.getType().equals("ADJP") || s.getType().equals("ADVP")*/){
//						
//						String expressao = "";
//						for(int i= s.getStart(); i< s.getEnd(); i++){
//							
//							if(probabilidades[i] < 0.7){
//								int a = 0;
//								int b = a;
//								continue;
//							}
//							
//							expressao += tokens[i];
//							/*if(tokens[i].equals("(")){
//								int a =0;
//								int b=a;
//								for(int w=0; w< tags.length;w++){
//									System.out.println(tokens[w] + " " + tags[w]);
//								}
//								
//							}*/
//								
//							
//							if(i != s.getEnd()-1)
//								expressao += "_";
//						}
//						
//						if(expressao.endsWith("_"))
//							expressao = expressao.substring(0, expressao.length()-1);
//						strResultado += expressao + "\n";
//					}
//				}
			}

			
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return strResultado;
	}
	
	public String[] DetectaSetencas(String texto){
		String sentences[] = null;
		try{
//			sentences = sdetector.sentDetect(texto);
			return sentences;
		}catch(Exception e){
			e.printStackTrace();
		}
		return sentences;
	}
} 