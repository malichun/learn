package cn.itcast.test;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @description:
 * @author: malichun
 * @time: 2021/7/13/0013 11:51
 */
@Slf4j(topic = "c.Test21")
public class Test21_MessageQueue {
    public static void main(String[] args) {

        MessageQueue queue = new MessageQueue(2);
        // 生产者生产消息
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                queue.put(new Message(id, "值"+id));
            }, "生产者" + i).start();
        }


        // 1个消费者
        new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = queue.take();
            }
        },"消费者").start();
    }
}

// 消息队列,java线程间通信的消息队列
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    // 消息的队列集合
    private final LinkedList<Message> list = new LinkedList<>();
    // 队列的容量
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取消息
    public Message take() {
        // 检查队列是否为空
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    log.warn("队列为空,消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 从队列的头部获取消息返回
            Message message = list.removeFirst();
            log.warn("已消费消息{}",message);
            // 通知生产者,队列空了,唤醒
            list.notifyAll();
            return message;
        }
    }

    // 存入消息
    public void put(Message message) {
        synchronized (list) {
            // 检查队列是否满了,满了阻塞住
            while (list.size() == capacity) {
                try {
                    log.warn("队列以满,生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 将消息加入到队列尾部
            list.addLast(message);
            log.warn("已生产消息{}",message);
            list.notifyAll(); // 把正在等待新的消息的线程唤醒
        }
    }
}

// 消息类
final class Message {
    private int id; // 消息的id
    private Object value; // 值

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Message{" +
            "id=" + id +
            ", value=" + value +
            '}';
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }
}


