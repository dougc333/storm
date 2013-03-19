package test4.demo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestBoltScheduling {
	private static Logger LOG = Logger.getLogger("TestBoltScheduling.class");
	private static AtomicInteger num;
	
	static class TestBolt extends BaseRichBolt{
		Map conf;
		TopologyContext context;
		OutputCollector collector;
		
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			conf = stormConf;
			this.context = context;
			this.collector = collector;
			num.set(0);
			LOG.info("TestBoltScheduling prepare");
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			// there will never be any input here. So we will never see this trigger. 
			LOG.info("bolt execute!!!!!!!!!!!");
			num.addAndGet(1);
			collector.emit(new Values(1));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("boltfield"));
		}
		
		
	}
	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			TestBolt tb = new TestBolt();
			builder.setBolt("boltname",tb,3);
			
			// we can trigger tb.execute(Tuple input ); manually here
			// 
			
			Config conf = new Config();
			StormSubmitter.submitTopology("TestBoltScheduling",conf,builder.createTopology());
			
			// once it is copied over we will never see it. 
			// how do we send http requests to a spout/bolt in a topology?  We
			// can publish the adddress in a log file and see if we can connect to it
			// using an embedded tomcat/jetty server. How to handle the parallelism with 
			// multiple addresses? have to configure virtual hosts? will this work? 
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
