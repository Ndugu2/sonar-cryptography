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
package com.ibm.engine.language.cxx;

import com.ibm.engine.detection.DetectionStore;
import com.ibm.engine.detection.EnumMatcher;
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IBaseMethodVisitorFactory;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.detection.MethodMatcher;
import com.ibm.engine.executive.DetectionExecutive;
import com.ibm.engine.language.ILanguageSupport;
import com.ibm.engine.language.ILanguageTranslation;
import com.ibm.engine.language.IScanContext;
import com.ibm.engine.rule.IDetectionRule;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.antlr.v4.runtime.ParserRuleContext;

public final class CxxLanguageSupport
        implements ILanguageSupport<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> {

    @Nonnull private final Handler<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> handler;

    @Nonnull private final CxxLanguageTranslation translation;

    public CxxLanguageSupport() {
        this.handler = new Handler<>(this);
        this.translation = new CxxLanguageTranslation();
    }

    @Nonnull
    @Override
    public ILanguageTranslation<ParserRuleContext> translation() {
        return translation;
    }

    @Nonnull
    @Override
    public DetectionExecutive<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
            createDetectionExecutive(
                    @Nonnull ParserRuleContext tree,
                    @Nonnull IDetectionRule<ParserRuleContext> detectionRule,
                    @Nonnull IScanContext<CxxCheck, ParserRuleContext> scanContext) {
        return new DetectionExecutive<>(tree, detectionRule, scanContext, this.handler);
    }

    @Nonnull
    @Override
    public IDetectionEngine<ParserRuleContext, CxxSymbol> createDetectionEngineInstance(
            @Nonnull
                    DetectionStore<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
                            detectionStore) {
        return new CxxDetectionEngine(detectionStore, this.handler);
    }

    @Nonnull
    @Override
    public IBaseMethodVisitorFactory<ParserRuleContext, CxxSymbol> getBaseMethodVisitorFactory() {
        return CxxBaseMethodVisitor::new;
    }

    @Nonnull
    @Override
    public Optional<ParserRuleContext> getEnclosingMethod(@Nonnull ParserRuleContext expression) {
        return Optional.empty();
    }

    @Nullable @Override
    public MethodMatcher<ParserRuleContext> createMethodMatcherBasedOn(
            @Nonnull ParserRuleContext methodDefinition) {
        return null;
    }

    @Nullable @Override
    public EnumMatcher<ParserRuleContext> createSimpleEnumMatcherFor(
            @Nonnull ParserRuleContext enumIdentifier, @Nonnull MatchContext matchContext) {
        return null;
    }
}
