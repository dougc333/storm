package test4.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//test w/no spout, set numparallelism to 4
//play w/bolt parallelism, you see the 4 bolts being started w/num tasks, 25, which is 4*25=100
//which is what topology.tasks is set to
//- Loading executor bolt:[2 26]
// Loading executor bolt:[52 76]
//	2348 [Thread-9] INFO  backtype.storm.daemon.executor  - Loading executor bolt:[27 51]
//	Loading executor bolt:[77 101]
// if we set numparallelism to 1 we should see 1 bolt w/100 tasks
//2231 [Thread-6] INFO  backtype.storm.daemon.executor  - Finished loading executor bolt:[2 101]
//works. we have to set the num tasks & numparallelism hint
// 
public class TestStorm7 {
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
//			builder.setBolt("bolt", new GetTweeters(), 4);
			builder.setBolt("bolt", new GetTweeters(), 1);
					
			Config conf = new Config();
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm7", conf,builder.createTopology() );
			//leave this off, never see prepare method called!!
			//	cluster.activate();
			Utils.sleep(10000);
			cluster.deactivate("TestStorm7");
			cluster.shutdown();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
