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
package com.ibm.plugin.translation;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.language.cxx.CxxCheck;
import com.ibm.engine.language.cxx.CxxScanContext;
import com.ibm.engine.language.cxx.CxxSymbol;
import com.ibm.enricher.Enricher;
import com.ibm.mapper.ITranslationProcess;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.reorganizer.IReorganizerRule;
import com.ibm.mapper.reorganizer.Reorganizer;
import com.ibm.mapper.utils.Utils;
import com.ibm.plugin.translation.translator.CxxTranslator;
import java.util.List;
import javax.annotation.Nonnull;
import org.antlr.v4.runtime.ParserRuleContext;

public class CxxTranslationProcess
        extends ITranslationProcess<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> {

    public CxxTranslationProcess(@Nonnull List<IReorganizerRule> reorganizerRules) {
        super(reorganizerRules);
    }

    @Nonnull
    @Override
    public List<INode> initiate(
            @Nonnull
                    DetectionStore<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
                            rootDetectionStore) {
        // 1. Translate
        final CxxTranslator translator = new CxxTranslator();
        final List<INode> translatedValues = translator.translate(rootDetectionStore);
        Utils.printNodeTree("translated ", translatedValues);

        // 2. Reorganize
        final Reorganizer cxxReorganizer = new Reorganizer(reorganizerRules);
        final List<INode> reorganizedValues = cxxReorganizer.reorganize(translatedValues);
        Utils.printNodeTree("reorganised", reorganizedValues);

        // 3. Enrich
        final List<INode> enrichedValues = Enricher.enrich(reorganizedValues).stream().toList();
        Utils.printNodeTree("enriched   ", enrichedValues);

        return enrichedValues.stream().toList();
    }
}
