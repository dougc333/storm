package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.jetty.webapp.Configuration;

import com.esotericsoftware.minlog.Log;
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

//try using an iterator
//
//
public class TestStorm2 {
	static Logger LOG = Logger.getLogger(TestStorm2.class);

	// represents accountId/list of key/value pairs in String like OS:MSWINDOWS
	static HashMap<Integer, List<String>> data = new HashMap<Integer, List<String>>() {
		{
			put(0, new ArrayList() {
				{
					add("OS:MSWINDOWS");
					add("DEVICEID:23425426334534534534");
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
					add("OS:LINUX");
					add("DEVICEID:234sfdsfs34534534");
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
					add("OS:MACOS");
					add("DEVICEID:67868678686534534");
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
					add("OS:WINDOWS7");
					add("DEVICEID:0494586534534");
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

	// send with accountid/field
	static class TestSpout extends BaseRichSpout {
		static Integer next = 0;
		static Integer numPackets = 0;
		SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;

		}

		@Override
		public void nextTuple() {
			if (next == 4) {
				// restart
				next = 0;
			}

			List<String> sendMe = data.get(next);
			Joiner joiner = Joiner.on(",").skipNulls();
			String send = joiner.join(sendMe);
			next++;
			numPackets++;
			collector.emit(new Values(send));
			collector.emit(new Values("numPackets:" + numPackets));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("packet"));
		}

	}

	// parse out the spout fields/values and send reformatted
	// w/DEVICEESN/field:value
	// this demonstrates the parallelism
	static class FormatBolt extends BaseRichBolt {
		Integer numFields = 0;
		OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;

		}

		@Override
		public void execute(Tuple input) {
			// parse out tuples and send deviceid with each field
			List<Object> vals = input.getValues();
			for (Object o : vals) {
				LOG.info("TestBolt execute tuple value:" + o.toString());
			}

		}

		// problem is how we identify the device?
		// need deviceid with each field
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("devicefields"));
		}

	}

	// see output of fields and packets
	public static void main(String[] args) {
		try {

			TopologyBuilder builder = new TopologyBuilder();
			TestSpout spout = new TestSpout();
			builder.setSpout("packet", spout);
			builder.setBolt("words", new FormatBolt());

			Config conf = new Config();
			conf.setDebug(true);
			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology("TestStorm2", conf, builder.createTopology());
			Utils.sleep(30000);

			cluster.deactivate("TestStorm2");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
