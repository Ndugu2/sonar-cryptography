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
import com.ibm.engine.detection.Handler;
import com.ibm.engine.detection.IDetectionEngine;
import com.ibm.engine.detection.MethodDetection;
import com.ibm.engine.detection.ResolvedValue;
import com.ibm.engine.detection.TraceSymbol;
import com.ibm.engine.language.cxx.antlr.CPP14ParserBaseListener;
import com.ibm.engine.model.factory.IValueFactory;
import com.ibm.engine.rule.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class CxxDetectionEngine implements IDetectionEngine<ParserRuleContext, CxxSymbol> {
    private final DetectionStore<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
            detectionStore;
    private final Handler<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> handler;

    public CxxDetectionEngine(
            @Nonnull
                    DetectionStore<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext>
                            detectionStore,
            @Nonnull Handler<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> handler) {
        this.detectionStore = detectionStore;
        this.handler = handler;
    }

    @Override
    public void run(@Nonnull ParserRuleContext tree) {
        run(TraceSymbol.createStart(), tree);
    }

    @Override
    public void run(@Nonnull TraceSymbol<CxxSymbol> traceSymbol, @Nonnull ParserRuleContext tree) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(
                new CPP14ParserBaseListener() {
                    @Override
                    public void enterEveryRule(ParserRuleContext ctx) {
                        if (detectionStore
                                .getDetectionRule()
                                .match(ctx, handler.getLanguageSupport().translation())) {
                            detectionStore.onReceivingNewDetection(
                                    new MethodDetection<>(ctx, null));
                        }
                    }
                },
                tree);
    }

    @Nullable @Override
    public ParserRuleContext extractArgumentFromMethodCaller(
            @Nonnull ParserRuleContext methodDefinition,
            @Nonnull ParserRuleContext methodInvocation,
            @Nonnull ParserRuleContext methodParameterIdentifier) {
        // In C++, methodDefinition might be a FunctionDefinitionContext
        // and methodInvocation a PostfixExpressionContext (the call)
        if (methodInvocation instanceof CPP14Parser.PostfixExpressionContext callCtx) {
            // postfixExpression '(' expressionList? ')'
            if (callCtx.getChildCount() >= 3 && callCtx.getChild(1).getText().equals("(")) {
                ParseTree thirdChild = callCtx.getChild(2);
                if (thirdChild instanceof CPP14Parser.ExpressionListContext argList) {
                    // This is a simplified version. We'd need to match the parameter index.
                    // For now, let's just return the whole list or a specific index if we knew it.
                    // But usually, the engine asks for a specific parameter by its identifier.
                    // In C++, we need to find the index of methodParameterIdentifier in methodDefinition.
                    
                    // TODO: Implement proper parameter index matching
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public <O> List<ResolvedValue<O, ParserRuleContext>> resolveValuesInInnerScope(
            @Nonnull Class<O> clazz,
            @Nonnull ParserRuleContext expression,
            @Nullable IValueFactory<ParserRuleContext> valueFactory) {
        // Basic resolution: return the expression itself as a resolved value
        // if it matches the expected type (best effort).
        List<ResolvedValue<O, ParserRuleContext>> results = new LinkedList<>();
        // For now, just return the expression text if O is String
        if (clazz.equals(String.class)) {
            results.add(new ResolvedValue<>((O) expression.getText(), expression));
        }
        return results;
    }

    @Override
    public void resolveValuesInOuterScope(
            @Nonnull ParserRuleContext expression,
            @Nonnull Parameter<ParserRuleContext> parameter) {
        // TODO: Implement outer scope resolution (requires cross-file/cross-function tracking)
    }

    @Override
    public <O> void resolveMethodReturnValues(
            @Nonnull Class<O> clazz,
            @Nonnull ParserRuleContext methodDefinition,
            @Nonnull Parameter<ParserRuleContext> parameter) {
        // TODO: Implement
    }

    @Nullable @Override
    public <O> ResolvedValue<O, ParserRuleContext> resolveEnumValue(
            @Nonnull Class<O> clazz,
            @Nonnull ParserRuleContext enumClassDefinition,
            @Nonnull LinkedList<ParserRuleContext> selections) {
        return null;
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<CxxSymbol>> getAssignedSymbol(
            @Nonnull ParserRuleContext expression) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<CxxSymbol>> getMethodInvocationParameterSymbol(
            @Nonnull ParserRuleContext methodInvocation,
            @Nonnull Parameter<ParserRuleContext> parameter) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<TraceSymbol<CxxSymbol>> getNewClassParameterSymbol(
            @Nonnull ParserRuleContext newClass, @Nonnull Parameter<ParserRuleContext> parameter) {
        return Optional.empty();
    }

    @Override
    public boolean isInvocationOnVariable(
            ParserRuleContext methodInvocation, @Nonnull TraceSymbol<CxxSymbol> variableSymbol) {
        return false;
    }

    @Override
    public boolean isInitForVariable(
            ParserRuleContext newClass, @Nonnull TraceSymbol<CxxSymbol> variableSymbol) {
        return false;
    }
}
