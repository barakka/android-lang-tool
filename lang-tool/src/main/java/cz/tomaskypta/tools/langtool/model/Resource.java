package cz.tomaskypta.tools.langtool.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resource {	
	private String name;
	private Map<String, ResourceEntry> entries;
	
	public Resource(String fileName) {
		this.name = fileName;
		this.entries = new HashMap<String, ResourceEntry>();
	}

	public Term getTerm(String key) {
		ResourceEntry entry = entries.get(key);
		if (entry==null){
			entry = new Term(key);
			entries.put(key, entry);
		} else{
			checkEntryTypeCompliance(Term.class,entry);
		}
		return (Term)entry;
	}

	private void checkEntryTypeCompliance(Class<? extends ResourceEntry> clazz,
			ResourceEntry entry) {
		if (!clazz.isInstance(entry)){
			throw new RuntimeException("Found incompatible entry with key '" + entry.getKey() + "'. Original type: " + entry.getClass().getSimpleName() + ", Offending type: " + clazz.getSimpleName());
		}
		
	}

	public Plural getPlural(String key) {
		ResourceEntry entry = entries.get(key);
		if (entry==null){
			entry = new Plural(key);
			entries.put(key, entry);
		} else{
			checkEntryTypeCompliance(Plural.class,entry);
		}
		return (Plural)entry;
	}

	public StringArray getStringArray(String key) {
		ResourceEntry entry = entries.get(key);
		if (entry==null){
			entry = new StringArray(key);
			entries.put(key, entry);
		} else{
			checkEntryTypeCompliance(StringArray.class,entry);
		}
		return (StringArray)entry;
	}

	public String getFileName() {		
		return name;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (ResourceEntry entry : entries.values()) {
			builder.append(entry.toString());
			builder.append("\r\n");
		}
		
		return builder.toString();
	}

	public List<ResourceEntry> getSortedEntries() {
		List<ResourceEntry> sortedEntries = new ArrayList<ResourceEntry>(entries.values());
		
		final Map<Class<? extends ResourceEntry>, Integer> classPriority = new HashMap<Class<? extends ResourceEntry>, Integer>(3);
		classPriority.put(Term.class, 0);
		classPriority.put(StringArray.class, 1);
		classPriority.put(TypedArray.class, 1);
		classPriority.put(Plural.class, 2);
		
		Collections.sort(sortedEntries, new Comparator<ResourceEntry>() {

			public int compare(ResourceEntry o1, ResourceEntry o2) {
				
				Integer pO1 = classPriority.get(o1.getClass());
				Integer pO2 = classPriority.get(o2.getClass());
				
				int res = pO1.compareTo(pO2);
				if (res==0){
					return o1.getKey().toUpperCase().compareTo(o2.getKey().toUpperCase());
				} else {
					return res;
				}
			}
		});
						
		return sortedEntries;
	}

	public TypedArray getTypedArray(String key) {
		ResourceEntry entry = entries.get(key);
		if (entry==null){
			entry = new TypedArray(key);
			entries.put(key, entry);
		} else{
			checkEntryTypeCompliance(TypedArray.class,entry);
		}
		return (TypedArray)entry;
	}
}
