/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.generated.external_functions;

import com.gs.dmn.generated.AbstractHandwrittenDecisionTest;
import com.gs.dmn.generated.external_functions.type.Transaction;
import com.gs.dmn.generated.external_functions.type.TransactionImpl;
import com.gs.dmn.generated.external_functions.type.TransactionTaxMetaData;
import com.gs.dmn.generated.external_functions.type.TransactionTaxMetaDataImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandwrittenDecisionTest extends AbstractHandwrittenDecisionTest {
    private final FetchForexRate decision = new FetchForexRate();

    @Test
    public void testApply() {
        String derivativeType = "RIGHTS_WARRANTS";
        String taxChargeType = "2";
        Transaction transaction = new TransactionImpl();
        TransactionTaxMetaData transactionTaxMetaData = new TransactionTaxMetaDataImpl();
        assertEquals("forexRate", applyDecision(derivativeType, taxChargeType, transaction, transactionTaxMetaData));
    }

    @Override
    protected void applyDecision() {
        applyDecision(null, null, null, null);
    }

    private String applyDecision(String derivativeType, String taxChargeType, Transaction transaction, TransactionTaxMetaData transactionTaxMetaData) {
        return decision.apply(derivativeType, taxChargeType, transaction, transactionTaxMetaData, context);
    }
}
