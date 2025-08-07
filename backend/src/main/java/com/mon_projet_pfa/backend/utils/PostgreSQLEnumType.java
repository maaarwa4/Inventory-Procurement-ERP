package com.mon_projet_pfa.backend.utils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Classe utilitaire pour gérer les enums PostgreSQL avec Hibernate
 * Note: Cette classe n'est nécessaire que si vous utilisez PostgreSQL avec des
 * enums personnalisés
 */
public class PostgreSQLEnumType implements UserType<Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    public PostgreSQLEnumType() {
        // Constructeur par défaut requis
    }

    public PostgreSQLEnumType(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Enum<?>> returnedClass() {
        return (Class<Enum<?>>) enumClass;
    }

    @Override
    public boolean equals(Enum<?> x, Enum<?> y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Enum<?> x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enum<?> nullSafeGet(ResultSet rs, int position,
            SharedSessionContractImplementor session,
            Object owner) throws SQLException {
        String columnValue = rs.getString(position);
        if (columnValue == null) {
            return null;
        }

        try {
            return Enum.valueOf((Class<? extends Enum>) enumClass, columnValue);
        } catch (IllegalArgumentException e) {
            throw new SQLException("Cannot convert " + columnValue + " to enum " + enumClass.getSimpleName(), e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Enum<?> value, int index,
            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public Enum<?> deepCopy(Enum<?> value) throws HibernateException {
        return value; // Les enums sont immutables
    }

    @Override
    public boolean isMutable() {
        return false; // Les enums sont immutables
    }

    @Override
    public Serializable disassemble(Enum<?> value) throws HibernateException {
        return value;
    }

    @Override
    public Enum<?> assemble(Serializable cached, Object owner) throws HibernateException {
        return (Enum<?>) cached;
    }
}