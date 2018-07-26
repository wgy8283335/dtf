package com.coconason.dtf.demo.service.impl;


import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.demo.service.IDemoService;
import org.springframework.stereotype.Service;

/**
 * @Author: Jason
 * @date: 2018/7/24-12:13
 */
@Service
public class DemoServiceImpl implements IDemoService{
    @DtfTransaction
    @Override
    public String getDemoById(String id){
        System.out.println(id+" in Service");
        return id+" in Controller";
    }
}
