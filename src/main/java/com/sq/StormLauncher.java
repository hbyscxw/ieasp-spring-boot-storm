package com.sq;

import com.sq.po.User;
import com.sq.utils.KafkaUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author chengxuwei
 * @date 2018/4/28 16:49
 * @description
 */
@SpringBootApplication
@EnableTransactionManagement
@PropertySource(value = {"classpath:config.properties", "classpath:application.properties"})
public class StormLauncher extends SpringBootServletInitializer {

    //设置 安全线程launcher实例
    private volatile static StormLauncher stormLauncher;
    //设置上下文
    private ApplicationContext context;

    public static void main(String[] args) {

        SpringApplicationBuilder application = new SpringApplicationBuilder(StormLauncher.class);
        // application.web(false).run(args);该方式是spring boot不以web形式启动
        application.run(args);
        StormLauncher s = new StormLauncher();
        s.setApplicationContext(application.context());
        setStormLauncher(s);
        new Thread(()->{
            for (int i=0;i<10;i++){
                User u = new User();
                u.setUsername("aa"+i);
                u.setPassword("aa"+i);
                KafkaUtils.sendMsgToKafka(u);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void setStormLauncher(StormLauncher stormLauncher) {
        StormLauncher.stormLauncher = stormLauncher;
    }

    public static StormLauncher getStormLauncher() {
        return stormLauncher;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StormLauncher.class);
    }


    /**
     * 获取上下文
     *
     * @return the application context
     */
    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 设置上下文.
     *
     * @param appContext 上下文
     */
    private void setApplicationContext(ApplicationContext appContext) {
        this.context = appContext;
    }

    /**
     * 通过自定义name获取 实例 Bean.
     *
     * @param name the name
     * @return the bean
     */
    public Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the bean
     */
    public <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param <T>   the type parameter
     * @param name  the name
     * @param clazz the clazz
     * @return the bean
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}