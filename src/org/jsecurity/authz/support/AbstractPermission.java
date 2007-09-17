/*
 * Copyright (C) 2005-2007 Les Hazlewood
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 *
 * Free Software Foundation, Inc.
 * 59 Temple Place, Suite 330
 * Boston, MA 02111-1307
 * USA
 *
 * Or, you may view it online at
 * http://www.opensource.org/licenses/lgpl-license.php
 */
package org.jsecurity.authz.support;

import org.jsecurity.authz.NamedPermission;
import org.jsecurity.authz.Permission;

import java.io.Serializable;

/**
 * Abstract implementation of the JSecurity's core Permission interface.
 *
 * @deprecated Subclass {@link SimplePermission} or {@link SimpleNamedPermission} as a base for implementation instead -
 * this implementation has itself been updated to use an internally wrapped SimpleNamedPermission only to maintain
 * this class's API.  This class will be removed before 1.0 final.
 *
 * @since 0.1
 * @author Les Hazlewood
 */
public abstract class AbstractPermission implements NamedPermission, Serializable {

    private SimpleNamedPermission wrapped = null;

    protected AbstractPermission() {
        wrapped = new SimpleNamedPermission();
    }

    public AbstractPermission( String name ) {
        wrapped = new SimpleNamedPermission( name );
    }

    public String getName() {
        return wrapped.getName();
    }

    protected void setName( String name ) {
        wrapped.setName( name );
    }

    public boolean implies( Permission p ) {
        return wrapped.implies( p );
    }

    public String toString() {
        return wrapped.toString();
    }

    protected StringBuffer toStringBuffer() {
        return wrapped.toStringBuffer();
    }

    @SuppressWarnings( { "EqualsWhichDoesntCheckParameterClass" } )
    public boolean equals( Object o ) {
        return wrapped.equals( o );
    }

    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    @SuppressWarnings( { "CloneDoesntDeclareCloneNotSupportedException" } )
    public Object clone() {
        AbstractPermission ap;
        try {
            ap = (AbstractPermission)super.clone();
        } catch ( CloneNotSupportedException e ) {
            String msg = "Unable to clone AbstractTargetedPermission of type [" +
                    getClass().getName() + "].  Check implementation (this should never " +
                    "happen).";
            throw new InternalError( msg );
        }
        ap.wrapped = (SimpleNamedPermission)this.wrapped.clone();
        return ap;
    }
}