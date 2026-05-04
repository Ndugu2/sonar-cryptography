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
package com.ibm.mapper.mapper.ssl;

import com.ibm.mapper.mapper.IMapper;
import com.ibm.mapper.model.Algorithm;
import com.ibm.mapper.model.algorithms.AES;
import com.ibm.mapper.model.mode.CBC;
import com.ibm.mapper.model.mode.CTR;
import com.ibm.mapper.model.mode.ECB;
import com.ibm.mapper.model.mode.GCM;
import com.ibm.mapper.utils.DetectionLocation;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OsslCipherMapper implements IMapper {

    @Nonnull
    @Override
    public Optional<? extends Algorithm> parse(
            @Nullable String str, @Nonnull DetectionLocation detectionLocation) {
        if (str == null) {
            return Optional.empty();
        }

        String normalized = str.toLowerCase().replace("_", "-");
        // Example: aes-256-cbc, EVP_aes_256_cbc()
        if (normalized.startsWith("evp-")) {
            normalized = normalized.substring(4);
        }
        if (normalized.endsWith("()")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }

        if (normalized.contains("aes")) {
            int keySize = 0;
            if (normalized.contains("128")) keySize = 128;
            else if (normalized.contains("192")) keySize = 192;
            else if (normalized.contains("256")) keySize = 256;

            AES aes =
                    (keySize != 0)
                            ? new AES(keySize, detectionLocation)
                            : new AES(detectionLocation);

            if (normalized.contains("cbc")) aes.put(new CBC(detectionLocation));
            else if (normalized.contains("gcm")) aes.put(new GCM(detectionLocation));
            else if (normalized.contains("ctr")) aes.put(new CTR(detectionLocation));
            else if (normalized.contains("ecb")) aes.put(new ECB(detectionLocation));

            return Optional.of(aes);
        }

        return Optional.empty();
    }
}
