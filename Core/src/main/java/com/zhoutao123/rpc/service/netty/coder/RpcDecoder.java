package com.zhoutao123.rpc.service.netty.coder;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.zhoutao123.rpc.utils.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {

  private Log log = LogFactory.get();

  private Class<?> genericClass;

  public RpcDecoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    log.info("执行解码:{}", in);
    if (in.readableBytes() < 4) {
      log.info("数据长度不正确2");
      return;
    }

    in.markReaderIndex();
    int dataLength = in.readInt();

    if (in.readableBytes() < dataLength) {
      in.resetReaderIndex();
      log.info("数据长度不正确");
      return;
    }

    byte[] data = new byte[dataLength];
    in.readBytes(data);

    Object obj = SerializationUtil.deserialize(data, genericClass);
    out.add(obj);
    log.info("解码完成:{}", obj);
  }
}
