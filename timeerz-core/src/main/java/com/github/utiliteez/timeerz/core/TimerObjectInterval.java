package com.github.utiliteez.timeerz.core;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.cronutils.model.Cron;

public class TimerObjectInterval implements TimerObject {
    private final long interval;
    private TimeUnit timeUnit;
    private Long startTime;
    private final boolean repeat;

    private final Consumer<Long> eventConsumer;
    private Supplier<Object> runnableMethod;
    private CompletableFuture<Object> job;
    private ConcurrentLinkedQueue<CompletableFuture> jobs = new ConcurrentLinkedQueue<>();
	private Runnable jobCompletionRunnable;

    private boolean active;

    public TimerObjectInterval(long interval, TimeUnit timeUnit, boolean repeat, Consumer<Long> eventConsumer, Supplier<Object> runnableMethod, final Runnable jobCompletionRunnable) {
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.repeat = repeat;
        this.eventConsumer = eventConsumer;
        this.runnableMethod = runnableMethod;
	    this.jobCompletionRunnable = jobCompletionRunnable;
	    this.active = true;
    }

    @Override
    public String getId() {
        return "TODO";
    }

    public void reset() {
        this.startTime = null;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delta;
        initializeStartTime();
        delta = startTime - currentTime(timeUnit);
        return unit.convert(delta, timeUnit);
    }
    
    private void initializeStartTime() {
        if (this.startTime == null) {
            this.startTime = currentTime(timeUnit) + this.interval;
        }
    }
    
    @Override
    public int compareTo(Delayed o) {
        TimerObjectInterval timerObjectInterval = (TimerObjectInterval) o;
        initializeStartTime();
        if (this.startTime < timerObjectInterval.startTime) {
            return -1;
        }
        if (this.startTime > timerObjectInterval.startTime) {
            return 1;
        }
        return 0;
    }
    
    private Long initializedStartTime() {
        if (this.startTime == null) {
            this.startTime = currentTime(timeUnit) + this.interval;
        }
        return this.startTime;
    }
    
    public Consumer<Long> getTimerEventConsumer() {
        return eventConsumer;
    }

    @Override
    public Supplier<Object> getJobMethod() {
        return runnableMethod;
    }

    @Override
    public Queue<CompletableFuture> getJobs() {
        return jobs;
    }

    public boolean isRepeat() {
        return repeat;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
	public synchronized boolean isActive() {
		return active;
	}

	@Override
	public synchronized boolean toggleActivation() {
        this.active = !this.active;
        return this.active;
	}

	@Override
	public void changeCron(final Cron cron) {
		// TODO
	}

	@Override
	public Runnable getJobCompletionRunnable() {
		return jobCompletionRunnable;
	}

	private long currentTime(TimeUnit timeUnit) {
        // these units are not possible in Quartz cron.. so they are provided in TimerObjectInterval !
        switch (timeUnit) {
            case NANOSECONDS:
                return System.nanoTime();
            case MICROSECONDS:
                return System.nanoTime() / 1000;
            case MILLISECONDS:
                return System.currentTimeMillis();
        }
        throw new IllegalArgumentException("TimeUnit " + timeUnit + " not supported");
    }

    @Override
    public String toString() {
        return "TimerObjectInterval{" +
                "interval=" + interval +
                ", timeUnit=" + timeUnit +
                ", startTime=" + startTime +
                ", repeat=" + repeat +
                '}';
    }

}