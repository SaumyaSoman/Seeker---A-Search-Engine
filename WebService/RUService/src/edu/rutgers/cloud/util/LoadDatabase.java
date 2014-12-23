package edu.rutgers.cloud.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import edu.rutgers.cloud.model.DocWords;
import edu.rutgers.cloud.model.Document;
import edu.rutgers.cloud.model.InLinks;
import edu.rutgers.cloud.model.Index;

/**
 * Java application class to upload data into database.
 * @author Saumya
 *
 */
public class LoadDatabase {
	
	public static void main(String[] args) {
		LoadDatabase db=new LoadDatabase();
		db.createInvertedIndex();
//		db.createDocWords();
//		db.createInLinks();
//		db.createDocs();
	}
	
	/**
	 * Parses text documents and saves in database
	 */
	private void createDocs() {
		
		ArrayList<Document> docs=new ArrayList<>();
		String foldername="E:\\graduate\\cloud\\project\\data\\input";
		File folder=new File(foldername);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			try {
				FileInputStream fisTargetFile = new FileInputStream(new File(foldername+"\\"+file.getName()));
				String fileContents = IOUtils.toString(fisTargetFile, "UTF-8");
				String[] text=fileContents.split("ParseText::");
				if(text.length>1){
					String snippet=text[1].trim().length()>100 ? text[1].trim().substring(0, 100): text[1].trim();
					Document doc=new Document();
					doc.setFileName(file.getName().replace(".txt", ""));
					doc.setUrl(text[0].split("URL::")[1].trim());
					doc.setText(snippet+"...");
					docs.add(doc);
				}
			}catch(IOException e){
				e.printStackTrace();
			}	
		}	
		DBUtil.insertDocs(docs);
	}
	
	/**
	 * Counts words in a document and saves in database
	 */
	private void createDocWords() {
		ArrayList<DocWords> docList=new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("E:\\graduate\\cloud\\project\\data\\docWords.txt"));
			String line = "";
			while ((line = in.readLine()) != null) {
				String parts[] = line.split(" ");
				DocWords docWords=new DocWords();
				docWords.setDocName(parts[0].replace(".txt", ""));
				docWords.setCount(Float.parseFloat(parts[1]));
				docList.add(docWords);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DBUtil.insertDocWords(docList);
	}
	
	/**
	 * Parses inverted index text file and saves in database
	 */
	private void createInvertedIndex() {
		ArrayList<Index> invertedIndex=new ArrayList<>();
		try{
			BufferedReader in = new BufferedReader(new FileReader("E:\\graduate\\cloud\\project\\data\\part-r-00001"));
			String line = "";
			while ((line = in.readLine()) != null) {
				String parts[] = line.split("\t");
				parts[1]=parts[1].replace("{", "").replace("}", "");
				String counts[]=parts[1].split(",");
				HashMap<String,Integer> fileList=new HashMap<String,Integer>();
				for (String count : counts) {
					String file[]=count.split("=");
					fileList.put(file[0].trim().replace(".txt", ""), Integer.parseInt(file[1].trim()));
				}
				Index index=new Index();
				index.setWord(parts[0]);
				index.setFileList(fileList);
				invertedIndex.add(index);
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		DBUtil.insertIndex(invertedIndex);
	}

	/**
	 * Parses inlinks text file and saves in database
	 */
	private void createInLinks() {
		ArrayList<InLinks> inlinkList=new ArrayList<>();
		try{
			ArrayList<String> inlinks=null;
			String url="";
			BufferedReader in = new BufferedReader(new FileReader("E:\\graduate\\cloud\\project\\data\\inlinks"));
			String line = "";
			while ((line = in.readLine()) != null) {
				String[] urls;
				if(line.contains("Inlinks:")){
					if(!url.isEmpty()){
						InLinks inlink=new InLinks();
						inlink.setUrl(url);
						inlink.setLinks(inlinks);
						inlinkList.add(inlink);
					}
						
					inlinks=new ArrayList<>();
					urls=line.trim().split("Inlinks:");
					url=urls[0].trim();
				}else{
					urls=line.trim().split("anchor:");
					String inlink=urls[0].replace("fromUrl:", "").trim();
					if(!inlink.isEmpty()){
						inlinks.add(inlink);
					}

				}
			}
			InLinks inlink=new InLinks();
			inlink.setUrl(url);
			inlink.setLinks(inlinks);
			inlinkList.add(inlink);
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		DBUtil.insertInLinks(inlinkList);
	}
}
