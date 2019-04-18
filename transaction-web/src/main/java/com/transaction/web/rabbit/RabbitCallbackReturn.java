package com.transaction.web.rabbit;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by Hua wb on 2018/12/13.
 */
public class RabbitCallbackReturn implements RabbitTemplate.ReturnCallback {
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("return begin!");
        System.out.println(message);
        System.out.println(i);
        System.out.println(s);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println("return end!");
    }
}
