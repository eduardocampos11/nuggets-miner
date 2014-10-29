package br.com.inf.nuggets.miner.utils;
public class HtmlToPlainText {
	
	public static final String unescapeHTML(String s, int f) {  
		  String [][] escape =  
		   {{  "&lt;"     , "<" } ,  
		    {  "&gt;"     , ">" } ,  
		    {  "&amp;"    , "&" } ,  
		    {  "&quot;"   , "\"" } ,  
		    {  "&aacute;"  , "�" } ,  
		    {  "&Aacute;"  , "�" } ,  
		    {  "&eacute;"  , "�" } ,  
		    {  "&Eacute;"  , "�" } ,  
		    {  "&iacute;"  , "�" } ,  
		    {  "&Iacute;"  , "�" } ,  
		    {  "&oacute;"  , "�" } ,  
		    {  "&Oacute;"  , "�" } ,  
		    {  "&uacute;"  , "�" } ,  
		    {  "&Uacute;"  , "�" } ,  
		    {  "&Ntilde;"  , "�" } ,  
		    {  "&Ntilde;"  , "�" } ,  
		    {  "&apos;"   , "'" } ,  
		    {  "&deg;;"   , "�" } ,  
		    {  "&agrave;" , "�" } ,  
		    {  "&Agrave;" , "�" } ,  
		    {  "&acirc;"  , "�" } ,  
		    {  "&auml;"   , "�" } ,  
		    {  "&Auml;"   , "�" } ,  
		    {  "&Acirc;"  , "�" } ,  
		    {  "&aring;"  , "�" } ,  
		    {  "&Aring;"  , "�" } ,   
		    {  "&aelig;"  , "�" } ,   
		    {  "&AElig;"  , "�" } ,  
		    {  "&ccedil;" , "�" } ,  
		    {  "&Ccedil;" , "�" } ,  
		    {  "&eacute;" , "�" } ,  
		    {  "&Eacute;" , "�" } ,  
		    {  "&egrave;" , "�" } ,  
		    {  "&Egrave;" , "�" } ,  
		    {  "&ecirc;"  , "�" } ,  
		    {  "&Ecirc;"  , "�" } ,  
		    {  "&euml;"   , "�" } ,  
		    {  "&Euml;"   , "�" } ,  
		    {  "&iuml;"   , "�" } ,   
		    {  "&Iuml;"   , "�" } ,  
		    {  "&ocirc;"  , "�" } ,  
		    {  "&Ocirc;"  , "�" } ,  
		    {  "&ouml;"   , "�" } ,  
		    {  "&Ouml;"   , "�" } ,  
		    {  "&oslash;" , "�" } ,  
		    {  "&Oslash;" , "�" } ,  
		    {  "&szlig;"  , "�" } ,  
		    {  "&ugrave;" , "�" } ,  
		    {  "&Ugrave;" , "�" } ,  
		    {  "&ucirc;"  , "�" } ,  
		    {  "&Ucirc;"  , "�" } ,   
		    {  "&uuml;"   , "�" } ,  
		    {  "&Uuml;"   , "�" } ,  
		    {  "&nbsp;"   , " " } ,  
		    {  "&reg;"    , "\u00a9" } ,  
		    {  "&copy;"   , "\u00ae" } ,  
		    {  "&euro;"   , "\u20a0" } };  
		   int i, j, k, l ;  
		     
		   i = s.indexOf("&", f);  
		   if (i > -1) {  
		      j = s.indexOf(";" ,i);  
		      // --------  
		      // we don't start from the beginning   
		      // the next time, to handle the case of  
		      // the &  
		      // thanks to Pieter Hertogh for the bug fix!  
		      f = i + 1;  
		      // --------  
		      if (j > i) {  
		         // ok this is not most optimized way to  
		         // do it, a StringBuffer would be better,  
		         // this is left as an exercise to the reader!  
		         String temp = s.substring(i , j + 1);  
		         // search in escape[][] if temp is there  
		         k = 0;  
		         while (k < escape.length) {  
		           if (escape[k][0].equals(temp)) break;  
		           else k++;  
		           }  
		         if (k < escape.length) {  
		           s = s.substring(0 , i) + escape[k][1] + s.substring(j + 1);  
		           return unescapeHTML(s, f); // recursive call  
		           }  
		         }  
		      }     
		   return s;  
		   }  
}

