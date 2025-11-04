package com.uber.notification.tracing;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

public class KafkaTracing {

    private static final OpenTelemetry otel = GlobalOpenTelemetry.get();
    private static final Tracer tracer = otel.getTracer("uber-backend-notification");

    private static final TextMapSetter<Headers> setter = (carrier, key, value) -> {
        if (carrier != null && key != null && value != null) {
            carrier.remove(key);
            carrier.add(key, value.getBytes(StandardCharsets.UTF_8));
        }
    };

    private static final TextMapGetter<Headers> getter = new TextMapGetter<Headers>() {
        @Override
        public Iterable<String> keys(Headers carrier) {
            return () -> carrier.toArray().length == 0 ? java.util.Collections.<String>emptyIterator() :
                    java.util.Arrays.stream(carrier.toArray()).map(Header::key).iterator();
        }
        @Override
        public String get(Headers carrier, String key) {
            if (carrier == null) return null;
            Header h = carrier.lastHeader(key);
            return h != null ? new String(h.value(), StandardCharsets.UTF_8) : null;
        }
    };

    public static <K, V> ProducerRecord<K, V> inject(ProducerRecord<K, V> record) {
        Context ctx = Context.current();
        otel.getPropagators().getTextMapPropagator().inject(ctx, record.headers(), setter);
        return record;
    }

    public static Context extract(Headers headers) {
        return otel.getPropagators().getTextMapPropagator().extract(Context.current(), headers, getter);
    }

    public static Span startConsumerSpan(String name, Headers headers) {
        Context parent = extract(headers);
        return tracer.spanBuilder(name)
                .setSpanKind(SpanKind.CONSUMER)
                .setParent(parent)
                .startSpan();
    }
}
