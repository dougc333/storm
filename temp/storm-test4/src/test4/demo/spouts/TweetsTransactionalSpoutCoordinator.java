package test4.demo.spouts;

import java.math.BigInteger;

import backtype.storm.transactional.ITransactionalSpout;

public class TweetsTransactionalSpoutCoordinator implements
		ITransactionalSpout.Coordinator<TransactionMetadata> {

	private Integer MAX_TRANSACTION_SIZE = 10;

	TransactionMetadata lastTransactionMetadata;
	RQ rq = new RQ();
	long nextRead = 1108;

	public TweetsTransactionalSpoutCoordinator() {
		nextRead = rq.getNextRead();
	}

	@Override
	public TransactionMetadata initializeTransaction(BigInteger txid,
			TransactionMetadata prevMetadata) {
		long quantity = rq.getAvailableToRead(nextRead);
		quantity = quantity > MAX_TRANSACTION_SIZE ? MAX_TRANSACTION_SIZE
				: quantity;
		TransactionMetadata ret = new TransactionMetadata(nextRead,
				(int) quantity);

		nextRead += quantity;
		return ret;
	}

	@Override
	public boolean isReady() {
		return rq.getAvailableToRead(nextRead) > 0;
	}

	@Override
	public void close() {
		rq.close();
	}

}
