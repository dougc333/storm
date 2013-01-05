package test4.examples;

import backtype.storm.transactional.ICommitter;

public class KeyedCountingCommitterBolt extends KeyedCountingBatchBolt implements ICommitter {

}
