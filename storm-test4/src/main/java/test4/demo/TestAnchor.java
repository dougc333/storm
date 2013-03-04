package test4.demo;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.GlobalStreamId;
import backtype.storm.generated.Grouping;
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
//run in dist  mode and verify w/storm  ui?  Yes and with logs. 
//will  you see the acks in local mode? No. No thrift, but you can add print statements
// to to the spout ack/fail method. 
// 3 steps: 
// 1) no message id, verify no acks
// 2) add messageId to spout emit, see acks. no fails
// 3) add fails for even. 
// 4) count msgIds to verify parallelism
// debug: look at logs, takes time to get to steady state. Can't really measure performance w/fixed time
// notes; most don't know how to use threaads b/c they arent using Utils.Time to do a sleep which calls
// zookeeper through curator. 
//https://github.com/nathanmarz/storm/blob/master/src/jvm/backtype/storm/drpc/JoinResult.java
public class TestAnchor {
	static Logger LOG = Logger.getLogger(TestAnchor.class);

	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Map conf; 
		AtomicInteger next = new AtomicInteger(0);
	
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			this.conf = conf;
			LOG.info("TOPOLOGY CONTEXT IN SPOUT OPEN");
			printTopologyContext(context);
			
			LOG.info("JAVA PROCESS NAME:"+ManagementFactory.getRuntimeMXBean().getName());
		}

		
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			LOG.info("STORM CALLING TESTSPOUT NEXTTUPLE");
			if (next.get() < 100) {
				//next = 0;
				LOG.info("SPOUT EMITTING:" + next);
				
				collector.emit(new Values(next.toString()),next);
				next.getAndIncrement();				
			}
			LOG.info("SPOUT nextTuple called no EMIT!!!!!!");
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}

		public void ack(Object msgId){
			LOG.info("TEST SPOUT ACK CALLED!!!!!!!!!!! msgId:"+msgId.toString());
		}
		public void fail(Object msgId){
			LOG.info("TEST SPOUT FAIL CALLED!!!!!!!!!!! msgId:"+msgId.toString());			
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
		AtomicInteger numIssued = new AtomicInteger(0);
		AtomicInteger numFailed = new AtomicInteger(0);
		
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("BOLT PREPARE");	
			printTopologyContext(context);
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("BOLT EXECUTE :" + input.getString(0) + " numIssued:"
					+ numIssued.toString());
			collector.emit(input, new Values());
			//test fail by commenting below out and failing once
			//collector.ack(input);
			numIssued.getAndIncrement();
			
			if (numIssued.get() % 2 ==0) {
				collector.fail(input);
				LOG.info("BOLT EXECUTE FAIL CALLED!!!!!!!!!!!!!!!!!!!!!! numFailed:"+numFailed.get());
				numFailed.getAndIncrement();
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
		
		public void ack(Object msgId){
			LOG.info("TEST SPOUT ACK CALLED!!!!!!!!!!!");
		}
		public void fail(Object msgId){
			LOG.info("TEST SPOUT FAIL CALLED!!!!!!!!!!!");			
		}
		
	}

	private static void printTopologyContext(TopologyContext context){
		LOG.info("-------------------------------------");
		LOG.info("----------TOPOLOGY CONTEXT-----------");
		LOG.info("context codedir:"+context.getCodeDir());
		LOG.info("context PIDDIR:"+context.getPIDDir());
		LOG.info("context stormid:"+context.getStormId());
		LOG.info("context this.Componentid:"+context.getThisComponentId());
		LOG.info("context this.TaskId:"+context.getThisTaskId());
		LOG.info("context this.TaskIndex:"+context.getThisTaskIndex());
		LOG.info("context this.WorkerPort:"+context.getThisWorkerPort());
		LOG.info("context JSON STRING:"+context.toJSONString());
		LOG.info("context maxTopologyMessageTimeout:"+context.maxTopologyMessageTimeout());
		
		//output streamids + grouping.  
		Map<GlobalStreamId, Grouping> sources = context.getThisSources();
		for(GlobalStreamId g:sources.keySet()){
			LOG.info("global stream id:"+g.toString()+" componentid:"+g.get_componentId()+" streamId:"+g.get_streamId()+" grouping union"+sources.get(g).toString());
		}
		
		/**
		//print targets
		Map<String, Map<String, Grouping>> target = context.getThisTargets();
		for(String key:target.keySet()){
			LOG.info("thisTargets key:"+key);
			for(String thisKey:target.get(key).keySet()){
			//	LOG.info("	thisKey:"+thisKey+" grouping:"+target.get(key).);
			}
		}
		
		//worker topology context
		java.util.List<Integer> workerTasks = context.getThisWorkerTasks();
		for(Integer i: workerTasks){
			LOG.info("worker tasks:"+i);
		}
		
		//general topology context
		java.util.Set<String> compIds = context.getComponentIds();
		for(String componentId:compIds){
			LOG.info("context componentIDs:"+componentId);
			//components streams per id: 
			java.util.Set<String> compStreams = context.getComponentStreams(componentId);
			for(String stream:compStreams){
				LOG.info("context component id:"+componentId+ " stream:"+stream);
			}
			java.util.List<Integer> compTasks = context.getComponentTasks(componentId);
			for(Integer i:compTasks){
				LOG.info("context component id:"+componentId+" tasks:"+i);
			}
			//java.util.Map<GlobalStreamId,Grouping> compSources = context.getSources(componentId);
			//for(GlobalStreamId global:compSources.keySet()){
			//	Grouping grouping = compSources.get(global);
			//	java.util.List<String> fieldNames = grouping.get_fields();
			//	for(String fName:fieldNames){
			//		LOG.info("GlobalStreamId:"+global.toString()+" Grouping fieldName:"+fName+" grouping:"+grouping.getFieldValue());
			//	}
			//}
		}
		*/
		
		LOG.info("-------------------------------------");
		
	}

	
	
	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TestSpout(), 3);
			builder.setBolt("bolt", new TestBolt(), 3).shuffleGrouping("spout");

			Config config = new Config();
			config.setDebug(true);

			StormSubmitter.submitTopology("TestAnchor", config, builder.createTopology());
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
