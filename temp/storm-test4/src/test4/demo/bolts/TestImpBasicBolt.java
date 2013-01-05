package test4.demo.bolts;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.*;

import java.io.*;

public class TestImpBasicBolt implements IBasicBolt {
	static BufferedWriter bw;
	static int num = 0;

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields());
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// TODO Auto-generated method stub
		try {
			bw = new BufferedWriter(new FileWriter("/home/dc/IBasicBolt.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// TODO Auto-generated method stub
		try {
			bw.write("processing tuple:" + input.getString(0));
			bw.newLine();
			collector.emit("output", new Values("testoutput", num));
			num++;
			bw.write("emit output num:" + num);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
