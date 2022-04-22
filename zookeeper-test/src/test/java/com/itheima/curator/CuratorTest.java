package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author: malichun
 * @date 2022/4/17 0017 23:11
 */
public class CuratorTest {
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

    // ====================create=================================================================

    /**
     * 创建结点: create 持久 临时 顺序 数据
     * 1. 基本创建
     * 2. 创建结点,带有数据
     * 3. 设置结点类型
     * 4. 创建多级结点 /app1/p1
     */
    @Test
    public void testCreate() throws Exception {
        // 1.基本创建
        // 如果创建结点,没有指定数据,则默认将当前客户端的ip作为数据存储
        String path = client.create().forPath("/app1");
        System.out.println(path);
    }

    @Test
    public void testCreate2() throws Exception {
        // 1.基本创建
        // 如果创建结点,没有指定数据,则默认将当前客户端的ip作为数据存储
        String path = client.create().forPath("/app2", "hehe".getBytes());
        System.out.println(path);
    }

    @Test
    public void testCreate3() throws Exception {
        // 3. 设置结点类型
        // 默认类型: 持久化
        String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");
        System.out.println(path);
    }

    @Test
    public void testCreate4() throws Exception {
        // 3. 创建多级结点
        // creatingParentContainersIfNeeded(): 如果父接地那不存在,则创建父节点
        String path = client.create().creatingParentContainersIfNeeded().forPath("/app4/p1");
        System.out.println(path);
    }
    // ====================get=================================================================

    /**
     * 查询结点:
     * 1. 查询数据: get: getData().forPath()
     * 2. 查询结点: ls: getChildren().forPath()
     * 3. 查询结点状态信息: ls -s: getData().storingStatIn(状态对象).forPath()
     */

    @Test
    public void testGet1() throws Exception {
        // 1. 查询数据: get
        byte[] data = client.getData().forPath("/app1");
        System.out.println(new String(data));
    }


    @Test
    public void testGet2() throws Exception {
        // 2. 查询结点: ls
        List<String> children = client.getChildren().forPath("/");
        System.out.println(children);
    }

    @Test
    public void testGet3() throws Exception {
        // 3. 查询结点状态信息: ls -s
        Stat status = new Stat();
        client.getData().storingStatIn(status).forPath("/app1");
        System.out.println(status);
    }

    // ====================set=================================================================

    /**
     * 修改数据
     * 1. 基本修改数据: setData().forPath()
     * 2. 根据版本修改: setData().withVersion(version).forPath()
     *   * version是通过查询出来的. 目的就是为了让其他客户端或者线程不干扰我
     * @throws Exception
     */
    @Test
    public void testSet() throws Exception {
        // 设置数据
        client.setData().forPath("/app1","itcast".getBytes());
    }

    @Test
    public void testSetForVersion() throws Exception {
        // 设置数据

        Stat status = new Stat();
        client.getData().storingStatIn(status).forPath("/app1");

        int version = status.getVersion(); // 查询出来的
        System.out.println(version);
        client.setData().withVersion(version).forPath("/app1","haha".getBytes());
    }

// ====================delete=================================================================

    /**
     * 删除节点: delete deleteall
     * 1.删除单个节点: delete().forPath("/app1")
     * 2.删除带有子节点的节点: delete().deletingChildrenIfNeeded.forPath("/app1")
     * 3. 必须成功的成功, 防止网络抖动,(重试): delete().guaranteed().forPath("/app4");
     * 4. 回调: inBackground
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // 1.删除单个节点
        client.delete().forPath("/app1");
    }

    @Test
    public void testDelete2() throws Exception {
        // 2.删除带有子节点的节点
        client.delete().deletingChildrenIfNeeded().forPath("/app4");
    }

    @Test
    public void testDelete3() throws Exception {
        // 3. 必须成功的删除
        client.delete().guaranteed().forPath("/app4");
    }


    @Test
    public void testDelete4() throws Exception {
        // 4. 回调
        client.delete().guaranteed().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("我被删除了~");
                System.out.println(event);
            }
        }).forPath("/app4");
    }

    @After
    public void close() {
        if (client != null) {
            client.close();
        }
    }

}
