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

import com.ibm.engine.detection.IType;
import com.ibm.engine.detection.MatchContext;
import com.ibm.engine.language.ILanguageTranslation;
import com.ibm.engine.language.cxx.antlr.CPP14Parser;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class CxxLanguageTranslation implements ILanguageTranslation<ParserRuleContext> {
    @Nonnull
    @Override
    public Optional<String> getEnumIdentifierName(
            @Nonnull MatchContext context, @Nonnull ParserRuleContext identifier) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<String> getMethodName(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext methodInvocation) {
        if (methodInvocation instanceof CPP14Parser.PostfixExpressionContext ctx) {
            // Check for function call pattern: postfixExpression '(' expressionList? ')'
            if (ctx.getChildCount() >= 3 && ctx.getChild(1).getText().equals("(")) {
                ParseTree firstChild = ctx.getChild(0);
                // Handle complex postfix expressions like obj.func or obj->func
                if (firstChild instanceof CPP14Parser.PostfixExpressionContext firstPostfix) {
                    if (firstPostfix.getChildCount() >= 3) {
                        String op = firstPostfix.getChild(1).getText();
                        if (op.equals(".") || op.equals("->")) {
                            return Optional.of(firstPostfix.getChild(2).getText());
                        }
                    }
                }
                return Optional.of(firstChild.getText());
            }
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IType> getInvokedObjectTypeString(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext methodInvocation) {
        // For C++, object type might be hard to get without semantic analysis.
        // For now, we can try to extract the qualifier if it's a member access.
        if (methodInvocation instanceof CPP14Parser.PostfixExpressionContext ctx) {
            if (ctx.getChildCount() >= 3 && ctx.getChild(1).getText().equals("(")) {
                ParseTree firstChild = ctx.getChild(0);
                if (firstChild instanceof CPP14Parser.PostfixExpressionContext firstPostfix) {
                    if (firstPostfix.getChildCount() >= 3) {
                        String op = firstPostfix.getChild(1).getText();
                        if (op.equals(".") || op.equals("->")) {
                            String qualifier = firstPostfix.getChild(0).getText();
                            return Optional.of((String type) -> type.equals(qualifier));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IType> getMethodReturnTypeString(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext methodInvocation) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<IType> getMethodParameterTypes(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext methodInvocation) {
        // In C++, we don't have easy access to types without a symbol table.
        // We could potentially return the text of the parameters as a fallback for matching.
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Optional<String> resolveIdentifierAsString(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext identifierTree) {
        if (identifierTree instanceof CPP14Parser.IdExpressionContext
                || identifierTree instanceof CPP14Parser.UnqualifiedIdContext) {
            return Optional.of(identifierTree.getText());
        }
        return Optional.ofNullable(identifierTree.getText());
    }

    @Nonnull
    @Override
    public Optional<String> getEnumClassName(
            @Nonnull MatchContext matchContext, @Nonnull ParserRuleContext enumClass) {
        return Optional.empty();
    }
}
