package test4.demo;

import java.util.Map;

import backtype.storm.LocalCluster;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.*;

//infinite loop, test storm ui
//test jedis i/f
public class TestStorm1 {

	public static class TestBolt extends BaseRichBolt {
		private OutputCollector collector;

		@Override
		public void prepare(Map conf, TopologyContext context,
				OutputCollector collector) {
			this.collector = collector;
		}

		@Override
		public void execute(Tuple tuple) {
			// this has anchoring, the difference is on the failure, will replay
			// to the root
			_collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
			_collector.ack(tuple);
		}

		@Override
		// where does Fields match up with?
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

	public static void main() {
		try {
			TopologyBuilder top = new TopologyBuilder();

			Config conf = new Config();
			conf.setDebug(true);
			// settings for slots vs. executors/tasks

			LocalCluster cluster = new LocalCluster();

			cluster.submitTopology(arg0, arg1, arg2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
