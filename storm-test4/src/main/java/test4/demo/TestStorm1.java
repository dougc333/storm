package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

import backtype.storm.LocalCluster;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.*;

import java.util.List;
//infinite loop, test storm ui
//test jedis i/f
//test design patterns https://github.com/nathanmarz/storm/wiki/Common-patterns
import org.apache.log4j.*;
import com.google.common.base.Joiner;

public class TestStorm1 {
	private static Jedis jedis;
	private static Logger LOG = Logger.getLogger("TestStorm1");

	// Tuplesa are list of fields, fields are list of Java Objects or Strings or
	// Integers. Field has iterator interface.
	// http://nathanmarz.github.com/storm/doc/backtype/storm/tuple/Fields.html
	//
	private static Map<Integer, List<String>> data = new HashMap<Integer, List<String>>() {
		{
			put(2000, new ArrayList() {
				{
					add("a");
					add("aa");
					add("aaa");
				}
			});
			put(2001, new ArrayList() {
				{
					add("b");
					add("bb");
					add("bbb");
				}
			});
			put(2002, new ArrayList() {
				{
					add("c");
					add("cc");
					add("ccc");
				}
			});
		}
	};

	//spout to read from redis
	public static class TestSpout extends {
		
		
	}
	
	public static class TestBolt extends BaseRichBolt {
		private OutputCollector collector;

		// test if data in redis
		public void initRedis() {
			try {
				// init data from maps
				Set<Integer> s = data.keySet();
				Iterator<Integer> it = s.iterator();
				while (it.hasNext()) {
					Integer key = (Integer) it.next();
					List<String> li = data.get(key);
					Joiner joiner = Joiner.on(",").skipNulls();
					jedis.set(key.toString(), joiner.join(li));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void prepare(Map conf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
			jedis = new Jedis("localhost");
			jedis.connect();

			if (jedis.get("a") == null) {
				// test read into redis, not true on null connection string
				initRedis();
			} else {
				LOG.info("redis get 2000:" + jedis.get("2000"));
			}
		}

		@Override
		public void execute(Tuple tuple) {
			// this has anchoring, the difference is on the failure, will replay
			// to the root
			collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
			collector.ack(tuple);
		}

		@Override
		// where does Fields match up with?
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	// baserich bolt is for emitting tuple for each input tuple from spout
	public static class JedisBolt extends BaseRichBolt {
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

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder top = new TopologyBuilder();
			// the shuffle grouping has to match declarer.declare from TestBolt
			top.setBolt("firstbolt", new TestBolt(), 2).shuffleGrouping("word");
			// the second bolt matches the first bolt output. A shuffle grouping
			// renames output?
			top.setBolt("secondbolt", new TestBolt(), 2).shuffleGrouping(
					"firstbolt");

			Config conf = new Config();
			conf.setDebug(true);
			// settings for slots vs. executors/tasks

			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology(arg0, arg1, arg2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
