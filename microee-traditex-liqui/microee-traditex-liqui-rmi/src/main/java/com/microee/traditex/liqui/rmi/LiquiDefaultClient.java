package com.microee.traditex.liqui.rmi;

import org.springframework.cloud.netflix.feign.FeignClient;
import com.microee.traditex.liqui.oem.rmi.ILiquiDefaultRMi;

@FeignClient(name = "microee-traditex-liqui-app",
        url = "${micro.services.microee-traditex-liqui-app.listOfServers}", path = "/",
        configuration = LiquiClientConfiguration.class)
public interface LiquiDefaultClient extends ILiquiDefaultRMi {

}
