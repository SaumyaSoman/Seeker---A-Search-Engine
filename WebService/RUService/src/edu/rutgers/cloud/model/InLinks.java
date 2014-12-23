package edu.rutgers.cloud.model;

import java.util.ArrayList;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

@Entity("inlinks")
public class InLinks {

	@Id
	@Property("id")
	private String url;
	private ArrayList<String> links;
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the links
	 */
	public ArrayList<String> getLinks() {
		return links;
	}
	/**
	 * @param links the links to set
	 */
	public void setLinks(ArrayList<String> links) {
		this.links = links;
	}
	
}
