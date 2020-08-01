package com.zhoutao123.rpc.service.netty.coder;

import com.zhoutao123.rpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcEncoder extends MessageToByteEncoder<Object> {

  @Override
  public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    log.trace("执行编码");
    byte[] data = SerializationUtil.serialize(in);
    out.writeInt(data.length);
    out.writeBytes(data);
    log.trace("执行编码完成");
  }
}
