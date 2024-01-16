package org.jingtao8a.register;

public interface RegisterFactory {
    /***
     *
     * @param address 注册中心地址
     * @return
     */
    Register getRegister(String address);
}
