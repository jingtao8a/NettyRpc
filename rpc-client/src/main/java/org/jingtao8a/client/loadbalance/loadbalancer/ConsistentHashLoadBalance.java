package org.jingtao8a.client.loadbalance.loadbalancer;

import org.jingtao8a.client.loadbalance.LoadBalance;
import org.jingtao8a.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  一致性哈希
 */
public class ConsistentHashLoadBalance implements LoadBalance {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();
    @Override
    public String doSelect(List<String> serviceAddress, RpcRequest rpcRequest) {
        int identityHashCode = System.identityHashCode(serviceAddress);
        String rpcServiceName = rpcRequest.getClassName();
        ConsistentHashSelector selector = selectors.get(rpcServiceName);
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(rpcServiceName, new ConsistentHashSelector(serviceAddress, 160, identityHashCode));
            selector = selectors.get(rpcServiceName);
        }
        return selector.select(rpcServiceName + Arrays.stream(rpcRequest.getParameters()));
    }

    static class ConsistentHashSelector {
        //存储Hash值与节点映射关系的TreeMap
        private final TreeMap<Long, String> virtualAddressMap;// HashCode: address
        private final int identityHashCode;//System.identityHashCode(serviceAddress) 用于判断该服务的地址集合是否更改

        ConsistentHashSelector(List<String> serviceAddress, int replicaNumber, int identityHashCode) {
            this.virtualAddressMap = new TreeMap<>();
            this.identityHashCode = identityHashCode;
            //每个address生成replicaNumber个virtualAddress存放于TreeMap中
            for (String address : serviceAddress) {
                for (int i = 0; i < replicaNumber / 4; ++i) {
                    // 根据md5算法为每4个结点生成一个消息摘要，摘要长为16字节 128位
                    byte[] digest = md5(address + i);
                    // 随后将128位分为4部分，0-31,32-63,64-95,95-128，并生成4个32位数，存于long中，long的高32位都为0
                    // 并作为虚拟结点的key。
                    for (int h = 0; h < 4; ++h) {
                        long m = hash(digest, h);
                        virtualAddressMap.put(m, address);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        // hash算法这里实际上是MD5hash算法的变种，能够最大限度的让所有位都参与运算，
        // 这样hash值才更加均匀；除了MD5hash算法之外，还有FNV1hash
        // number = 0 ，digest数组倒置，右起
        // 数组下标3，先&掩码FF，左移24位（高位字节1）
        // 数组下标2，先&掩码FF，左移16位（高位字节2）
        // 数组下标1，先&掩码FF，左移8位（高位字节3）
        // 数组下标0，先&掩码FF，无需移位，最后一个字节字，
        // 然后这4个字节做或运算，其实就是直接拼起来
        // 最后再跟掩码做与运算
        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceKey) {
            //生成消息摘要
            byte[] digest = md5(rpcServiceKey);
            //调用hash(digest, 0), 将消息摘要转换为hashcode，这里仅取0-31位来生成HashCode
            return selectForKey(hash(digest, 0));
        }

        private String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualAddressMap.ceilingEntry(hashCode);
            if (entry == null) {
                entry = virtualAddressMap.firstEntry();
            }
            return entry.getValue();
        }
    }
}
