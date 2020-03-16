package com.coconason.dtf.demo3.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.client.core.spring.client.RestClientAsync;
import com.coconason.dtf.demo3.dao.ScMapper;
import com.coconason.dtf.demo3.model.DemoResult;
import com.coconason.dtf.demo3.po.Sc;
import com.coconason.dtf.demo3.service.ISCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
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

    @Override
    @Transactional
    public DemoResult addSCInfoWithoutDtf(Sc sc) throws Exception {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addSCInfo(Sc sc) throws Exception {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    @Override
    @DtfTransaction(type="SYNC_STRONG")
    @Transactional
    public DemoResult addSCInfoStrong(Sc sc) throws Exception {
        //int i = 6/0;
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }

    @Override
    @DtfTransaction(type="ASYNC_FINAL")
    @Transactional
    public DemoResult addSCInfoAsync(Sc sc) throws Exception {
        scMapper.insertSelective(sc);
        return new DemoResult().ok();
    }
}
