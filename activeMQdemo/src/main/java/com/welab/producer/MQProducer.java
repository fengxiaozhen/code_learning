package com.welab.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author markfeng
 * @version v1.0
 * @desc 业务控制类
 * @date 2018/7/5 下午8:35
 */
public class MQProducer {

    public static void main(String[] args) {
        ConnectionFactory connectionFactory= new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            Connection connection=connectionFactory.createConnection();
            connection.start();
            Session session=connection.createSession(Boolean.FALSE,Session.AUTO_ACKNOWLEDGE);
            Destination destination=session.createQueue("firstQueue");
            MessageProducer producer=session.createProducer(destination);
            TextMessage message=session.createTextMessage("hello mark");
            producer.send(message);
            System.out.println("发送消息成功");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
