package test4.demo;

//package storm.starter;

import java.util.Map;

import test4.demo.spouts.FileSpout;
//import storm.starter.storm.starter.TestStorm.TestBolt;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class TestStorm {

	public static class TestBolt extends BaseRichBolt {
		OutputCollector _collector;

		@Override
		public void prepare(Map conf, TopologyContext context,
				OutputCollector collector) {
			_collector = collector;
		}

		@Override
		public void execute(Tuple tuple) {
			_collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
			_collector.ack(tuple);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			// builder.setSpout("word", new FileSpout());
			 builder.setBolt("id1", new TestBolt(),
			 3).shuffleGrouping("word");
			 builder.setBolt("id2", new TestBolt(), 2).shuffleGrouping("id1");

			//builder.setSpout("testspout", new FileSpout());
			//builder.setBolt("firstBolt", new TestBolt());

			Config conf = new Config();
			conf.setDebug(true);

			if (args != null && args.length > 0) {
				conf.setNumWorkers(3);

				StormSubmitter.submitTopology(args[0], conf,
						builder.createTopology());
			} else {

				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("test", conf, builder.createTopology());
				Utils.sleep(10000);
				cluster.killTopology("test");
				cluster.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
