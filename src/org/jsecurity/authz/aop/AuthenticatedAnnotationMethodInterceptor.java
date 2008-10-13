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
package org.jsecurity.authz.aop;

import org.jsecurity.aop.MethodInvocation;
import org.jsecurity.authz.UnauthenticatedException;
import org.jsecurity.authz.annotation.RequiresAuthentication;

/**
 * Checks to see if a @{@link org.jsecurity.authz.annotation.RequiresAuthentication RequiresAuthenticated} annotation
 * is declared, and if so, ensures the calling
 * <code>Subject</code>.{@link org.jsecurity.subject.Subject#isAuthenticated() isAuthenticated()} before invoking
 * the method.
 *
 * @since 0.9.0 RC3
 * @author Les Hazlewood
 */
public class AuthenticatedAnnotationMethodInterceptor extends AuthorizingAnnotationMethodInterceptor {

    /**
     * Default no-argument constructor that ensures this interceptor looks for
     * @{@link org.jsecurity.authz.annotation.RequiresAuthentication RequiresAuthentication} annotations in a method
     * declaration.
     */
    public AuthenticatedAnnotationMethodInterceptor() {
        super(RequiresAuthentication.class);
    }

    /**
     * Ensures that the calling <code>Subject</code> is authenticated, and if not, throws an
     * {@link UnauthenticatedException UnauthenticatedException} indicating the method is not allowed to be executed.
     *
     * @param mi the method invocation to check for authentication
     * @throws UnauthenticatedException if the calling <code>Subject</code> has not yet
     * authenticated.
     */
    public void assertAuthorized(MethodInvocation mi) throws UnauthenticatedException {
        RequiresAuthentication annotation = (RequiresAuthentication)getAnnotation(mi);
        if ( annotation != null ) {
            if ( !getSubject().isAuthenticated() ) {
                throw new UnauthenticatedException( "The current Subject is not authenticated.  Method invocation denied." );
            }
        }
    }
}