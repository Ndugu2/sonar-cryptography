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
package com.ibm.engine.language.cxx.antlr;

import org.antlr.v4.runtime.*;

public abstract class CPP14ParserBase extends Parser {
    protected CPP14ParserBase(TokenStream input) {
        super(input);
    }

    protected boolean IsPureSpecifierAllowed() {
        try {
            var x = this._ctx; // memberDeclarator
            var c = x.getChild(0).getChild(0);
            var c2 = c.getChild(0);
            var p = c2.getChild(1);
            if (p == null) return false;
            return (p instanceof CPP14Parser.ParametersAndQualifiersContext);
        } catch (Exception e) {
        }
        return false;
    }
}
