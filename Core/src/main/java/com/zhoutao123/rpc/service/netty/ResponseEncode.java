package com.zhoutao123.rpc.service.netty;

import com.alibaba.fastjson.JSON;
import com.zhoutao123.rpc.entity.ResponseInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class ResponseEncode extends MessageToMessageEncoder<ResponseInfo> {

  @Override
  protected void encode(ChannelHandlerContext ctx, ResponseInfo msg, List<Object> out)
      throws Exception {
    if (msg == null) {
      return;
    }

    out.add(JSON.toJSONString(msg));
  }
}
