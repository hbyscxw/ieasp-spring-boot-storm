package com.sq.po;

import javax.persistence.*;

/**
 * @author chengxuwei
 * @date 2018/4/28 17:45
 * @description
 */
@Entity
@Table(name = "tb_user")
public class User {
    private int id;
    private String username;
    private String password;

    public User(){

    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}