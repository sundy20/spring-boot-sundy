package com.sundy.boot.freq.BO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreqBO {
    private RateBO rateBO;
    private List<ItemBO> itemBOList;
}
