package com.dtf.demo3.service;

/**
 * Interface of SC.
 * 
 * @author wangguangyuan
 */

import com.dtf.demo3.model.DemoResult;
import com.dtf.demo3.po.Sc;

public interface ISCService {
    
    /**
     * Add SC information without distributed transaction.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addSCInfoWithoutDtf(Sc sc) throws Exception;
    
    /**
     * Add SC information with distributed transaction in final synchronization mode.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addSCInfo(Sc sc) throws Exception;

    /**
     * Add SC information with distributed transaction in strong synchronization mode.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addSCInfoStrong(Sc sc) throws Exception;
    
    /**
     * Add SC information with distributed transaction in asynchronization mode.
     * 
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addSCInfoAsync(Sc sc) throws Exception;

    /**
     * Get sc information.
     * 
     * @param id int
     * @return Sc
     * @throws Exception exception
     */
    Sc getSCInfo(int id) throws Exception;

}
