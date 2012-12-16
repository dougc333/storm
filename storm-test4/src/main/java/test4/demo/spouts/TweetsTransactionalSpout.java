package test4.demo.spouts;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalSpout;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.tuple.Fields;

public class TweetsTransactionalSpout extends
		BaseTransactionalSpout<TransactionMetadata> {

	@Override
	public ITransactionalSpout.Coordinator<TransactionMetadata> getCoordinator(
			Map conf, TopologyContext context) {
		return new TweetsTransactionalSpoutCoordinator();
	}

	@Override
	public backtype.storm.transactional.ITransactionalSpout.Emitter<TransactionMetadata> getEmitter(
			Map conf, TopologyContext context) {
		return new TweetsTransactionalSpoutEmitter();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("txid", "tweet_id", "tweet"));
	}

}
