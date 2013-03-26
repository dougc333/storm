package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import backtype.storm.Config;
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


public class TestDataDelete {
	private static Logger LOG = Logger.getLogger(TestDataDelete.class);
	
	   
	static class DataDeleteSpout extends BaseRichSpout{
		SpoutOutputCollector collector;
		//private static JedisPool pool = new JedisPool(new JedisPoolConfig(),ServerAddressPort.HOST);
		private static Jedis jedis;
			
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
		    LOG.info("TEST DATADELETE SPOUT NEXT TUPLE!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    System.out.println("TEST DATADELETE SPOUT NEXT TUPLE!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

			String isReady = jedis.get("deviceready");
			if(isReady==null){
				System.out.println("SPOUT isReady null isReady:"+isReady);	
				LOG.info("SPOUT isReady null isReady:"+isReady);	
			}else{
				System.out.println("SPOUT isReady NOT NULL isReady:"+isReady);
				LOG.info("SPOUT isReady NOT NULL isReady:"+isReady);
				//process ESNs, read the tables using accountID in Redis
				// get by groupID:
				//
			}
			
			Long size = jedis.dbSize();
			System.out.println("db size: "+size);
			LOG.info("db size: "+size);
			
			
			LOG.info("SPOUT COMING!!!!!!!!!!!!!!!!!");
			collector.emit(new  Values("some ESNs in a set? "));
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2) {
			// TODO Auto-generated method stub
			this.collector = arg2;
			
		    LOG.info("OPEN TEST DATADELETE SPOUT!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		    System.out.println("OPEN TEST DATADELETE SPOUT!!!!!!!!!!!!!!!!!!!!!!!!!!!!");			
			
		  //  jedis = pool.getResource(); //replace w/storm0 or local ip when nto at starbucks
			jedis = new Jedis("localhost");
		
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			arg0.declare(new Fields("spoutoutput"));
		}
		
	}
	
	static class DataDeleteBolt extends BaseRichBolt{

		Map conf;
		TopologyContext context;
		OutputCollector collector;
		
		
		@Override
		public void execute(Tuple arg0) {
			// TODO Auto-generated method stub
			//do a mongo access here?, this is scheduled like a spout? No. 
			System.out.println("bolt called");
			collector.emit(new Values(1));
		}

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
			// TODO Auto-generated method stub
			this.conf = arg0;
			this.context= arg1;
			this.collector = arg2;
			LOG.info("TestDataDelete BOLT!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println("TestDataDelete BOLT!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			arg0.declare(new Fields("boltoutput"));
		}
		
		
	}
	
	public static void main(String []args){
		
		try{
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("ddspout", new DataDeleteSpout(),3);
			builder.setBolt("ddbolt", new DataDeleteBolt(), 3).shuffleGrouping("ddspout");
			
			Config conf = new Config();
			StormSubmitter.submitTopology("TestDataDelete", conf, builder.createTopology());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
