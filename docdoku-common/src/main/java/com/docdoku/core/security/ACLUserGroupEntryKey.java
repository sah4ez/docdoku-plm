/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2017 DocDoku SARL
 *
 * This file is part of DocDokuPLM.
 *
 * DocDokuPLM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DocDokuPLM is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  
 * GNU Affero General Public License for more details.  
 *  
 * You should have received a copy of the GNU Affero General Public License  
 * along with DocDokuPLM.  If not, see <http://www.gnu.org/licenses/>.  
 */
package com.docdoku.core.security;

import com.docdoku.core.common.UserGroupKey;

import java.io.Serializable;

/**
 *
 * @author Florent Garin
 */
public class ACLUserGroupEntryKey implements Serializable {

    private UserGroupKey principal;
    private int acl;

    public ACLUserGroupEntryKey() {
    }

    public ACLUserGroupEntryKey(int acl, UserGroupKey principal) {
        this.acl = acl;
        this.principal = principal;
    }

    public int getAcl() {
        return acl;
    }

    public UserGroupKey getPrincipal() {
        return principal;
    }

    public void setAcl(int aclId) {
        this.acl = aclId;
    }

    public void setPrincipal(UserGroupKey principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return acl + "/" + principal;
    }

    @Override
    public boolean equals(Object pObj) {
        if (this == pObj) {
            return true;
        }
        if (!(pObj instanceof ACLUserGroupEntryKey)) {
            return false;
        }
        ACLUserGroupEntryKey key = (ACLUserGroupEntryKey) pObj;
        return key.acl==acl && key.principal.equals(principal);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + principal.hashCode();
        hash = 31 * hash + acl;
        return hash;
    }
}
