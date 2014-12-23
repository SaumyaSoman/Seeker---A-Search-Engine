package edu.rutgers.cloud.service.response;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 * Annotations response  
 * @author Saumya
 *
 */
public class WSResponse {

	private String status;
	private ArrayList<SearchResult> responses=new ArrayList<SearchResult>();
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the responses
	 */
	public ArrayList<SearchResult> getResponses() {
		return responses;
	}
	/**
	 * @param responses the responses to set
	 */
	public void setResponses(ArrayList<SearchResult> responses) {
		this.responses = responses;
	}
	
		
}
