package edu.rutgers.cloud.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;

import edu.rutgers.cloud.model.DocWords;
import edu.rutgers.cloud.model.Document;
import edu.rutgers.cloud.model.InLinks;
import edu.rutgers.cloud.model.Index;

/**
 * Class to query mongoDB collections
 *
 */
public class DBUtil {
	
	/**
	 * Save inverted index
	 * @param invertedIndex
	 */
	public static void insertIndex(ArrayList<Index> invertedIndex){
		Datastore datastore = MorphiaUtil.getDatastore();
		datastore.save(invertedIndex);
	}

	/**
	 * Save documents
	 * @param docs
	 */
	public static void insertDocs(ArrayList<Document> docs) {
		Datastore datastore = MorphiaUtil.getDatastore();
		datastore.save(docs);
	}
	
	/**
	 * Save total number of words for each document
	 * @param docWords
	 */
	public static void insertDocWords(ArrayList<DocWords> docWords) {
		Datastore datastore = MorphiaUtil.getDatastore();
		datastore.save(docWords);
	}

	/**
	 * Save inlinks
	 * @param inlinks
	 */
	public static void insertInLinks(ArrayList<InLinks> inlinks) {
		Datastore datastore = MorphiaUtil.getDatastore();
		datastore.save(inlinks);
	}
	
	/**
	 * Returns inverted index for each query word
	 * @param queryWords
	 * @return invertedIndex
	 */
	public static ArrayList<Index> getInvertedIndex(ArrayList<String> queryWords) {
		ArrayList<Index> indexList=new ArrayList<>();
		try {
			Datastore datastore = MorphiaUtil.getDatastore();
			for (String word : queryWords) {
				Query<Index> index=  datastore.find(Index.class).field("word").equal(word);
				if(index.get()!=null){
					indexList.add(index.get());
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();

		}
		return indexList;
	}
	
	/**
	 * Counts total number of documents in database
	 * @return count
	 */
	public static double getDocCount() {
		Datastore datastore = MorphiaUtil.getDatastore();
		long count=datastore.find(DocWords.class).countAll();
		return count;
	}
	
	/**
	 * Returns number of words for a document 
	 * @param docName
	 * @return count
	 */
	public static double getDocWords(String docName) {
		Datastore datastore = MorphiaUtil.getDatastore();
		Query<DocWords> query=datastore.find(DocWords.class).field("docName").equal(docName);
		DocWords docWord=query.get();
		double count= (docWord==null)? 0.0 : docWord.getCount();
		return count;
	}
	
	/**
	 * Returns document content for each file name
	 * @param pages , list of document names
	 * @param threshold maximum number of documents to retreive
	 * @return document content
	 */
	public static LinkedHashMap<String, String> getDocText(Set<String> pages, int threshold) {
		LinkedHashMap<String, String> docTextList=new LinkedHashMap<>();
		Datastore datastore = MorphiaUtil.getDatastore();
		int i=0;
		for (String fileName : pages) {
			if(threshold!=0 && i==threshold) break;
			Query<Document> query=  datastore.find(Document.class).field("fileName").equal(fileName);
			Document doc=query.get();
			if(doc!=null && doc.getUrl()!=null && doc.getText()!=null)
				docTextList.put(doc.getUrl(), doc.getText());
			i++;
		}	
		return docTextList;	
	}
	
	/**
	 * Retrieve inlinks for an url
	 * @param url
	 * @return list of inlinks
	 */
	public static ArrayList<String> getInLinks(String url) {
		Datastore datastore = MorphiaUtil.getDatastore();
		Query<InLinks> query=  datastore.find(InLinks.class).field("url").equal(url);
		InLinks resp=query.get();
		if(resp!=null)
			return resp.getLinks();
		else
			return null;
	}
}
