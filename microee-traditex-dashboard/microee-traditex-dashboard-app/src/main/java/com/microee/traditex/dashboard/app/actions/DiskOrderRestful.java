package com.microee.traditex.dashboard.app.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.rmi.LiquiDiskClient;

@RestController
@RequestMapping("/disk-orders")
public class DiskOrderRestful {

    @Autowired
    private LiquiDiskClient liquiDiskClient;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<?> list() {
        return liquiDiskClient.orders();
    }
    
}
