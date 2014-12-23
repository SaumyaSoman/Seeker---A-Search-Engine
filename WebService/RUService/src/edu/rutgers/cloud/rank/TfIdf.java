package edu.rutgers.cloud.rank;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import edu.rutgers.cloud.model.Index;
import edu.rutgers.cloud.util.DBUtil;

/**
 * 
 * Implementation of Tf-Idf Algorithm
 *
 */
public class TfIdf {

	private HashMap<String, HashMap<String, Integer>> invertedIndex=new HashMap<>();
	private Set<String> fileList;
	private String query;
	private LinkedHashMap<String,Double> queryVector;
	double totalDocs=0.0;

	public TfIdf(Set<String> fileList, String query){
		this.query=query;
		this.fileList=fileList;
	}

	/**
	 * Method to calculate Tf-Idf
	 * @return fileList , list of file names sorted by rank.
	 */
	public Set<String> calculate() {

		//Tokenize,remove stop words and stem the query
		ArrayList<String> queryWords=tokenize();
		if(queryWords!=null && queryWords.size()>0){
			//Get total number of documents in database
			totalDocs=DBUtil.getDocCount();
			//Get inverted index of the query words
			getInvertedIndex(queryWords);
			//Create query vector
			getQueryVector(queryWords);
			//Create Document matrix based on query vector
			LinkedHashMap<String,LinkedHashMap<String,Double>> documentVector =getDocumentVector();
			//Rank the documents
			LinkedHashMap<String,Double> docNames=rank(documentVector);	
			fileList= docNames.keySet();
		}			
		return fileList;
	}

	/**
	 * Retrieve inverted index from database.
	 * @param queryWords List of query words
	 */
	private void getInvertedIndex(ArrayList<String> queryWords) {
		ArrayList<Index> indexes=DBUtil.getInvertedIndex(queryWords);
		if(indexes.size()>0){
			for (Index index : indexes) {
				invertedIndex.put(index.getWord(), index.getFileList());
			}
		}		
	}

	/**
	 * Rank the documents based on tf-idf
	 * @param documentVector
	 * @return documentVector
	 */
	private LinkedHashMap<String,Double> rank(LinkedHashMap<String, LinkedHashMap<String, Double>> documentVector) {
		LinkedHashMap<String,Double> docNames= new LinkedHashMap<>();
		
		//Calculate product of query vector and document vector
		for (String docName : documentVector.keySet()) {
			LinkedHashMap<String, Double> docVector=documentVector.get(docName);
			double score=0.0;
			for (String word : docVector.keySet()) {
				double queryTfidf= queryVector.get(word);
				score+= queryTfidf* docVector.get(word);
			}
			docNames.put(docName, score);
		}
		
		//Sort based on tf-idf score
		docNames=sortByValues(docNames);
		System.out.println(docNames.toString());
		return docNames;
	}

	/**
	 * Create document matrix based on query vector
	 * @return documentVector
	 */
	private LinkedHashMap<String, LinkedHashMap<String, Double>> getDocumentVector() {

		LinkedHashMap<String, LinkedHashMap<String, Double>> documentVector=new LinkedHashMap<>();
		Set<String> queryWords=queryVector.keySet();
		Set<String> docNames=new TreeSet<String>();
		//Get all the valid documents from database based on query words
		for (String word : queryWords) {
			HashMap<String, Integer> fileList=invertedIndex.get(word);
			docNames.addAll(fileList.keySet());		
		}
		// Calculate Tf-idf for each query word in a document
		
		for (String docName : docNames) {
			double totalWords=DBUtil.getDocWords(docName);
			LinkedHashMap<String, Double> vector=new LinkedHashMap<>();
			if(totalWords!=0){
				for (String word : queryWords){				
					double noOfOccurences=invertedIndex.get(word).get(docName)!=null ? invertedIndex.get(word).get(docName) : 0.0;				
					double tf=noOfOccurences/totalWords;
					double noOfDocs=invertedIndex.get(word).size();
					double idf=noOfDocs!=0 ? Math.log(totalDocs/noOfDocs) : 0;
					vector.put(word, tf*idf);
				}
				documentVector.put(docName, vector);
			}			
		}

		return documentVector;
	}

	/**
	 * Create query vector
	 * @param queryWords
	 */
	private void getQueryVector(ArrayList<String> queryWords) {
		LinkedHashMap<String,Double> queryTf=new LinkedHashMap<String,Double>();
		double total=queryWords.size();
		
		//Calculate tf-idf for each query word.
		for (String word : queryWords) {
			double count= queryTf.get(word)==null? 1.0 : (queryTf.get(word)*total)+1.0;
			queryTf.put(word, count/total );
		}
		queryVector=new LinkedHashMap<>();
		for (String key : queryTf.keySet()) {
			if(invertedIndex.get(key)!=null){
				double noOfDocs=invertedIndex.get(key).size();
				double idf=noOfDocs!=0 ? Math.log(totalDocs/noOfDocs) : 0;
				queryVector.put(key, queryTf.get(key)*idf );
			}
		}
	}

	/**
	 * Method to sort hashmap by value
	 * @param map
	 * @return map
	 */
	public  <K extends Comparable,V extends Comparable> LinkedHashMap<K,V> sortByValues(Map<K,V> map){
		List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());

		Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {
			//overrides the compare function
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				//sorts by descending order
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		//Sorted map
		LinkedHashMap<K,V> sortedMap = new LinkedHashMap<K,V>();	      
		for(Map.Entry<K,V> entry: entries){
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}


	/**
	 * To tokenize, remove stop words and stem words in a query.
	 * @return ArrayList<String> , list of query words.
	 */
	private ArrayList<String> tokenize() {
		ArrayList<String> queryWords=new ArrayList<String>();
		try {			
			//Analyzer which tokenizes,removes stop words and stems words
			EnglishAnalyzer stopWords = new EnglishAnalyzer(Version.LUCENE_36);
			TokenStream analyzedTokens=stopWords.tokenStream(null, new StringReader(query.toLowerCase()));

			//To read the analyzed tokens
			CharTermAttribute cattr = analyzedTokens.addAttribute(CharTermAttribute.class);
			analyzedTokens.reset();
			while (analyzedTokens.incrementToken()) {
				queryWords.add(cattr.toString());
			}
			analyzedTokens.end();
			analyzedTokens.close();
			stopWords.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return queryWords;
	}
}
