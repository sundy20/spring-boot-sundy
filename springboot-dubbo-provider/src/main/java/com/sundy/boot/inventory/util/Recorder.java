package com.sundy.boot.inventory.util;

import com.google.common.base.Joiner;
import org.slf4j.Logger;

public interface Recorder {

    Joiner JOINER = Joiner.on("|").useForNull("-");

    String getMessage();

    Logger getLogger();

    default void record() {
        getLogger().warn(this.getMessage());
    }
}
