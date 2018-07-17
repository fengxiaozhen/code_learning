package com.welab.consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Consumer;

/**
 * @author markfeng
 * @version v1.0
 * @desc 业务控制类
 * @date 2018/7/5 下午8:57
 */
public class MQConsumer {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            Connection connection=connectionFactory.createConnection();
            Session session=connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);
            Destination destination=session.createQueue("firstQueue");
            MessageConsumer consumer=session.createConsumer(destination);
            Message message=(TextMessage)consumer.receive();
            System.out.println("接收到队列信息"+message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
