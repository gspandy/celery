/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.samples.learndemo.service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.xiaoyu.core.ActivemqFactory;
import com.xiaoyu.modules.samples.learndemo.api.ProducerService;

@Service
// @EnableScheduling
public class ProducerServiceImpl implements ProducerService {

    @Autowired
    private JmsMessagingTemplate template;

    @Autowired(required = false)
    private Queue queue;

    @Override
    public void sendNoSpringJms(String msg) {
        final ConnectionFactory factory = ActivemqFactory.INSTANCE.factory();

        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageProducer producer = null;
        try {
            connection = factory.createConnection();
            // connection.start();//生产者这行可有可无
            // 如果设置为true为事务型,之后需要session.commit();才能提交消息
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("xiaoyu.second");
            producer = session.createProducer(destination);
            final TextMessage mess = session.createTextMessage(msg);
            producer.send(mess);
        } catch (final JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final JMSException e) {
                e.printStackTrace();
            }
        }
    }

    // @Scheduled(fixedDelay = 15000)
    @Override
    public void sendScheduledWithSB() {
        final String msg = " a beautiful girl ";
        template.convertAndSend(queue, msg);
    }

    @Override
    public void sendWithSpringJms(String msg) {
        final Queue queue = new ActiveMQQueue("xiaoyu.third");
        template.convertAndSend(queue, msg);
    }

    @Override
    public void sendWithTopic(String msg) {

        final ConnectionFactory factory = ActivemqFactory.INSTANCE.factory();
        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic("xiaoyu.topic.second");
            final MessageProducer producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            final Message message = session.createTextMessage(msg);
            producer.send(message);
        } catch (final JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final JMSException e) {
                e.printStackTrace();
            }
        }

    }

}
