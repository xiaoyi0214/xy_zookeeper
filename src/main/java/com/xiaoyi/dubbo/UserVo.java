package com.xiaoyi.dubbo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created on 2021/1/23.
 *
 * @author 小逸
 * @description
 */
public class UserVo implements Serializable {

    Integer id;
    String name;
    Date birthDay;
    int port;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDay=" + birthDay +
                ", port=" + port +
                '}';
    }
}
