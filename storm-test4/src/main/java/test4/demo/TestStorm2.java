package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.google.common.base.Joiner;

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

//test using Redis/Jedis. Write data into jedis
//dont add more into here, goal is to test configuration
//if this fails Redis probably not started
//
public class TestStorm2 {
	private static Jedis jedis;
	private static Logger LOG = Logger.getLogger("TestStorm2");

	// Tuples are list of fields, fields are list of Java Objects or Strings or
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

	// spout to read from redis
	public static class TestSpout extends BaseRichSpout {
		private Integer readKey = 0;
		SpoutOutputCollector collector;
		Jedis jedis;

		// test if data in redis
		public void initRedis() {
			try {
				LOG.info("TestStorm2 initRedis()!!!!!!!!!!!!!!!!!!!!");
				// init data from maps
				Set<Integer> s = data.keySet();
				Iterator<Integer> it = s.iterator();
				while (it.hasNext()) {
					Integer key = (Integer) it.next();
					List<String> li = data.get(key);
					Joiner joiner = Joiner.on(",").skipNulls();

					String writeMe = joiner.join(li);

					LOG.info("TestStorm2 initReids writing key:"
							+ key.toString() + " redis value:" + writeMe);

					jedis.set(key.toString(), writeMe);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			LOG.info("CALLING TESTSPOUT OPEN!!!!!!!!!!!!! SAME AS BOLT PREPARE????");
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
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (readKey == 0) {
				readKey = 2000;
			} else if (readKey == 2003) {
				readKey = 2000;
			}
			String output = jedis.get(readKey.toString());
			readKey++;
			// emit list of values, how does this turn from joiner to splitter?
			collector.emit(new Values(output));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("words"));
		}
	}

	public static class TestBolt extends BaseRichBolt {
		private OutputCollector collector;

		@Override
		public void prepare(Map conf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		@Override
		public void execute(Tuple tuple) {
			// this has anchoring, the difference is on the failure, will replay
			collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
			collector.ack(tuple);
		}

		@Override
		// where does Fields match up with?
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder top = new TopologyBuilder();
			top.setSpout("words", new TestSpout(), 1);
			// the shuffle grouping has to match declarer.declare from TestBolt
			top.setBolt("firstbolt", new TestBolt(), 2)
					.shuffleGrouping("words");
			// the second bolt matches the first bolt output. A shuffle grouping
			// renames output?
			top.setBolt("secondbolt", new TestBolt(), 2).shuffleGrouping(
					"firstbolt");

			Config conf = new Config();
			conf.setDebug(true);

			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology("TestStorm2", conf, top.createTopology());
			Utils.sleep(10000);
			cluster.killTopology("TestStorm2");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
