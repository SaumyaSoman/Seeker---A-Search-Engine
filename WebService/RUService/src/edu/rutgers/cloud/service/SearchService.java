package edu.rutgers.cloud.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;

import com.sun.jersey.api.json.JSONWithPadding;

import edu.rutgers.cloud.rank.LinkAnalysis;
import edu.rutgers.cloud.rank.TfIdf;
import edu.rutgers.cloud.service.response.SearchResult;
import edu.rutgers.cloud.service.response.WSResponse;
import edu.rutgers.cloud.util.DBUtil;

@Path("/search")

/**
 * The Resource class, or access point. This class services the incoming request and returns
 * back search responses.
 */
public class SearchService {

	@GET
	@Produces({"application/x-javascript", "application/json"})
	/**
	 * Method that services the incoming request.
	 * @param query string
	 * @param callback for jquery
	 * @return JSONWithPadding enables cross-domain requesta
	 */
	public JSONWithPadding  search(@QueryParam("query") String query, @QueryParam("callback") String callback) {
		WSResponse response=new WSResponse();

		//Enter valid query
		if(query.isEmpty()){
			response.setStatus("Enter valid query");
			return new JSONWithPadding( new GenericEntity<WSResponse>(response) {}, callback);
		}

		//Rank pages by Tf-idf
		Set<String> pages=rankPages(query);		
		LinkedHashMap<String, String> pageContents=new LinkedHashMap<>();
		pageContents=getPageContent(pages,30,pageContents);
		LinkedHashMap<String,String> linkResp = new LinkedHashMap<>();
		int i=0;
		for (Entry<String, String> entry : pageContents.entrySet()) {
			if(i==100) break;
			linkResp.put(entry.getKey(), entry.getValue());
			i++;
		}

		//Link analysis on first 100 results of Tf-idf
		LinkAnalysis linkAnalysis=new LinkAnalysis(linkResp);
		pageContents=linkAnalysis.analysis();
		pageContents=getPageContent(pages,0,pageContents);

		//Set response
		response=setResponse(pageContents,response);
		System.out.println("response size..."+pageContents.size());
		return new JSONWithPadding( new GenericEntity<WSResponse>(response) {}, callback);
	}

	/**
	 * Retrieve document cntent and set in responses
	 * @param pages, list of document Names
	 * @param threshold to set the limit of number of pages to bre retrieved
	 * @param responses
	 * @return responses
	 */
	private LinkedHashMap<String,String> getPageContent(Set<String> pages, int threshold, LinkedHashMap<String, String> responses) {
		if(pages!=null && pages.size()>0){
			LinkedHashMap<String, String> dbResults=DBUtil.getDocText(pages,threshold);
			int i=0; 
			for (Entry<String, String> entry : dbResults.entrySet()) {
				if(i==100) break;
				if(!responses.containsKey(entry.getKey())){

					responses.put(entry.getKey(), entry.getValue());
				}
				i++;
			}
		}
		return responses;
	}

	/**
	 * Set Response from page contents
	 * @param pageContents
	 * @param response
	 * @return WSResponse
	 */
	private WSResponse setResponse(LinkedHashMap<String, String> pageContents, WSResponse response) {
		if(pageContents!=null && pageContents.size()>0){
			ArrayList<SearchResult> responses=new ArrayList<SearchResult>();
			response.setStatus("success");
			for (Entry<String, String> entry : pageContents.entrySet()) {
				SearchResult result=new SearchResult();
				result.setUrl(entry.getKey());
				result.setSnippet(entry.getValue());
				responses.add(result);

			}
			response.setResponses(responses);
		}else{
			response.setStatus("Sorry!! No results found!! :(");
		}
		System.out.println(response.toString());
		return response;
	}

	/**
	 * Rank pages by TF-IDF
	 * @param query
	 * @return document names in ranked order
	 */
	private Set<String> rankPages(String query) {
		Set<String> fileList=new TreeSet<>();
		TfIdf tfidf=new TfIdf(fileList,query.trim());
		fileList=tfidf.calculate();
		return fileList;
	}
}

