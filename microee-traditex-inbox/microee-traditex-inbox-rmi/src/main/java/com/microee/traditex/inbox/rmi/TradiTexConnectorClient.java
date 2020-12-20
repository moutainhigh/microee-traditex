package com.microee.traditex.inbox.rmi;

import org.springframework.cloud.netflix.feign.FeignClient;
import com.microee.traditex.inbox.oem.rmi.ITradiTexConnectorRMi;

@FeignClient(name = "microee-traditex-inbox-app",
        url = "${micro.services.microee-traditex-inbox-app.listOfServers}",
        path = "/traditex-ws-conns",
        configuration = TraditexClientConfiguration.class)
public interface TradiTexConnectorClient extends ITradiTexConnectorRMi {

}
