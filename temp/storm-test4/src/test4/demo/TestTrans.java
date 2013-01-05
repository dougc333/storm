package test4.demo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.MemoryTransactionalSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.topology.base.BaseTransactionalSpout;
import backtype.storm.transactional.*;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import test4.demo.spouts.FileSpoutTransactionalSpout;
import test4.demo.bolts.*;
import test4.demo.spouts.*;

//test transaction in a counting example. 
public class TestTrans {
	static Logger LOG = Logger.getLogger(TestTrans.class);

	public static final Map<Integer, List<List<Object>>> DATA = new HashMap<Integer, List<List<Object>>>() {
		{
			put(0, new ArrayList<List<Object>>() {
				{
					add(new Values("cat"));
					add(new Values("dog"));
					add(new Values("chicken"));
					add(new Values("cat"));
					add(new Values("dog"));
					add(new Values("apple"));
				}
			});
			put(1, new ArrayList<List<Object>>() {
				{
					add(new Values("cat"));
					add(new Values("dog"));
					add(new Values("apple"));
					add(new Values("banana"));
				}
			});
			put(2, new ArrayList<List<Object>>() {
				{
					add(new Values("cat"));
					add(new Values("cat"));
					add(new Values("cat"));
					add(new Values("cat"));
					add(new Values("cat"));
					add(new Values("dog"));
					add(new Values("dog"));
					add(new Values("dog"));
					add(new Values("dog"));
				}
			});
		}
	};

	// read from redis
	static class FirstBolt extends BaseRichBolt {

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

	// write into redis
	static class SecondBolt extends BaseTransactionalBolt implements ICommitter {

		@Override
		public void prepare(Map conf, TopologyContext context,
				BatchOutputCollector collector, TransactionAttempt id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void execute(Tuple tuple) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishBatch() {
			// TODO Auto-generated method stub

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	// we want TransSpout to have same output as memorytransspout
	static class TransSpout extends BaseTransactionalSpout {

		@Override
		public Coordinator getCoordinator(Map conf, TopologyContext context) {
			// TODO Auto-generated method stub
			return new TransCoordinator();
		}

		@Override
		public Emitter getEmitter(Map conf, TopologyContext context) {
			// TODO Auto-generated method stub
			return new TransEmitter();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	static class TransCoordinator implements ITransactionalSpout.Coordinator {

		@Override
		public Object initializeTransaction(BigInteger txid, Object prevMetadata) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

	}

	static class TransEmitter implements ITransactionalSpout.Emitter {

		@Override
		public void emitBatch(TransactionAttempt tx, Object coordinatorMeta,
				BatchOutputCollector collector) {
			// TODO Auto-generated method stub

		}

		@Override
		public void cleanupBefore(BigInteger txid) {
			// TODO Auto-generated method stub

		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {
		try {

			MemoryTransactionalSpout memSpout = new MemoryTransactionalSpout(
					DATA, new Fields("word"), 3);

			TransSpout transSpout = new TransSpout();

			TransactionalTopologyBuilder tt = new TransactionalTopologyBuilder(
					"TestTrans", "spout", new PacketTransSpout());

			PacketSplitterBolt ps = new PacketSplitterBolt();
			tt.setBolt("packetspliltter", ps, 1).shuffleGrouping("spout");

			LocalCluster cluster = new LocalCluster();
			Config config = new Config();
			config.setDebug(true);
			// config.setMaxSpoutPending(1);
			// config.setMaxTaskParallelism(1);
			cluster.submitTopology("TestTransTopology", config,
					tt.buildTopology());
			Thread.sleep(30000);
			cluster.killTopology("TestTransTopology");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
