package com.sq.bolt;

import com.alibaba.fastjson.JSON;
import com.sq.StormLauncher;
import com.sq.dao.UserDao;
import com.sq.po.User;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.IWindowedBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * @author chengxuwei
 * @date 2018/4/28 17:31
 * @description
 */
public class DBBolt extends BaseRichBolt {

    private OutputCollector collector;
    private UserDao userDao;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
        StormLauncher stormLauncher = StormLauncher.getStormLauncher();
        userDao =(UserDao)  stormLauncher.getBean("userDao");
    }

    //参考https://github.com/lzn312/storm-kafka/blob/master/src/main/java/lzn/storm/stormkafka/bolt/AlarmBolt.java

    @Override
    public void execute(Tuple tuple) {
        String userString = (String)tuple.getValue(0);
        //User u = (User) tuple.getValueByField("user");
        User u = JSON.parseObject(userString,User.class);
        System.out.println(u);
        userDao.save(u);
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}