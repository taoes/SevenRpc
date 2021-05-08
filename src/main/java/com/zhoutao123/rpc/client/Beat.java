package com.zhoutao123.rpc.client;

import com.zhoutao123.rpc.entity.RpcRequest;

public final class Beat {

  public static final String BEAT_ID = "BEAT_PING_PONG";

  public static RpcRequest BEAT_PING;

  static {
    BEAT_PING = new RpcRequest() {};
    BEAT_PING.setRequestId(BEAT_ID);
  }
}
