package test4.demo;

import java.util.Map;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalBolt;
import backtype.storm.transactional.ICommitter;
import backtype.storm.transactional.TransactionAttempt;
import backtype.storm.tuple.Tuple;

// test ICommitter interface, write into redis
// see example CountingCommiterBolt and KeyedCountingCommitterBolt 
// KeyedCountingBatchBolt code exists
// KeyedCountingCommitterBolt code doesnt exist
// Use CountingCommitBolt.. ICommitter has no functions in it, looks like finishBatch is part of BaseTransactionalBolt
// and there is no need for ICommitter
// Do we need to test w/o ICommiter I/F? 

// emit in finishBatch(). Increment count in execute(). 
// declarer includes a tx id, multi fields, not a single field.  strem looks like txid, "word", txid, "word", ? interleaved? 
// or is the transactionid embededd in an object and there is a stream of objects instead of strings?  
//
public class TestWriteICommiter {

	// can this exist as ICommitter vs. extends BaseTransactionalBolt implements
	// ICommitter has no Interface defined for it
	static class TestBolt extends BaseTransactionalBolt implements ICommitter {

		@Override
		public void prepare(Map conf, TopologyContext context,
				BatchOutputCollector collector, TransactionAttempt id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void execute(Tuple tuple) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishBatch() {
			// TODO Auto-generated method stub

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub

		}

	}

	public static void main(String[] args) {

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
