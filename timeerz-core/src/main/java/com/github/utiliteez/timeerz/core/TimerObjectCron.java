package com.github.utiliteez.timeerz.core;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.cronutils.model.Cron;
import com.cronutils.model.field.expression.Every;
import com.cronutils.model.time.ExecutionTime;

public class TimerObjectCron implements TimerObject {

    private final String id;
    private long startTime;
    private Cron cron;
    private final boolean repeat;

    private Consumer<Long> consumer;
    private CompletableFuture<Object> completableFuture;

	private boolean active;

	public TimerObjectCron(String id, Cron cron, Consumer<Long> consumer) {
        this.id = id;
        this.cron = cron;
        this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
        this.repeat = cron.retrieveFieldsAsMap().values().stream().anyMatch(cronField -> cronField.getExpression() instanceof Every);
        this.consumer = consumer;
        this.active = true;
    }

    public TimerObjectCron(String id, Cron cron) {
        this(id, cron, null);
    }

    public String getId() {
        return id;
    }

    public void setConsumer(Consumer<Long> consumer) {
        this.consumer = consumer;
    }

    @Override
    public Consumer<Long> getConsumer() {
        return consumer;
    }

    public CompletableFuture<Object> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<Object> completableFuture) {
        this.completableFuture = completableFuture;
    }

    @Override
    public void reset() {
        this.startTime = ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toInstant().toEpochMilli();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delta = startTime - System.currentTimeMillis();
        return unit.convert(delta, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        TimerObjectCron timerObjectCron = (TimerObjectCron) o;
        return compareStartTime(timerObjectCron);
    }

    private int compareStartTime(TimerObjectCron timerObjectCron) {
        if (this.startTime < timerObjectCron.startTime) {
            return -1;
        }
        if (this.startTime > timerObjectCron.startTime) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean isRepeat() {
        return repeat;
    }

	@Override
	public synchronized boolean isActive() {
		return active;
	}

	public synchronized void deactivate() {
		this.active = false;
	}

	public long getStartTime() {
        return startTime;
    }

    public Cron getCron() {
        return cron;
    }

    @Override
    public String toString() {
        return "TimerObjectCron{" +
                "id='" + id + '\'' +
                ", startTime=" + startTime +
                ", cron=" + cron +
                ", repeat=" + repeat +
                '}';
    }
}