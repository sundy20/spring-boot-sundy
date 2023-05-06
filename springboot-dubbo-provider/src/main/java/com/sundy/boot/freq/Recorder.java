package com.sundy.boot.freq;

import com.google.common.base.Joiner;
import org.slf4j.Logger;

public interface Recorder {
    Joiner joiner = Joiner.on("|").useForNull("-");

    String getMessage();

    Logger getLogger();

    default void record() {
        getLogger().warn(this.getMessage());
    }
}
