## 仿写XRPC实现一个轻量级RPC Demo



***
## 项目结构
### rpc-client
- [x] async: 实现RpcFuture，完成客户端异步和同步回调
- [x] core: Netty客户端核心逻辑，Channel复用、心跳保活
- [x] faultTolerantInvoker: 容错策略(fail-fast和retry)
- [x] loadbalance: 负载均衡(Random、FullRound(轮询)、ConsistentHash(一致性哈希))
- [x] proxy: 动态代理

### rpc-common
- [x] annotation: 自定义注解@RpcService(标识提供的服务)
- [x] codec: Netty编、解码器
- [x] dto: 自定义RPC传输协议，RpcMessage、RpcRequest、RpcResponse 
- [x] extension: SPI实现可拔插扩展设计
- [x] register:Zookeeper注册中心
- [x] serializer:ProtoBuf、Kryo、Hessian序列化和反序列化
- [ ] codec: Netty TCP粘包、拆包处理
- [ ] register: Nacos注册中心
- [ ] compress: dummy和gzip对数据包进行压缩

### rpc-server
- [x] core: Netty 服务端核心逻辑，注册服务，接受请求
- [x] invoke: 反射调用请求，实现jdk
- [ ] invoke: 反射调用请求，实现cglib

### another
- [ ] 集成Spring和SpringBoot
- [ ] 编写更多测试


***
参考项目：https://github.com/DongZhouGu/XRPC <br/>
参考博客：https://www.dzgu.top/XRPC/#/