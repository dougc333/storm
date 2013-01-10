package test4.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

import com.esotericsoftware.minlog.Log;
import com.google.common.base.Splitter;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

//test measure perf, use outputCollector settime in tuple, 
//send to bolt, then to another bolt and calculate difference
public class TestStorm5 {

	private static final String SMTP_HOST_NAME = "smpt.gmail.com";
	private static final String SMTP_HOST_PORT = "465";
	private static final String SMTP_AUTH_USER = "dougchang25";
	private static final String SMTP_AUTH_PWD = "H1onglam";

	static class EmailSpout extends BaseRichSpout {

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub

		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	// this only executes when a tuple is received
	static class EmailBolt extends BaseRichBolt {
		private static Properties props;
		private static Session session;
		private static Store store;

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
				session = Session.getDefaultInstance(props, null);
				store = session.getStore("imaps");
				store.connect("imap.gmail.com", "dougchang25", "H1onglam");
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

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
