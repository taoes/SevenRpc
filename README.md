## 1、说明
一款基于Netty 构建的RPC框架，尚处于开发阶段，仅完成部分特性,支持SpringBoot应用，依赖于Spring环境，提供了更简单的服务访问机制无需配置大量的配置文件和代码，仅需要引入依赖，添加注解，即可实现RPC服务访问

> 使用方式请查看测试代码，测试代码中演示了服务提供者和服务消费者的使用示例

## 2、执行时序图

![时序图](https://cdn.nlark.com/yuque/0/2020/png/437981/1587547986762-9193a42a-b237-401e-ab73-51aab63f91e2.png?x-oss-process=image/resize,w_746)

## 3、依赖库

+ Slf4j
+ Netty4
+ hutool
+ Spring-Context 5.x

## 4、特性

- [x] 支持 Spring 应用
- [ ] 支持多种注册中心
- [x] 快速响应，
- [x] 注解式驱动，无侵入代码设计
- [ ] 启动时检查应用
- [ ] 负载均衡
- [x] 访问日志
- [ ] 服务提供者直连模式
- [ ] 异步访问支持

### 5、实现原理


#### 5.1 服务提供者实现
​	通过在Application类上添加注解 `@EnableRpcService` 可实现启用服务提供者的功能，该注解中通过@Import 方法注入了配置`RpcServiceSelector.java 此类会在Spring环境中注入各种Bean对象,这些Bean对象大致分为三类: 服务启动类，配置类以及执行类。



```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcServiceSelector.class)
public @interface EnableRpcService {}


@Configuration
public class RpcServiceSelector implements ImportSelector {

  public String[] selectImports(AnnotationMetadata importingClassMetadata) {

    return new String[] {
      RpcService.class.getName(), // 用于启动服务
      RpcServiceContextImpl.class.getName(), // 注入RPC的上下文对象
      ZKRegister.class.getName(), // 注入服务注册器
      RpcConfig.class.getName(), // RPC 的相关配置类
      // 各种执行器
      InitExecutor.class.getName(), //执行 InitFunction 的实现对象
      NettyExecutor.class.getName(), // 执行启动Netty服务
      RegistryExecutor.class.getName(), // 扫描服务完成后，通过此方法向注册器中注册服务
      ScanExecutor.class.getName() // 扫描对外提供的服务完成
    };
  }
}
```



​	注入这些Bean到Spring的环境中后，Spring 继续启动，在Spring环境启动完成之后，RPCService继承了`ApplicationRunner`接口，此接口在SPring启动完成后，会执行器run方法, 在run方法中，主要是获取执行器以及打印Logo，首先从Spring上下文中或者Executor实现，然后对其按照@Order注解的顺序排序然后执行Executor。

```java

@Slf4j
public class RpcService implements ApplicationRunner, ApplicationContextAware {

  // Spring应用上下文
  private ApplicationContext context;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    long startTime = System.currentTimeMillis();
    Map<String, Executor> beansOfType = this.context.getBeansOfType(Executor.class);

    // 对执行器排序
    List<Executor> collect =
        beansOfType.values().stream()
            .sorted(
                (v1, v2) -> {
                  Order order1 = v1.getClass().getAnnotation(Order.class);
                  Order order2 = v2.getClass().getAnnotation(Order.class);
                  if (order1 == null || order2 == null) {
                    return 0;
                  }
                  return order1.value() - order2.value();
                })
            .filter(executor -> !(executor instanceof ClientExecutor))
            .collect(Collectors.toList());
    // 打印Logo
    LogoUtils.printLogo();
    for (Executor executor : collect) {
      executor.start();
    }
    log.info("Start RpcService in {}  ms!", System.currentTimeMillis() - startTime);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }
}
```



通过执行不同的执行器，完成初始化，RPC服务的扫描，注册，以及Netty对外提供服务。



#### 5.2 编码器和解码器

为了方便请求，这里封装了RpcRequest 和 RpcResponse  分别作为请求对象和响应对象。

```java
@Data
public class RpcRequest {

  // 请求ID
  private String requestId;

  // 请求类名
  private String className;

  // 请求方法名
  private String methodName;

  // 请求参数类型
  private Class<?>[] parameterTypes;

  // 请求参数
  private Object[] parameters;
}

@Data
public class RpcResponse {

  // 请求ID
  private String requestId;

  // 错误信息
  private String error;

  // 响应信息
  private Object result;
}
```

在Netty的处理器方面，创建对于RpcRequest/RpcResponse的编解码器。编码器在编码的过程中会在字节数组的前两位添加内容而长度，通过内容的长度来防止TCP 粘包。

```java
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
```



在解码的时候也是类似，首先获取一个整型INT的，此INT是表明后续内容的长度。

```java
@Slf4j
@AllArgsConstructor
public class RpcDecoder<T> extends ByteToMessageDecoder {

  private final Class<? extends T> aClass;

  @Override
  public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
    log.trace("执行解码:{}", in);
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
    log.trace("解码完成:{}", obj);
  }
```





#### 5.3 服务处理逻辑

正确接收到RPCRequest之后，服务端会根据请求的类和方法获取到对应的Method和Instance实例对象



```java
@Override
  protected void channelRead0(ChannelHandlerContext context, RpcRequest request) {
    String requestId = request.getRequestId();

    // 心跳包则不操作
    if (Beat.BEAT_ID.equals(requestId)) {
      return;
    }

    RpcResponse rpcResponse = new RpcResponse();
    rpcResponse.setRequestId(requestId);

    // 获取方法
    MethodInfo methodInfo = rpcServiceContext.getMethodPool().get(request.getMethodName());
    if (methodInfo == null) {
      writeErrorPage(context, requestId, "请求的方法不存在");
      return;
    }

    Method method = methodInfo.getMethod();
    Object instance = methodInfo.getInstance();

    Object[] objParams = request.getParameters();

    // 校验参数
    if (objParams != null && method.getParameterCount() != objParams.length) {
      writeErrorPage(context, requestId, "参数长度不匹配");
      return;
    }

    // 执行方法
    Object invokeResult = null;
    try {
      invokeResult = method.invoke(instance, objParams);
      // 返回响应数据
      rpcResponse.setResult(invokeResult);
      context.channel().writeAndFlush(rpcResponse);
      log.info("数据发送完成：{}", rpcResponse);
    } catch (Exception e) {
      writeErrorPage(context, requestId, e);
    }
  }
```

#### 5.2 服务消费者实现

启用服务消费者，需要在SpringBoot的启动类上添加注解 @EnableRpcClient ，兵器需要设置扫描指定的包，通过定义扫描位置，来注入需要的Bean对象。在Application上添加注解`@EnableRpcClient(scanPath = "xx")`  设置扫描路径，在此注解中注入了`@Import(RpcClientSelector.class)` 其会根据注入的扫描路径，扫描所需要的RPC服务, 并向IOC容器注入这些接口的代理类。

```java
  private void registryRpcServiceBean(BeanDefinitionRegistry registry, Class<?> clazz) {
    boolean addComplete = classes.add(clazz);
    if (!addComplete) {
      return;
    }
    String beanName = clazz.getName();
    // 如果此Bean已经在IOC容器中注入，那么不在进行二次注入
    boolean containsBeanDefinition = registry.containsBeanDefinition(beanName);
    if (containsBeanDefinition) {
      return;
    }

    // 获取代理BeanClass
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

    GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
    definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
    definition.setBeanClass(RpcServiceBeanProxyFactory.class);
    definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
    registry.registerBeanDefinition(clazz.getName(), definition);
  }
```

在代理类中主要是通过发送Netty 请求实现获取执行结果

```java
public class NettyProxyHandler implements InvocationHandler {

  public Object bind(Class<?> cls) {
    return Proxy.newProxyInstance(cls.getClassLoader(), new Class[] {cls}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    
    // 判断此方法是否是Object的默认方法，值得话直接执行并返回
    boolean contains = ClassUtils.allMethodNameOfClass(Object.class).contains(method.getName());
    if (contains) {
      return method.invoke(method, args);
    }

    // 生成请求对象
    RpcRequest request = generator(method, args);
    // 获取处理器
    RpcClientHandler handled = getHandled();

    // 发送请求 & 返回请求的Future对象
    RpcFuture future = handled.sendRequest(request);
    return future.get();
  }
```












