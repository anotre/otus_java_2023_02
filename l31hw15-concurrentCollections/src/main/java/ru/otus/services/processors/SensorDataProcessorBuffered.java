package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ConcurrentSkipListSet<SensorData> dataBuffer;
    private final Comparator<SensorData> comparator;
    private final AtomicBoolean flushFlag;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.comparator = new Comparator<>() {
            @Override
            public int compare(SensorData o1, SensorData o2) {
                return o1.getMeasurementTime().compareTo(o2.getMeasurementTime());
            }
        };
        this.dataBuffer = new ConcurrentSkipListSet<>(comparator);
        this.flushFlag = new AtomicBoolean(false);

    }

    @Override
    public void process(SensorData data) {
        this.dataBuffer.add(data);

        if (this.dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        try {
            if (this.dataBuffer.isEmpty()) {
                return;
            }

            if (this.flushFlag.compareAndSet(false, true)) {
                var dataBufferCloned = this.dataBuffer.clone();
                this.dataBuffer.removeAll(dataBufferCloned);
                this.writer.writeBufferedData(new ArrayList<>(dataBufferCloned));
                this.flushFlag.set(false);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}