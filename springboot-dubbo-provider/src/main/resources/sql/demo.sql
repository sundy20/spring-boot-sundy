##demo数据库表

CREATE TABLE user (
  id               bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
  nick_name        varchar(128) DEFAULT '' NOT NULL comment '名称',
  amount           decimal(5,2) DEFAULT 0 NOT NULL comment '余额',
  status           tinyint(4) DEFAULT 0 NOT NULL comment '用户状态 默认0: 有效 1: 无效',
  created_at       datetime DEFAULT CURRENT_TIMESTAMP NOT NULL comment '创建时间',
  created_by       bigint(20) DEFAULT 0 NOT NULL comment '创建者id',
  updated_at       datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL comment '更新时间',
  updated_by       bigint(20) DEFAULT 0 NOT NULL comment '更新者id',
  deleted          tinyint(4) DEFAULT 0 NOT NULL comment '0：未删除 1：已删除',
  remarks          varchar(256) DEFAULT '' NOT NULL comment '备注',
  PRIMARY KEY (id),
  KEY idx_created_at (created_at),
  KEY idx_updated_at (updated_at),
  KEY idx_nick_name (nick_name),
  ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci comment='用户demo表';

##jettison相关数据库表

CREATE TABLE `jettison_loc` (
                                `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                `nid` bigint(20) unsigned NOT NULL COMMENT '无业务含义的主键',
                                `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                `name` varchar(128) NOT NULL COMMENT '名称',
                                `type` smallint(6) NOT NULL COMMENT '类型',
                                `creator_id` varchar(128) NOT NULL COMMENT '创建者id',
                                `is_delete` smallint(6) NOT NULL COMMENT '删除标记',
                                `status` smallint(6) NOT NULL COMMENT '状态',
                                `code` varchar(128) DEFAULT NULL COMMENT '唯一code',
                                `site_id` bigint(20) unsigned DEFAULT NULL COMMENT '站点id',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_nid` (`nid`),
                                UNIQUE KEY `uk_code` (`code`),
                                KEY `idx_type` (`type`),
                                KEY `idx_creator_id` (`creator_id`),
                                KEY `idx_is_delete` (`is_delete`),
                                KEY `idx_site_id` (`site_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = 'jettison资源位表';

CREATE TABLE `jettison_plan` (
                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                 `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                 `nid` bigint(20) unsigned NOT NULL COMMENT '无业务含义的主键',
                                 `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                 `name` varchar(128) NOT NULL COMMENT '名称',
                                 `creator_id` varchar(128) NOT NULL COMMENT '创建者id',
                                 `is_delete` smallint(6) NOT NULL COMMENT '删除标记',
                                 `loc_id` bigint(20) unsigned NOT NULL COMMENT '绑定资源位id',
                                 `begin_time` bigint(20) unsigned NOT NULL COMMENT '开始时间',
                                 `end_time` bigint(20) unsigned NOT NULL COMMENT '结束时间',
                                 `status` smallint(6) NOT NULL COMMENT '状态',
                                 `type` smallint(6) NOT NULL COMMENT '类型',
                                 `target` json DEFAULT NULL COMMENT '定向信息',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_nid` (`nid`),
                                 KEY `idx_creator_id` (`loc_id`),
                                 KEY `idx_is_delete` (`is_delete`),
                                 KEY `idx_loc_id` (`loc_id`),
                                 KEY `idx_status` (`status`),
                                 KEY `idx_type` (`type`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = 'jettison排期表';

CREATE TABLE `jettison_supply` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                   `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                   `name` varchar(128) DEFAULT NULL COMMENT '名称',
                                   `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                   `type` bigint(20) unsigned DEFAULT NULL COMMENT '类型',
                                   `biz_key` varchar(128) DEFAULT NULL COMMENT '业务标识唯一key',
                                   `nid` bigint(20) unsigned NOT NULL COMMENT '无业务含义的主键',
                                   `begin_time` bigint(20) unsigned DEFAULT NULL COMMENT '开始时间',
                                   `end_time` bigint(20) unsigned DEFAULT NULL COMMENT '结束时间',
                                   `status` bigint(20) unsigned DEFAULT NULL COMMENT '状态',
                                   `owner_id` varchar(64) DEFAULT NULL COMMENT '主题id',
                                   `owner_type` varchar(64) DEFAULT NULL COMMENT '主题类型',
                                   `action_category` varchar(64) DEFAULT 'supply' COMMENT '活动类型',
                                   `supply_type` varchar(64) DEFAULT NULL COMMENT '权益供给类型',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_nid` (`nid`),
                                   UNIQUE KEY `uk_biz_key` (`biz_key`),
                                   KEY `idx_status` (`status`),
                                   KEY `idx_owner_type` (`owner_type`),
                                   KEY `idx_owner_id` (`owner_id`),
                                   KEY `idx_action_category` (`action_category`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = 'jettison供给表';

CREATE TABLE `jettison_strategy` (
                                     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `nid` bigint(20) unsigned NOT NULL COMMENT 'strategy id',
                                     `plan_id` bigint(20) unsigned NOT NULL COMMENT '排期id',
                                     `loc_id` bigint(20) unsigned NOT NULL COMMENT '资源位id',
                                     `strategy_json` json DEFAULT NULL COMMENT '策略参数',
                                     `priority` int(11) DEFAULT NULL COMMENT '优先级',
                                     `name` varchar(128) DEFAULT NULL COMMENT '策略名',
                                     `play_name` varchar(64) DEFAULT NULL COMMENT '玩法名称',
                                     `play_code` varchar(64) DEFAULT NULL COMMENT '玩法code',
                                     `description` varchar(256) DEFAULT NULL COMMENT '描述',
                                     `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                     `is_valid` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否有效',
                                     `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                     `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                     `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                     `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_nid` (`nid`),
                                     KEY `idx_plan_id` (`plan_id`),
                                     KEY `idx_loc_id` (`loc_id`),
                                     KEY `idx_play_code` (`play_code`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'jettison策略表';

CREATE TABLE `jettison_strategy_supply` (
                                            `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `nid` bigint(20) unsigned NOT NULL COMMENT '关系 id',
                                            `strategy_id` bigint(20) unsigned NOT NULL COMMENT '排期id jettison_strategy.nid',
                                            `supply_id` bigint(20) unsigned NOT NULL COMMENT '供给id supply.nid',
                                            `plan_id` bigint(20) unsigned NOT NULL COMMENT '排期id',
                                            `loc_id` bigint(20) unsigned NOT NULL COMMENT '资源位id',
                                            `priority` int(11) DEFAULT NULL COMMENT '优先级',
                                            `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                            `is_valid` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否有效',
                                            `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
                                            `creator_id` varchar(64) NOT NULL DEFAULT '' COMMENT '创建者',
                                            `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                            `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_nid` (`nid`),
                                            KEY `idx_strategy_id` (`strategy_id`),
                                            KEY `idx_supply_id` (`supply_id`),
                                            KEY `idx_plan_id` (`plan_id`),
                                            KEY `idx_loc_id` (`loc_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'jettison策略-供给关系表';

CREATE TABLE `jettison_freq_item` (
                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                             `gmt_create` datetime NOT NULL COMMENT '创建时间',
                             `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                             `nid` bigint(20) unsigned NOT NULL COMMENT '无业务含义的主键',
                             `attribute` json DEFAULT NULL COMMENT '扩展属性',
                             `type` smallint(6) NOT NULL COMMENT '类型',
                             `creator_id` varchar(128) NOT NULL COMMENT '创建者id',
                             `name` varchar(128) NOT NULL COMMENT '名称',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_nid` (`nid`),
                             KEY `idx_type` (`type`),
                             KEY `idx_creator_id` (`creator_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'jettison库存频次配置项';

CREATE TABLE `jettison_plan_freq` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                      `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                      `attribute` json DEFAULT NULL COMMENT '属性标签',
                                      `plan_id` bigint(20) unsigned NOT NULL COMMENT '排期id',
                                      `biz_type` varchar(128) NOT NULL COMMENT '业务类型',
                                      `creator_id` varchar(128) NOT NULL COMMENT '创建者id',
                                      `is_valid` smallint(6) NOT NULL DEFAULT '1' COMMENT '是否有效，1-有效；0-无效',
                                      `freq_id` bigint(20) unsigned NOT NULL COMMENT '频次id',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_plan_valid` (`plan_id`, `is_valid`),
                                      KEY `idx_plan_freq` (`plan_id`, `freq_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'jettison排期与库存频次配置项关系表';

CREATE TABLE `jettison_op_record` (
                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `gmt_create` datetime NOT NULL COMMENT '创建时间',
                                      `gmt_modified` datetime NOT NULL COMMENT '修改时间',
                                      `nid` bigint(20) unsigned NOT NULL COMMENT '无业务含义的主键',
                                      `attribute` json DEFAULT NULL COMMENT '扩展属性',
                                      `user_id` varchar(128) NOT NULL COMMENT '操作人',
                                      `entity_id` bigint(20) unsigned NOT NULL COMMENT '实体id',
                                      `entity_type` varchar(32) NOT NULL COMMENT '实体类型',
                                      `op_type` varchar(32) NOT NULL COMMENT '操作类型',
                                      `op_time` bigint(20) unsigned NOT NULL COMMENT '记录时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_nid` (`nid`),
                                      KEY `idx_user_id` (`user_id`),
                                      KEY `idx_entity_id_entity_type` (`entity_id`, `entity_type`),
                                      KEY `idx_op_type` (`op_type`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT = 'jettison后台操作日志';



