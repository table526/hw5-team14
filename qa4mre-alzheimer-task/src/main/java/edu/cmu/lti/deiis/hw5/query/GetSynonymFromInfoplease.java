package edu.cmu.lti.deiis.hw5.query;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.io.*;

public class GetSynonymFromInfoplease {

  final static int DEFAULT_RETURN_NUM = 6;
  public static String[] stopwordsStrings = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "none", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
  
	public ArrayList<String> getSynonyms(String query,int lineNum, String queryType, int returnNum) {
		StringBuffer document = new StringBuffer();
		
		try {
			String queryString = query;
			URL url = new URL("http://thesaurus.infoplease.com/" + queryString);
			URLConnection conn = url.openConnection();
			int status = ((HttpURLConnection)conn).getResponseCode();
			if(status == 404)
			{
			  ArrayList<String> errorList = new ArrayList<String>();
			  errorList.add(query);
			  return errorList;
			}
			//System.out.println(status);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null){
			  document.append(line + " ");
			}
				
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String source = document.toString();
		if(queryType.equals("Noun")){
		  if(source.indexOf("<h3>Noun</h3>")<0) 
		  {
        ArrayList<String> errorList = new ArrayList<String>();
        errorList.add(query);
        return errorList;
      }
		  int start = source.indexOf("<h3>Noun</h3>");
		  int end = source.indexOf("</dl>",start);
		  source = source.substring(start,end);
		}else if (queryType.equals("Verb")) {
      if(source.indexOf("<h3>Verb</h3>")<0)
      {
        ArrayList<String> errorList = new ArrayList<String>();
        errorList.add(query);
        return errorList;
      }
      int start = source.indexOf("<h3>Verb</h3>");
      int end = source.indexOf("</dl>",start);
      source = source.substring(start,end);
    }
		if(source.indexOf("<dl><dt>")<0) 
    {
      ArrayList<String> errorList = new ArrayList<String>();
      errorList.add(query);
      return errorList;
    }
		int start = source.indexOf("<dl><dt>") + 11;
		int end = source.indexOf("</dt>",start);
		String result =source.substring(start, end);
		ArrayList<String> finalResult = new ArrayList<String>();
		for(int i =1;i<lineNum;i++){
		  String xx = source.substring(start, end);
      String xxs[] = xx.split(" ");
      for(int j = 1; j<xxs.length;j++){
        if(!finalResult.contains(xxs[j].split(",")[0]))
        finalResult.add(xxs[j].split(",")[0]);
        //System.out.println(xxs[j]);    
      }         
		  if(source.substring(end).contains("<dt>")){
		    start = source.indexOf("<dt>",end) + 5;
		    end = source.indexOf("</dt>",start);
		    
		  }		  
		}		
		
		if(!finalResult.contains(query))
		{
		  finalResult.add(0, query);
		}
		if(finalResult.indexOf(query) != 0)
		{
		  System.out.println("Missing origin!\t");
		  finalResult.remove(finalResult.indexOf(query));
		  finalResult.add(0, query);
		}
		System.out.println(finalResult);
//		for(String x:finalResult){
//		  System.out.println(x);
//		}
		//String result = source.substring(start, end);
//		String[] resultList = result.split(",");
//		ArrayList<String> resultStrings = new ArrayList<String>();
//		for (int i = 0; i < resultList.length; i++) {
//			if (resultList[i].startsWith(" ")) {
//				resultList[i] = resultList[i].substring(1);
//			}
//			if (!resultStrings.contains(resultList[i])) {
//				resultStrings.add(resultList[i]);
//			}
//		}
//		
		HashSet<String> stopW = new HashSet<String>(Arrays.asList(stopwordsStrings));
	  for(int i = 0; i < finalResult.size(); i++)
	  {
	    if(stopW.contains(finalResult.get(i)))
	    {
	      finalResult.remove(i);
	    }
	  }
		if(returnNum >= finalResult.size())
		{
		  return finalResult;
		}
		else if(returnNum >= 1)
		{
		  ArrayList<String> temp = new ArrayList<String>();
		  for(int i =0 ; i< returnNum; i++){
		    temp.add(finalResult.get(i));
		  }
		  return temp;
    }else {
      return new ArrayList<String>();
    }
	}
	public ArrayList<String> getSynonyms(String query,int lineNum, String queryType) 
	{
	    return getSynonyms( query, lineNum, queryType, DEFAULT_RETURN_NUM) ;
	}
	public ArrayList<String> getSynonyms(String query) 
	{
	    return getSynonyms( query, 1, "Noun", 100) ;
	}
	
}