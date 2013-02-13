package test4.demo;

import java.util.Map;

import org.apache.log4j.Logger;

import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

//test storm parallelism. Looks like the replication occurs at task level. 
//a new jvm is allocated per supervisor:ports and topology.numWorkers settings. 
//then there are threads in each JVM. Each thread takes an instantiation of the spout/bolts? 
//how to test this? set numTasks and verify independent of #workers
//
public class TestStorm7 {
}
