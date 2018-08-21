package com.sundy.boot.mongodb.service;

import com.sundy.boot.mongodb.bson.SmsLog;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author plus.wang
 * @description mongodb 业务层
 * @date 2018/4/25
 */
@Service
public class SmsLogService {

    @Resource
    private MongoTemplate mongoTemplate;

    public void save(SmsLog smsLog) {

        mongoTemplate.save(smsLog);
    }

    public SmsLog findByPhone(String phone) {

        Query query = new Query(Criteria.where("phone").is(phone));

        query.with(new Sort(Sort.Direction.DESC, "id"));

        query.limit(1);

        return mongoTemplate.findOne(query, SmsLog.class);
    }
}
