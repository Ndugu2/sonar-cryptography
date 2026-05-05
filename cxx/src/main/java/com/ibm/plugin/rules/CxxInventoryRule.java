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
package com.ibm.plugin.rules;

import com.ibm.common.IObserver;
import com.ibm.engine.detection.Finding;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.cxx.CxxCheck;
import com.ibm.engine.language.cxx.CxxLanguageSupport;
import com.ibm.engine.language.cxx.CxxScanContext;
import com.ibm.engine.language.cxx.CxxSymbol;
import com.ibm.engine.language.cxx.antlr.CPP14Parser;
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.CxxAggregator;
import com.ibm.plugin.rules.detection.CxxDetectionRules;
import com.ibm.plugin.translation.CxxTranslationProcess;
import com.ibm.plugin.translation.reorganizer.CxxReorganizerRules;
import java.util.List;
import javax.annotation.Nonnull;
import org.antlr.v4.runtime.ParserRuleContext;
import org.sonar.check.Rule;

@Rule(key = "CxxInventoryRule")
public class CxxInventoryRule
        implements CxxCheck,
                IObserver<Finding<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>> {

    private final List<IDetectionRule<ParserRuleContext>> detectionRules;
    private final CxxTranslationProcess translationProcess;

    public CxxInventoryRule() {
        this.detectionRules = CxxDetectionRules.rules();
        this.translationProcess = new CxxTranslationProcess(CxxReorganizerRules.rules());
    }

    @Override
    public void scan(
            @Nonnull CxxScanContext scanContext, @Nonnull CPP14Parser.TranslationUnitContext tree) {
        CxxLanguageSupport languageSupport = new CxxLanguageSupport();
        for (IDetectionRule<ParserRuleContext> rule : detectionRules) {
            DetectionExecutive<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
                    detectionExecutive =
                            languageSupport.createDetectionExecutive(tree, rule, scanContext);
            detectionExecutive.subscribe(this);
            detectionExecutive.start();
        }
    }

    @Override
    public void update(
            @Nonnull Finding<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> finding) {
        final List<INode> nodes = translationProcess.initiate(finding.detectionStore());
        nodes.forEach(CxxAggregator::addNode);
    }
}
