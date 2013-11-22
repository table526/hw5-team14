package edu.cmu.lti.deiis.hw5.query;

import java.io.IOException;

public class Test {
	public static void main(String args[]) throws IOException{
		String query = "insulin";
		OnlineQuery test = new OnlineQuery();
		System.out.println(test.getSynonym(query));
	}
}
