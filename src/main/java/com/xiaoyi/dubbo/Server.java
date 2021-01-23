package com.xiaoyi.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;


import java.io.IOException;
import java.util.ArrayList;

/**
 * Created on 2021/1/23.
 *
 * @author 小逸
 * @description
 */
public class Server {

    public void openServer(int port){
        // 构建应用
        ApplicationConfig config = new ApplicationConfig();
        config.setName("simple_app");

        // 通信协议
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo", port);
        protocolConfig.setThreads(100);

        ServiceConfig<UserService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(config);
        serviceConfig.setProtocol(protocolConfig);
        RegistryConfig config1 = new RegistryConfig("zookeeper://101.133.167.247:2181");
        RegistryConfig config2 = new RegistryConfig("zookeeper://101.133.167.247:2182");
        RegistryConfig config3 = new RegistryConfig("zookeeper://101.133.167.247:2183");
        ArrayList<RegistryConfig> objects = new ArrayList<>();
        objects.add(config1);
        objects.add(config2);
        objects.add(config3);
        serviceConfig.setRegistries(objects);
        serviceConfig.setInterface(UserService.class);
        UserServiceImpl ref = new UserServiceImpl();
        serviceConfig.setRef(ref);

        // 开始提供服务
        serviceConfig.export();
        System.out.println("服务已开启!端口:"+serviceConfig.getExportedUrls().get(0).getPort());
        ref.setPort(serviceConfig.getExportedUrls().get(0).getPort());

    }

    public static void main(String[] args) throws IOException {
        new Server().openServer(-1);
        System.in.read();

        /**
        * zookeeper 中出现的节点
         *
        /dubbo
        /dubbo/com.xiaoyi.dubbo.UserService
        /dubbo/com.xiaoyi.dubbo.UserService/configurators
        /dubbo/com.xiaoyi.dubbo.UserService/providers
        /dubbo/com.xiaoyi.dubbo.UserService/providers/dubbo%3A%2F%2F192.168.0.106%3A20880%2Fcom.xiaoyi.dubbo.UserService%3Fanyhost%3Dtrue%26application%3Dsimple_app%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dcom.xiaoyi.dubbo.UserService%26methods%3DgetUser%26pid%3D23516%26side%3Dprovider%26threads%3D100%26timestamp%3D1611392414133
        * */
    }
}
