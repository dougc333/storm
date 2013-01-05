package test4.demo;

//package storm.starter;

import java.util.Map;

import org.apache.log4j.Logger;

//make separate test program for FileSpout test
//import test4.demo.spouts.FileSpout;
//import storm.starter.storm.starter.TestStorm.TestBolt;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class TestStorm {
	static Logger LOG = Logger.getLogger(TestStorm.class);

	private static String data[] = { "a", "b", "c", "d", "e", "f", "g", "h",
			"i", "j" };

	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		Integer next = 0;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			LOG.info("TestSpout open()!!!!!!!!!!!!");
			this.collector = collector;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (next == 11) {
				next = 0;
			}
			String emitMe = data[next];
			LOG.info("TestSpout emitMe:" + emitMe + " !!!!!!!!!!!!");
			next++;
			collector.emit(new Values(emitMe));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("word"));
		}

	}

	public static class TestBolt extends BaseRichBolt {
		OutputCollector collector;

		@Override
		public void prepare(Map conf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		@Override
		public void execute(Tuple tuple) {
			LOG.info("TestBolt execute!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
			// put in another test class test ack and fail
			// collector.ack(tuple);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			// builder.setSpout("letter", new TestSpout());
			builder.setSpout("word", new TestSpout(), 2);
			builder.setBolt("id1", new TestBolt(), 3).shuffleGrouping("word");
			builder.setBolt("id2", new TestBolt(), 2).shuffleGrouping("id1");

			// builder.setSpout("testspout", new FileSpout());
			// builder.setBolt("firstBolt", new TestBolt());

			Config conf = new Config();
			conf.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("test", conf, builder.createTopology());
			Utils.sleep(10000);
			cluster.killTopology("test");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
