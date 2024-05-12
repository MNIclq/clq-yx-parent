package com.atclq.ssyx.search.receiver;

import com.atclq.ssyx.mq.service.constant.MqConst;
import com.atclq.ssyx.search.service.SkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class SkuReceiver {

    @Autowired
    private SkuService skuService;

    /**
     * 商品上架
     * @param skuId
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_UPPER, durable = "true"),//durable = "true"队列持久化
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_UPPER}
    ))
    public void upperSku(Long skuId, Message message, Channel channel) throws IOException {
        if (null != skuId) {
            //调用方法 上架商品
            skuService.upperSku(skuId);
        }
        /** 手动确认消息
             * 手动确认消息是指消费者在消费完消息之后，需要手动的确认消息已经被消费，RabbitMQ才会从队列中删除该消息。
             * 只有在确认消息之后，RabbitMQ才会认为消息被消费完毕，才会从队列中删除该消息。
             * 手动确认消息有助于确保消息被正确消费，避免消息丢失。
             * 手动确认消息有两种方式：
             * 1. basicAck(long deliveryTag, boolean multiple)
             * 2. basicNack(long deliveryTag, boolean multiple, boolean requeue)
             * 其中，deliveryTag参数表示要确认的消息的标号，multiple参数表示是否确认多个消息，requeue参数表示是否重新入队。
             * 调用basicAck方法确认消息，调用basicNack方法拒绝消息。
             * 调用basicAck方法时，deliveryTag参数必须指定，表示要确认的消息的标号；multiple参数默认为false，表示只确认当前消息；requeue参数默认为false，表示不重新入队。
             * 调用basicNack方法时，deliveryTag参数必须指定，表示要拒绝的消息的标号；multiple参数默认为false，表示只拒绝当前消息；requeue参数默认为true，表示重新入队。
             * 注意：
             * 1. 调用basicAck或basicNack方法后，消息才会从队列中删除。
             * 2. 调用basicAck或basicNack方法后，消息的状态会变成已确认或已拒绝，不能再次修改。
             * 3. 调用basicAck或basicNack方法后，如果确认或拒绝的消息标号不正确，RabbitMQ会抛出异常。
             * 4. 调用basicAck或basicNack方法后，如果确认或拒绝的消息标号已经被确认或拒绝过，RabbitMQ会抛出异常。

             * 第一个参数：表示收到的消息的标号
             * 第二个参数：如果为true表示可以签收多个消息
             */
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 商品下架
     * @param skuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_LOWER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerSku(Long skuId, Message message, Channel channel) throws IOException {
        if (null != skuId) {
            //调用方法 下架商品
            skuService.lowerSku(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}