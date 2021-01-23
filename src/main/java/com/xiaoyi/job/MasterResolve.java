package com.xiaoyi.job;

import org.I0Itec.zkclient.ZkClient;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 2021/1/23.
 * 分布式 JOB
 *
 * 1.多个服务节点只允许其中一个主节点运行JOB任务。
 * 2.当主节点挂掉后能自动切换主节点，继续执行JOB任务。
 *
 *node结构：
 *  1.tuling-master
 *      a.server0001:master
 *      b.server0002:slave
 *      c.server000n:slave
 * 选举流程：
 * 服务启动：
 *      1.在tuling-maste下创建server子节点，值为slave
 *      2.获取所有tuling-master 下所有子节点
 *      3.判断是否存在master 节点
 *      4.如果没有设置自己为master节点
 * 子节点删除事件触发：
 *      1.获取所有tuling-master 下所有子节点
 *      2.判断是否存在master 节点
 *          如果没有设置最小值序号为master 节点
 * @author 小逸
 * @description
 */
public class MasterResolve {

    private String server = "192.168.0.149:2181";
    private ZkClient zkClient;
    private static final String rootPath = "/tuling-master";
    private static final String servicePath = rootPath + "/service";
    private String nodePath;
    private volatile boolean master = false;
    private static MasterResolve resolve;


    private MasterResolve() {
        zkClient = new ZkClient(server, 2000, 5000);
        buildRoot();
        createServerNode();
    }

    public static MasterResolve getInstance() {
        if (resolve == null) {
            resolve= new MasterResolve();
        }
        return resolve;
    }


    // 构建根节点
    public void buildRoot() {
        if (!zkClient.exists(rootPath)) {
            zkClient.createPersistent(rootPath);
        }
    }

    // 创建server节点
    public void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(servicePath, "slave");
        System.out.println("创建service节点:" + nodePath);
        initMaster();
        initListener();
    }



    private void initMaster() {
        boolean existMaster = zkClient.getChildren(rootPath)
                .stream()
                .map(p -> rootPath + "/" + p)
                .map(p -> zkClient.readData(p))
                .anyMatch(d -> "master".equals(d));
        if (!existMaster) {
            doElection();

            System.out.println("当前当选master");
        }
    }
    private void initListener() {
        zkClient.subscribeChildChanges(rootPath, (parentPath, currentChilds) -> {
            doElection();//  执行选举
        });
    }
    // 执行选举
    public void doElection() {
        Map<String, Object> childData = zkClient.getChildren(rootPath)
                .stream()
                .map(p -> rootPath + "/" + p)
                .collect(Collectors.toMap(p -> p, p -> zkClient.readData(p)));
        if (childData.containsValue("master")) {
            return;
        }

        childData.keySet().stream().sorted().findFirst().ifPresent(p -> {
            if (p.equals(nodePath)) { // 设置最小值序号为master 节点
                zkClient.writeData(nodePath, "master");
                master = true;
                System.out.println("当前当选master" + nodePath);
            }
        });

    }


    public static boolean isMaster() {
        return getInstance().master;
    }
}
