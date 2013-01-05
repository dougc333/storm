package test4.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import test4.demo.TestStorm2.TestSpout;
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

import com.esotericsoftware.minlog.Log;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

//import javax.mail.*;
import java.util.Properties;

//test email  connection
//
public class TestStorm3 {
	private static Properties props;
	// private static Session session;
	// private static Store store;

	private static final String SMTP_HOST_NAME = "smpt.gmail.com";
	private static final String SMTP_HOST_PORT = "465";
	private static final String SMTP_AUTH_USER = "dougchang25";
	private static final String SMTP_AUTH_PWD = "H1onglam";

	// represents accountId/list of key/value pairs in String like OS:MSWINDOWS
	static HashMap<Integer, List<String>> data = new HashMap<Integer, List<String>>() {
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
			Log.info("TestStorm3 sending:" + sendMe + "  !!!!!!!!!!!");
			collector.emit(new Values(send));
			collector.emit(new Values("TestStorm3 spout numPackets:"
					+ numPackets));
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
			// send email?
		}

		@Override
		public void execute(Tuple input) {
			// strip out device number and send packets with devicenumber:field
			// name: field value
			Log.info("FormatBolt input(0):" + input.getString(0));
			Iterable<String> it = Splitter.on(",").split(input.getString(0));
			String deviceID = null;
			List<String> list = new ArrayList<String>();
			Iterator<String> iter = it.iterator();
			while (iter.hasNext()) {
				String processMe = iter.next();
				Log.info("TESTSTORM3 processMe:" + processMe);
				if (processMe.startsWith("DEVICEID:")) {
					Log.info("TESTSTORM3 execute found DEVICEID!!!!!");
					Log.info("TESTSTORM3 substring:"
							+ processMe.substring(9, processMe.length()));
					deviceID = processMe.substring(9, processMe.length());
				} else {
					list.add(processMe);
				}
			}

			for (String str : list) {
				String see = deviceID + ":" + str;
				Log.info("FORMAT BOLT execute emitting:" + see);
				collector.emit(new Values(see));
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

	// this has no message id, make sure this isn't part of ack/retry
	// is there a separate interface for Bolts?
	static class EmailBolt extends BaseRichBolt {

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			try {
				props = new Properties();
				props.put("mail.store.protocol", "imaps");
				props.put("mail.imap.host", "imap.gmail.com");
				props.put("mail.imap.port", "993");
				props.put("mail.imap.connectiontimeout", "5000");
				props.put("mail.imap.timeout", "5000");
				// session = Session.getDefaultInstance(props, null);
				// store = session.getStore("imaps");
				// store.connect("imap.gmail.com", "dougchang25", "H1onglam");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// message,email address.
		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	// set stolen to YES from NO,
	// send tuple to email bolt for email
	static class AlertBolt extends BaseRichBolt {
		OutputCollector collector;
		String DEVICEID = "0494586534534";

		// substitute for reading in config
		// webserver to get config
		private void initConfig() {

		}

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			// read server for config data or read file for config file
			this.collector = collector;
			initConfig();
		}

		@Override
		public void execute(Tuple input) {
			// look for device id then set stolen to true from false
			// we can emit a message to a separate email bolt or we can do this
			// here.

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("alertoutput"));
		}

	}

	public static void main(String[] args) {
		try {

			TopologyBuilder builder = new TopologyBuilder();
			TestSpout spout = new TestSpout();
			builder.setSpout("packet", spout);
			builder.setBolt("words", new FormatBolt())
					.shuffleGrouping("packet");

			Config conf = new Config();
			conf.setDebug(true);
			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology("TestStorm3", conf, builder.createTopology());
			Utils.sleep(30000);

			cluster.deactivate("TestStorm3");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
