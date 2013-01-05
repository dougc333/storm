package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.*;

import java.util.List;
//infinite loop, test storm ui
//test jedis i/f
//test design patterns https://github.com/nathanmarz/storm/wiki/Common-patterns
import org.apache.log4j.*;

import com.esotericsoftware.minlog.Log;
import com.google.common.base.Joiner;

// we should add a test before this w/o jedis 
//test jedis in spout
// test tuples from spout to bolt.
// test writing to jedis, requires ICommitter bolt interface
// test fields/all grouping how to direct tuples to specific bolt? 
// test bolt join, important for parallelism
public class TestStorm1 {
	static Logger LOG = Logger.getLogger(TestStorm1.class);
	static String data[] = { "a", "b", "c", "d", "e", "f", "g", "h" };

	static class TestStorm1Spout extends BaseRichSpout {
		Integer next = 0;
		SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (next == 8) {
				next = 0;
			}
			LOG.info("TestStorm2 SPOUT EMITTING:" + data[next] + " !!!!!!!");
			LOG.info("TestStorm2 SPOUT next:" + next);
			collector.emit(new Values(data[next]));
			next++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("letter"));
		}

	}

	static class TestStorm1Bolt extends BaseRichBolt {
		OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TEST BOLT EXECUTE!!!!!!!!");
			collector.emit(new Values(input.getString(0)));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("bolt1output"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			builder.setSpout("letter", new TestStorm1Spout(), 1);
			builder.setBolt("id1", new TestStorm1Bolt(), 1).shuffleGrouping(
					"letter");

			Config conf = new Config();
			conf.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm1", conf, builder.createTopology());

			Utils.sleep(10000);
			cluster.deactivate("TestStorm1");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
