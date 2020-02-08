package com.coconason.dtf.demo3.springdemo3.controller;

import com.coconason.dtf.demo3.springdemo3.model.DemoResult;
import com.coconason.dtf.demo3.springdemo3.po.Sc;
import com.coconason.dtf.demo3.springdemo3.service.ISCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class SCController {

    @Autowired
    ISCService scService;

    @RequestMapping(value="/add_sc_info_without_dtf",method = RequestMethod.POST)
    public DemoResult addSCInfoWithoutDtf(@RequestBody Sc sc) throws Exception{
        DemoResult demoResult = scService.addSCInfoWithoutDtf(sc);
        return demoResult;
    }

    @RequestMapping(value="/add_sc_info",method = RequestMethod.POST)
    public DemoResult addSCInfo(@RequestBody Sc sc) throws Exception{
        DemoResult demoResult = scService.addSCInfo(sc);
        return demoResult;
    }

    @RequestMapping(value="/add_sc_info_strong",method = RequestMethod.POST)
    public DemoResult addSCInfoStrong(@RequestBody Sc sc) throws Exception{
        DemoResult demoResult = scService.addSCInfoStrong(sc);
        return demoResult;
    }

    @RequestMapping(value="/add_sc_info_async",method = RequestMethod.POST)
    public DemoResult addSCInfoAsync(@RequestBody Sc sc) throws Exception{
        DemoResult demoResult = scService.addSCInfoAsync(sc);
        return demoResult;
    }
}
