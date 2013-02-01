package test4.demo;

import java.util.Map;

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

public class TestPerf1 {

	static class TestSpout extends BaseRichSpout {
		SpoutOutputCollector collector;
		TopologyContext context;
		Integer next;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;

		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			PerfObject perf = new PerfObject();
//			perf.setDummyValue(next);
			next++;
			perf.setTimestamp(System.currentTimeMillis());
			Values val = new Values();
			val.add(perf);
			collector.emit(val);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("perf"));
		}

	}

	static class TestBolt extends BaseRichBolt {

		OutputCollector collector;
		TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.context = context;
			this.collector = collector;
		}

		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub

			// how is this serialized/deserialized?
			// use getBytes()?
			// collector.emit();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("field1"));
		}

	}

	public static void main(String[] args) {

		try {

			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout", new TestSpout(), 1);
			builder.setBolt("bolt", new TestBolt(), 3).shuffleGrouping("spout");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
