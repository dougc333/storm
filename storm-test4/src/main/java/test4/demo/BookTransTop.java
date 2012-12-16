package test4.demo;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.transactional.TransactionalTopologyBuilder;
import backtype.storm.tuple.Fields;
import test4.demo.spouts.*;
import test4.demo.bolts.*;

import org.apache.log4j.Logger;

public class BookTransTop {
	public static void main(String[] args) throws InterruptedException {
		Logger.getRootLogger().removeAllAppenders();

		TransactionalTopologyBuilder builder = new TransactionalTopologyBuilder(
				"test", "spout", new TweetsTransactionalSpout(), 4);

		builder.setBolt("users-splitter", new UserSplitterBolt(), 4)
				.shuffleGrouping("spout");
		builder.setBolt("hashtag-splitter", new HashtagSplitterBolt(), 4)
				.shuffleGrouping("spout");

		builder.setBolt("user-hashtag-merger", new UserHashtagJoinBolt(), 4)
				.fieldsGrouping("users-splitter", "users",
						new Fields("tweet_id"))
				.fieldsGrouping("hashtag-splitter", "hashtags",
						new Fields("tweet_id"));

		builder.setBolt("redis-commiter", new RedisCommiterCommiterBolt())
				.globalGrouping("users-splitter", "users")
				.globalGrouping("hashtag-splitter", "hashtags")
				.globalGrouping("user-hashtag-merger");

		LocalCluster cluster = new LocalCluster();

		Config config = new Config();
		config.setMaxSpoutPending(1);
		config.setMaxTaskParallelism(20);

		cluster.submitTopology("test-topology", config, builder.buildTopology());

		Thread.sleep(300000);
	}
}
