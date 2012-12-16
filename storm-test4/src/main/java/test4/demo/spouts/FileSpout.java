package test4.demo.spouts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class FileSpout extends BaseRichSpout {
	FileInputStream fs;
	SpoutOutputCollector _collector;
	BufferedReader br;

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		_collector = collector;
		try {
			fs = new FileInputStream(new File("/home/dc/input.txt"));
			br = new BufferedReader(new FileReader("/home/dc/input.txt"));
			br.mark(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {
		Utils.sleep(100);
		try {
			String emitMe = br.readLine();
			if (emitMe.equals("null")) {
				br.reset();
				emitMe = "header lastfield lastvalue";
			}
			_collector.emit(new Values(emitMe));

		} catch (Exception e) {
			e.printStackTrace();
		}
		// String sentence = sentences[_rand.nextInt(sentences.length)];
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

}
