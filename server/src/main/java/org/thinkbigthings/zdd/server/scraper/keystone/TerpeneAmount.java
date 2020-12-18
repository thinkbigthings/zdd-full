package org.thinkbigthings.zdd.server.scraper.keystone;

import org.thinkbigthings.zdd.server.entity.Terpene;

import java.math.BigDecimal;

public record TerpeneAmount(Terpene terpene, BigDecimal amount) {

}
