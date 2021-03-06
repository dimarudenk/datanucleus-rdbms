/**********************************************************************
Copyright (c) 2012 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
   ...
**********************************************************************/
package org.datanucleus.store.rdbms.mapping.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.types.converters.ColumnLengthDefiningTypeConverter;
import org.datanucleus.store.types.converters.TypeConverter;
import org.datanucleus.store.types.converters.TypeConverterHelper;

/**
 * Mapping where the member has its value converted to/from some storable datastore type using a TypeConverter.
 */
public class TypeConverterMapping extends SingleFieldMapping
{
    TypeConverter converter;

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping#initialize(org.datanucleus.store.rdbms.RDBMSStoreManager, java.lang.String)
     */
    @Override
    public void initialize(RDBMSStoreManager storeMgr, String type)
    {
        ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        Class fieldType = clr.classForName(type);
        converter = storeMgr.getNucleusContext().getTypeManager().getDefaultTypeConverterForType(fieldType);
        if (converter == null)
        {
            throw new NucleusUserException("Unable to find TypeConverter for converting " + fieldType + " to String");
        }

        super.initialize(storeMgr, type);
    }

    public void initialize(AbstractMemberMetaData mmd, Table table, ClassLoaderResolver clr)
    {
        this.initialize(mmd, table, clr, null);
    }

    public void initialize(AbstractMemberMetaData mmd, Table table, ClassLoaderResolver clr, TypeConverter conv)
    {
        if (mmd.getTypeConverterName() != null)
        {
            // Use specified converter (if found)
            converter = table.getStoreManager().getNucleusContext().getTypeManager().getTypeConverterForName(mmd.getTypeConverterName());
        }
        else if (conv != null)
        {
            converter = conv;
        }
        else
        {
            throw new NucleusUserException("Unable to initialise mapping of type " + getClass().getName() + " for field " + mmd.getFullFieldName() + " since no TypeConverter was provided");
        }

        super.initialize(mmd, table, clr);
    }

    public TypeConverter getTypeConverter()
    {
        return converter;
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping#getDefaultLength(int)
     */
    @Override
    public int getDefaultLength(int index)
    {
        if (converter instanceof ColumnLengthDefiningTypeConverter)
        {
            return ((ColumnLengthDefiningTypeConverter) converter).getDefaultColumnLength(index);
        }
        return super.getDefaultLength(index);
    }

    /**
     * Accessor for the name of the java-type actually used when mapping the particular datastore
     * field. This java-type must have an entry in the datastore mappings.
     * @param index requested datastore field index.
     * @return the name of java-type for the requested datastore field.
     */
    public String getJavaTypeForDatastoreMapping(int index)
    {
        return TypeConverterHelper.getDatastoreTypeForTypeConverter(converter, getJavaType()).getName();
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.JavaTypeMapping#getJavaType()
     */
    @Override
    public Class getJavaType()
    {
        return mmd != null ? mmd.getType() : storeMgr.getNucleusContext().getClassLoaderResolver(null).classForName(type);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setBoolean(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], boolean)
     */
    @Override
    public void setBoolean(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, boolean value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setBoolean(ps, exprIndex[0], (Boolean)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getBoolean(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public boolean getBoolean(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return false;
        }

        Boolean datastoreValue = getDatastoreMapping(0).getBoolean(resultSet, exprIndex[0]);
        return (Boolean)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setByte(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], byte)
     */
    @Override
    public void setByte(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, byte value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setByte(ps, exprIndex[0], (Byte)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getByte(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public byte getByte(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Byte datastoreValue = getDatastoreMapping(0).getByte(resultSet, exprIndex[0]);
        return (Byte)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setChar(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], char)
     */
    @Override
    public void setChar(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, char value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setChar(ps, exprIndex[0], (Character)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getChar(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public char getChar(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Character datastoreValue = getDatastoreMapping(0).getChar(resultSet, exprIndex[0]);
        return (Character)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setDouble(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], double)
     */
    @Override
    public void setDouble(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, double value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setDouble(ps, exprIndex[0], (Double)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getDouble(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public double getDouble(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Double datastoreValue = getDatastoreMapping(0).getDouble(resultSet, exprIndex[0]);
        return (Double)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setFloat(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], float)
     */
    @Override
    public void setFloat(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, float value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setFloat(ps, exprIndex[0], (Float)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getFloat(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public float getFloat(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Float datastoreValue = getDatastoreMapping(0).getFloat(resultSet, exprIndex[0]);
        return (Float)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setInt(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], int)
     */
    @Override
    public void setInt(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, int value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setInt(ps, exprIndex[0], (Integer)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getInt(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public int getInt(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Integer datastoreValue = getDatastoreMapping(0).getInt(resultSet, exprIndex[0]);
        return (Integer)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setLong(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], long)
     */
    @Override
    public void setLong(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, long value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setLong(ps, exprIndex[0], (Long)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getLong(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public long getLong(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Long datastoreValue = getDatastoreMapping(0).getLong(resultSet, exprIndex[0]);
        return (Long)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setShort(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], short)
     */
    @Override
    public void setShort(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, short value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setShort(ps, exprIndex[0], (Short)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getShort(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public short getShort(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return 0;
        }

        Short datastoreValue = getDatastoreMapping(0).getShort(resultSet, exprIndex[0]);
        return (Short)converter.toMemberType(datastoreValue);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setString(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], java.lang.String)
     */
    @Override
    public void setString(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, String value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setString(ps, exprIndex[0], (String)converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getString(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public String getString(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return null;
        }

        String datastoreValue = getDatastoreMapping(0).getString(resultSet, exprIndex[0]);
        return (datastoreValue != null ? (String)converter.toMemberType(datastoreValue) : null);
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#setObject(org.datanucleus.store.ExecutionContext, java.lang.Object, int[], java.lang.Object)
     */
    @Override
    public void setObject(ExecutionContext ec, PreparedStatement ps, int[] exprIndex, Object value)
    {
        if (exprIndex == null)
        {
            return;
        }

        getDatastoreMapping(0).setObject(ps, exprIndex[0], converter.toDatastoreType(value));
    }

    /* (non-Javadoc)
     * @see org.datanucleus.store.rdbms.mapping.SingleFieldMapping#getObject(org.datanucleus.store.ExecutionContext, java.lang.Object, int[])
     */
    @Override
    public Object getObject(ExecutionContext ec, ResultSet resultSet, int[] exprIndex)
    {
        if (exprIndex == null)
        {
            return null;
        }

        Object datastoreValue = getDatastoreMapping(0).getObject(resultSet, exprIndex[0]);
        return (datastoreValue != null ? converter.toMemberType(datastoreValue) : null);
    }
}