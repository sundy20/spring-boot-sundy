package com.sundy.boot.inventory.util;

import com.sundy.boot.inventory.domain.Item;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Accessors(chain = true)
@Data
public class FreqRecorder implements Recorder, Cloneable {
    private static final Logger LOGGER_MONITOR = LoggerFactory.getLogger("freqMonitor");

    private String action;

    private String bizIds;

    private String bizCode;

    private String step;

    private String result;

    public FreqRecorder setItem(Item item) {
        this.result = item.getType() + "|" + item.getUnit() + "|" + item.getInterval();
        return this;
    }

    @Override
    public String getMessage() {
        return JOINER.join(action, bizIds, bizCode, step, result);
    }

    @Override
    public Logger getLogger() {
        return LOGGER_MONITOR;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}