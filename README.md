## 说明

一款基于Netty 构建的RPC框架，尚处于开发阶段，仅完成部分特性,支持SpringBoot 应用，依赖于Spring环境，提供了更简单的服务访问机制

无需配置大量的配置文件和代码，仅需要引入依赖，添加注解，即可实现RPC服务访问



## 执行时序图

![时序图](https://cdn.nlark.com/yuque/0/2020/png/437981/1587547986762-9193a42a-b237-401e-ab73-51aab63f91e2.png?x-oss-process=image/resize,w_746)




## 依赖库

+ Slf4j
+ Netty4
+ hutool
+ Spring-Context 5.x

## 特性

- [x] 支持 Spring 应用
- [x] 支持多种注册中心
- [x] 快速响应，
- [x] 注解式驱动，无侵入代码设计


## 即将支持

- [ ] 启动时检查应用
- [ ] 负载均衡
- [ ] 访问日志
- [ ] 服务提供者直连模式
- [ ] 异步访问支持


## 使用方法

### 服务端配置

> 当前正在开发中，主要用于学习，请勿用于生产环境

1.创建SpringBoot 项目 ，并添加依赖

```xml

<dependency>
  <groupId>com.zhoutao123.rpc</groupId>
  <artifactId>SevenRpc</artifactId>
  <version>0.0.1</version>
</dependency>
```


```groovy
compile 'com.zhoutao123.rpc:SevenRpc:0.0.1'
```


2. 定义服务提供者接口,需要在接口上标记 ` @RpcConsumer `


```java
@RpcConsumer
public interface TestService {

  Integer sum(Integer a, Integer b);

  Map<String, Integer> result(Integer a, Integer b);

  double sqrt(int x);
}
```

在服务端实现该接口，并在实现上添加注解 `@RpcService` 代码如下:

```java
@RpcService
public class RpcServiceTest implements TestService {

  @Override
  public Integer sum(Integer a, Integer b) {
    return a + b;
  }

  @Override
  public Map<String, Integer> result(Integer a, Integer b) {
    Map<String, Integer> map = new HashMap<>(3);
    map.put("和", a + b);
    map.put("差", a - b);
    map.put("积", a * b);
    return map;
  }

  @Override
  public double sqrt(int x) {
    return Math.sqrt(x);
  }
}

```


3. 在applicaiton.yml 或者 application.properties 中配置Zk信息

```yaml
application:
  seven-rpc:
    port: 8888
    name: ServiceProvide
    zk:
      host: localhost
      port: 2181
```

4. 启动服务端，调用方法 `http://localhost:${port}/method` 可以查看已经注册的服务，或者观察日志

```text
注册服务完成:/seven-rpc/public abstract double com.zhoutao123.rpc.test.TestService.sqrt(int)
注册服务完成:/seven-rpc/public abstract java.util.Map<java.lang.String, java.lang.Integer> com.zhoutao123.rpc.test.TestService.result(java.lang.Integer,java.lang.Integer)
注册服务完成:/seven-rpc/public abstract java.lang.Integer com.zhoutao123.rpc.test.TestService.sum(java.lang.Integer,java.lang.Integer)
```

值此，服务端配置完成

### 服务消费端配置

1. 同服务端一样，创建SpringBoot项目，添加依赖

2. 在SpringApplication类上,添加注解 `@EnabledRpcConsumer` 启用服务消费者

3. 在代码中注入 此接口，如：

```java
@RestController
public class TestController {
  
  // 可以使用值注入或者构造注入
  @Autowired private TestService testService;

  @GetMapping("/sum")
  public Result test() {
    long startTime = System.currentTimeMillis();
    Integer sum = testService.sum(3, 5);
    long consumerTime = System.currentTimeMillis() - startTime;
    Result result = new Result(sum);
    result.setTime(consumerTime);
    return result;
  }

  @GetMapping("/double/{val}")
  public Result doubleValue(@PathVariable("val") String val) {
    long startTime = System.currentTimeMillis();
    double doubleValue = testService.sqrt(Integer.parseInt(val));
    long consumerTime = System.currentTimeMillis() - startTime;
    Result result = new Result(doubleValue);
    result.setTime(consumerTime);
    return result;
  }

  @GetMapping("/result")
  public Result result() {
    long startTime = System.currentTimeMillis();
    Map<String, Integer> result1 = testService.result(3, 5);
    long consumerTime = System.currentTimeMillis() - startTime;
    Result result = new Result(result1);
    result.setTime(consumerTime);
    return result;
  }
}



public class Result {

  Object result;

  long time;
}


```

4. 启动服务消费端，访问接口

+ /sum

```json
{
    "result": 8,
    "time": 247
}
```

+ double/100

```json
{
    "result": 10,
    "time": 5
}
```

+ result

```json
{
    "result": 8,
    "time": 247
}
```


值此完成RPC 服务的接入
