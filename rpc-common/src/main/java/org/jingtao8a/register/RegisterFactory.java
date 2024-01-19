package org.jingtao8a.register;

import org.jingtao8a.extension.SPI;

/**
 * 注册中心工厂
 */
@SPI(value="zookeeper")
public interface RegisterFactory {
    /***
     *
     * @param address 注册中心地址
     * @return
     */
    Register getRegister(String address);
}
