package test4.demo.bolts;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;

import java.io.*;
import java.util.*;

//debug bolt write to file
public class WriteToFileBolt implements IBasicBolt {
	static BufferedWriter bw;
	static String test;

	public WriteToFileBolt() {
	}

	public WriteToFileBolt(String testId) {
		test = testId;
	}

	public void prepare(Map conf, TopologyContext tc) {
		try {
			bw = new BufferedWriter(new FileWriter(
					"/home/dc/WriteToFileBoltOutput.txt"));
			bw.write("bolt debug componentId:" + tc.getThisComponentId()
					+ " taskId:" + tc.getThisTaskId() + " index:"
					+ tc.getThisTaskIndex());
			if (test != null) {
				bw.newLine();
				bw.write("test: " + test);
				bw.newLine();
			}
			bw.newLine();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// String st = s + tc.getThisComponentId() + " : " + tc.getThisTaskId();
		try {
			for (Object o : tuple.getValues()) {
				bw.write(o.toString());
				bw.write("\t");
			}
			bw.newLine();
			bw.flush();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cleanup() {
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("asdf"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
}
