package com.microee.traditex.inbox.rmi;

import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient(name = "microee-traditex-inbox-app",
        path = "/traditex-ws-conns",
        configuration = TraditexClientConfiguration.class)
public interface TradiTexConnectorRMi {

}
