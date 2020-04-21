package com.zhoutao123.rpc.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

public class SerializationUtil {

  private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

  private static Objenesis objenesis = new ObjenesisStd(true);

  private SerializationUtil() {}

  private static <T> Schema<T> getSchema(Class<T> cls) {
    return (Schema<T>) cachedSchema.computeIfAbsent(cls, RuntimeSchema::createFrom);
  }

  public static <T> byte[] serialize(T obj) {
    Class<T> cls = (Class<T>) obj.getClass();
    LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    try {
      Schema<T> schema = getSchema(cls);
      return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    } finally {
      buffer.clear();
    }
  }

  public static <T> T deserialize(byte[] data, Class<T> cls) {
    try {
      T message = (T) objenesis.newInstance(cls);
      Schema<T> schema = getSchema(cls);
      ProtostuffIOUtil.mergeFrom(data, message, schema);
      return message;
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }
}
