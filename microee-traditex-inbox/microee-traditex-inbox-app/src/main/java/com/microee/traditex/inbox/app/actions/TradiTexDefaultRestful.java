package com.microee.traditex.inbox.app.actions;

import javax.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.http.assets.HttpClient;
import com.microee.plugin.http.assets.HttpClientResult;
import com.microee.plugin.response.R;
import okhttp3.Headers;

@RestController
@RequestMapping("/")
public class TradiTexDefaultRestful {

    private HttpClient httpClient;
    
    @PostConstruct
    public void init() {
        this.httpClient = HttpClient.create(null, null);
    }

     // Use 3rd party web-sites to get your IP
     // curl checkip.amazonaws.com
     // curl ifconfig.me
     // curl icanhazip.com
     // curl ipecho.net/plain
     // curl ifconfig.co
    // #### 查看本机外网ip
    @RequestMapping(value = "/pubip", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> pubip() {
        HttpClientResult httpResult = httpClient.doGet("http://ifconfig.co", Headers.of("User-Agent", "curl/7.54.0"));
        if (httpResult.isSuccess()) {
            return R.ok(httpResult.getResult().trim());
        }
        return R.ok(httpResult.getMessage());
    }
    
}
