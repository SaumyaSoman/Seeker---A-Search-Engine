package edu.rutgers.cloud.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 * Java application to count total number of words in all input documents
 *
 */
public class CountWords {
	
	static ArrayList<Double> numWords = new ArrayList<Double>();
	
	/**
	 * Return total number of words in a document
	 * @param filename
	 * @return count
	 * @throws IOException
	 */
	public Integer countTotalWordsInDoc(String filename) throws IOException{

		int noOfWords = 0;
		String fileContent = new Scanner(new File(filename)).useDelimiter(" \\A").next();
		boolean text = false;
		
		//Tokenize, remove stop words and stem file content
		EnglishAnalyzer stopWords = new EnglishAnalyzer(Version.LUCENE_36);
		TokenStream analyzedTokens=stopWords.tokenStream(null, new StringReader(fileContent.toString().toLowerCase()));

		//To read the analyzed tokens
		CharTermAttribute cattr = analyzedTokens.addAttribute(CharTermAttribute.class);
		analyzedTokens.reset();

		while (analyzedTokens.incrementToken()) {
			if(text){
				noOfWords++; //gives total number of words	
			}
			if(cattr.toString().equals("parsetext"))
			{
				text=true;
			}
		}
		analyzedTokens.end();
		analyzedTokens.close();
		stopWords.close();
		return noOfWords;
	}
	
	/**
	 * Write output counts in a text file.
	 * @param directoryPath
	 * @throws IOException
	 */
	public void countWordsDirectory(String directoryPath) throws IOException{
		CountWords countObject = new CountWords();
		File dir = new File(directoryPath);
		File[] directoryListing = dir.listFiles();

		String outputFilePath = "/home/aishwarya/Documents/workspace/Tfidf/src";
		File file = new File(outputFilePath+"/"+"docWords.txt");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		if(directoryListing != null)
		{
			for(File child : directoryListing) {

				Double tfValue = 1.0 * countTotalWordsInDoc(directoryPath+"/"+child.getName());
				numWords.add(tfValue);
				bw.write(child.getName()+" "+tfValue);
				bw.newLine();
			}
		}

		bw.close();

	}

	public static void main(String args[]) throws IOException{
		CountWords countObject = new CountWords();
		String directoryPath = "/home/aishwarya/Documents/workspace/Tfidf/src/input_5000";
		countObject.countWordsDirectory(directoryPath);
	}

	
}
