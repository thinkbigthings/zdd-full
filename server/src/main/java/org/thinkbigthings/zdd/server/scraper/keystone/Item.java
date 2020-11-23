package org.thinkbigthings.zdd.server.scraper.keystone;

import java.math.BigDecimal;
import java.util.List;

public record Item(Subspecies subspecies, String strain,
                   BigDecimal thc, BigDecimal cbd, List<TerpeneAmount> terpeneAmounts,
                   BigDecimal weightGrams, Long priceDollars, String vendor) {

}
