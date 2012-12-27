package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;

public class TestJoin {

	private static Map<Integer, List<String>> data = new HashMap<Integer, List<String>>() {
		{
			put(0, new ArrayList() {
				{
					add("a");
					add("aa");
					add("aaa");
				}
			});
			put(1, new ArrayList() {
				{
					add("b");
					add("bb");
					add("bbb");
				}
			});
			put(2, new ArrayList() {
				{
					add("c");
					add("cc");
					add("ccc");
				}
			});
		}
	};

	public static void main(String[] args) {
		try {
			Set<Integer> s = data.keySet();
			Iterator<Integer> it = s.iterator();
			while (it.hasNext()) {
				Integer key = it.next();
				List<String> l = data.get(key);
				Joiner joiner = Joiner.on(",").skipNulls();
				System.out.println("joiner:" + joiner.join(l));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
