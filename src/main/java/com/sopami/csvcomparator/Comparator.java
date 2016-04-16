package com.sopami.csvcomparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This class is used to compare different CSV.
 * It will allow us to know which records are new, which ones have been deleted.
 * @author SOPAMI
 *
 */
public class Comparator {

	// CSV File names, they are supposed to be saved in the resource folder.
	private static final String originalFileName = "liste_parents.csv";
	private static final String newFileName = "liste_parents15_16.csv";
	
	private static final Logger LOG = Logger.getLogger(Comparator.class);
	
	/**
	 * Convert list of contact in a map. 
	 * Key is the name;
	 * Value is the name, email & phone number.
	 * @param c Collection containing csv lines.
	 * @return Map.
	 */
	private static Map<String, String> getContactMap(Collection<String> c){
		
		Map<String, String> map = new HashMap<String, String>();
		
		for(String line : c){
			// Information are supposed to be coming 
			// from a csv file and separated by ";"
			String[] info = line.split(";");
			if(info.length > 0){
				map.put(info[0], line);
			}
		}
		
		return map;
		
	}
	
	/**
	 * Compare two csv file
	 * @param originalFile Original file name
	 * @param newFile new file name
	 */
	public static void compare(String originalFile, String newFile){
		
		LOG.info("Compare file " + originalFile + " and " + newFile);
		
		//Get classLoader
		ClassLoader classLoader = Comparator.class.getClassLoader();

		//Get file from resource folder
		String originalFilePath = classLoader.getResource(originalFile).getFile();
		String newFilePath = classLoader.getResource(newFile).getFile();
		
		//Create lists for updated and deleted contact
		List<String> newContact = new ArrayList<String>();
		List<String> updatedContact = new ArrayList<String>();
		List<String> deletedContact = new ArrayList<String>();
		try {
			
			Set<String> originalData = new HashSet<String>(
					FileUtils.readLines(new File(originalFilePath)));
			Map<String, String> originalDataMap = getContactMap(originalData);
			
			Set<String> newData = new HashSet<String>(
					FileUtils.readLines(new File(newFilePath)));
			Map<String, String> newDataMap = getContactMap(newData);

			for(String name : newDataMap.keySet()){
				
				if(!originalDataMap.containsKey(name)){
					newContact.add(newDataMap.get(name));
				}else{
					String originalContact = originalDataMap.get(name);
					String updatedContactInfo = newDataMap.get(name);
					if(originalContact.equals(updatedContactInfo)){
						updatedContact.add(updatedContactInfo);
					}
				}
			}
			
			for(String name : originalDataMap.keySet()){
				
				if(!newDataMap.containsKey(name)){
					deletedContact.add(originalDataMap.get(name));
				}
			}
			
			LOG.info(" >>>>>> New contacts: ");
			
			for(String contact : newContact){
				LOG.info(contact.split(";")[0]);
			}
			
			LOG.info(" >>>>>> Updated contacts: ");
			
			for(String contact : updatedContact){
				LOG.info(contact.split(";")[0]);
			}
			
			LOG.info(" >>>>>> Deleted contacts: ");
			
			for(String contact : deletedContact){
				LOG.info(contact.split(";")[0]);
			}
			
		} catch (IOException e) {
			LOG.fatal("An exception occured. Message : " + e.getMessage());
		}
		
		
	}
	
	
	public static void main(String[] args) {
		
		Comparator.compare(originalFileName, newFileName);
	}
	
	

}
