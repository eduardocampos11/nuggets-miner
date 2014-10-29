package br.com.inf.nuggets.miner.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeParser {
	//JAVA: DECALARA플O OU CHAMADA DE METODO
	String strMethodDeclarationJavaMethod = "(\\w+) *\\([^\\)]*\\)";
	Pattern patternJavaMethods = Pattern.compile(strMethodDeclarationJavaMethod);
	
	//JAVA: DECALARA플O DE CLASSE 
	String strClassDeclarationJavaClazz = ".*class\\s+(\\w+)(\\s+extends\\s+(\\w+))?(\\s+implements\\s+([\\w,\\s]+))?";
	Pattern patternJavaClazz = Pattern.compile(strClassDeclarationJavaClazz);
	
	//JAVA: DECLARA플O DE INTERFACE
	String strInterfaceDeclaration = ".*interface\\s+(\\w+)(\\s+extends\\s+(\\w+))?(\\s+implements\\s+([\\w,\\s]+))?";
	Pattern patternJavaInterface = Pattern.compile(strInterfaceDeclaration);
	
	//C++: DECALARA플O OU CHAMADA DE METODO
    String strMethodDeclarationCPlusplusMethod = "(\\w+) *\\([^\\)]*\\)";
    Pattern patternCPlusPlusMethods = Pattern.compile(strMethodDeclarationCPlusplusMethod);
    
    //C++: DECLARA플O DE CLASSE 
    String strClassDeclarationCPlusplusClass = "class\\s+(\\w+)\\s*[:]?";
    Pattern patternCPlusPlusClazz = Pattern.compile(strClassDeclarationCPlusplusClass);
    
    //C++: DECLARA플O DE STRUCT
    String strStructDeclarationCPlusPlus = "struct\\s+(\\w+)\\s*[:]?";
    Pattern patternCPlusPlusStruct = Pattern.compile(strStructDeclarationCPlusPlus);
    
    public List<String> getJavaMethods(String[] codeLinesVet) {
    	List<String> javaMethodsLst = new ArrayList<String>();
    	String javaMethod = null;
    	Matcher methodDeclarationMatcher = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		methodDeclarationMatcher = patternJavaMethods.matcher(codeLinesVet[i]);
    		if (methodDeclarationMatcher.find()) {
        		//int groupCount = methodDeclarationMatcher.groupCount();
    			javaMethod = methodDeclarationMatcher.group(1);
    			javaMethodsLst.add(javaMethod);
//        		System.out.println("Found Java method: " + javaMethod);
            }
    	}
    	return javaMethodsLst;
    }
    
    public List<String> getJavaClasses(String[] codeLinesVet) {
    	List<String> javaClassesLst = new ArrayList<String>();
    	String javaClazz = null;
    	Matcher clazzDeclarationMatcher = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		clazzDeclarationMatcher = patternJavaClazz.matcher(codeLinesVet[i]);
    		if (clazzDeclarationMatcher.find()) {
    			javaClazz = clazzDeclarationMatcher.group(1);
    			javaClassesLst.add(javaClazz);
        		System.out.println("Found Java Class: " + javaClazz);
            }
    	}
    	return javaClassesLst;
    }
    
    public List<String> getJavaInterfaces(String[] codeLinesVet) {
    	List<String> javaInterfacesLst = new ArrayList<String>();
    	String javaInterface = null;
    	Matcher interfaceDeclarationMacther = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		interfaceDeclarationMacther = patternJavaInterface.matcher(codeLinesVet[i]);
    		if (interfaceDeclarationMacther.find()) {
    			javaInterface = interfaceDeclarationMacther.group(1);
    			javaInterfacesLst.add(javaInterface);
    			System.out.println("Found Java Interface: " + javaInterface);
    		}	
    	}
    	return javaInterfacesLst;
    }
    
    public List<String> getCPlusPlusMethods(String[] codeLinesVet) {
    	List<String> cPlusPlusMethodsLst = new ArrayList<String>();
    	String cPlusPlusMethod = null;
    	Matcher methodDeclarationMatcher = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		methodDeclarationMatcher = patternCPlusPlusMethods.matcher(codeLinesVet[i]);
    		if(methodDeclarationMatcher.find()){
        		//int groupCount = methodDeclarationMatcher.groupCount();
    			cPlusPlusMethod = methodDeclarationMatcher.group(1);
    			cPlusPlusMethodsLst.add(cPlusPlusMethod);
        		System.out.println("Found C++ method: " + cPlusPlusMethod);
            }
    	}
    	return cPlusPlusMethodsLst;
    }
    
    public List<String> getCPlusPlusClasses(String[] codeLinesVet) {
    	List<String> cPlusPlusClassesLst = new ArrayList<String>();
    	String cPlusPlusClazz = null;
    	Matcher clazzDeclarationMatcher = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		clazzDeclarationMatcher = patternCPlusPlusClazz.matcher(codeLinesVet[i]);
    		if (clazzDeclarationMatcher.find()){
    			cPlusPlusClazz = clazzDeclarationMatcher.group(1);
    			cPlusPlusClassesLst.add(cPlusPlusClazz);
        		System.out.println("Found C++ Class: " + cPlusPlusClazz);
            }
    	}
    	return cPlusPlusClassesLst;
    }
    
    public List<String> getCPlusPlusStruct(String[] codeLinesVet) {
    	List<String> cPlusPlusStructLst = new ArrayList<String>();
    	String cPlusPlusStruct = null;
    	Matcher structDeclarationMatcher = null;
    	for (int i = 0; i < codeLinesVet.length; i++) {
    		structDeclarationMatcher = patternCPlusPlusStruct.matcher(codeLinesVet[i]);
    		if (structDeclarationMatcher.find()) {
    			cPlusPlusStruct = structDeclarationMatcher.group(1);
    			cPlusPlusStructLst.add(cPlusPlusStruct);
    			System.out.println("Found C++ Struct: " + cPlusPlusStruct);
    		}
    	}
    	return cPlusPlusStructLst;
    }
    
    public static String splitCamelCase(String s) {
        String newString = "";
        if(s.contains("_")){
            String terms[] = s.split("_");
            for(int i=0; i< terms.length; i++){
               
                if(i != (terms.length - 1))
                    newString += splitCamelCase(terms[i]) + "\n";
                else
                    newString += splitCamelCase(terms[i]);
            }
        }
        else{
            newString =    s.replaceAll(
                      String.format("%s|%s|%s",
                                 "(?<=[A-Z])(?=[A-Z][a-z])",
                                 "(?<=[^A-Z])(?=[A-Z])",
                                 "(?<=[A-Za-z])(?=[^A-Za-z])"
                              ),
                              " "
                           );
            newString = newString.replace("$", "");
            newString = newString.replace("0", "");
            newString = newString.replace("1", "");
            newString = newString.replace("2", "");
            newString = newString.replace("3", "");
            newString = newString.replace("4", "");
            newString = newString.replace("5", "");
            newString = newString.replace("6", "");
            newString = newString.replace("7", "");
            newString = newString.replace("8", "");
            newString = newString.replace("9", "");
        }
       
        newString = newString.toLowerCase();
        return newString;
    }  
    
    public StringBuilder getFormattedCode(String code) {
		String[] codeLines = getCodeLinesVet(code);
		StringBuilder str = new StringBuilder();
		for (int j = 0; j < codeLines.length; j++) {
			if (codeLines[j].contains("&lt;") || codeLines[j].contains("&gt;")) {
				continue;
			} else {
				str.append(codeLines[j]).append("\n");				
			}
		}
		return str;
	}
    
    public String[] getCodeLinesVet(String code) {
		String[] codeLines = null;
		try {
          codeLines = code.split("\r\n|\r|\n");
		} catch(Exception e) {
          e.printStackTrace();
		}
		return codeLines;
	}
    
}
