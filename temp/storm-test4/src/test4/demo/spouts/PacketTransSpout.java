package test4.demo.spouts;

import java.util.Map;

import org.apache.log4j.Logger;

import test4.demo.FailTest;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseTransactionalSpout;
import backtype.storm.tuple.Fields;

public class PacketTransSpout extends
		BaseTransactionalSpout<TransactionMetadata> {
	static Logger LOG = Logger.getLogger(PacketTransSpout.class);

	public PacketTransSpout() {
		LOG.info("PacketTransSpout ctor");
	}

	@Override
	public backtype.storm.transactional.ITransactionalSpout.Coordinator<TransactionMetadata> getCoordinator(
			Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		LOG.info("PACKETTRANSSPOUT getCoordinator");
		return new PacketSpoutCoordinator();
	}

	@Override
	public backtype.storm.transactional.ITransactionalSpout.Emitter<TransactionMetadata> getEmitter(
			Map conf, TopologyContext context) {
		// TODO Auto-generated method stub
		LOG.info("PACKETTRANSSPOUT getEmitter");
		return new PacketSpoutEmitter();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("txid", "packet_id", "packet"));
	}

}
