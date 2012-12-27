package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

// test fields grouping and global grouping to 
// parallelize tasks then merge into single bolt
// need this for global count and transactional words,
// using noneGrouping and fieldsGrouping
// create 2 hash maps, read them into redis and create 2 streams from them
//
// we used 2 spouts and 2 bolts, we can combine into 1 spout. What is difference? 
//
//
public class TestStormFieldsGrouping {
	private static Map<Integer, List<String>> data1 = new HashMap<Integer, List<String>>() {
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

	private static Map<Integer, List<String>> data2 = new HashMap<Integer, List<String>>() {
		{
			put(3000, new ArrayList() {
				{
					add("1");
					add("11");
					add("111");
				}
			});
			put(3001, new ArrayList() {
				{
					add("2");
					add("22");
					add("222");
				}
			});
			put(3002, new ArrayList() {
				{
					add("3");
					add("33");
					add("333");
				}
			});
		}
	};

	public static class TestSpoutLetters extends BaseRichSpout {
		private static Jedis jedis;
		private static Integer nextRead = 2000;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			try {
				jedis = new Jedis("localhost");
				jedis.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (nextRead < 2003) {
				String db = jedis.get(nextRead.toString());
				collector.emit(new Values(db));
			} else {
				nextRead = 2000;
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("words"));
		}

	}

	public static class TestSpoutNumbers extends BaseRichSpout {
		private Jedis jedis;
		private Integer nextRead = 3000;
		private SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			try {
				this.collector = collector;
				jedis = new Jedis("localhost");
				jedis.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (nextRead < 3003) {
				String db = jedis.get(nextRead.toString());
				collector.emit(new Values(db));
				nextRead++;
			} else {
				nextRead = 3000;
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("numbers"));
		}

	}

	// process a,aa,aaa
	public static class TestBolt1 extends BaseRichBolt {

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub

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

	// process 1,11,111
	public static class TestBolt2 extends BaseRichBolt {

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub

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

	// do we do 2 spouts or 1 spout with a bunch of queues like
	// MemoryTransSpout? 2 spouts, then you can see these merge into 1 stream.
	// print stats on number of tuples processed?
	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
