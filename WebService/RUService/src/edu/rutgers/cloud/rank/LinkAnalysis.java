package edu.rutgers.cloud.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.rutgers.cloud.util.DBUtil;

/**
 * 
 * Class to perform Link Analysis. It retrieves number of inlinks for each url and outputs urls
 * with maximum number of inlinks.
 * 
 */
public class LinkAnalysis {

	private LinkedHashMap<String, String> responses;	
	
	public LinkAnalysis(LinkedHashMap<String, String> responses) {
		this.responses=responses;
	}
	
	/**
	 * It counts inlinks if the link is present in tfidf result.
	 * Returns url list sorted by maximum number of inlinks.
	 * @return
	 */
	public LinkedHashMap<String, String> analysis() {
		LinkedHashMap<String, Integer> countMap=new LinkedHashMap<>();
		
		//Iterates through tfidf results and counts inlinks
		for (String url : responses.keySet()) {
			int count=0;
			ArrayList<String> linkList=DBUtil.getInLinks(url);
			if(linkList!=null){
				for (String inlink : linkList) {
					if(responses.containsKey(inlink))
						count++;
				}
				countMap.put(url, count);
			}				
		}
		
		//sort by inlinks count
		countMap=sortByValues(countMap);
		
		//Url map sorted by inlinks count.
		LinkedHashMap<String, String> newResponse =new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry: countMap.entrySet()) {
			newResponse.put(entry.getKey(), responses.get(entry.getKey()));
		}

		return newResponse;
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
}
