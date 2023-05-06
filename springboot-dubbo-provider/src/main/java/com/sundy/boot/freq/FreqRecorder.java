package com.sundy.boot.freq;

import com.sundy.boot.freq.BO.ItemBO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Accessors(chain = true)
@Data
public class FreqRecorder implements Recorder, Cloneable {
    private static final Logger LOGGER_MONITOR = LoggerFactory.getLogger("freqMonitor");

    private String action;

    private List<String> bizIds;

    private String bizCode;

    private String step;

    private String result;

    public FreqRecorder setItemBO(ItemBO itemBO) {
        StringBuilder sb = new StringBuilder(itemBO.getType());
        sb.append("|");
        sb.append(itemBO.getUnit());
        sb.append("|");
        sb.append(itemBO.getInterval());
        this.result = sb.toString();
        return this;
    }

    @Override
    public String getMessage() {
        return joiner.join(action, bizIds, bizCode, step, result);
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