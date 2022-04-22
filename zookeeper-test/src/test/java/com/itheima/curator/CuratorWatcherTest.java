package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: malichun
 * @date 2022/4/17 0017 23:11
 */
public class CuratorWatcherTest {
    private CuratorFramework client;


    /**
     * 建立连接
     */
    @Before
    public void testConnect() {
        /*
         *  @param connectString       list of servers to connect to
         *  @param sessionTimeoutMs    session timeout
         *  @param connectionTimeoutMs connection timeout
         *  @param retryPolicy         重试策略,
         *  @param zkClientConfig      ZKClientConfig
         *  @return client
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        // 1.第一种方式
        // CuratorFramework client =
        //     CuratorFrameworkFactory.newClient("localhost:2181",60*1000, 15 * 1000, retryPolicy );

        // 2.第二种方式
        client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(60 * 1000)
            .connectionTimeoutMs(15 * 1000)
            .retryPolicy(retryPolicy)
            .namespace("itheima") // 名称空间
            .build();

        // 开启连接
        client.start();
    }

    /**
     * 演示NodeCache: 给指定的一个节点注册监听器
     */
    @Test
    public void testNodeCache() throws Exception {
        // 1. 创建NodeCache对象
        NodeCache nodeCache = new NodeCache(client, "/app1");

        // 2. 注册监听
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点变化了~");

                byte[] data = nodeCache.getCurrentData().getData();
                System.out.println(new String(data));
            }
        });

        // 3. 开启监听, 如果设置为true,则开启监听时加载缓存数据
        nodeCache.start(true);

        while(true){
            Thread.sleep(1000);
        }

    }



    /**
     * 演示PathChildrenCache: 监听某个结点的所有子节点们
     */
    @Test
    public void testPathChildrenCache() throws Exception {
        // 1. 创建PathChildrenCache对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,
            "/app2",
            true // 是否缓存状态信息
            );

        // 2. 绑定监听器
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("子节点变化了~");
                System.out.println(event);
                // 监听子节点的数据变更, 并且拿到变更后的数据
                // 1. 获取类型
                PathChildrenCacheEvent.Type type = event.getType();
                // 2. 判断类型是否是update
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("数据变了!!!!");
                    byte[] data = event.getData().getData();
                    System.out.println(new String(data));
                }
            }
        });

        // 3. 开启监听, 如果设置为true,则开启监听时加载缓存数据
        pathChildrenCache.start(true);

        while(true){
            Thread.sleep(1000);
        }

    }

    /**
     * 演示 TreeCache: 监听某个结点自己和所有的子节点们
     * @throws Exception
     */
    @Test
    public void testTreeCache() throws Exception {
        // 1. 创建监听器
        TreeCache treeCache = new TreeCache(client, "/app2");
        // 2. 注册监听
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println("结点变化了");
                System.out.println(event);
            }
        });
        // 3.开启
        treeCache.start();
        while(true){
            TimeUnit.SECONDS.sleep(3);
        }
    }


    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
