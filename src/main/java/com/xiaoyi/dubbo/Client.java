package com.xiaoyi.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created on 2021/1/23.
 *
 * @author 小逸
 * @description
 */
public class Client {
    UserService service;

    public UserService buildService(String url){
        ApplicationConfig config = new ApplicationConfig("young-app");

        ReferenceConfig<UserService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(config);
        referenceConfig.setInterface(UserService.class);
        RegistryConfig config1 = new RegistryConfig("zookeeper://101.133.167.247:2181");
        RegistryConfig config2 = new RegistryConfig("zookeeper://101.133.167.247:2182");
        RegistryConfig config3 = new RegistryConfig("zookeeper://101.133.167.247:2183");
        ArrayList<RegistryConfig> objects = new ArrayList<>();
        objects.add(config1);
        objects.add(config2);
        objects.add(config3);
        referenceConfig.setRegistries(objects);
        referenceConfig.setTimeout(50000);
        this.service = referenceConfig.get();
        return service;

    }

    static int i = 0;

    public static void main(String[] args) throws IOException {
        Client client1 = new Client();
        client1.buildService("");
        String cmd;
        while (!(cmd = read()).equals("exit")) {
            UserVo u = client1.service.getUser(Integer.parseInt(cmd));
            System.out.println(u);
        }
    }


    private static String read() throws IOException {
        byte[] b = new byte[1024];
        int size = System.in.read(b);
        return new String(b, 0, size).trim();
    }

    /**
     *
     * zookeeper 实现注册中心，以临时节点的方式实现服务发现和服务失联
     /dubbo  持久节点
     /dubbo/com.xiaoyi.dubbo.UserService 持久节点
     /dubbo/com.xiaoyi.dubbo.UserService/configurators  持久节点
     /dubbo/com.xiaoyi.dubbo.UserService/consumers  持久节点
     /dubbo/com.xiaoyi.dubbo.UserService/providers  持久节点
     /dubbo/com.xiaoyi.dubbo.UserService/routers  持久节点
     /dubbo/com.xiaoyi.dubbo.UserService/consumers/consumer%3A%2F%2F192.168.0.106%2Fcom.xiaoyi.dubbo.UserService%3Fapplication%3Dyoung-app%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.6.2%26interface%3Dcom.xiaoyi.dubbo.UserService%26methods%3DgetUser%26pid%3D18964%26side%3Dconsumer%26timeout%3D50000%26timestamp%3D1611393112868  临时节点
     * */
}
