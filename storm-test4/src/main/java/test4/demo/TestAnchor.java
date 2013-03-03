package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
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

//how to test Tuple anchor? 
//run in dist  mode and verify w/storm  ui? 
//will  you see the acks in local mode? if you dont stop the cluster? 
//https://github.com/nathanmarz/storm/blob/master/src/jvm/backtype/storm/drpc/JoinResult.java
public class TestAnchor {
	static Logger LOG = Logger.getLogger(TestAnchor.class);

	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Map conf; 
		Integer next = 0;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			this.conf = conf; 
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			LOG.info("STORM CALLING TESTSPOUT NEXTTUPLE");
			if (next < 100) {
				//next = 0;
				LOG.info("SPOUT EMITTING:" + next);
				collector.emit(new Values(next.toString()));
				next++;				
			}
			LOG.info("SPOUT nextTuple called no EMIT!!!!!!");
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}

		public void ack(Object msgId){
			LOG.info("TEST SPOUT ACK CALLED!!!!!!!!!!!");
		}
		public void fail(Object msgId){
			LOG.info("TEST SPOUT FAIL CALLED!!!!!!!!!!!");			
		}
		public void activate(){
			LOG.info("TESTSPOUT ACTIVATE!!!!!!!!!!!!!!!");
		}
		public void deactivate(){
			LOG.info("TESTSPOUT DEACTIVATE!!!!!!!!!!!!!!!");
		}
		public void close(){
			LOG.info("TESTSPOUT CLOSE!!!!!!!!!!!!!!!");			
		}
	}

	static class TestBolt extends BaseRichBolt {
		OutputCollector collector;
		TopologyContext context;
		Integer numIssued = 0;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("BOLT PREPARE");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("BOLT EXECUTE :" + input.getString(0) + " numIssued:"
					+ numIssued.toString());
			collector.emit(input, new Values());
			numIssued++;
			if (numIssued == 5) {
				collector.fail(input);
				LOG.info("BOLT EXECUTE FAIL CALLED!!!!!!!!!!!!!!!!!!!!!!");
			} else {
				collector.ack(input);
			}

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			LOG.info("TESTBOLT declareOutputFields!!!!!!!!");
			declarer.declare(new Fields("boltoutput"));
		}

		public Map<String,Object> getComponentConfiguration(){
			LOG.info("CALLING BOLT COMPONENTCONFIGURATION");
			return new java.util.HashMap<String,Object>();
		}
		
	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TestSpout(), 3);
			builder.setBolt("bolt", new TestBolt(), 3).shuffleGrouping("spout");

			Config config = new Config();
			config.setDebug(true);

			StormSubmitter.submitTopology("TestAnchor", config, builder.createTopology());
			
			
//			LocalCluster cluster = new LocalCluster();
//			cluster.submitTopology("testtop", config, builder.createTopology());
//			cluster.activate("testtop");
			//you should see the UI working at this point. 
//			Utils.sleep(10000);

//			cluster.deactivate("TestAnchor");
//			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
