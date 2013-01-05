package test4.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.TupleImpl;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//fields grouping test revisited. Need a simpler version 
public class TestStorm4a {
	Logger LOG = Logger.getLogger("class.TestStorm4a");

	static class TestSpout extends BaseRichSpout {
		private Integer next = 0;
		private TopologyContext context;
		private SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;

		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			ArrayList<Object> al = new ArrayList<Object>();
			al.add(next);
			next++;
			// Tuple tuple = new TupleImpl(context, al, 1, "1");

			// streamID: you define this, depends on fanout from spout->bolts
			// taskId: you define this, depends on fanout from spout->bolts
			// messageId: a unique id for each emitted packet, you can use this
			// for fanout

			// stream test is this higher the performance of a single stream?
			if (next % 2 == 0) {
				collector.emit("stream1", al, next);
			} else {
				collector.emit("stream2", al, next);
			}

			// #of taskIds is same as parallelism, choose 2 or 3?
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}

	}

	static class TestBolt extends BaseRichBolt {

		private OutputCollector collector;
		private TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			input.getFields();
			collector.emit((List<Object>) input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("words"));
		}

	}

	public void streamFanout() {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			builder.setSpout("spoutComponent", new TestSpout(), 1);
			builder.setBolt("boltComonent", new TestBolt(), 2).shuffleGrouping(
					"spoutComponent");

			Config config = new Config();
			config.setDebug(true);
			config.setNumWorkers(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("testTop", config, builder.createTopology());
			cluster.activate("testTop");

			Utils.sleep(10000);
			cluster.deactivate("testTop");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void taskIdFanout() {

	}

	public static void messageIdFanout() {

	}

	public static void main(String[] args) {
		try {

			new TestStorm4a().streamFanout();
			new TestStorm4a().taskIdFanout();
			new TestStorm4a().messageIdFanout();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}