package org.jingtao8a.all;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jingtao8a.annotation.RpcService;
import org.jingtao8a.server.core.NettyServer;
import org.jingtao8a.server.core.ServiceRegisterCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Setter
public class ServiceInjectProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private ServiceRegisterCache serviceRegisterCache;
    private NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("ServiceInjectProcessor onApplication");
        //Spring启动完成后会接受到event
        if (Objects.isNull(contextRefreshedEvent.getApplicationContext().getParent())) {
            ApplicationContext context = contextRefreshedEvent.getApplicationContext();
            Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(RpcService.class);
            if (serviceBeanMap != null && !serviceBeanMap.isEmpty()) {
                for (Object serviceBean : serviceBeanMap.values()) {
                    RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                    String interfaceName = rpcService.value().getName();
                    String version = rpcService.version();
                    serviceRegisterCache.addService(interfaceName, version, serviceBean);

                }
            }
        }
        nettyServer.start();
    }
}
