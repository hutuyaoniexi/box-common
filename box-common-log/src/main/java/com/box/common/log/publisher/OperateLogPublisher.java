package com.box.common.log.publisher;

import com.box.common.log.model.OperateLogRecord;

/**
 * 操作日志发布器。
 */
public interface OperateLogPublisher {

    void publish(OperateLogRecord record);
}
