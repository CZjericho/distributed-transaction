package com.transaction.web.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * 发送rabbit消息回调
 * Created by Hua wb on 2018/12/12.
 */
public class RabbitCallbackConfirm implements RabbitTemplate.ConfirmCallback{

    private final Logger logger = LoggerFactory.getLogger(RabbitCallbackConfirm.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        logger.info(" 回调id:" + correlationData);
        if (b) {
            logger.info("消息成功消费");
        } else {
            logger.info("消息消费失败:" + s);
        }
    }

}
