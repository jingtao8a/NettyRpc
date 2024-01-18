package org.jingtao8a.register.zk;

import org.apache.curator.framework.CuratorFramework;
import org.jingtao8a.consts.enums.RpcErrorMsgEnum;
import org.jingtao8a.exception.RpcException;
import org.jingtao8a.register.Register;

import java.net.InetSocketAddress;
import java.util.List;

public class ZkRegister implements Register {
    private CuratorFramework zkClient;

    public ZkRegister(String address) {
        this.zkClient = CuratorUtils.getZkClient(address);
    }

    /**
     *
     * @param rpcServiceName 服务名称
     * @param inetSocketAddress 服务器开放的地址
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }

    @Override
    public void unregisterAllService(InetSocketAddress inetSocketAddress) {
        CuratorUtils.clearRegistry(zkClient, inetSocketAddress);
        zkClient.close();
    }

    @Override
    public List<String> lookupService(String serviceKey) {
        //从注册中心拿到该rpcService下的所有server的Address
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, serviceKey);
        if (serviceUrlList.isEmpty()) {
            throw new RpcException(RpcErrorMsgEnum.SERVICE_CAN_NOT_BE_FOUND, serviceKey);
        }
        return serviceUrlList;
    }

    @Override
    public void stop() {
        this.zkClient.close();
    }
}
