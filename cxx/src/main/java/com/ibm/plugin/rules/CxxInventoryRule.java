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
import com.ibm.engine.rule.IDetectionRule;
import com.ibm.mapper.model.INode;
import com.ibm.plugin.CxxAggregator;
import com.ibm.plugin.rules.detection.CxxDetectionRules;
import com.ibm.plugin.translation.CxxTranslationProcess;
import java.util.List;

// TODO: Should extend the sonar-cxx specific Visitor/Check class
public class CxxInventoryRule implements IObserver<List<INode>> {

    private final List<IDetectionRule<Object>> detectionRules;
    private final CxxTranslationProcess translationProcess;

    public CxxInventoryRule() {
        this.detectionRules = CxxDetectionRules.rules();
        this.translationProcess = new CxxTranslationProcess();
    }

    @Override
    public void update(List<INode> nodes) {
        nodes.forEach(CxxAggregator::addNode);
    }
}
