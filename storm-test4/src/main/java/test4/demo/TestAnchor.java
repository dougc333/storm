package test4.demo;

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
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//how to test Tuple anchor? 
//Replay/ack logic, make a tuple fail w and w/o anchoring;
//an anchor is required for reliable messaging. test nonanchor vs. anchor
//
public class TestAnchor {
	static Logger LOG = Logger.getLogger(TestAnchor.class);

	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Integer next = 0;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (next > 100) {
				next = 0;
			}
			LOG.info("SPOUT EMITTING:" + next);
			collector.emit(new Values(next.toString()));
			next++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}

	}

	static class TestBolt extends BaseRichBolt {
		OutputCollector collector;
		TopologyContext context;
		Integer numIssued = 0;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("BOLT PREPARE");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("BOLT EXECUTE :" + input.getString(0) + " numIssued:"
					+ numIssued.toString());
			collector.emit(input, new Values());
			numIssued++;
			if (numIssued == 5) {
				collector.fail(input);
				LOG.info("BOLT EXECUTE FAIL CALLED!!!!!!!!!!!!!!!!!!!!!!");
			} else {
				collector.ack(input);
			}

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("boltoutput"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TestSpout(), 3);
			builder.setBolt("bolt", new TestBolt(), 3).shuffleGrouping("spout");

			Config config = new Config();
			config.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("testtop", config, builder.createTopology());
			cluster.activate("testtop");

			Utils.sleep(10000);

			cluster.deactivate("testtop");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
