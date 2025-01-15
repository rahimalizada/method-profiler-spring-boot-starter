package com.jyvee.spring.methodprofiler.aspect;

import lombok.Getter;
import org.slf4j.event.Level;

@Getter
public enum ProfiledLevel {

    TRACE(Level.TRACE),
    DEBUG(Level.DEBUG),
    INFO(Level.INFO),
    WARN(Level.WARN),
    ERROR(Level.ERROR);

    private final Level level;

    ProfiledLevel(final Level level) {
        this.level = level;
    }
}
