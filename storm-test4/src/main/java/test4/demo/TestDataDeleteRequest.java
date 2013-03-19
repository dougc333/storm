package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

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

public class TestDataDeleteRequest {
	static Logger LOG = Logger.getLogger("TestDataDeleteRequest.class");
	
	
	static class TDDRSpout extends BaseRichSpout{
		private static Map conf;
		private static TopologyContext context;
		private static SpoutOutputCollector collector;
		private static Jedis jedis;
		
		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.conf = conf;
			this.context = context;
			this.collector = collector;
			jedis = new Jedis("localhost");
			LOG.info("TDDR Spout open!!!!");
		}

		
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			//check for last request then process....
			jedis.watch("1","accountID");
			Transaction trans = jedis.multi();
			//trans.select(0);
			trans.set("fool", "bar"); 
			Response<String> result1 = trans.get("fool");
			
			Response<Long> dbSizeResp = trans.dbSize();
			Response<String> response = trans.hget("1","accountID");
			trans.exec();
			LOG.info("numRecords:"+dbSizeResp.get());
			LOG.info("RESPONSE:"+result1.get());
			LOG.info("response:"+response.get());
			
			collector.emit(new Values(234));

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}
		
		
	}
	
	static class TDDRBolt extends BaseRichBolt{

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
	
	
	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TDDRSpout(), 1);
			
			Config conf = new Config();
			
			StormSubmitter.submitTopology("TestDataDelete", conf, builder.createTopology());
			
			
			
		}catch(Exception e){
			
		}
	}
}
