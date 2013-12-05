package edu.cmu.lti.deiis.hw5.query;

import java.io.IOException;

public class Test {
	public static void main(String args[]) throws IOException{
		String query = "building";
		GetSynonymFromInfoplease test = new GetSynonymFromInfoplease();
		System.out.println(test.getSynonyms(query, 3, "Verb", 10));
	}
}
