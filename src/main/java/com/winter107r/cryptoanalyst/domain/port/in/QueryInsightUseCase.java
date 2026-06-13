package com.winter107r.cryptoanalyst.domain.port.in;

import com.winter107r.cryptoanalyst.domain.model.AnalysisQuery;
import com.winter107r.cryptoanalyst.domain.model.RiskInsight;

/** Inbound port — called by REST controller. */
public interface QueryInsightUseCase {
    RiskInsight query(AnalysisQuery query);
}
