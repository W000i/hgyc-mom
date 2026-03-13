package com.deer.wms.base.system.service.rabbitMQ;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MsgProducer implements RabbitTemplate.ConfirmCallback {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //由于rabbitTemplate的scope属性设置为ConfigurableBeanFactory.SCOPE_PROTOTYPE，所以不能自动注入
    private RabbitTemplate rabbitTemplate;
    /**
     * 构造方法注入rabbitTemplate
     */
    @Autowired
    public MsgProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this); //rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    }

    public void sendMsg(String content) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入ROUTINGKEY_A对应的队列当中去，对应的是队列A
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_A, RabbitMQConfig.ROUTINGKEY_A, content, correlationId);
    }

    public void sendAlarm(String content) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //把消息放入ROUTINGKEY_B对应的队列当中去，对应的是队列B
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_A, RabbitMQConfig.ROUTINGKEY_B, content, correlationId);
    }
    /**
     * 回调
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info(" 回调id:" + correlationData);
        if (ack) {
            logger.info("消息成功发送");
        } else {
            logger.info("消息发送失败:" + cause);
        }
    }

}
