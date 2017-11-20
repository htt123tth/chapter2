package org.smart4j.chapter2.service;

import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.helper.DatabaseHelper;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.util.PropsUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;



public class CustomerService {
    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(PropsUtil.class);


    /**
     * 获取客户列表
     * 
     * @return
     */
    public List<Customer> getCustomerList(){
        String sql = "select * from customer";
         return DatabaseHelper.queryEntityList(Customer.class,sql);

    }

    /**
     * 获取客户
     * @param id
     * @return
     */
    public Customer getCustomer(long id){

        return null;
    }
    /*
    创建客户
     */
    public boolean createCustomer(Map<String,Object> filedMap){

        return DatabaseHelper.insertEntity(Customer.class,filedMap);
    }
    /*
    更新客户
     */
    public boolean updateCustomer(long id,Map<String,Object> filedMap){
        return DatabaseHelper.updateEntity(Customer.class,id,filedMap);
    }
    /*
    删除客户
     */
    public boolean deleteCustomer(long id){

        return DatabaseHelper.deleteEntity(Customer.class,id);
    }
}
