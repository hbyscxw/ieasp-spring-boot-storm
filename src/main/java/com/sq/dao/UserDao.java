package com.sq.dao;

import com.sq.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author chengxuwei
 * @date 2018/4/28 17:36
 * @description
 */
public interface UserDao extends JpaRepository<User,Integer> {
}