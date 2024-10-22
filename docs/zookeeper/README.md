## 拉取镜像
```shell
docker pull zookeeper:latest
```

## 启动容器
```shell
docker run -v D:/NettyRpc/docs/zookeeper/data:/data --name Zookeeper -p 2181:2181 --restart=always -d zookeeper
```
