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
package com.ibm.plugin;

import com.ibm.engine.language.cxx.CxxCheck;
import com.ibm.engine.language.cxx.CxxParserErrorListener;
import com.ibm.engine.language.cxx.CxxScanContext;
import com.ibm.engine.language.cxx.antlr.CPP14Lexer;
import com.ibm.engine.language.cxx.antlr.CPP14Parser;
import java.io.IOException;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class CryptoCxxSensor implements Sensor {

    private static final Logger LOG = LoggerFactory.getLogger(CryptoCxxSensor.class);

    private final Collection<CxxCheck> checks;

    public CryptoCxxSensor(@Nonnull CheckFactory checkFactory) {
        this.checks =
                checkFactory
                        .<CxxCheck>create(CxxScannerRuleDefinition.REPOSITORY_KEY)
                        .addAnnotatedChecks(CxxRuleList.getChecks())
                        .all();
    }

    @Override
    public void describe(@Nonnull SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage("cpp").name("Cryptography for C++");
    }

    @Override
    public void execute(@Nonnull SensorContext context) {
        if (checks.isEmpty()) {
            return;
        }

        FileSystem fs = context.fileSystem();
        Iterable<InputFile> cxxFiles =
                fs.inputFiles(
                        fs.predicates()
                                .and(
                                        fs.predicates().hasLanguage("cpp"),
                                        fs.predicates().hasType(InputFile.Type.MAIN)));

        for (InputFile inputFile : cxxFiles) {
            if (context.isCancelled()) {
                return;
            }
            analyzeFile(context, inputFile);
        }
    }

    private void analyzeFile(@Nonnull SensorContext context, @Nonnull InputFile inputFile) {
        String content;
        try {
            content = inputFile.contents();
        } catch (IOException e) {
            LOG.warn("Unable to read file: {}", inputFile, e);
            return;
        }

        CPP14Parser.TranslationUnitContext parseTree = parseContent(content, inputFile);
        if (parseTree == null) {
            return;
        }

        CxxScanContext scanContext =
                new CxxScanContext(context, inputFile, CxxScannerRuleDefinition.REPOSITORY_KEY);

        for (CxxCheck check : checks) {
            try {
                check.scan(scanContext, parseTree);
            } catch (RuntimeException e) {
                LOG.warn(
                        "Error running check {} on {}: {}",
                        check.getClass().getSimpleName(),
                        inputFile,
                        e.getMessage(),
                        e);
            }
        }
    }

    @Nullable private CPP14Parser.TranslationUnitContext parseContent(
            @Nonnull String content, @Nonnull InputFile inputFile) {
        try {
            CPP14Lexer lexer =
                    new CPP14Lexer(CharStreams.fromString(content, inputFile.toString()));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new CxxParserErrorListener(inputFile));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CPP14Parser parser = new CPP14Parser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new CxxParserErrorListener(inputFile));

            return parser.translationUnit();
        } catch (RuntimeException e) {
            LOG.warn("Unable to parse file: {}", inputFile, e);
            return null;
        }
    }
}
