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
	static String data[] = { "a", "b", "c", "d", "e", "f", "g", "h","i","j","k","l","m","n","o","p","q","r","s","t" };

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
			if (next == 20) {
				next = 0;
			}
			LOG.info("TestStorm1 SPOUT EMITTING:" + data[next] + " !!!!!!!");
			LOG.info("TestStorm1 SPOUT next:" + next);
			//add an ID have to put in. How does this behave?
			// look at SS code to duplicate
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
			collector.emit(input,new Values(input.getString(0)));
			collector.ack(input);
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

			builder.setSpout("letter", new TestStorm1Spout(), 8).setNumTasks(20);
			//builder.setSpout("secondletter", new TestStorm1Spout(), 5);
			//builder.setSpout("thirdletter", new TestStorm1Spout(), 5);
			
			builder.setBolt("id1", new TestStorm1Bolt(), 8).setNumTasks(20).shuffleGrouping(
					"letter");
			//builder.setBolt("id2", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"letter");
			//builder.setBolt("id3", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"secondletter");
			//builder.setBolt("id4", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"secondletter");
			//builder.setBolt("id5", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"thirdletter");
			//builder.setBolt("id6", new TestStorm1Bolt(), 10).shuffleGrouping(
			//		"thirdletter");

			Config conf = new Config();
			conf.setDebug(true);
			conf.setNumWorkers(6);
//			conf.setNumAckers(10);
//			conf.setMaxSpoutPending(10000);
			
//			LocalCluster cluster = new LocalCluster();
			StormSubmitter.submitTopology("TestStorm1", conf, builder.createTopology());
			//cluster.submitTopology("TestStorm1", conf, builder.createTopology());
			//Utils.sleep(1000000);
			//cluster.deactivate("TestStorm1");
			//cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
