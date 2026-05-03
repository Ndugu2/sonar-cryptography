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

public final class CxxLanguageSupport
        implements ILanguageSupport<Object, Object, Object, CxxScanContext> {

    @Nonnull private final Handler<Object, Object, Object, CxxScanContext> handler;

    @Nonnull private final CxxLanguageTranslation translation;

    public CxxLanguageSupport() {
        this.handler = new Handler<>(this);
        this.translation = new CxxLanguageTranslation();
    }

    @Nonnull
    @Override
    public ILanguageTranslation<Object> translation() {
        return translation;
    }

    @Nonnull
    @Override
    public DetectionExecutive<Object, Object, Object, CxxScanContext> createDetectionExecutive(
            @Nonnull Object tree,
            @Nonnull IDetectionRule<Object> detectionRule,
            @Nonnull IScanContext<Object, Object> scanContext) {
        return new DetectionExecutive<>(tree, detectionRule, scanContext, this.handler);
    }

    @Nonnull
    @Override
    public IDetectionEngine<Object, Object> createDetectionEngineInstance(
            @Nonnull DetectionStore<Object, Object, Object, CxxScanContext> detectionStore) {
        return new CxxDetectionEngine(detectionStore, this.handler);
    }

    @Nonnull
    @Override
    public IBaseMethodVisitorFactory<Object, Object> getBaseMethodVisitorFactory() {
        return CxxBaseMethodVisitor::new;
    }

    @Nonnull
    @Override
    public Optional<Object> getEnclosingMethod(@Nonnull Object expression) {
        return Optional.empty();
    }

    @Nullable @Override
    public MethodMatcher<Object> createMethodMatcherBasedOn(@Nonnull Object methodDefinition) {
        return null;
    }

    @Nullable @Override
    public EnumMatcher<Object> createSimpleEnumMatcherFor(
            @Nonnull Object enumIdentifier, @Nonnull MatchContext matchContext) {
        return null;
    }
}
