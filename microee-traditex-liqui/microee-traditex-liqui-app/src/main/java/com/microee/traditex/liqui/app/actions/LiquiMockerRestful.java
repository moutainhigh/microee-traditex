package com.microee.traditex.liqui.app.actions;

import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.commons.UUIDObject;
import com.microee.plugin.http.assets.HttpAssets;
import com.microee.plugin.response.R;
import com.microee.traditex.liqui.app.components.OrderStatService;
import com.microee.traditex.liqui.app.core.disk.DiskTableService;

@RestController
@RequestMapping("/mocker")
public class LiquiMockerRestful {


    @Autowired
    private DiskTableService diskService;

    @Autowired
    private OrderStatService orderStatChangeService;

    // 模拟 orderbook
    @RequestMapping(value = "/orderbook", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> orderbook(@RequestBody Map<String, Object> orderbook) {
        String orderbookString = HttpAssets.toJsonString(orderbook);
        diskService.process(new JSONObject(orderbookString).put("the-order-book-id", UUIDObject.get().toString()));
        return R.ok(true);
    }

    // 模拟订单全部成交
    @RequestMapping(value = "/orderFilled", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> orderFilled(@RequestBody Map<String, Object> orderMessage,
            @RequestParam("connid") String connid, @RequestParam("vender") String vender) {
        JSONObject orderMessageObject = new JSONObject("{\"data\": " + HttpAssets.toJsonString(orderMessage) + "}");
        orderStatChangeService.process(new JSONObject().put("connid", connid).put("vender", vender).put("message", orderMessageObject));
        return R.ok(true);
    }

    // 模拟订单部分成交
    @RequestMapping(value = "/orderPartialFilled", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<Boolean> orderPartialFilled(@RequestBody Map<String, Object> orderMessage) {
        String orderMessageString = HttpAssets.toJsonString(orderMessage);
        orderStatChangeService.process(new JSONObject(orderMessageString));
        return R.ok(true);
    }

}
