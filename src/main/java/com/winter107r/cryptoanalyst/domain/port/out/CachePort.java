package com.winter107r.cryptoanalyst.domain.port.out;

import com.winter107r.cryptoanalyst.domain.model.RiskInsight;
import java.util.Optional;

/** Outbound port for cache reads/writes — implemented by Redis adapter. */
public interface CachePort {
    Optional<RiskInsight> get(String key);
    void put(String key, RiskInsight insight);
}
