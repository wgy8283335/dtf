package com.coconason.dtf.demo3.springdemo3.service;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */

import com.coconason.dtf.demo3.springdemo3.model.DemoResult;
import com.coconason.dtf.demo3.springdemo3.po.Sc;

public interface ISCService {
    DemoResult addSCInfo(Sc course) throws Exception;

    DemoResult addSCInfoAsync(Sc course) throws Exception;

}
