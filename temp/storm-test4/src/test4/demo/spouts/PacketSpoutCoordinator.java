package test4.demo.spouts;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import test4.demo.FailTest;

import backtype.storm.transactional.ITransactionalSpout;

public class PacketSpoutCoordinator implements
		ITransactionalSpout.Coordinator<TransactionMetadata> {
	static Logger LOG = Logger.getLogger(PacketSpoutCoordinator.class);

	private Integer MAX_TRANSACTION_SIZE = 10;

	TransactionMetadata lastTransactionMetadata;
	RQ rq = new RQ();
	long nextRead;

	public PacketSpoutCoordinator() {
		LOG.info("PACKETSPOUTCOORDINATOR ctor");
		nextRead = rq.getNextRead();
		LOG.info("PACKETSPOUTCOORDINATOR NEXTREAD:" + nextRead);
	}

	@Override
	public TransactionMetadata initializeTransaction(BigInteger txid,
			TransactionMetadata prevMetadata) {
		// TODO Auto-generated method stub
		long quantity = rq.getAvailableToRead(nextRead);
		LOG.info("PACKET SPOUT COORDINATOR init transaction quantity:"
				+ quantity);
		quantity = quantity > MAX_TRANSACTION_SIZE ? MAX_TRANSACTION_SIZE
				: quantity;
		TransactionMetadata ret = new TransactionMetadata(nextRead,
				(int) quantity);

		nextRead += quantity;
		return ret;

	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPOUTCOORDINATOR isReady()");
		return rq.getAvailableToRead(nextRead) > 0;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		LOG.info("PACKETSPOUTCOORDINATOR close()");
		rq.close();
	}

}
