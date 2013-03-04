package test4.demo.spouts;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;



public class ModifiedWordSpout extends BaseRichSpout implements IRichSpout{
	public static Logger LOG = Logger.getLogger(ModifiedWordSpout.class);
    boolean _isDistributed;
    SpoutOutputCollector _collector;
    private static Integer num = 0;
    
    public ModifiedWordSpout() {
        this(true);
    }

    public ModifiedWordSpout(boolean isDistributed) {
        _isDistributed = isDistributed;
    }
    
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
    }

    public void close() {

    }

    public Integer getNum(){
    	return num;
    }
    
    public void nextTuple() {
        Utils.sleep(100);
        final String[] words = new String[] {"nathan", "mike", "jackson", "golda", "bertels"};
        final Random rand = new Random();
        final String word = words[rand.nextInt(words.length)];
        if(num<10){
        	LOG.info("MODIFIEDWORDSPOUT num:"+num+" word:"+word);
        	_collector.emit(new Values(word));
        }
        num++;
    }

    public void ack(Object msgId) {
    	LOG.info("MODIFIEDWORDSPOUT ack msgId:"+msgId);
    }

    public void fail(Object msgId) {
    	LOG.info("MODIFIEDWORDSPOUT fail msgId:"+msgId);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    public Map<String, Object> getComponentConfiguration() {
        if(!_isDistributed) {
            Map<String, Object> ret = new HashMap<String, Object>();
            ret.put(Config.TOPOLOGY_MAX_TASK_PARALLELISM, 1);
            return ret;
        } else {
            return null;
        }
    }

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
    
    
    

}
