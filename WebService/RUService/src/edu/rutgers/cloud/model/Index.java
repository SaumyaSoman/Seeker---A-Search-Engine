package edu.rutgers.cloud.model;

import java.util.HashMap;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Property;


@Entity("index")
public class Index {
	
	 @Id
	 @Property("id")
	private String word;
	private HashMap<String, Integer> fileList;
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the fileList
	 */
	public HashMap<String, Integer> getFileList() {
		return fileList;
	}
	/**
	 * @param fileList the fileList to set
	 */
	public void setFileList(HashMap<String, Integer> fileList) {
		this.fileList = fileList;
	}
	
}
