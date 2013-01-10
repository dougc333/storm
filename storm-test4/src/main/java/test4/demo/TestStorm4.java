package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
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
import backtype.storm.tuple.TupleImpl;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//packet + serialization
// https://groups.google.com/forum/?fromgroups=#!searchin/storm-user/kryo/storm-user/PwTCOM6zRD0/ESl24wozi94J
// baserich bolt needs ack
public class TestStorm4 {
	static Logger LOG = Logger.getLogger(TestStorm4.class);

	// each entry is 1k-3kb message
	// accountId/list of key/value pairs in String like OS:MSWINDOWS
	static HashMap<Integer, ArrayList<Object>> data = new HashMap<Integer, ArrayList<Object>>() {
		{
			put(0, new ArrayList() {
				{
					add("DEVICEID:23425426334534534534");
					add("OS:MSWINDOWS");
					add("STOLEN:NO");
					add("APPS:1000");
					add("CPU:INTEL");
					add("SPEED:2.4GHZ");
					add("NUMCPUS:10");
					add("MEMORY:10GB");
				}

			});

			put(1, new ArrayList() {
				{
					add("DEVICEID:234sfdsfs34534534");
					add("OS:LINUX");
					add("STOLEN:YES");
					add("APPS:10");
					add("CPU:AMD");
					add("SPEED:4GHZ");
					add("NUMCPUS:1");
					add("MEMORY:1GB");
				}

			});

			put(2, new ArrayList() {
				{
					add("DEVICEID:67868678686534534");
					add("OS:MACOS");
					add("STOLEN:NO");
					add("APPS:34");
					add("CPU:ARM");
					add("SPEED:4GHZ");
					add("NUMCPUS:2");
					add("MEMORY:1GB");
				}

			});

			put(3, new ArrayList() {
				{
					add("DEVICEID:0494586534534");
					add("OS:WINDOWS7");
					add("STOLEN:NO");
					add("APPS:0");
					add("CPU:INTEL");
					add("SPEED:.3GHZ");
					add("NUMCPUS:1");
					add("MEMORY:512M");
				}

			});
		}
	};

	static class TestBolt extends BaseRichBolt {
		OutputCollector collector;
		Integer next = 0;
		TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("TESTBOLT prepare threadID:"
					+ Thread.currentThread().getId() + " this.taskID:"
					+ context.getThisTaskId());
		}

		// how are the below fields set in the spout?
		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TESTBOLT execute");
			LOG.info("TESTBOLT tuple getSourceComponent:"
					+ input.getSourceComponent());
			LOG.info("TESTBOLT tuple getStreamID:" + input.getSourceStreamId());
			LOG.info("TESTBOLT tuple getSourceTask:" + input.getSourceTask());
			LOG.info("TESTBOLT tuple getMessageId:" + input.getMessageId());
			LOG.info("TESTBOLT tuple getValues()Size():"
					+ input.getValues().size());
			LOG.info("TESTBOLT TaskID:" + context.getThisTaskId());

			// parse out packet, timestamp, messageid

			long timestamp = input.getLongByField("timestamp");
			// how to get the packet?
			// how to use serialization?
			ArrayList<Object> al = (ArrayList) input.getValueByField("packet");
			Integer numPacket = (Integer) input.getValueByField("numMsg");

			LOG.info("BOLT EXECUTE timestamp:" + timestamp);
			LOG.info("BOLT EXECUTE numPacket:" + numPacket);
			LOG.info("BOLT EXECUTE arraylist size:" + al.size());

			// parse out stolen and output to email bolt

			// only output list of values
			Values val = new Values();
			val.add(numPacket);
			// val.add("");
			collector.emit(val);
			next++;
			collector.ack(input);
			// remove the tuple from redis(add edis to spout)

		}

		// this doesnt matter for our test. Any output will work
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("numpacket"));
		}

	}

	// tricky, this is why the interface is an arraylist of objects
	// tuple=packet,timestamp where tuple is a ArrayList and packet is
	// arraylist
	// to have the ack/replay work need to anchor the tuple?

	static class TestSpout extends BaseRichSpout {
		Integer numMsg = 0;
		SpoutOutputCollector collector;
		TopologyContext context;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
		}

		@Override
		public void nextTuple() {
			// list of list+timestamp.
			ArrayList<Object> al = new ArrayList<Object>();
			al.add(data.get(numMsg % 3));
			al.add(System.currentTimeMillis());
			al.add(numMsg);
			// need message ID for ack to work
			collector.emit(al, numMsg.intValue());
			numMsg++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("packet", "timestamp", "numMsg"));
		}

	}

	static class TestSpoutPerfObject extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
		}

		// emit PerfObject and verify bolt can see it
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			builder.setSpout("packet", new TestSpout(), 1);

			builder.setBolt("bolt", new TestBolt(), 2).setNumTasks(4)
					.shuffleGrouping("packet");

			// more packets w/more workers and more tasks?
			Config config = new Config();
			config.registerSerialization(PerfObject.class);
			// this should be included per docs
			config.registerSerialization(ArrayList.class);
			// config.setNumWorkers(30);
			config.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm4", config,
					builder.createTopology());
			cluster.activate("TestStorm4");

			Utils.sleep(10000);
			cluster.deactivate("TestStorm4");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
