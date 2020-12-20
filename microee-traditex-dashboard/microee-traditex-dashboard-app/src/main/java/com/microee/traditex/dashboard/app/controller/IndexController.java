package com.microee.traditex.dashboard.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.microee.traditex.dashboard.app.message.RedisMessageListener;

@Controller
@RequestMapping("/")
public class IndexController {


    @Value("${topic.traditex.orderbook.message}")
    private String tradiTexOrderBookMessageTopic;

    @Value("${topic.traditex.pricing.message}")
    private String tradiTexPricingTopic;
    
    @Value("${topic.traditex.diskorders.message}")
    private String tradiTexDiskOrdersMessageTopic;
    
    @Value("${topic.traditex.revokeorder.count.message}")
    private String tradiTexRevokeOrderCountMessageTopic;
    
    @Value("${topic.traditex.httpnetwork.message}")
    private String tradiTexHttpNetworkMessageTopic;
    
    @Value("${topic.traditex.account.balance.message}")
    private String tradiTexAccountBalanceMessageTopic;

    @Value("${topic.traditex.account-disk.balancess.message}")
    private String tradiTexAccountDiskBalancessMessageTopic;

    @Value("${topic.traditex.account-solr.balancess.message}")
    private String tradiTexAccountSolrBalancessMessageTopic;
    
    @Value("${topic.traditex.httplog.listener.message}")
    private String tradiTexHttpLogListererMessageTopic;
    
    @Autowired
    private RedisMessageListener redisMessageListener;
    
    @RequestMapping(value = { "/index", "" }, method = RequestMethod.GET)
    public ModelAndView defaults() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("tradiTexOrderBookMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexOrderBookMessageTopic));
        mav.addObject("tradiTexPricingTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexPricingTopic));
        mav.addObject("tradiTexDiskOrdersMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexDiskOrdersMessageTopic));
        mav.addObject("tradiTexRevokeOrderCountMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexRevokeOrderCountMessageTopic));
        mav.addObject("tradiTexHttpNetworkMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexHttpNetworkMessageTopic));
        mav.addObject("tradiTexAccountBalanceMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexAccountBalanceMessageTopic));
        mav.addObject("tradiTexAccountDiskBalancessMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexAccountDiskBalancessMessageTopic));
        mav.addObject("tradiTexAccountSolrBalancessMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexAccountSolrBalancessMessageTopic));
        mav.addObject("tradiTexHttpLogListererMessageTopic", "/topic/" + redisMessageListener.removeENV_NAME(tradiTexHttpLogListererMessageTopic));
        mav.setViewName("index");
        return mav;
    }
    
}
