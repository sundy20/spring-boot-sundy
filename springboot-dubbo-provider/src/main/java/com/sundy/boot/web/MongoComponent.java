package com.sundy.boot.web;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author plus.wang
 * @description
 * @date 2018/8/18
 */
@Component
public class MongoComponent {

    private static final String WEB_LOG_COLL_NAME = "backend_weblog";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Async
    public void asyncSaveWebLog(BasicDBObject basicDBObject) {

        mongoTemplate.insert(basicDBObject, WEB_LOG_COLL_NAME);
    }
}
