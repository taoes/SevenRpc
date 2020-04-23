package com.zhoutao123.rpc.service.netty.coder;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class RpcDecoder<T> extends ByteToMessageDecoder {

  private Log log = LogFactory.get();

  private Class<? extends T> aClass;

  public RpcDecoder(Class<? extends T> aClass) {
    if (aClass == null) {
      throw new RuntimeException("解码对象类型不能为NULL");
    }
    this.aClass = aClass;
  }

  @Override
  public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    log.trace("执行解码:{}", in);
    if (in.readableBytes() < 4) {
      return;
    }

    in.markReaderIndex();
    int dataLength = in.readInt();

    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex();
      return;
    }

    byte[] data = new byte[dataLength];
    in.readBytes(data);

    T obj = SerializationUtil.deserialize(data, aClass);
    out.add(obj);
    log.trace("解码完成:{}", obj);
  }
}
