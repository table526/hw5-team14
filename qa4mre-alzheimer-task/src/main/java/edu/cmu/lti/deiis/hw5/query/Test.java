package edu.cmu.lti.deiis.hw5.query;

import java.io.IOException;

public class Test {
	public static void main(String args[]) throws IOException{
		String query = "head";
		//OnlineQuery test = new OnlineQuery();
		GetSynonymFromInfoplease test = new GetSynonymFromInfoplease();
		System.out.println(test.getSynonyms(query,5));
	}
}
