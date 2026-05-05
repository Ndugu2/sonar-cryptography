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

import org.sonar.api.server.rule.RulesDefinition;

public class CxxScannerRuleDefinition implements RulesDefinition {
    public static final String REPOSITORY_KEY = "cbomkit-cryptography-cxx";

    @Override
    public void define(Context context) {
        NewRepository repository =
                context.createRepository(REPOSITORY_KEY, "cpp")
                        .setName("CBOMkit Cryptography Repository");

        // Use RuleMetadataLoader to load metadata from annotations and resources
        // For C++, we might need to manually register if we don't have the resource files yet,
        // but let's try to follow the pattern or at least register the class.
        for (Class<?> checkClass : CxxRuleList.getChecks()) {
            repository.createRule(checkClass.getAnnotation(org.sonar.check.Rule.class).key())
                    .setName(checkClass.getSimpleName())
                    .setHtmlDescription("Detection rule for C++ cryptographic assets.");
        }

        repository.done();
    }
}
