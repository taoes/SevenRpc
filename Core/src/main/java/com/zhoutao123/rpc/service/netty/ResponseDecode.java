package com.zhoutao123.rpc.service.netty;

import com.alibaba.fastjson.JSON;
import com.zhoutao123.rpc.entity.RequestInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class ResponseDecode extends MessageToMessageDecoder<String> {

  @Override
  protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
    if (msg == null || msg.length() == 0) {
      return;
    }

    msg = msg.replace("\r\n", "");
    RequestInfo requestInfo = JSON.parseObject(msg, RequestInfo.class);
    out.add(requestInfo);
  }
}
