package edu.rutgers.cloud.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * Java application to count number of dynamic pages
 *
 */
public class DynamicPageCount {

	public Integer countDynamicPages(String filename) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		String delims = "[ ]";
		String[] tokens;
		Integer numDynPages = 0;
		
		while((line = br.readLine()) != null)
		{
			tokens = line.split(delims);
			if(tokens[1].equals("0.0"))
				numDynPages++;
		}
		
		return numDynPages;
	}
	
	public static void main(String[] args) throws IOException {
		
		String filename = "/home/aishwarya/Documents/workspace/Tfidf/src/docWords.txt";
		DynamicPageCount dynamicPageCountObj = new DynamicPageCount();
		System.out.println("Num dyn pages: "+dynamicPageCountObj.countDynamicPages(filename));
		
	}
}
