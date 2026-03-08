package com.box.common.log.publisher;

import com.box.common.core.util.JsonUtils;
import com.box.common.log.model.OperateLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认操作日志发布器。
 */
public class NoopOperateLogPublisher implements OperateLogPublisher {

    private static final Logger log = LoggerFactory.getLogger(NoopOperateLogPublisher.class);

    @Override
    public void publish(OperateLogRecord record) {
        if (record == null) {
            return;
        }
        log.info("operate-log={}", JsonUtils.toJson(record));
    }
}
