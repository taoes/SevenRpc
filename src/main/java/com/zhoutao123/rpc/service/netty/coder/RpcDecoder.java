package com.zhoutao123.rpc.service.netty.coder;

import com.zhoutao123.rpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RpcDecoder<T> extends ByteToMessageDecoder {

  private final Class<? extends T> aClass;

  @Override
  public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    ChannelId channelId = ctx.channel().id();
    log.debug("执行解码开始, channelId = {}", channelId);
    // 如果不够一个整数，那么继续等待
    if (in.readableBytes() < 4) {
      return;
    }

    // 够一个整数之后，读取这个整数
    in.markReaderIndex();
    int dataLength = in.readInt();

    // 获得内容的长度之后，判断字节是否足够
    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex();
      return;
    }

    // 读取数据
    byte[] data = new byte[dataLength];
    in.readBytes(data);

    // 反序列化
    T obj = SerializationUtil.deserialize(data, aClass);
    out.add(obj);
    log.debug("执行解码结束, res = {} channelId = {}", obj, channelId);
  }
}
