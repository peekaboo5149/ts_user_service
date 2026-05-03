package org.bloggers.ts_users.config;

import brave.Tracing;
import brave.context.slf4j.MDCScopeDecorator;
import brave.handler.SpanHandler;
import brave.propagation.ThreadLocalCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean(destroyMethod = "close")
    Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName("ts_user_service")
                .addSpanHandler(new SpanHandler() {
                })
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.get())
                        .build())
                .build();
    }

    @Bean
    Tracer tracer(Tracing tracing) {
        return new BraveTracer(
                tracing.tracer(),
                BraveCurrentTraceContext.fromBrave(tracing.currentTraceContext()),
                new BraveBaggageManager()
        );
    }
}
