package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

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
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//take multifield output from spout as in TestStorm6 to test field grouping
//2 bolts, each fieldGrouping by field1, field2
public class TestStorm7 {
	static Logger Log = Logger.getLogger(TestStorm7.class);

	static class TestSpout extends BaseRichSpout {
		TopologyContext context;
		SpoutOutputCollector collector;
		Integer next = 0;

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
			if (next > 10) {
				next = 0;
			}
			Values val = new Values();
			val.add(next);
			next++;
			val.add(next);
			collector.emit(val);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("fields1", "fields2"));
		}

	}

	static class TestBolt extends BaseRichBolt {
		TopologyContext context;
		OutputCollector collector;

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
			Log.info("BOLT EXECUTE:" + input.getInteger(0) + " ,"
					+ input.getInteger(1) + " task:" + context.getThisTaskId());
			collector
					.emit(new Values(input.getInteger(0), input.getInteger(1)));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("fields1", "fields2"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spoutID", new TestSpout(), 3);
			// builder.setBolt("boltID", new TestBolt(), 3).shuffleGrouping(
			// "spoutID");

			builder.setBolt("boltID", new TestBolt(), 3).fieldsGrouping(
					"spoutID", new Fields("fields1", "fields2"));

			Config config = new Config();
			config.setDebug(true);

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
}
