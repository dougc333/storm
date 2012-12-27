package test4.demo;



import java.math.BigInteger;
import java.util.Map;

import org.apache.log4j.Logger;
import java.util.*;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.MemoryTransactionalSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBatchBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.ICommitter;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.transactional.TransactionalTopologyBuilder;
import backtype.storm.tuple.*;


// global count example with failure of bolt simulation
//
public class TestSimpleTransFail {
		static Logger LOG = Logger.getLogger(TestSimpleTransaction.class);
		
		//there is no default ctor call; test instantiation in prepare()
		   public static final Map<Integer, List<List<Object>>> DATA = new HashMap<Integer, List<List<Object>>>() {{
		        put(0, new ArrayList<List<Object>>() {{
		            add(new Values("cat"));
		            add(new Values("dog"));
		            add(new Values("chicken"));
		            add(new Values("cat"));
		            add(new Values("dog"));
		            add(new Values("apple"));
		        }});
		        put(1, new ArrayList<List<Object>>() {{
		            add(new Values("cat"));
		            add(new Values("dog"));
		            add(new Values("apple"));
		            add(new Values("banana"));
		        }});
		        put(2, new ArrayList<List<Object>>() {{
		            add(new Values("cat"));
		            add(new Values("cat"));
		            add(new Values("cat"));
		            add(new Values("cat"));
		            add(new Values("cat"));
		            add(new Values("dog"));
		            add(new Values("dog"));
		            add(new Values("dog"));
		            add(new Values("dog"));
		        }});
		    }};

		
		
	public static class TestSimpleTransactionBolt extends BaseBatchBolt{
		Object id;
		BatchOutputCollector collector;
			int count=0;
			
			@Override
			public void prepare(Map conf, TopologyContext context,
					BatchOutputCollector collector, Object id) {
				// TODO Auto-generated method stub
				// hey, the below doesnt work without a this as we showed earlier!
				this.collector = collector;
				this.id = id;
			}

			//do we fail the first and/or second bolts? 
			//how to tell this doesnt cascade? 
			@Override
			public void execute(Tuple tuple) {
				// TODO Auto-generated method stub
				LOG.info("TestSimpleTransaction Bolt Phase1 execute ");
				count++;
			}

			@Override
			public void finishBatch() {
				// TODO Auto-generated method stub
				collector.emit(new Values(id,count));
			}

			@Override
			public void declareOutputFields(OutputFieldsDeclarer declarer) {
				// TODO Auto-generated method stub
				declarer.declare(new Fields("id","count"));
			}
		}
		
		public static class Value{
			int count=0;
			BigInteger txid;
		}
		
		public static Map<String,Value> DATABASE = new HashMap<String, Value>();
		public static final String GLOBAL_COUNT_KEY = "GLOBAL-COUNT";
		
		
		
		//second stage should only start once first stage completes
		//use transaction attempt, where is the rewind on a fail? 
		//we need to test that part. This isnt in the test code. 
		public static class TestSimpleTransactionCommitterBolt extends BaseTransactionalBolt implements ICommitter{
			
			BatchOutputCollector collector;
			TransactionAttempt attempt;
			
			int sum=0;
			
			@Override
			public void prepare(Map conf, TopologyContext context,
					BatchOutputCollector collector, TransactionAttempt id) {
				// TODO Auto-generated method stub
				this.collector = collector;
				this.attempt=id; 
			}

			@Override
			public void execute(Tuple tuple) {
				// TODO Auto-generated method stub
				LOG.info("2nd phase bolt execute");
				sum = tuple.getInteger(1); //where is this set? 
			}

			@Override
			public void finishBatch() {
				// TODO Auto-generated method stub
				LOG.info("finishBatch!!!!!!!!!!!!!!!!!!!!!!");
				Value val = DATABASE.get(GLOBAL_COUNT_KEY);
				
				Value newVal=null;
				
				if(val == null || val.txid.equals(attempt.getTransactionId())){
					newVal = new Value();
					newVal.txid = attempt.getTransactionId();
					
					if(val==null){
						newVal.count = sum;
					}else{
						newVal.count=sum+val.count;
					}
					DATABASE.put(GLOBAL_COUNT_KEY, newVal);
					
				}else{
					newVal=val;
				}
				if(collector==null){
					LOG.info("COMMITTER BOLT COLLECTOR NULL!!!!!!!!!!!!!!!");
				}
				if(attempt==null){
					LOG.info("COMMITTER BOLT attempt NULL!!!!!!!!!!!!!!!");
				}
				if(newVal==null){
					LOG.info("COMMITTER BOLT newVal NULL!!!!!!!!!!!!!!!");
				}
				collector.emit(new Values(attempt,newVal.count));
				
			}

			@Override
			public void declareOutputFields(OutputFieldsDeclarer declarer) {
				// TODO Auto-generated method stub
				declarer.declare(new Fields("id","sum"));
			}
			
		}
		
		
		
		public static void main(String []args){
			try{
				
				//PARTITION_TAKE_PERBATCH=3 This isnt a partitioned transcation? 
				MemoryTransactionalSpout spout = new MemoryTransactionalSpout(DATA, new Fields("word"),3);
				TransactionalTopologyBuilder builder = new TransactionalTopologyBuilder("global-count","spout",spout,2);
				
				builder.setBolt("partial-count", new TestSimpleTransactionBolt(), 1).noneGrouping("spout");
				builder.setBolt("global-count", new TestSimpleTransactionCommitterBolt()).globalGrouping("partial-count");
				
				LocalCluster cluster = new LocalCluster();
				Config config = new Config();
				config.setDebug(true);
				
				cluster.submitTopology("test",config, builder.buildTopology());
				Thread.sleep(3000);
				cluster.shutdown();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
}
	
