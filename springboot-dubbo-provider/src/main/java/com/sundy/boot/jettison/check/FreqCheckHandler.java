package com.sundy.boot.jettison.check;

import com.sundy.boot.jettison.manager.FreqManager;
import com.sundy.boot.jettison.query.GoodsTradeQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class FreqCheckHandler implements CheckHandler {

    @Autowired
    private FreqManager freqManager;

    @Override
    public String getCheckCode() {
        return CheckCodeEnum.FREQ_CHECK.getCode();
    }

    @Override
    public CheckResult handleCheck(GoodsTradeQuery goodsTradeQuery, Map<String, Object> extra) {

        return CheckResult.success();
    }
}
