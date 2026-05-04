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
package com.ibm.plugin.rules.detection.openssl;

import com.ibm.engine.rule.IDetectionRule;
import com.ibm.engine.rule.builder.DetectionRuleBuilder;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;

public final class OsslEvpRule {

    private OsslEvpRule() {
        // private
    }

    public static final IDetectionRule<ParserRuleContext> EVP_ENCRYPT_INIT_EX =
            new DetectionRuleBuilder<ParserRuleContext>()
                    .createDetectionRule()
                    .forMethods("EVP_EncryptInit_ex")
                    .withMethodParameter("const EVP_CIPHER *")
                    .buildForContext(new com.ibm.engine.model.context.CipherContext())
                    .inBundle(() -> "OpenSSL")
                    .withoutDependingDetectionRules();

    public static final IDetectionRule<ParserRuleContext> EVP_DECRYPT_INIT_EX =
            new DetectionRuleBuilder<ParserRuleContext>()
                    .createDetectionRule()
                    .forMethods("EVP_DecryptInit_ex")
                    .withMethodParameter("const EVP_CIPHER *")
                    .buildForContext(new com.ibm.engine.model.context.CipherContext())
                    .inBundle(() -> "OpenSSL")
                    .withoutDependingDetectionRules();

    public static final IDetectionRule<ParserRuleContext> EVP_CIPHER_INIT_EX =
            new DetectionRuleBuilder<ParserRuleContext>()
                    .createDetectionRule()
                    .forMethods("EVP_CipherInit_ex")
                    .withMethodParameter("const EVP_CIPHER *")
                    .buildForContext(new com.ibm.engine.model.context.CipherContext())
                    .inBundle(() -> "OpenSSL")
                    .withoutDependingDetectionRules();

    public static List<IDetectionRule<ParserRuleContext>> rules() {
        return List.of(EVP_ENCRYPT_INIT_EX, EVP_DECRYPT_INIT_EX, EVP_CIPHER_INIT_EX);
    }
}
