package test4.demo.bolts;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.*;

import java.io.*;

public class ExtBaseRichBolt extends BaseRichBolt {
	private static BufferedWriter bw;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		// TODO Auto-generated method stub
		try {
			bw = new BufferedWriter(new FileWriter("/home/dc/BaseRichBolt.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	// the APIs change!!!
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		try {
			bw.write("tuple source component:"
					+ input.getSourceComponent().toString());
			bw.newLine();
			bw.write("tuple sourceStreamId:" + input.getSourceStreamId());
			bw.newLine();
			bw.write("tuple sourceTask " + input.getSourceTask());
			bw.newLine();
			java.util.List<Object> valueList = input.getValues();
			for (Object o : valueList) {
				bw.write("tuple value:" + o.toString());
				bw.newLine();
			}
			Fields fields = input.getFields();
			java.util.List<String> fieldList = fields.toList();
			for (String s : fieldList) {
				bw.write("tuple field:" + s);
				bw.newLine();
			}
			//
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

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields());
	}

}
