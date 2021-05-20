package com.dtf.demo3.controller;

import com.dtf.demo3.model.DemoResult;
import com.dtf.demo3.po.Sc;
import com.dtf.demo3.service.ISCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SC Controller.
 * 
 * @Author: wangguangyuan
 * @date: 2018/8/27-15:00
 */
@RestController
public class SCController {
    
    @Autowired
    private ISCService scService;

    /**
     * Add sc information without distributed transaction.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_sc_info_without_dtf", method = RequestMethod.POST)
    public DemoResult addSCInfoWithoutDtf(@RequestBody final Sc sc) throws Exception {
        DemoResult demoResult = scService.addSCInfoWithoutDtf(sc);
        return demoResult;
    }
    
    /**
     * Add Sc information with distributed transaction in final synchronization mode.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_sc_info", method = RequestMethod.POST)
    public DemoResult addSCInfo(@RequestBody final Sc sc) throws Exception {
        DemoResult demoResult = scService.addSCInfo(sc);
        return demoResult;
    }
    
    /**
     * Add Sc information with distributed transaction in strong synchronization mode. 
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_sc_info_strong", method = RequestMethod.POST)
    public DemoResult addSCInfoStrong(@RequestBody final Sc sc) throws Exception {
        DemoResult demoResult = scService.addSCInfoStrong(sc);
        return demoResult;
    }
    
    /**
     * Add Sc information with distributed transaction in asynchronization mode.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_sc_info_async", method = RequestMethod.POST)
    public DemoResult addSCInfoAsync(@RequestBody final Sc sc) throws Exception {
        DemoResult demoResult = scService.addSCInfoAsync(sc);
        return demoResult;
    }
    
    /**
     * Get Sc.
     *
     * @param id int
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/get_sc", method = RequestMethod.GET)
    public DemoResult getSc(@RequestParam final int id) throws Exception {
        Sc sc = scService.getSCInfo(id);
        return new DemoResult().ok(sc);
    }
    
}
