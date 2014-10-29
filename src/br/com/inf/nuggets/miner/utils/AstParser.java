package br.com.inf.nuggets.miner.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;


public class AstParser {
	
	public static void main(String[] args) {
		StringBuilder str = new StringBuilder();
		str.append("import java.util.*; \n");
		str.append("public class BeerTaste { \n");
		str.append("public static void main(String args[]) { \n");
		str.append("Has<String> m = new HashSet<String>(); \n");
		str.append("m.add(\"beer\"); \n");
		str.append("m.put(\"bad\"); \n");
		str.append("System.out.println(m.size()); \n");
		str.append("} }");
	
		System.out.println("" + extractFacts(str));	
	} 
	
	public static String extractFacts(StringBuilder str){
		
		System.out.println("AstParser string = " + str.toString());
		final StringBuilder variableDeclarationFacts = new StringBuilder();
		final StringBuilder methodDeclarationFacts = new StringBuilder();
		final StringBuilder methodInvocationFacts = new StringBuilder();
		final StringBuilder usageFacts = new StringBuilder();

		try {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(str.toString().toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			
			IProgressMonitor monitor = new NullProgressMonitor();
			
			final CompilationUnit cu = (CompilationUnit) parser.createAST(monitor);
			
			cu.accept(new ASTVisitor() {
				
				Set<String> names = new HashSet<String>();
				
				public boolean visit(VariableDeclarationFragment declaration) {
					SimpleName name = declaration.getName();
					
					FieldDeclaration fieldDeclaration = null;
					Type type = null;
					String instantiation = null;
					if (declaration.getParent() instanceof FieldDeclaration) {
						fieldDeclaration = (FieldDeclaration) declaration.getParent();
						type = fieldDeclaration.getType();

						variableDeclarationFacts.append("FieldDeclaration: " + type.toString()+" " +
								"at line " +cu.getLineNumber(name.getStartPosition()));
						variableDeclarationFacts.append("\n");
					} else {
						VariableDeclarationStatement variableDeclaration = null;
						if (declaration.getParent() instanceof VariableDeclarationStatement) {
							
							variableDeclaration = (VariableDeclarationStatement) declaration.getParent();
							type = variableDeclaration.getType();
							
							instantiation = variableDeclaration.toString().split("=")[1].replace("new", "")
									.replace(";", "").trim();
							
							variableDeclarationFacts.append("VariableDeclaration: " + type.toString()+ " of " +
							        instantiation + " at line " +cu.getLineNumber(name.getStartPosition()));
							variableDeclarationFacts.append("\n");
						}
					}
					this.names.add(name.getIdentifier());
					return true; // do not continue to avoid usage info
				}
				
				public boolean visit(final MethodDeclaration declaration) {
					SimpleName name = declaration.getName();
	
					List<String> parameters = new ArrayList<String>();
	                for (Object parameter : declaration.parameters()) {
	                    VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
	                    String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY)
	                            .toString();
	                    for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
	                        type += "()";
	                    }
	                    parameters.add(type);
	                }
	                
	               Type returnType = declaration.getReturnType2();
	               String returnTypeStr = "";
	               if (returnType == null) {
	            	   returnTypeStr = "void";
	               } else {
	            	   returnTypeStr = declaration.getReturnType2().toString();
	               }
	                
	                methodDeclarationFacts.append("MethodDeclaration: " + returnTypeStr + " " + name.toString() + parameters);
	                methodDeclarationFacts.append(" at line " +cu.getLineNumber(name.getStartPosition()));
	                methodDeclarationFacts.append("\n");

	                return true;
	            }
					
				public boolean visit(final MethodInvocation invocation) {
					SimpleName name = invocation.getName();
					
					methodInvocationFacts.append("MethodInvocation: " + name + " " + 
							"at line " +cu.getLineNumber(name.getStartPosition()));
					
					methodInvocationFacts.append("\n");
					
					return true;
				}
				
				/*public boolean visit(SimpleName node) {
					
					int nodeType = node.getNodeType();
					if (this.names.contains(node.getIdentifier())) {
						usageFacts.append("Usage of " + node.getIdentifier() + " " + 
								"at line " +cu.getLineNumber(node.getStartPosition()));
						usageFacts.append("\n");
					}
					return true;
				}*/
				
			});
		} catch (IllegalArgumentException e) {
			return "IllegalArgumentException";
		}
		
		String response = variableDeclarationFacts.toString() + 
				methodDeclarationFacts.toString() + 
				methodInvocationFacts.toString() +
				usageFacts.toString();
		
		return response.replaceAll("&#x0;", "");
	}
}