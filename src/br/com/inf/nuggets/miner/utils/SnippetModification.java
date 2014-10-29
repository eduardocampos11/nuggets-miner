package br.com.inf.nuggets.miner.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetModification implements IModification {

	@Override
	public String modifySnippet(String snippetCodeText) {
		String regex = new String(Constants.regexp2);
		String modifiedSnippet = "";
		Matcher matcher = Pattern.compile(regex).matcher(snippetCodeText);
		while (matcher.find()) {
			String group = matcher.group();
			if(!modifiedSnippet.contains(group)){
				modifiedSnippet += group +" ";
			}
		}
		return modifiedSnippet;
	}
}
