package com.rozgar.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String NOTIFICATION_QUEUE   = "rozgar.notifications";
    public static final String RFQ_EVENTS_QUEUE     = "rozgar.rfq.events";
    public static final String ORDER_EVENTS_QUEUE   = "rozgar.order.events";

    // Exchange
    public static final String ROZGAR_EXCHANGE      = "rozgar.exchange";

    // Routing keys
    public static final String NOTIFICATION_KEY     = "notification.#";
    public static final String RFQ_KEY              = "rfq.#";
    public static final String ORDER_KEY            = "order.#";

    @Bean
    public TopicExchange rozgarExchange() {
        return new TopicExchange(ROZGAR_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue rfqEventsQueue() {
        return QueueBuilder.durable(RFQ_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue orderEventsQueue() {
        return QueueBuilder.durable(ORDER_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(rozgarExchange()).with(NOTIFICATION_KEY);
    }

    @Bean
    public Binding rfqBinding() {
        return BindingBuilder.bind(rfqEventsQueue())
                .to(rozgarExchange()).with(RFQ_KEY);
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderEventsQueue())
                .to(rozgarExchange()).with(ORDER_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
