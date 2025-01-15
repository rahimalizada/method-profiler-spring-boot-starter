/*
 * Copyright (c) 2024 Rahim Alizada
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jyvee.spring.methodprofiler.aspect;

import com.jyvee.spring.methodprofiler.MethodProfilerConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ProfiledAdvice {

    private final MethodProfilerConfigurationProperties properties;

    // @Around("@annotation(com.jyvee.spring.methodprofiler.aspect.Profiled)")
    @Around("execution(public * *(..)) && @annotation(profiled)")
    @SuppressWarnings("IllegalThrows")
    public Object executionTime2(final ProceedingJoinPoint point, @SuppressWarnings("unused") final Profiled profiled)
        throws Throwable {
        if (!this.properties.getLoggingEnabled() && !this.properties.getEventPublishingEnabled()) {
            return point.proceed();
        }
        final long startMillis = System.currentTimeMillis();
        final ProfiledEvent event =
            new ProfiledEvent(point.getTarget().getClass().getSimpleName(), point.getSignature().getName());
        if (this.properties.getEventPublishingEnabled()) {
            event.begin();
        }

        final Object object = point.proceed();

        if (this.properties.getEventPublishingEnabled()) {
            event.commit();
        }
        if (this.properties.getLoggingEnabled() && log.isEnabledForLevel(profiled.value().getLevel())) {
            log
                .atLevel(profiled.value().getLevel())
                .log("{}.{}: {} ms",
                    point.getTarget().getClass().getSimpleName(),
                    point.getSignature().getName(),
                    NumberFormat.getNumberInstance(Locale.US).format(System.currentTimeMillis() - startMillis));
        }
        return object;
    }

}
