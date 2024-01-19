package org.jingtao8a.register;

/**
 * 注册中心工厂
 */
public interface RegisterFactory {
    /***
     *
     * @param address 注册中心地址
     * @return
     */
    Register getRegister(String address);
}
