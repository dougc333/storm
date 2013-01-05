package test4.demo.spouts;

import java.io.*;

public class TestMetadata implements Serializable {
	private static final long serialVersionUID = 1L;

	private static int num;

	public TestMetadata(int n) {
		this.num = n;
	}
}
