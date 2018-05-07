/**
 * @author chengxuwei
 * @date 2018/4/28 16:46
 * @description TODO
 */
package com.sq.config;

import com.sq.bolt.DBBolt;
import com.sq.spout.LocalSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author Leezer
 * @date  2017/12/28
 * spring加载完后自动自动提交Topology
 **/
@Configuration
@Component
public class AutoLoad implements ApplicationListener<ContextRefreshedEvent> {

    private static String BROKERZKSTR;
    private static String TOPIC;
    private static String HOST;
    private static String PORT;
    public AutoLoad(@Value("${storm.kafka.address}") String brokerZkstr,
                    @Value("${zookeeper.host}") String host,
                    @Value("${zookeeper.port}") String port,
                    @Value("${kafka.default-topic}") String topic
    ){
        BROKERZKSTR = brokerZkstr;
        HOST= host;
        TOPIC= topic;
        PORT= port;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            //实例化topologyBuilder类。
            TopologyBuilder topologyBuilder = new TopologyBuilder();
            //设置喷发节点并分配并发数，该并发数将会控制该对象在集群中的线程数。
            BrokerHosts brokerHosts = new ZkHosts(HOST+":"+PORT);
            // 配置Kafka订阅的Topic，以及zookeeper中数据节点目录和名字
            SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, TOPIC, "/storm", "test");
            spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
            spoutConfig.zkServers = Collections.singletonList(HOST);
            spoutConfig.zkPort = Integer.parseInt(PORT);
            //从Kafka最新输出日志读取
            spoutConfig.startOffsetTime = System.currentTimeMillis();
            KafkaSpout receiver = new KafkaSpout(spoutConfig);

            //LocalSpout receiver = new LocalSpout();

            topologyBuilder.setSpout("kafka-spout", receiver, 1).setNumTasks(2);
            topologyBuilder.setBolt("alarm-bolt", new DBBolt(), 1).setNumTasks(2).shuffleGrouping("kafka-spout");
            Config config = new Config();
            config.setDebug(false);
            /*设置该topology在storm集群中要抢占的资源slot数，一个slot对应这supervisor节点上的以个worker进程,如果你分配的spot数超过了你的物理节点所拥有的worker数目的话，有可能提交不成功，加入你的集群上面已经有了一些topology而现在还剩下2个worker资源，如果你在代码里分配4个给你的topology的话，那么这个topology可以提交但是提交以后你会发现并没有运行。 而当你kill掉一些topology后释放了一些slot后你的这个topology就会恢复正常运行。
             */
            config.setNumWorkers(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("kafka-spout", config, topologyBuilder.createTopology());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}