package test4.demo;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Tuple;


//test Redis performance equivalent to TestPerf1
//
public class TestPerf4 {

	
	
	static class TestSpout extends BaseRichSpout{
		Map conf;
		TopologyContext context;
		SpoutOutputCollector collector;
		
	
		private void initRedis(){
			
		}
		
		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void open(Map arg0, TopologyContext arg1,
				SpoutOutputCollector arg2) {
			// TODO Auto-generated method stub
			this.conf = arg0;
			this.context = arg1;
			this.collector = arg2;
			initRedis();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	static class TestBolt extends BaseRichBolt{

		@Override
		public void execute(Tuple arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	public static void main(String []args){
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
