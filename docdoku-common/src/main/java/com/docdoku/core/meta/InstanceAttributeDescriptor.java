/*
 * DocDoku, Professional Open Source
 * Copyright 2006 - 2015 DocDoku SARL
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

package com.docdoku.core.meta;

import java.io.Serializable;

/**
 * Wraps type and name for attributes facilities
 * 
 * @author Morgan Guimard
 */

public class InstanceAttributeDescriptor implements Serializable {

    private String name;
    private Type type;

    public enum Type {
        TEXT, NUMBER, DATE, BOOLEAN, URL, LOV
    }

    public InstanceAttributeDescriptor() {
    }

    public InstanceAttributeDescriptor(InstanceAttribute o) {

        this.name = o.getName();

        if(o instanceof InstanceDateAttribute){
            type = Type.DATE;
        }
        else if(o instanceof InstanceNumberAttribute){
            type = Type.NUMBER;
        }
        else if(o instanceof InstanceTextAttribute){
            type = Type.TEXT;
        }
        else if(o instanceof InstanceURLAttribute){
            type = Type.URL;
        }
        else if(o instanceof InstanceBooleanAttribute){
            type = Type.BOOLEAN;
        }
        else if(o instanceof InstanceListOfValuesAttribute){
            type = Type.LOV;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getStringType(){
        String type = "";
        switch (this.type){
            case LOV:
                type = "LOV";
                break;
            case DATE:
                type = "DATE";
                break;
            case NUMBER:
                type = "NUMBER";
                break;
            case URL:
                type = "URL";
                break;
            case BOOLEAN:
                type = "BOOLEAN";
                break;
            default :
                type = "TEXT";
                break;
        }
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstanceAttributeDescriptor)){
            return false;
        }
        InstanceAttributeDescriptor other = (InstanceAttributeDescriptor) o;
        return other.name.equals(name) && other.type.equals(type);
    }


    @Override
    public int hashCode() {
        return (name+type).hashCode();
    }
}
