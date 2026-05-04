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
package com.ibm.plugin.translation.translator;

import com.ibm.engine.language.cxx.CxxCheck;
import com.ibm.engine.language.cxx.CxxScanContext;
import com.ibm.engine.language.cxx.CxxSymbol;
import com.ibm.engine.model.IValue;
import com.ibm.engine.model.context.CipherContext;
import com.ibm.engine.model.context.IDetectionContext;
import com.ibm.engine.rule.IBundle;
import com.ibm.mapper.ITranslator;
import com.ibm.mapper.model.INode;
import com.ibm.mapper.utils.DetectionLocation;
import com.ibm.plugin.translation.translator.contexts.CxxCipherContextTranslator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.antlr.v4.runtime.ParserRuleContext;

public class CxxTranslator
        extends ITranslator<CxxCheck, ParserRuleContext, CxxSymbol, CxxScanContext> {

    @Nonnull
    @Override
    protected Optional<INode> translate(
            @Nonnull IBundle bundleIdentifier,
            @Nonnull IValue<ParserRuleContext> value,
            @Nonnull IDetectionContext detectionValueContext,
            @Nonnull String filePath) {
        DetectionLocation detectionLocation =
                getDetectionContextFrom(value.getLocation(), bundleIdentifier, filePath);
        if (detectionLocation == null) {
            return Optional.empty();
        }

        if (detectionValueContext.is(CipherContext.class)) {
            CxxCipherContextTranslator cipherContextTranslator = new CxxCipherContextTranslator();
            return cipherContextTranslator.translate(
                    bundleIdentifier, value, detectionValueContext, detectionLocation);
        }

        return Optional.empty();
    }

    @Nullable @Override
    protected DetectionLocation getDetectionContextFrom(
            @Nonnull ParserRuleContext location,
            @Nonnull IBundle bundle,
            @Nonnull String filePath) {
        int lineNumber = location.getStart().getLine();
        int offset = location.getStart().getCharPositionInLine();
        List<String> keywords = List.of(location.getText());
        return new DetectionLocation(filePath, lineNumber, offset, keywords, bundle);
    }
}
