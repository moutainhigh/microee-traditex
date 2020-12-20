package com.microee.traditex.liqui.rmi;

import org.springframework.cloud.netflix.feign.FeignClient;
import com.microee.traditex.liqui.oem.rmi.ILiquiDefaultRMi;

@FeignClient(name = "microee-traditex-liqui-app", path = "/",
        configuration = LiquiClientConfiguration.class)
public interface LiquiDefaultRMi extends ILiquiDefaultRMi {

}
