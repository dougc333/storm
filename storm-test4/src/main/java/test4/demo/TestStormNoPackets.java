package test4.demo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Tuple;

//no packets, what is perf as function of bolts/spouts?
//there wont be a bolt bc/ no tuple is sent from spout
public class TestStormNoPackets {
	static Logger LOG = Logger.getLogger(TestStormNoPackets.class);
	static String data[] = { "a", "b", "c", "d", "e", "f", "g", "h","i","j","k","l","m","n","o","p","q","r","s","t" };
	static AtomicInteger numSpoutCalls= new AtomicInteger(0);
			
			
			
	public static class TestSpoutNoPackets extends BaseRichSpout{

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			numSpoutCalls.addAndGet(1);
			LOG.info("incrementing!!!"+numSpoutCalls.get());
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class TestBoltNoPackets extends BaseRichBolt{

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
		
		
	}
}
