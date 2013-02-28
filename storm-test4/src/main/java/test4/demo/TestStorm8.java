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

//storm distributed mode
//if we send one ack does storm log this? 
//if we send 1 tuple does storm see it? 
public class TestStorm8 {
	static Logger LOG = Logger.getLogger(TestStorm8.class);
	static Integer next= 0;
	
	static class OneTupleSpout extends BaseRichSpout{
		SpoutOutputCollector collector;
		TopologyContext context;
		
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if(next ==0){
				LOG.info("EMIT ONE!!!!!!!!!!!!!!!!!!!!!!");
				collector.emit(new Values(1));
				next++;
			}
		}

		@Override
		public void open(Map arg0, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context=context;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("spoutoutput"));
		}	
		
	}
	
	static class OneTupleBolt extends BaseRichBolt{
		OutputCollector collector;
		TopologyContext context;
		Map conf;
		
		@Override
		public void execute(Tuple tuple) {
			LOG.info("OneTupleBolt EMIT ONE!!!!!!");
			collector.emit(new Values(tuple.getInteger(0)));
			//adding this I get no log entry for the spoutS!!!
			//collector.ack(tuple);
		}

		@Override
		public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
			this.conf = conf;
			this.context = context;
			this.collector = collector;
			LOG.info("TESTSTORM8 PREPARE!!!!!!!!!!!!!!!!!!!!!!");
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("boltoutput"));
		}
		
		
	}
	
	public static void main(String []args){
		try{
			
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new OneTupleSpout(),4);
			builder.setBolt("bolt",new OneTupleBolt(),4).shuffleGrouping("spout");
			
			Config conf = new Config();
			//conf.setDebug(true);
//			conf.setNumWorkers(10);
			
			//LocalCluster cluster  = new LocalCluster();
			//cluster.submitTopology("TestStorm8", conf, builder.createTopology());
			//Utils.sleep(10000);
			//cluster.deactivate("TestStorm8");
			//cluster.shutdown();
			
			StormSubmitter.submitTopology("TestStorm8",conf,builder.createTopology());
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
