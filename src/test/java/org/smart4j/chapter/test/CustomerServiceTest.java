package org.smart4j.chapter.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.service.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
单元测试
 */
public class CustomerServiceTest {
  private final CustomerService customerService;

    public CustomerServiceTest() {
        customerService = new CustomerService();
    }
    @Before
    public void init(){
        //初始化数据库
    }

    @Test
    public void getCustomerListTest() throws Exception{
        List<Customer> customerList=customerService.getCustomerList();
        Assert.assertEquals(2,customerList.size());
    }

    public void getCustomerTest() throws Exception{
        long id=1;
        Customer customer=customerService.getCustomer(id);
        Assert.assertNotNull(customer);

    }
    public void crrateCustomerTest() throws Exception{
        Map<String,Object> filedMap=new HashMap<String,Object>();
        filedMap.put("name","customer100");
        filedMap.put("contact","John");
        filedMap.put("telephone","13512345678");
        boolean result=customerService.createCustomer(filedMap);
        Assert.assertTrue(result);
    }
}
