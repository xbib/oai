package org.xbib.oai.util;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @param <T> token parameter type
 */
public class ResumptionToken<T> {
    
    private static final int DEFAULT_INTERVAL_SIZE = 1000;

    private static final ConcurrentHashMap<UUID, ResumptionToken<?>> cache = new ConcurrentHashMap<>();

    private final UUID uuid;

    private final int interval;
    
    private int position;
    
    private T value;
    
    private Date expirationDate;
    
    private int completeListSize;
    
    private int cursor;
    
    private String metadataPrefix;
    
    private String set;
    
    private Date from;
    
    private Date until;

    private boolean completed;
    
    private ResumptionToken() {
        this(DEFAULT_INTERVAL_SIZE);
        this.completed = false;
    }
    
    private ResumptionToken(int interval) {
        this.uuid = UUID.randomUUID();
        this.position = 0;
        this.interval = interval;
        this.value = null;
        cache.put(uuid, this);
    }
    
    public static <T> ResumptionToken<T> newToken(T value) {
        return new ResumptionToken<T>().setValue(value);
    }
    
    public static ResumptionToken<?> get(UUID token) {
        return cache.get(token);
    }
    
    public UUID getKey() {
        return uuid;
    }
    
    public ResumptionToken<T> setPosition(int position) {
        this.position = position;
        return this;
    }
    
    public int getPosition() {
        return position;
    }
    
    public int advancePosition() {
        setPosition(position + interval);
        return getPosition();
    }
    
    public int getInterval() {
        return interval;
    }
    
    public ResumptionToken<T> setValue(T value) {
        this.value = value;
        return this;
    }
    
    public T getValue() {
        return value;
    }
    
    public ResumptionToken<T> setExpirationDate(Date date) {
        this.expirationDate = date;
        return this;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public ResumptionToken<T> setCompleteListSize(int size) {
        this.completeListSize = size;
        completed = size < interval;
        return this;
    }
    
    public int getCompleteListSize() {
        return completeListSize;
    }
    
    public ResumptionToken<T> setCursor(int cursor) {
        this.cursor = cursor;
        return this;
    }
    
    public int getCursor() {
        return cursor;
    }

    public ResumptionToken<T> setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
        return this;
    }
    
    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public ResumptionToken<T> setSet(String set) {
        this.set = set;
        return this;
    }
    
    public String getSet() {
        return set;
    }
    
    public ResumptionToken<T> setFrom(Date from) {
        this.from = from;
        return this;
    }
    
    public Date getFrom() {
        return from;
    }
    
    public ResumptionToken<T> setUntil(Date until) {
        this.until = until;
        return this;
    }
    
    public Date getUntil() {
        return until;
    }
    
    public void update(int completeListSize, int pageSize, int currentPage) {
        this.completeListSize = completeListSize;
        this.cursor = pageSize * currentPage;
    }

    public boolean isComplete() {
        return completed;
    }
    
    @Override
    public String toString() {
        return value != null ? value.toString() : null;
    }
}

