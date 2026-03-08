package it.uniroma1.ingestion;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RestPollingConfigService {

    private final AtomicLong pollingDelayMs;

    public RestPollingConfigService(
            @Value("${ingestion.rest.polling-delay-ms:10000}") long initialDelayMs) {
        this.pollingDelayMs = new AtomicLong(initialDelayMs);
    }

    public long getPollingDelayMs() {
        return pollingDelayMs.get();
    }

    public void setPollingDelayMs(long newDelayMs) {
        if (newDelayMs < 0) {
            throw new IllegalArgumentException("Polling interval must be greater than 0 ms");
        }
        pollingDelayMs.set(newDelayMs);
    }
}