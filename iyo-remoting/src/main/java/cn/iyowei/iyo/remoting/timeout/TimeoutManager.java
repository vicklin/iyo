package cn.iyowei.iyo.remoting.timeout;

import java.util.concurrent.Future;

/**
 * 超时管理
 * @see DefaultTimeoutManager
 */
public interface TimeoutManager {
	Future<?> onTimeout(Runnable task, long timeout);
}
