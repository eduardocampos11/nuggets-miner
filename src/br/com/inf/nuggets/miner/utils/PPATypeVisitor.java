package br.com.inf.nuggets.miner.utils;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PPABindingsUtil;

public class PPATypeVisitor extends ASTVisitor {

	private String snippetId;
	private Map<String, String> map;
	
	public PPATypeVisitor(String snippetId, Map<String, String> ppaMap) {
		super();
		this.snippetId = snippetId;
		this.map = ppaMap;
	}

	@Override
	public void postVisit(ASTNode node) {
		super.postVisit(node);

		if (node instanceof Expression) {
			Expression exp = (Expression) node;

			IBinding binding = null;
			if (exp instanceof Name) {
				Name name = (Name) exp;
				binding = name.resolveBinding();
			} else if (exp instanceof MethodInvocation) {
				MethodInvocation mi = (MethodInvocation) exp;
				binding = mi.resolveMethodBinding();
			} else if (exp instanceof ClassInstanceCreation) {
				ClassInstanceCreation cic = (ClassInstanceCreation) exp;
				binding = cic.resolveConstructorBinding();
			} else {
				return;
			}
			
			StringBuilder facts = new StringBuilder();
//			printer.println("Node " + node.toString());
			facts.append("Node " + node.toString());
			
			ITypeBinding tBinding = exp.resolveTypeBinding();
			if (tBinding != null) {
//				printer.println("  Type Binding " + tBinding.getQualifiedName());
				facts.append("  Type Binding " + tBinding.getQualifiedName());
			} 

			if (binding != null) {
//				printer.println("  " + getBindingText(binding));
				facts.append("  " + getBindingText(binding));
			} 
//			printer.flush();
			
			String currentFact = "";
			if (map.containsKey(snippetId)) {				
				currentFact = unescapeSpecialCharacters(facts.toString());
				currentFact = getStringValue(snippetId, map) + "\n" + currentFact;
				map.put(snippetId, currentFact);
			} else {
				currentFact = unescapeSpecialCharacters(facts.toString());
				map.put(snippetId, currentFact);
			}
		}
	}
	
	private String getStringValue(String key, Map<String, String> map) {
		return map.get(key) != null ? String.valueOf(map.get(key)) : "";
 	}
	
	private String unescapeSpecialCharacters(String query) {
		  String response = query;
		  response = response.replaceAll("\\+", " ");
		  response = response.replaceAll("\\-", " ");
		  response = response.replaceAll("\\&&", "");
		  response = response.replaceAll("\\||", "");
		  response = response.replaceAll("\\!", "");
		  response = response.replaceAll("\\(", " ");
		  response = response.replaceAll("\\)", " ");
		  response = response.replaceAll("\\{", "");
		  response = response.replaceAll("\\}", "");
		  response = response.replaceAll("\\[", "");
		  response = response.replaceAll("\\]", "");
		  response = response.replaceAll("\\^", "");
		  response = response.replaceAll("\\~", "");
		  response = response.replaceAll("\\*", " ");
		  response = response.replaceAll("\\?", "");
		  response = response.replaceAll("<EOF>", "");
		  response = response.replaceAll("\\/", " ");
		  response = response.replaceAll("\\:", " ");
		 
		  return response;
	  }
	
	public static String getBindingText(IBinding binding) {
		String bindingText = "";
		if (binding != null) {
			if (binding instanceof IMethodBinding) {
				IMethodBinding mBinding = (IMethodBinding) binding;
				bindingText = "MBinding " + PPABindingsUtil.getFullMethodSignature(mBinding);
			} else if (binding instanceof IVariableBinding) {
				IVariableBinding vBinding = (IVariableBinding) binding;
				if (vBinding.isField()) {
					String type = "nil";
					if (vBinding.getType() != null) {
						type = getTypeString(vBinding.getType());
					}

					String decType = "nil";
					if (vBinding.getDeclaringClass() != null) {
						decType = getTypeString(vBinding.getDeclaringClass());
					}
					bindingText = "FBinding " + type + " " + decType + ":" + vBinding.getName();
				} else {
					String type = "nil";
					if (vBinding.getType() != null) {
						type = getTypeString(vBinding.getType());
					}
					bindingText = "VBinding " + type + " " + vBinding.getName();
				}
			} else if (binding instanceof ITypeBinding) {
				ITypeBinding typeBinding = (ITypeBinding) binding;
				bindingText = "TBinding " + typeBinding.getName();
			} else if (binding instanceof IPackageBinding) {
				IPackageBinding pBinding = (IPackageBinding) binding;
				bindingText = "PBinding " + pBinding.getName();
			}
		}
		return bindingText;
	}
	
	public static String getTypeString(ITypeBinding binding) {
		if (binding == null) {
			return "nil";
		} else {
			return binding.getQualifiedName();
		}
	}
}
