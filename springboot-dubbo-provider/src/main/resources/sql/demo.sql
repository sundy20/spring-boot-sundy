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