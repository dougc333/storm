package test4.demo.spouts;

import java.math.BigInteger;
import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalSpout;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Fields;

public class FileSpoutTransactionalSpout extends
		BaseTransactionalSpout<TransactionMetadata> {

	public static class FileSpoutTransactionCoordinator implements
			ITransactionalSpout.Coordinator<TransactionMetadata> {

		@Override
		// this stores the metadata into zookeeper
		public TransactionMetadata initializeTransaction(BigInteger txid,
				TransactionMetadata prevMetadata) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isReady() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

	}

	public static class FileSpoutTransactionEmitter implements
			ITransactionalSpout.Emitter<TransactionMetadata> {

		@Override
		public void cleanupBefore(BigInteger txid) {
			// TODO Auto-generated method stub

		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

		@Override
		public void emitBatch(TransactionAttempt tx,
				TransactionMetadata coordinatorMeta,
				BatchOutputCollector collector) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public backtype.storm.transactional.ITransactionalSpout.Coordinator<TransactionMetadata> getCoordinator(
			Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return new FileSpoutTransactionCoordinator();
	}

	@Override
	public backtype.storm.transactional.ITransactionalSpout.Emitter<TransactionMetadata> getEmitter(
			Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		return new FileSpoutTransactionEmitter();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("txid", "packet_id", "packetdata"));
	}
}
