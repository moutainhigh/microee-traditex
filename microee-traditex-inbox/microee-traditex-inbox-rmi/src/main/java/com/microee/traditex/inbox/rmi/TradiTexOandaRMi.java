package com.microee.traditex.inbox.rmi;

import org.springframework.cloud.netflix.feign.FeignClient;
import com.microee.traditex.inbox.oem.rmi.ITradiTexOandaRMi;

@FeignClient(name = "microee-traditex-inbox-app", path = "/traditex-hbitex-order",
        configuration = TraditexClientConfiguration.class)
public interface TradiTexOandaRMi extends ITradiTexOandaRMi {

}
