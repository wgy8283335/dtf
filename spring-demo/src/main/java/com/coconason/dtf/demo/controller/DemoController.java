package com.coconason.dtf.demo.controller;

import com.coconason.dtf.demo.service.IDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author: Jason
 * @date: 2018/7/24-12:07
 */

@RestController
@RequestMapping("/demo_lay")
public class DemoController{

    @Autowired
    private IDemoService iDemoService;

    @RequestMapping(value = "/get_demo_by_id",method=RequestMethod.GET)
    public void getDemoById(@RequestParam String id){
        try {
            String result = iDemoService.getDemoById(id);
            System.out.println(result);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
