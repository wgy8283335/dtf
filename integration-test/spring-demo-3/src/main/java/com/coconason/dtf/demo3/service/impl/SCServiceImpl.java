package com.coconason.dtf.demo3.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.client.core.spring.client.RestClientAsync;
import com.coconason.dtf.demo3.dao.ScMapper;
import com.coconason.dtf.demo3.model.DemoResult;
import com.coconason.dtf.demo3.po.Sc;
import com.coconason.dtf.demo3.po.ScExample;
import com.coconason.dtf.demo3.service.ISCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of SC Service.
 * 
 * @Author: Jason
 * @date: 2018/8/27-15:08
 */
@Service
public class SCServiceImpl implements ISCService {
    
    @Autowired
    private ScMapper scMapper;
    
    @Autowired
    private RestClient restClient;
    
    @Autowired
    private RestClientAsync restClientAsync;

    /**
     * Add SC information without distributed transaction.
     *
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @Transactional
    public DemoResult addSCInfoWithoutDtf(final Sc sc) throws Exception {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    /**
     * Add SC information with distributed transaction in final synchronization mode.
     *
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addSCInfo(final Sc sc) {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    /**
     * Add SC information with distributed transaction in strong synchronization mode.
     *
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction(type = "SYNC_STRONG")
    @Transactional
    public DemoResult addSCInfoStrong(final Sc sc) throws Exception {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    /**
     * Add SC information with distributed transaction in asynchronization mode.
     *
     * @param sc Sc
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction(type = "ASYNC_FINAL")
    @Transactional
    public DemoResult addSCInfoAsync(final Sc sc) throws Exception {
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    /**
     * Get sc information.
     *
     * @param id int
     * @return Sc
     * @throws Exception exception
     */
    @Override
    public Sc getSCInfo(final int id) throws Exception {
        ScExample scExample = new ScExample();
        ScExample.Criteria criteria = scExample.createCriteria();
        criteria.andSEqualTo(id);
        List<Sc> list = scMapper.selectByExample(scExample);
        Sc result = list.get(0);
        return result;
    }
    
}
