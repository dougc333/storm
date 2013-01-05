package test4.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.TupleImpl;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

// test field grouping, user group post doesnt match examples. You can rename the field groupings? 
// where does default come from? 
// print out status info for shuffle, none, fieldsGrouping
// .setBolt(...).fieldsGrouping("source1", new Fields("a"))
//
public class TestStorm4 {
	static Logger LOG = Logger.getLogger(TestStorm4.class);

	static class TestBolt extends BaseRichBolt {
		OutputCollector collector;
		Integer next = 0;
		TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("TESTBOLT prepare threadID:"
					+ Thread.currentThread().getId() + " this.taskID:"
					+ context.getThisTaskId());
		}

		// how are the below fields set in the spout?
		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TESTBOLT execute");
			LOG.info("TESTBOLT tuple getSourceComponent:"
					+ input.getSourceComponent());
			LOG.info("TESTBOLT tuple getStreamID:" + input.getSourceStreamId());
			LOG.info("TESTBOLT tuple getSourceTask:" + input.getSourceTask());
			LOG.info("TESTBOLT tuple getMessageId:" + input.getMessageId());
			LOG.info("TESTBOLT tuple getValues()Size():"
					+ input.getValues().size());

			// only output list of values
			collector.emit(new Values("bolt:" + next));
			next++;
		}

		// this doesnt matter for our test. Any output will work
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("word"));
		}

	}

	static class TestBolt1 extends BaseRichBolt {
		OutputCollector collector;
		Integer next = 0;
		TopologyContext context;

		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
			LOG.info("TESTBOLT1 prepare");
		}

		// how are the below fields set in the spout?
		@Override
		public void execute(Tuple input) {
			// TODO Auto-generated method stub
			LOG.info("TESTBOLT1 tuple getSourceComponent:"
					+ input.getSourceComponent());
			LOG.info("TESTBOLT1 tuple getSourceGlobalStreamID:"
					+ input.getSourceGlobalStreamid());
			LOG.info("TESTBOLT1 tuple getStreamID:" + input.getSourceStreamId());
			LOG.info("TESTBOLT1 tuple getSourceTask:" + input.getSourceTask());
			LOG.info("TESTBOLT1 tuple getMessageId:" + input.getMessageId());
			LOG.info("TESTBOLT1 tuple numValues:" + input.getValues().size());
			LOG.info("TESTBOLT1 tuple numFields:" + input.size());

			Set<String> set = this.getComponentConfiguration().keySet();
			for (String s : set) {
				LOG.info("bolt key:" + s + " value:"
						+ this.getComponentConfiguration().get(s).toString());
			}
			// only output list of values, depending on streamID, emit all to
			// alert bolt,
			// emit all to db bolt
			collector.emit(new Values("bolt:" + next));
			next++;
		}

		// this doesnt matter for our test. Any output will work
		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("bolt1"));
		}

	}

	static class TestSpout extends BaseRichSpout {
		Integer next = 0;
		Integer next1 = 200;
		SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (next > 100) {
				next = 0;
			}

			// format of stream? odd and even numbers?
			// how to set streamId?, messageId? by emit methods
			// this is wrong? Using Values() which creates Tuples. What are
			// default Tuple params using Values()?
			List<Integer> li = collector.emit("stream1", new Values(next));
			LOG.info("TESTSPOUNT num taskIDS:" + li.size() + " ThreadID:"
					+ Thread.currentThread().getId());
			next++;

			if (next1 > 300) {
				next1 = 200;
			}
			List<Integer> li1 = collector.emit("stream2", new Values(next1));
			LOG.info("TESTSPOUT numTaskIds:" + li1.size());
			next1++;

		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			declarer.declare(new Fields("word"));
		}

	}

	// direct streams
	static class DirectStreamSpout extends BaseRichSpout {

		Integer next = 0;
		SpoutOutputCollector collector;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub

			collector.emit(new Values(next));
			next++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			// declarer.declare("directstream", , new Fields(next));
		}

	}

	// what is the difference between tuples from spout/streamid and
	// spout1/streamid?
	// how to tell difference?
	static class TestSpout1 extends BaseRichSpout {
		Integer next = 1000;
		SpoutOutputCollector collector;
		TopologyContext context;

		@Override
		public void open(Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			// TODO Auto-generated method stub
			this.collector = collector;
			this.context = context;
		}

		@Override
		public void nextTuple() {
			// TODO Auto-generated method stub
			if (next > 1100) {
				next = 1000;
			}

			// how to get taskId int and String?
			Tuple tuple = new TupleImpl(context, new Values(next), 0, "");

			ArrayList<Object> al = new ArrayList<Object>();
			al.add(tuple);
			collector.emit(al);
			next++;
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// TODO Auto-generated method stub
			// streamId, fields. Else streamId is ???
			declarer.declare(new Fields("spout output"));

		}

	}

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			// 3 is number of tasks
			builder.setSpout("word", new TestSpout(), 1);
			// only 1 thread
			// builder.setSpout("word", new TestSpout1(), 1);

			// first argument in shuffleGrouping is sourceId
			// what is name of the declarer.output?
			builder.setBolt("bolt", new TestBolt(), 2).setNumTasks(4)
					.shuffleGrouping("word");

			// only 1 thread stored as TestStorm4output
			builder.setBolt("bolt1", new TestBolt1(), 1)
					.shuffleGrouping("word");

			// fieldGrouping sends tuples w/same set of fields to the same task
			// how to test this?

			// how does field grouping differ? Can join 2 streams using
			// fieldsGrouping,
			// can also send same xxx to same task?
			// should steer only named tuples to bolt. Break up the stream using
			// shuffleGroupings vs. filtering using execute();

			// what is fieldsGrouping w/noneGrouping
			// what is fieldsGrouping w/globalGrouping

			Config config = new Config();
			// config.setNumWorkers(30);
			config.setDebug(true);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("TestStorm4", config,
					builder.createTopology());
			cluster.activate("TestStorm4");

			Utils.sleep(10000);
			cluster.deactivate("TestStorm4");
			cluster.shutdown();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
