/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import javax.swing.text.*;
import javax.swing.text.html.*;
import org.jsoup.*;
import org.jsoup.select.Elements;



/**
 * This example demonstrates the recommended way of using API to make sure
 * the underlying connection gets released back to the connection manager.
 */
public class WebFetchTest {
	public void doSubmit(String url, Map<String, String> data) throws Exception {
		URL siteUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		
		Set keys = data.keySet();
		Iterator keyIter = keys.iterator();
		String content = "";
		for(int i=0; keyIter.hasNext(); i++) {
			Object key = keyIter.next();
			if(i!=0) {
				content += "&";
			}
			content += key + "=" + URLEncoder.encode(data.get(key), "UTF-8");
		}
		System.out.println(content);
		out.writeBytes(content);
		out.flush();
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line = "";
		String temp = "";
		while((line=in.readLine())!=null) {
			temp += line+'\n';
			//System.out.println(line);
			
		}
		in.close();
		//System.out.println(Jsoup.parse(temp).getElementsByTag("td "));
		Jsoup.parse(temp).text();
		
	    Elements test = Jsoup.parse(temp).getElementsByTag("td ");
	    String str = test.toString();
	    String[] resultList = str.split("\n");
	    for(String tmp : resultList){
	    	if(tmp.startsWith("<td class=\"name\">")){
	    		System.out.println(tmp.substring(17, tmp.length()-5));
	    	}
	    }
	}
	


		class HTMLTableParser extends HTMLEditorKit.ParserCallback {

		    private boolean encounteredATableRow = false;

		    public void handleText(char[] data, int pos) {
		        if(encounteredATableRow) System.out.println(new String(data));
		    }

		    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
		        if(t == HTML.Tag.TR) encounteredATableRow = true;
		    }

		    public void handleEndTag(HTML.Tag t, int pos) {
		        if(t == HTML.Tag.TR) encounteredATableRow = false;
		    }
		}


    public final static void main(String[] args) throws Exception {
    	WebFetchTest test = new WebFetchTest();
    	Map<String, String> data = new HashMap();
    	data.put("name", "indy");
    	test.doSubmit("http://gpsdb.expasy.org/cgi-bin/gpsdb/show", data);
    }

}
