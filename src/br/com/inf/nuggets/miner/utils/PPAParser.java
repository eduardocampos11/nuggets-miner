package br.com.inf.nuggets.miner.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import ca.mcgill.cs.swevo.ppa.PPAOptions;
import ca.mcgill.cs.swevo.ppa.SnippetUtil;
import ca.mcgill.cs.swevo.ppa.ui.PPAUtil;

public class PPAParser {
	
	private static final String PPA_FILE_PATH = "C:\\Users\\online\\workspace\\NuggetsMiner\\ppa\\";

	public static String extractPPAFacts(String snippet) {
		Map<String, String> map = new HashMap<String, String>();
		CompilationUnit cu = null;
		String snippetId = snippet.length() + "";
		CodeParser codeParser = new CodeParser();
		List<String> javaClazzLst = null;
		String formattedCode = null;
		
		javaClazzLst = codeParser.getJavaClasses(getCodeLinesVet(snippet));
		formattedCode = getFormattedCode(snippet);
		
		File file = null;
		if (javaClazzLst != null && !javaClazzLst.isEmpty()) {
			file = createNewFile(formattedCode, snippetId);
		} else {
			StringBuilder builder = new StringBuilder();
			String clazzNoName = "public class NoName {\n";
			builder.append(clazzNoName);
			builder.append(formattedCode);
			builder.append("\n}");
			
			file = createNewFile(builder.toString(), snippetId);
		}

		if (file != null) {
			try {
				cu = PPAUtil.getCU(file, new PPAOptions());			
			} catch (Exception e) {
				map.put(snippetId, "");
			}
			if (cu != null) {
				PPATypeVisitor visitor = new PPATypeVisitor(snippetId, map);
				cu.accept(visitor);			
			}
		}
		String facts = String.valueOf(map.get(snippetId));
	    return facts;
	}
	
	public static File createNewFile(String content, String id) {
		File file = null;
		try {
			file = new File(PPA_FILE_PATH + SnippetUtil.SNIPPET_CLASS + "_" + id + ".java");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(HtmlToPlainText.unescapeHTML(content, 0));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static String getFormattedCode(String code) {
		String[] codeLines = getCodeLinesVet(code);
		StringBuilder str = new StringBuilder();
		for (int j = 0; j < codeLines.length; j++) {
			if (codeLines[j].contains("</")) {
				continue;
			} else {
				str.append(codeLines[j]).append("\n");				
			}
		}
		return str.toString();
	}
	
	public static String[] getCodeLinesVet(String code) {
		String[] codeLines = null;
		try{
            codeLines = code.split("\r\n|\r|\n");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return codeLines;
    }
}
