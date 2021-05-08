package com.zhoutao123.rpc.component.client;

import com.zhoutao123.rpc.entity.RpcResponse;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/** 这是默认的配置信息 */
public class RpcFuture implements Future<Object> {

  private final Sync sync;

  private final long startTime = System.currentTimeMillis();

  private RpcResponse response;

  public RpcFuture() {
    this.sync = new Sync();
  }

  public void done(RpcResponse response) {
    this.response = response;
    sync.release(1);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return false;
  }

  @Override
  public boolean isCancelled() {
    return false;
  }

  @Override
  public boolean isDone() {
    return sync.isDone();
  }

  @Override
  public Object get() {
    sync.acquire(-1);
    return Optional.ofNullable(this.response).map(RpcResponse::getResult).orElse(null);
  }

  @Override
  public Object get(long timeout, TimeUnit unit) throws InterruptedException {
    boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
    if (!success) {
      throw new RuntimeException("Timeout");
    }

    return Optional.ofNullable(this.response).map(RpcResponse::getResult).orElse(null);
  }

  static class Sync extends AbstractQueuedSynchronizer {

    private static final long serialVersionUID = 1L;

    private final int done = 1;

    @Override
    protected boolean tryAcquire(int arg) {
      return getState() == done;
    }

    @Override
    protected boolean tryRelease(int arg) {
      int pending = 0;
      if (getState() == pending) {
        return compareAndSetState(pending, done);
      } else {
        return true;
      }
    }

    protected boolean isDone() {
      return getState() == done;
    }
  }
}
