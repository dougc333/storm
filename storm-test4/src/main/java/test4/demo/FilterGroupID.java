package test4.demo;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Tuple;

public class FilterGroupID {

	
	static class FilterGroupIDSpout extends BaseRichSpout{

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	static class FilterGroupIDBolt extends BaseRichBolt{

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
