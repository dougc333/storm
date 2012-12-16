package test4.demo.spouts;

import java.math.BigInteger;
import java.util.List;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Values;

public class TweetsTransactionalSpoutEmitter implements
		ITransactionalSpout.Emitter<TransactionMetadata> {
	RQ rq = new RQ();

	public TweetsTransactionalSpoutEmitter() {
	}

	@Override
	public void emitBatch(TransactionAttempt tx,
			TransactionMetadata coordinatorMeta, BatchOutputCollector collector) {
		rq.setNextRead(coordinatorMeta.from + coordinatorMeta.quantity);
		List<String> messages = rq.getMessages(coordinatorMeta.from,
				coordinatorMeta.quantity);

		long tweetId = coordinatorMeta.from;

		for (String message : messages) {
			collector.emit(new Values(tx, "" + tweetId, message));
			tweetId++;
		}
	}

	@Override
	public void cleanupBefore(BigInteger txid) {
	}

	@Override
	public void close() {
		rq.close();
	}

}
