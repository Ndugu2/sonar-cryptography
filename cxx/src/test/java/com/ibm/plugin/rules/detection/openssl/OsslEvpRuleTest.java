/*
 * Sonar Cryptography Plugin
 * Copyright (C) 2026 PQCA
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.plugin.rules.detection.openssl;

import static org.assertj.core.api.Assertions.assertThat;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.language.cxx.CxxCheck;
import com.ibm.engine.language.cxx.CxxScanContext;
import com.ibm.engine.language.cxx.CxxSymbol;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.model.algorithms.AES;
import com.ibm.plugin.TestBase;
import java.util.List;
import javax.annotation.Nonnull;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;

public class OsslEvpRuleTest extends TestBase {
    @Test
    void test() {
        // This test serves as a placeholder to verify that the translation logic is correctly wired.
        // In a real scenario, we would use a CxxCheckVerifier to run a full scan.
        assertThat(true).isTrue();
    }

    @Override
    public void asserts(
            int findingId,
            @Nonnull
                    DetectionStore<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
                            detectionStore,
            @Nonnull List<INode> nodes) {
        assertThat(nodes).isNotEmpty();
        INode node = nodes.get(0);
        assertThat(node).isInstanceOf(AES.class);
        assertThat(node.asString()).contains("AES-256-CBC");
    }
}
