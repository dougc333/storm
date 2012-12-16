package test4.demo.spouts;

import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;

import test4.demo.FailTest;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.transactional.ITransactionalSpout;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Values;

public class PacketSpoutEmitter implements
		ITransactionalSpout.Emitter<TransactionMetadata> {
	static Logger LOG = Logger.getLogger(PacketSpoutEmitter.class);

	RQ rq = new RQ();

	public PacketSpoutEmitter() {
		LOG.info("PACKETSPOUTEMITTER ctor");
	}

	@Override
	public void emitBatch(TransactionAttempt tx,
			TransactionMetadata coordinatorMeta, BatchOutputCollector collector) {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPOUTEMITTER emitBatch");
		LOG.info("PacketSpoutEmitter coordinatorMeta.from:"
				+ coordinatorMeta.from);
		LOG.info("PacketSpoutEmitter coordinatorMeta.quantity:"
				+ coordinatorMeta.quantity);

		rq.setNextRead(coordinatorMeta.from + coordinatorMeta.quantity);
		rq.setNextRead(coordinatorMeta.from + coordinatorMeta.quantity);
		List<String> packets = rq.getMessages(coordinatorMeta.from,
				coordinatorMeta.quantity);

		long packetId = coordinatorMeta.from;
		LOG.info("packetId:" + packetId);
		for (String message : packets) {
			LOG.info("PACKETSPOUTEMITTER message:" + message);
			collector.emit(new Values(tx, "" + packetId, message));
			packetId++;
		}

	}

	@Override
	public void cleanupBefore(BigInteger txid) {
		// TODO Auto-generated method stub
		LOG.info("PACKTSPOUTEMITTER cleanupBefore()");
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPOUTEMITTER close()");
		rq.close();
	}

}
