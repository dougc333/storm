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
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.MemoryTransactionalSpout;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.ICommitter;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.transactional.TransactionalTopologyBuilder;
import backtype.storm.tuple.*;


//sum each item to a global count
//this requires a bucket for each word and storing this state in a db or zookeeper
public class TestSimpleTransPartialCount {
	static Logger LOG = Logger.getLogger(TestSimpleTransPartialCount.class);

	
	
	//this is different from Value includes prevCount, why? 
	public static class CountValue{
		Integer prevCount=null;
		int count=0;
		BigInteger txid=null;
	}
	
	//this is same as Value in GlobalTransaction
	public static class BucketValue{
		int count=0;
		BigInteger txid;
	}
	
	// this is new
	public static final int BUCKET_SIZE=10;
	
	public static Map<String,CountValue> COUNT_DATABASE = new HashMap<String,CountValue>();
	public static Map<Integer,BucketValue> BUCKET_DATABASE = new HashMap<Integer,BucketValue>();
	
	public static final int PARTITION_TAKE_PER_BATCH=3;
	
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

	
	    
	public static class KeyedCountUpdater extends BaseTransactionalBolt implements ICommitter{

		Map<String, Integer> counts = new HashMap<String, Integer>();
	    BatchOutputCollector batchCollector;
	    TransactionAttempt id;
		
	    
	    int count=0;
	    
		
		@Override
		public void prepare(Map conf, TopologyContext context,
				BatchOutputCollector collector, TransactionAttempt id) {
			// TODO Auto-generated method stub
			this.batchCollector = collector;
			this.id = id;
		}

		@Override
		public void execute(Tuple tuple) {
			// TODO Auto-generated method stub
			LOG.info("KeyedCountUpdated execute");
			
			String key =  tuple.getString(1);
			Integer curr = counts.get(key);
			if(curr==null){
				curr=0;
			}
			counts.put(key, curr+1);
		}

		@Override
		public void finishBatch() {
			// TODO Auto-generated method stub
			LOG.info("KeyedCountUpdated finishBatch");
			
			for(String key:counts.keySet()){
				CountValue val =COUNT_DATABASE.get(key);
				CountValue newVal;
				if(val==null || !val.txid.equals(id)){
					newVal = new CountValue();
					newVal.txid = id.getTransactionId();
					
					if(val!=null){
						newVal.prevCount = val.count;
						newVal.count = val.count;
					}
					newVal.count = newVal.count + counts.get(key);
					COUNT_DATABASE.put(key, newVal);
				}else{
					newVal = val;
				}
				batchCollector.emit(new Values(id,key,newVal.count, newVal.prevCount));
			}
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("id","key","count","prev-count"));
		}
		
	}
	    
	public static class Bucketize extends BaseBasicBolt{

		@Override
		public void execute(Tuple input, BasicOutputCollector collector) {
			// TODO Auto-generated method stub
			LOG.info("Bucketize execute");
			TransactionAttempt attempt = (TransactionAttempt) input.getValue(0);
			int curr = input.getInteger(2);
			Integer prev = input.getInteger(3);
		
			int currBucket = curr/BUCKET_SIZE;
			Integer prevBucket = null;
			if(prev!=null){
				prevBucket = prev/BUCKET_SIZE;
			}
			
			if(prevBucket==null){
				collector.emit(new Values(attempt,currBucket,1));
			}else if (currBucket!=prevBucket){
				collector.emit(new Values(attempt, currBucket,1));
				collector.emit(new Values(attempt,prevBucket,-1));
			}
			
		
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("attempt","bucket","delta"));
		}
		
		
	}
	    
	    
	public static class BucketCountUpdater extends BaseTransactionalBolt{
		Map<Integer,Integer> accum = new HashMap<Integer, Integer>();
		BatchOutputCollector batchCollector;
		TransactionAttempt attempt;
		
		int count=0;
		
		@Override
		public void prepare(Map conf, TopologyContext context,
				BatchOutputCollector collector, TransactionAttempt id) {
			// TODO Auto-generated method stub
			this.batchCollector = collector;
			this.attempt = id;
		}

		@Override
		public void execute(Tuple tuple) {
			// TODO Auto-generated method stub
			LOG.info("BucketCountUpdater execute");

			Integer bucket = tuple.getInteger(1);
			Integer delta = tuple.getInteger(2);
			Integer curr = accum.get(bucket);
			if(curr==null){
				curr=0;
			}
			accum.put(bucket, curr+delta);
		}

		@Override
		public void finishBatch() {
			// TODO Auto-generated method stub
			LOG.info("BucketCountUpdater finishBatch");

			for(Integer bucket:accum.keySet()){
				BucketValue currVal = BUCKET_DATABASE.get(bucket);
				BucketValue newVal;
				
				if(currVal==null || !currVal.txid.equals(attempt.getTransactionId())){
					newVal = new BucketValue();
					newVal.txid = attempt.getTransactionId();
					newVal.count = accum.get(bucket);
					if(currVal!=null){
						newVal.count = newVal.count + currVal.count;
					}
					BUCKET_DATABASE.put(bucket,newVal);
				}else{
					newVal = currVal;
				}
				batchCollector.emit(new Values(attempt,bucket, newVal.count));
			}
			
			
			
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("attempt","bucket","count"));
		}
		
	}
	 
	public static void main(String []args){
		try{
			MemoryTransactionalSpout spout = new MemoryTransactionalSpout(DATA, new Fields("word"), PARTITION_TAKE_PER_BATCH);
			TransactionalTopologyBuilder builder = new TransactionalTopologyBuilder("top-n-words","spout",spout, 2);
			
			builder.setBolt("count",new KeyedCountUpdater(),1).fieldsGrouping("spout",new Fields("word"));
			builder.setBolt("bucketize",new Bucketize(),1).noneGrouping("count");
			builder.setBolt("buckets",new BucketCountUpdater(),1).fieldsGrouping("bucketize",new Fields("bucket"));
			
			
			LocalCluster cluster = new LocalCluster();
			Config config = new Config();
			config.setDebug(true);
			
			cluster.submitTopology("top-n-topology",config, builder.buildTopology());
			Thread.sleep(3000);
			cluster.shutdown();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
