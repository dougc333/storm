package test4.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import test4.demo.TestStorm7.GetTweeters;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;


//test numworkers setting and tasks from TestStorm7
//
public class TestStorm7a {

	static Logger LOG = Logger.getLogger(TestStorm7.class);
	
    public static Map<String, List<String>> TWEETERS_DB = new HashMap<String, List<String>>() {{
        put("foo.com/blog/1", Arrays.asList("sally", "bob", "tim", "george", "nathan")); 
        put("engineering.twitter.com/blog/5", Arrays.asList("adam", "david", "sally", "nathan")); 
        put("tech.backtype.com/blog/123", Arrays.asList("tim", "mike", "john")); 
     }};
     
     public static Map<String, List<String>> FOLLOWERS_DB = new HashMap<String, List<String>>() {{
         put("sally", Arrays.asList("bob", "tim", "alice", "adam", "jim", "chris", "jai"));
         put("bob", Arrays.asList("sally", "nathan", "jim", "mary", "david", "vivian"));
         put("tim", Arrays.asList("alex"));
         put("nathan", Arrays.asList("sally", "bob", "adam", "harry", "chris", "vivian", "emily", "jordan"));
         put("adam", Arrays.asList("david", "carissa"));
         put("mike", Arrays.asList("john", "bob"));
         put("john", Arrays.asList("alice", "nathan", "jim", "mike", "bob"));
     }};

     public static class GetTweeters extends BaseBasicBolt {
    	 OutputCollector collector;
    	 TopologyContext context;
    	 
    	 
    	 public void prepare(Map stormConf, TopologyContext context,
 				OutputCollector collector) {
 			// TODO Auto-generated method stub
 			this.collector = collector;
 			this.context = context;
 			LOG.info("GETTWEETERS prepare!!!!!!!!!!!!!!!!!!!!!!!!!!");
 		}
    	 
         @Override
         public void execute(Tuple tuple, BasicOutputCollector collector) {
             Object id = tuple.getValue(0);
             String url = tuple.getString(1);
             LOG.info("id:"+id);
             LOG.info("url:"+url);
             List<String> tweeters = TWEETERS_DB.get(url);
             if(tweeters!=null) {
                 for(String tweeter: tweeters) {
                     collector.emit(new Values(id, tweeter));
                 }
             }
         }
         @Override
         public void declareOutputFields(OutputFieldsDeclarer declarer) {
             declarer.declare(new Fields("id", "tweeter"));
         }        
     }

	
	
	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			//local  mode get 4 threads with 25 tasks each
//			builder.setBolt("bolt", new GetTweeters(), 4);
			//local mode get 1 thread w/100 tasks or 99. 
			builder.setBolt("bolt", new GetTweeters(), 1);
					
			Config conf = new Config();
			//verify can override w/this setting w/200
//			conf.TOPOLOGY_TASKS="200";
			//what does setNumWorkers do in local mode if numThreads is set by parallelism hint?
//			conf.setNumWorkers(2); //2 threads w/50 tasks
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm7", conf,builder.createTopology() );
			//verify see prepare LOG statement
			cluster.activate("TestStorm7");
			Utils.sleep(10000);
			cluster.deactivate("TestStorm7");
			cluster.shutdown();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}
