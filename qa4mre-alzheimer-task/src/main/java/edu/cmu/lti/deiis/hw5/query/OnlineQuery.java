package edu.cmu.lti.deiis.hw5.query;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup; 
import org.jsoup.select.Elements;

public class OnlineQuery {

	public ArrayList<String> getSynonym(String query) throws IOException {
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", query);
		String url = "http://gpsdb.expasy.org/cgi-bin/gpsdb/show";
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);

		DataOutputStream out = new DataOutputStream(conn.getOutputStream());

		Set keys = data.keySet();
		Iterator keyIter = keys.iterator();
		String content = "";
		for (int i = 0; keyIter.hasNext(); i++) {
			Object key = keyIter.next();
			if (i != 0) {
				content += "&";
			}
			content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
		}
		System.out.println(content);
		out.writeBytes(content);
		out.flush();
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = "";
		String temp = "";
		while ((line = in.readLine()) != null) {
			temp += line + '\n';
			// System.out.println(line);

		}
		in.close();
		// System.out.println(Jsoup.parse(temp).getElementsByTag("td "));
		Jsoup.parse(temp).text();

		Elements test = Jsoup.parse(temp).getElementsByTag("td ");
		String str = test.toString();
		String[] resultList = str.split("\n");
        ArrayList<String> resultArrayList = new ArrayList<String>();
		for (String tmp : resultList) {
			if (tmp.startsWith("<td class=\"name\">")) {
				if(tmp.substring(17, tmp.length() - 5).length()>0)
				resultArrayList.add(tmp.substring(17, tmp.length() - 5));
			}
		}
		return resultArrayList;
	}

}
