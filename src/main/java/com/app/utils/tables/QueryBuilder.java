package com.app.utils.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.app.utils.tables.annotation.TableDescriptor;
import com.app.utils.tables.filter.BaseFilter;


public class QueryBuilder<T> {

    private List<List<BaseFilter<?>>> filters = new ArrayList<>();
    private Map<String, String> defaultFilters;
    private List<String> columns = new ArrayList<>();
    private Integer size;
    private Integer offset;

    final Class<T> typeParameterClass;
    protected static Logger log = Logger.getLogger(QueryBuilder.class.getName());

    public QueryBuilder addFilters(List<BaseFilter<?>> filters) {
        this.filters.add(filters);
        return this;
    }

    public QueryBuilder addAllFilters(List<List<BaseFilter<?>>> filters) {
        this.filters.addAll(filters);
        return this;
    }

    public QueryBuilder setPageSize(Integer size) {
        this.size = size;
        return this;
    }

    public QueryBuilder setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public QueryBuilder addColumn(String column) {
        this.columns.add(column);
        return this;
    }

    public QueryBuilder addColumns(List<String> columns) {
        this.columns.addAll(columns);
        return this;
    }

    public QueryBuilder addDefaultFilters(Map<String, String> defaultFilters) {
        this.defaultFilters = defaultFilters;
        return this;
    }

    public QueryBuilder(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public String build() {

        String columnList = getColumnList();

        String query = buildBaseQuery(columnList, false);

        /* Agregar limit y offset si tiene */
        if (size != null) {
            query += " LIMIT " + size;
        }
        if (offset != null) {
            query += " OFFSET " + offset;
        }

        return query;
    }

    public String buildSelectAll() {
        String columnList = "*";
        return buildBaseQuery(columnList, false);
    }

    public String buildTotalCount() {
        String table = getTable();
        String[] tableDotSplit = table.split("\\.");
        StringBuilder sql = new StringBuilder("SELECT sum(c.reltuples) as cant FROM pg_class c");
        sql.append(" JOIN pg_namespace n ON n.oid = c.relnamespace");
        if (tableDotSplit.length > 1) {
            sql.append(" WHERE c.relname like '")
                .append(tableDotSplit[1])
                .append("%'");
            sql.append(" AND n.nspname = '")
                .append(tableDotSplit[0])
                .append("'");
        } else {
            sql.append(" WHERE c.relname like '")
                .append(tableDotSplit[0])
                .append("%'");
        }
        return sql.toString();
    }

    public String buildCount() {
        String columnList = "count(*)";
        return buildBaseQuery(columnList, true);
    }

    public String buildWordsQuery(String column, BaseFilter<?> filter) {
        String query = buildWordsBaseQuery(column, filter, false);

        /* Agregar limit y offset si tiene */
        if (size != null) {
            query += " LIMIT " + size;
        }
        if (offset != null) {
            query += " OFFSET " + offset;
        }

        return query;
    }

    public String buildWordsCount(String column, BaseFilter<?> filter) {
        return "select count_estimate(E'" + buildWordsBaseQuery(column, filter, true) + "');";
    }

    public String buildWordsCount(String sql) {
        return "select count_estimate(E'" + sql + "');";
    }

    private String buildBaseQuery(String columnList, Boolean isCount) {
        String table = getTable();
        String query = "SELECT " + columnList + " FROM " + table;
        String wheres = "";
        String orders = "";
        String tail = ") AND ";

        /* Obtener y agregar los filtros y ordenes al query */
        if (!filters.isEmpty()) {

            for (List<BaseFilter<?>> filtersOr : filters) {
                int count = 0;
                int countFilters = filtersOr.size();
                String subWhere = "";
                wheres += "";
                for (BaseFilter filter : filtersOr) {
                    /* Carga de condiciones where */
                    count++;
                    if (!filter.getCondition().isEmpty()) {
                        if (count < countFilters) {
                            subWhere += filter.getCondition() + " OR ";
                        } else {
                            subWhere += filter.getCondition();
                        }
                    }

                    /* Carga de parametro de ordenamiento */
                    if (filter.getSorting().compareTo("") != 0) {
                        if (orders.compareTo("") == 0) {
                            orders += filter.getSorting();
                        } else {
                            //have multiple sorting
                            orders += ", " + filter.getSorting();
                        }
                    }
                }
                if (!subWhere.isEmpty()) {
                    wheres += "(" + subWhere + tail;
                }
            }

        }

        if (defaultFilters != null) {
            int count = 0;
            Set<String> keys = defaultFilters.keySet();
            for (String key : keys) {
                String value = defaultFilters.get(key);
                wheres += "(lower(unaccent( cast(" + key + " as text) ))) LIKE (lower(unaccent('" + value + "')) " + tail;
            }
        }

        if (!wheres.isEmpty()) {
            wheres = wheres.substring(0, wheres.length() - (tail.length() - 1));
            query += " WHERE ";
            query += wheres;
        }
        
        if (!(orders.isEmpty() || isCount)) {
            query += " ORDER BY ";
            query += orders;
        }
        
        System.out.println("query : " + query);
        
        return query;
    }

    /* Si hay muchas condiciones para el where pone todas entre OR */
    private String buildWordsBaseQuery(String column, BaseFilter<?> filter, Boolean isCount) {
        String table = getTable();
        String query = "SELECT " + column;

        /*if (isCount){
         query += "count( " + column + " )";
         } else {
         query += " " + column;
         }*/
        query += " FROM " + table;

        if (!filter.isEmpty()) {
            query += " WHERE ";
            if (isCount) {
                query += filter.getCondition(isCount);
            } else {
                query += filter.getCondition();
            }
        }

        return query;
    }

    private String getColumnList() {
        String columnList = "";
        for (String column : columns) {
            columnList += column + ", ";
        }
        if (!columnList.isEmpty()) {
            columnList = columnList.substring(0, columnList.length() - 2);
        }
        return columnList;
    }

    private String getTable() {
        /* Obtener el nombre de la clase, define el nombre de la tabla, se pasa de CamelCase a camel_case */
        Class<?> clazz = this.typeParameterClass;
        String table = clazz.getSimpleName().replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();

        /* Si esta anotado, se considera la anotacion (convencion sobre configuracion) */
        if (clazz.isAnnotationPresent(TableDescriptor.class)) {
            TableDescriptor descriptor = clazz.getAnnotation(TableDescriptor.class);
            if (descriptor.name() != null) {
                table = descriptor.name();
            }
            if (descriptor.schema() != null) {
                table = descriptor.schema() + "." + table;
            }
        }
        return table;
    }
}
