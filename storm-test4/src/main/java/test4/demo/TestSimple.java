package test4.demo;


import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import backtype.storm.tuple.*;
import backtype.storm.utils.Utils;


//test functionality, practice programming model
//something 
public class TestSimple {
	static Logger LOG = Logger.getLogger(TestSimple.class);
	
 	public static class TestSimpleBolt extends BaseRichBolt{
		OutputCollector collector;
		
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			LOG.info("Bolt prepare");
			// TODO Auto-generated method stub
			this.collector = collector;
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("bolt execute tuple toString:"+input.toString());
			LOG.info("bolt execute tuple getString(0):"+input.getString(0));
			if(input==null){
				LOG.info("bolt TUPLE NULL!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}else{
				LOG.info("bolt TUPLE NOT NULL----------------------");
			}
			if(collector==null){
				LOG.info("COLLECTOR NULL!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
			String dummy="asdf";
			String inputString = input.getString(0);
			if(inputString==null){
				collector.emit(input, new Values("bolt execute dummy:"+dummy));	
			}else{
				collector.emit(input, new Values("bolt execute tuple:"+inputString));
			}
			collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			// it doesnt matter what this is b/c we dont use the stream past this output
			LOG.info("bolt declareOutputFields");
			declarer.declare(new Fields("words"));
		}
		
		
	}
	
	public static void main(String []args){
		try{
			TopologyBuilder builder = new TopologyBuilder();
			
			builder.setSpout("word",new TestWordSpout(),2);
			builder.setBolt("test1bolt",new TestSimpleBolt(),2).shuffleGrouping("word");
	//		builder.setBolt("test2bolt",new TestSimpleBolt(),1).shuffleGrouping("test1bolt");
			
			Config conf = new Config();
			conf.setDebug(true);
			
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("test", conf, builder.createTopology());
			
			Utils.sleep(10000);
			cluster.killTopology("test");
			cluster.shutdown();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
}
