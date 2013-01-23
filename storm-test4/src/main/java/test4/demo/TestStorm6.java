package test4.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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

//Storm config test, print out config of TopologyContext, 
//etc.. .
public class TestStorm6 {
	static Logger LOG = Logger.getLogger("TestStorm6.class");

	static class TestSpout extends BaseRichSpout {
		Integer next = 0;
		TopologyContext context;
		SpoutOutputCollector collector;
		Integer numLoops = 1;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
			// next = 0;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (numLoops < 100) {
				if (next > 100) {
					next = 0;
				}

				LOG.info("Spout EMITTING:" + next + " numLoops:" + numLoops);
				// ArrayList<Object> list = new ArrayList<Object>();
				// LOG.info("SPOUT EMITTING" + next + " ," + (next + 1));
				// list.add(next);
				// list.add(next++);
				Values v = new Values();
				v.add(next);
				v.add(next + 1);
				collector.emit(v);
				next = next + 2;
				numLoops++;
			}
			// numSpoutCalled++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			// this is different than "first,second"
			declarer.declare(new Fields("a", "b"));
		}
	}

	static class TestBolt extends BaseRichBolt {
		private TopologyContext context;
		private OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
			LOG.info("TESTBOLT PREPARE");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TestBolt EXECUTE tuple size:" + input.size());
			java.util.List<Object> list = input.getValues();
			LOG.info("Bolt EXECUTE lIst(0):" + list.get(0).toString());
			Integer next = new Integer(list.get(0).toString());
			// LOG.info("Bolt EXECUTE lIst(1):" + list.get(1).toString());

			for (Object o : list) {
				LOG.info("TESTBOLT execute values:" + o.toString());
			}

			Fields fields = input.getFields();
			Iterator<String> it = fields.iterator();
			while (it.hasNext()) {
				String f = it.next();
				LOG.info("TestBolt execute field:" + f);

			}
			Values v = new Values();
			v.add(next);
			v.add(next + 1);
			collector.emit(v);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("first", "second"));
		}

	}

	static class TestBolt1 extends BaseRichBolt {
		OutputCollector collector;
		TopologyContext context;
		Integer numBolt = 0;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("TESTBOLT1 prepare");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TESTBOLT1 execute");
			Values v = new Values();
			v.add(input.getString(0));
			v.add("numBolt" + numBolt.toString());
			collector.emit(v);
			numBolt++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("first", "second"));
		}

	}

	static void printConfig(Config config) {

		Set<String> set = config.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			LOG.info("key:" + key + " value:" + config.get(key));
		}

		LOG.info("Config.DEV_ZOOKEEPER_PATH" + Config.DEV_ZOOKEEPER_PATH);
		LOG.info(Config.DRPC_INVOCATIONS_PORT);
		LOG.info(Config.DRPC_PORT);
		LOG.info(Config.DRPC_REQUEST_TIMEOUT_SECS);
		LOG.info(Config.DRPC_SERVERS);
		LOG.info(Config.JAVA_LIBRARY_PATH);
		LOG.info(Config.NIMBUS_CHILDOPTS);
		LOG.info(Config.NIMBUS_CLEANUP_INBOX_FREQ_SECS);
		LOG.info(Config.NIMBUS_FILE_COPY_EXPIRATION_SECS);
		LOG.info(Config.NIMBUS_HOST);
		LOG.info(Config.NIMBUS_INBOX_JAR_EXPIRATION_SECS);
		LOG.info(Config.NIMBUS_MONITOR_FREQ_SECS);
		LOG.info(Config.NIMBUS_REASSIGN);
		LOG.info(Config.NIMBUS_SUPERVISOR_TIMEOUT_SECS);
		LOG.info(Config.NIMBUS_TASK_LAUNCH_SECS);
		LOG.info(Config.NIMBUS_TASK_TIMEOUT_SECS);
		LOG.info(Config.NIMBUS_THRIFT_PORT);
		LOG.info(Config.STORM_CLUSTER_MODE);
		LOG.info(Config.STORM_ID);
		LOG.info(Config.STORM_LOCAL_DIR);
		LOG.info(Config.STORM_LOCAL_HOSTNAME);
		LOG.info(Config.STORM_LOCAL_MODE_ZMQ);
		LOG.info(Config.STORM_SCHEDULER);
		LOG.info(Config.STORM_ZOOKEEPER_AUTH_PAYLOAD);
		LOG.info(Config.STORM_ZOOKEEPER_AUTH_SCHEME);
		LOG.info(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT);
		LOG.info(Config.STORM_ZOOKEEPER_PORT);
		LOG.info(Config.STORM_ZOOKEEPER_RETRY_INTERVAL);
		LOG.info(Config.STORM_ZOOKEEPER_RETRY_TIMES);
		LOG.info(Config.STORM_ZOOKEEPER_ROOT);
		LOG.info(Config.STORM_ZOOKEEPER_SERVERS);
		LOG.info(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT);
		LOG.info(Config.SUPERVISOR_CHILDOPTS);
		LOG.info(Config.SUPERVISOR_ENABLE);
		LOG.info(Config.SUPERVISOR_HEARTBEAT_FREQUENCY_SECS);
		LOG.info(Config.SUPERVISOR_SCHEDULER_META);
		LOG.info(Config.SUPERVISOR_SLOTS_PORTS);
		LOG.info(Config.SUPERVISOR_WORKER_START_TIMEOUT_SECS);
		LOG.info(Config.SUPERVISOR_WORKER_TIMEOUT_SECS);
		LOG.info("Config.TOPOLOGY_WORKERS:"
				+ config.get("Config.TOPOLOGY_WORKERS"));
		LOG.info("----------------------------------");
	}

	// the spout and bolt threads are different?
	// default config:3 TESTBOLTPREPARE
	// config.setNumWorkers(3);: 2 TESTBOLTPREPARE
	// config.put(Config.TOPOLOGY_WORKERS, 3); : 2 TESTBOLTPREPARE
	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spoutoutput", new TestSpout(), 1);
			builder.setBolt("testbolt", new TestBolt(), 4).shuffleGrouping(
					"spoutoutput");

			builder.setBolt("testbolt1", new TestBolt(), 4).shuffleGrouping(
					"testbolt");
			Config config = new Config();
			// this overrides the parameter in setBolt, not sure where threads
			// come from. not a multiply
			// config.put(Config.TOPOLOGY_WORKERS, 3);
			// make this lower and see if we get more bolt logs
			// config.put("MAX_SPOUT_PENDING", 100);
			// config.put("TOPOOGY_ACKERS", 10); // change this to see if we get
			// more bolt action
			// config.put("TOPOLOGY_MESSAGE_TIMEOUT_SECS", 30); // make s maller

			// this is same as config.setNumWorkers? No!!!
			config.setNumWorkers(3);

			config.setDebug(true);
			LOG.info("----------------------------------------");
			printConfig(config);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("testtop", config, builder.createTopology());
			cluster.activate("testtop");

			Utils.sleep(10000);

			cluster.deactivate("testtop");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
