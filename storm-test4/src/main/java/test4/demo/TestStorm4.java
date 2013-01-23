package test4.demo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import java.util.concurrent.atomic.AtomicInteger;

//packet + serialization
// https://groups.google.com/forum/?fromgroups=#!searchin/storm-user/kryo/storm-user/PwTCOM6zRD0/ESl24wozi94J
// baserich bolt needs ack
public class TestStorm4 {
	static Logger LOG = Logger.getLogger(TestStorm4.class);
	static Integer numMsg = 0;

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
		String refId;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			this.refId = this.toString();
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

	static class TestSpoutPerf extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Integer numMsg = 0;
		AtomicInteger numAtomic = new AtomicInteger(0);
		long firstPacket;
		String refId;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			this.refId = this.toString();
			LOG.info("TEST SPOUT PERF refID:" + this.refId);
		}

		// emit PerfObject and verify bolt can see it
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			PerfObject perf = new PerfObject();

			if (numMsg == 0) {
				firstPacket = System.currentTimeMillis();
			}

			perf.setList(data.get(numMsg % 3));
			perf.setMsgNum(numMsg);
			perf.setTimestamp(System.currentTimeMillis());
			numMsg++;

			// this is unanchored, verify we get perf in bolt
			collector.emit(new Values(firstPacket, perf));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("startTime", "packet"));
		}

	}

	static class TestBoltPerf extends BaseRichBolt {
		Map conf;
		TopologyContext context;
		OutputCollector collector;
		HashMap<String, String> alerts = new HashMap<String, String>();

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.conf = stormConf;
			this.context = context;
			this.collector = collector;
			initAlerts();
		}

		private void initAlerts() {
			alerts.put("23425426334534534534", null);
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub

			PerfObject perf = (PerfObject) input.getValueByField("packet");
			long startTime = input.getLongByField("startTime");

			ArrayList<Object> packet = perf.getList();
			// measures time for packet to get to bolt only
			LOG.info("TESTBOLT PERF startTime:" + startTime);
			LOG.info("TESTBOLT PERF timestamp:" + perf.getTimestamp());
			LOG.info("TESTBOLT PERF MsgNum:" + perf.getMsgNum());
			LOG.info("TESTBOLT PERF size:" + perf.getList().size());

			// bolt processing for alert.. test for ESN then send emit to
			// alert bolt
			for (Object o : packet) {
				if (alerts.get(o.toString()) != null
						&& perf.getMsgNum() % 1000 == 0) {
					LOG.info("TESTBOLT ALERT FOUND!!!!!!!!!");
					// can you do this? send a list? prob not.
					collector.emit(new Values(startTime, perf));
				}
			}
			// end time for packet, print perf #s here:
			long endTime = System.currentTimeMillis();
			double elapsed = (endTime - startTime) / 1000.0;

			LOG.info("TESTBOLT packets/sec:" + perf.getMsgNum() / elapsed);

			// collector.ack(input); if this is anchored
			// test perf difference if anchored, can use msgid instead of
			// keeping numpackets
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("startTime", "alert"));
		}

	}

	static class AlertBolt extends BaseRichBolt {
		Map conf;
		TopologyContext context;
		OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.conf = stormConf;
			this.context = context;
			this.collector = collector;
			LOG.info("ALERT BOLT PREPARE");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			// decode alert, send to webpage?
			try {
				LOG.info("ALERT BOLT EXECUTE");

				PerfObject perf = (PerfObject) input.getValueByField("alert");

				ArrayList<Object> list = perf.getList();
				long startTime = input.getLongByField("startTime");

				LOG.info("ALERT BOLT startTime:" + startTime);

				URL url;
				url = new URL("http://localhost:7080");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Language", "en-US");

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("firstparam", "THIS IS ALERT!!!!!");

				StringBuffer requestParams = new StringBuffer();

				if (params != null && params.size() > 0) {
					Iterator<String> paramIterator = (Iterator<String>) params
							.keySet();
					while (paramIterator.hasNext()) {
						String key = paramIterator.next();
						String value = params.get(key);
						requestParams.append(URLEncoder.encode(key, "UTF-8"));
						requestParams.append("=").append(
								URLEncoder.encode(value, "UTF-8"));
						requestParams.append("&");
					}
				}

				OutputStreamWriter wr = new OutputStreamWriter(
						connection.getOutputStream());
				wr.write(requestParams.toString());
				// wr.writeBytes("alert recieved: print out the packet");
				wr.flush();
				wr.close();

				java.io.InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(
						new java.io.InputStreamReader(is));

				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append("\r");
				}
				rd.close();
				LOG.info("ALERT RESPONSE:" + response.toString());

				long current = System.currentTimeMillis();
				double elapsed = (current - startTime) / 1000.0;
				LOG.info("ALERT BOLT time for 1k packets:" + elapsed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("none"));
		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();

			// builder.setSpout("packet", new TestSpout(), 1);
			// builder.setBolt("bolt", new TestBolt(), 2).setNumTasks(4)
			// .shuffleGrouping("packet");

			// serialization test
			// num threads, tasks
			builder.setSpout("packet", new TestSpoutPerf(), 2);
			builder.setBolt("bolt", new TestBoltPerf(), 4).setNumTasks(4)
					.shuffleGrouping("packet");
			builder.setBolt("alertbolt", new AlertBolt(), 4).setNumTasks(4)
					.shuffleGrouping("bolt");

			// numPackets performance test, with varying spouts/bolts

			// more packets w/more workers and more tasks?
			Config config = new Config();
			config.registerSerialization(PerfObject.class);
			// this should be included per docs
			config.registerSerialization(ArrayList.class);
			// num workers, same as TOPOLOGY_WORKERS in Config
			config.setNumWorkers(3);
			config.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm4", config,
					builder.createTopology());
			cluster.activate("TestStorm4");

			Utils.sleep(1000000);
			cluster.deactivate("TestStorm4");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
