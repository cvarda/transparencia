package org.cvarda.transparencia.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepeatedStrings {
	
	public static List<String> cleanUp(List<String> strings) {
		return cleanUp(strings, "?string? (?repeat?x)", null);
	}
	
	public static List<String> cleanUp(List<String> strings, String pattern) {
		return cleanUp(strings, pattern, null);
	}
	
	public static List<String> cleanUp(List<String> strings, String pattern, Comparator<String> comparator) {
		List<String> list = new ArrayList<String>();
		list.addAll(strings);
		
		if (comparator == null) {
			Collections.sort(list);
		} else {
			Collections.sort(list, comparator);
		}
		
		Map<String, Integer> m = new LinkedHashMap<String, Integer>();
		for (String s : list) {
			m.put(s, m.containsKey(s) ? m.get(s) + 1 : 1);
		}
		list.clear();
		
		for (String string : m.keySet()) {
			list.add(pattern.replace("?string?", string).replace("?repeat?", String.valueOf(m.get(string))));
		}
		
		return list;
	}
	
}
