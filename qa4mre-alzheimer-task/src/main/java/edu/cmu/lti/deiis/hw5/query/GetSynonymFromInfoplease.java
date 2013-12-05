package edu.cmu.lti.deiis.hw5.query;

import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class GetSynonymFromInfoplease {

	public ArrayList<String> getSynonyms(String query) {
		StringBuffer document = new StringBuffer();
		try {
			String queryString = query;
			URL url = new URL("http://thesaurus.infoplease.com/" + queryString);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
			conn.setRequestProperty("Accept","*/*");
			if(((HttpURLConnection) conn).getResponseCode()==404) return null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null)
				document.append(line + " ");
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String source = document.toString();
		//if(source.indexOf("<dl><dt>")<0) return null;
		int start = source.indexOf("<dl><dt>") + 11;
		int end = source.indexOf("</dt>");
		String result = source.substring(start, end);
		String[] resultList = result.split(",");
		ArrayList<String> resultStrings = new ArrayList<String>();
		for (int i = 0; i < resultList.length; i++) {
			if (resultList[i].startsWith(" ")) {
				resultList[i] = resultList[i].substring(1);
			}
			if (!resultStrings.contains(resultList[i])) {
				resultStrings.add(resultList[i]);
			}
		}
		
		
		return resultStrings;
	}
}