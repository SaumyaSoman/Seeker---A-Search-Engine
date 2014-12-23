package edu.rutgers.cloud.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;

@Entity("docWords")
public class DocWords {

	@Id
	@Property("id")
	private String docName;
	private float count;
	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}
	/**
	 * @param docName the docName to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}
	/**
	 * @return the count
	 */
	public float getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(float count) {
		this.count = count;
	}

	
}
