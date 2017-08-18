package com.app.utils.tables.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import com.app.utils.tables.QueryBuilder;
import com.app.utils.tables.annotation.AttributeDescriptor;
import com.app.utils.tables.filter.BaseFilter;
import java.util.logging.Level;

public class GenericDaoImpl<T> implements GenericDao<T> {

    protected static Logger log = Logger.getLogger(GenericDaoImpl.class
            .getName());

    @Inject
    protected EntityManager em;

    @Override
    public List<T> getEntities(List<String> attributes,
            List<List<BaseFilter<?>>> filters, Integer pageSize, Integer offset, Map<String, String> defaultFilters) {
        List<String> columns = getColumns(attributes);
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());
        String query;

        if (defaultFilters != null) {
            //Map<String, String> defaultFiltersColumns = getDefaultFilterColumns(defaultFilters);
            query = getQuery(builder, columns, filters, pageSize, offset, defaultFilters);
        } else {
            query = getQuery(builder, columns, filters, pageSize, offset, null);
        }
        System.out.println(query);

        Class _class;
        List<T> entities = null;
        Session session = null;
        try {
            _class = Class.forName(this.getEntityClass().getCanonicalName());
            System.out.println("+++++++++++++++++++++ _class "
                    + this.getEntityClass().getCanonicalName());
            session = em.unwrap(Session.class);
            QueryExecuter queryExecuter = new QueryExecuter();
            queryExecuter.setQuery(query);
            session = session.getSessionFactory().openSession();
            entities = session.doReturningWork(queryExecuter);
            // entities = em.createNativeQuery(query, _class).getResultList();
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Context = {}", e);
            System.out.println("No se encuentra la clase "
                    + this.getEntityClass().getCanonicalName());
        } catch (HibernateException e) {
            log.log(Level.SEVERE, "Context = {}", e);
        } finally {
            session.close();
        }

        return entities;

    }

    @Override
    public Integer getEntitiesCount(Map<String, String> defaultFilters) {
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());

        if (defaultFilters != null) {
            //Map<String, String> defaultFiltersColumns = getDefaultFilterColumns(defaultFilters);
            builder.addDefaultFilters(defaultFilters);
        }

        String countQuery = builder.buildTotalCount();
        System.out.println(countQuery);
        Object singleResult = em.createNativeQuery(countQuery)
                .getSingleResult();
        int count = singleResult != null
                ? Math.round((Float) singleResult)
                : 0;
        System.out.println("count: " + count);
        return count;

    }

    @Override
    public Integer getFilteredEntitiesCount(List<String> attributes,
            List<List<BaseFilter<?>>> filters, Map<String, String> defaultFilters) {
        List<String> columns = getColumns(attributes);
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());
        builder.addAllFilters(filters);
        builder.addColumns(columns);
        if (defaultFilters != null) {
            //Map<String, String> defaultFiltersColumns = getDefaultFilterColumns(defaultFilters);
            builder.addDefaultFilters(defaultFilters);
        }
        String countQuery = null;

        String sql = builder.build();

        if (sql.indexOf("WHERE") > 0) {
            sql = sql.replace("\'", "\\'");
            System.out.println("sql: " + sql);
            countQuery = builder.buildWordsCount(sql);
            System.out.println(countQuery);

            Integer count = (Integer) em.createNativeQuery(countQuery).getSingleResult();
            System.out.println("filteredCount: " + count);
            return count.intValue();
        } else {
            countQuery = builder.buildTotalCount();
            System.out.println(countQuery);
            int count = Math.round((Float) em.createNativeQuery(countQuery)
                    .getSingleResult());
            System.out.println("filteredCount: " + count);
            return count;
        }
    }

    @Override
    public List<T> getAllFilteredEntities(List<List<BaseFilter<?>>> filters) {
        List<String> columns = new ArrayList<>();
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());
        builder.addAllFilters(filters);
        String query = builder.buildSelectAll();

        Class _class;
        List<T> entities = null;
        try {
            _class = Class.forName(this.getEntityClass().getCanonicalName());
            System.out.println("+++++++++++++++++++++ _class "
                    + this.getEntityClass().getCanonicalName());
            entities = em.createNativeQuery(query, _class).getResultList();
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Context = {}", e);
            System.out.println("No se encuentra la clase "
                    + this.getEntityClass().getCanonicalName());
        } catch (HibernateException e) {
            log.log(Level.SEVERE, "Context = {}", e);
        }

        return entities;

    }

    public List<String> getWords(String column, BaseFilter<?> filter, Integer pageSize, Integer offset) {
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());
        if (pageSize != null) {
            builder.setPageSize(pageSize);
        }
        if (offset != null) {
            builder.setOffset(offset);
        }
        String query = builder.buildWordsQuery(getColumn(column), filter);
        System.out.println("+++++++++++ query " + query);
        List<String> words = null;
        words = em.createNativeQuery(query).getResultList();
        return words;
    }

    public Integer getWordsCount(String column, BaseFilter<?> filter) {
        QueryBuilder<T> builder = new QueryBuilder<>(getEntityClass());
        String countQuery = null;
        if (filter != null) {
            countQuery = builder.buildWordsCount(getColumn(column), filter);
            System.out.println("+++++++++++ countQuery " + countQuery);
            Integer count = (Integer) em.createNativeQuery(countQuery)
                    .getSingleResult();
            System.out.println("+++++++++++ count " + count);
            return count.intValue();
        } else {
            countQuery = builder.buildTotalCount();
            System.out.println("+++++++++++ countQuery " + countQuery);
            int count = Math.round((Float) em.createNativeQuery(countQuery)
                    .getSingleResult());
            System.out.println("+++++++++++ count " + count);
            return count;
        }

    }

    private String getQuery(QueryBuilder<T> builder, List<String> columns,
            List<List<BaseFilter<?>>> filters, Integer pageSize, Integer offset, Map<String, String> defaultFilters) {
        builder.addAllFilters(filters);
        builder.addColumns(columns);
        if (pageSize != null && pageSize > 0) {
            builder.setPageSize(pageSize);
        }
        if (offset != null) {
            builder.setOffset(offset);
        }
        if (defaultFilters != null) {
            builder.addDefaultFilters(defaultFilters);
        }
        return builder.build();
    }

    protected Map<String, String> getDefaultFilterColumns(Map<String, String> defaultFilters) {
        Map<String, String> map = new HashMap<String, String>();
        Set<String> keys = defaultFilters.keySet();
        for (String key : keys) {
            String value = defaultFilters.get(key);
            map.put(getColumn(key), value);
        }
        return map;
    }

    protected List<String> getColumns(List<String> attributes) {
        List<String> result = new ArrayList<>();
        for (String attr : attributes) {
            result.add(getColumn(attr));
        }
        return result;
    }

    private String getColumn(String attrName) {
        try {
            Field f = this.getEntityClass().getDeclaredField(attrName);
            AttributeDescriptor descriptor = f
                    .getAnnotation(AttributeDescriptor.class);
            if (descriptor == null || descriptor.path() == null) {
                /*
                 * Convencion sobre configuracion, se asume que la columna de la
                 * BD se llama como el atributo, pero pasando de camelCase a
                 * camel_case.
                 */
                return attrName.replaceAll("(.)(\\p{Upper})", "$1_$2")
                        .toLowerCase();
            } else {
                return descriptor.path();
            }
        } catch (NoSuchFieldException e) {
            String msg = "No existe el atributo " + attrName + " en la clase "
                    + this.getEntityClass().getCanonicalName();
            // log.error(msg);
            log.info(msg);
            throw new RuntimeException(msg);
        }
    }

    private List<T> getEntitiesFromResultSet(ResultSet resultSet)
            throws SQLException {
        List<Object> entities = new ArrayList<Object>();
        String columnName = "";
        try {
            Class _class = Class.forName(this.getEntityClass()
                    .getCanonicalName());
            // System.out.println(" ++++++++++++++++++++ _class " + _class);
            Map<String, Field> columns = new HashMap<String, Field>();
            for (Field f : _class.getDeclaredFields()) {
                AttributeDescriptor descriptor = f
                        .getAnnotation(AttributeDescriptor.class);
                if (descriptor != null) {
                    columns.put(descriptor.path(), f);
                } else {
                    columns.put(f.getName(), f);
                }
            }

            while (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                Object entity = _class.newInstance();
                // System.out.println(" ++++++++++++++++++++ entity " + entity);
                for (int i = 1; i <= columnCount; ++i) {
                    columnName = metaData.getColumnName(i).toLowerCase();
                    Object object = resultSet.getObject(i);
                    System.out.println("+++++++++++++++++ columnName " + columnName);
                    Field f = columns.get(columnName);
                    f.setAccessible(true);
                    f.set(entity, object);
                }
                entities.add(entity);
            }
        } catch (ClassNotFoundException e) {
            String msg = "No se existe la clase "
                    + this.getEntityClass().getCanonicalName();
            System.out.println(msg);
            throw new RuntimeException(msg);
        } catch (InstantiationException e) {
            String msg = "No se pudo instanciar la clase "
                    + this.getEntityClass().getCanonicalName();
            System.out.println(msg);
            throw new RuntimeException(msg);
        } catch (IllegalAccessException e) {
            String msg = "No se puede acceder al atributo " + columnName
                    + " de la clase "
                    + this.getEntityClass().getCanonicalName();
            System.out.println(msg);
            throw new RuntimeException(msg);
        } catch (SecurityException e) {
            System.out.println(e.toString());
        }

        return (List<T>) entities;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {

        ParameterizedType genericSuperclass = (ParameterizedType) getClass()
                .getGenericSuperclass();
        return (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    private class QueryExecuter implements ReturningWork<List<T>> {

        private List<T> res;
        
        private String query = "";

        public List<T> execute(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet result = ps.executeQuery();
            res = getEntitiesFromResultSet(result);
            return res;
        }

        public void setQuery(String query) {
            this.query = query;
        }

    }

}
