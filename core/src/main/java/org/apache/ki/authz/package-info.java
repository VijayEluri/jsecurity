/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * Core interfaces and exceptions supporting Authorization (access control).
 * <p/>
 * Ki abbreviates the word 'AuthoriZation' as <tt>authz</tt> to distinguish it seperately from
 * 'AuthentiCation', abbreviated as <tt>authc</tt>.
 * <p/>
 * This package's primary interface of interest, which is the core of Ki authorization functionality,
 * is the <tt>Authorizer</tt>. This interface handles all aspects of principal-related security and is the
 * facade to all other Ki authorization components.
 * <p/>
 * Ki has the ability to authorize subjects (a.k.a. users) without being intrusive to the application's
 * domain model. Most applications will utilize the concepts of <tt>group</tt>s, <tt>role</tt>s, and
 * <tt>permission</tt>s, but Ki tries to be as non-invasive as possible doesn't require any such
 * interfaces (although a Permission interface is made available for fine-grained access control policies if
 * you want to use Ki's permission support out-of-the-box).
 * <p/>
 * Although it is possible for applications to implement this and other interfaces directly, it is not
 * recommended. Ki already has base implementations which should be suitable for 99% of deployments.
 */
package org.apache.ki.authz;
