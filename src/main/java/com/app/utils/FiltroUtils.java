package com.app.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FiltroUtils {
    
    private FiltroUtils() {}
    
    public static String getWhereWithIgnoreNames(List<Filtro> filtros,
            String... ignoreNames) {
        StringBuilder where = new StringBuilder("WHERE");
        String separator = "AND";
        String prefix, posfix;
        Set<String> ignoreNamesSet = 
                ignoreNames != null? new HashSet<>(Arrays.asList(ignoreNames))
                : new HashSet<String>();
        List<Filtro> filtrosWithouIgnoreNames = new ArrayList<>();
        for (Filtro filtro : filtros) {
            if (!ignoreNamesSet.contains(filtro.getName())) {
                filtrosWithouIgnoreNames.add(filtro);
            }
        }
        for (int i = 0; i < filtrosWithouIgnoreNames.size(); i++) {
            Filtro filtro = filtrosWithouIgnoreNames.get(i);
            if (ignoreNamesSet.contains(filtro.getName())) {
                continue;
            }
            if ("categoria".equals(filtro.getName())) {
                filtro.setName("upper(substring(categoria,1,1))");
            }
            where.append(" ");
            switch (filtro.getType()) {
            case "string":
                where.append("upper");
                prefix = "'";
                posfix = "'";
                break;
            default:
                prefix = "";
                posfix = "";
                break;
            }
            where.append(" (");
            if (!filtro.getType().startsWith("list")) {
                where.append("edad".equals(filtro.getName())
                    ? "cast(substring(text(age(now(),fecha_nacimiento)) from 1 for 2) as int)"
                    : filtro.getName());
            } else {
                prefix = "'";
                posfix = "'";
            }
            switch (filtro.getOperator().toUpperCase()) {
                case "LIKE":
                    where.append(") LIKE upper(");
                    where.append(prefix).append("%");
                    where.append(filtro.getValues().get(0));
                    where.append("%").append(posfix);
                    where.append(") ");
                    break;
                case "BETWEEN":
                    where.append(") BETWEEN ");
                    where.append(prefix);
                    where.append(filtro.getValues().get(0));
                    where.append(posfix);
                    where.append(" AND ");
                    where.append(prefix);
                    where.append(filtro.getValues().get(1));
                    where.append(posfix);
                    where.append(" ");
                    break;
                case "IN":
                    where.append(") IN (");
                    for (int j = 0; j < filtro.getValues().size(); j++) {
                        where.append(prefix);
                        where.append(filtro.getValues().get(j));
                        where.append(posfix);
                        if ((j+1) < filtro.getValues().size()) {
                            where.append(",");
                        }
                    }
                    where.append(") ");
                    break;
                case "INOR": // IN like an OR
                    Object value;
                    String operator = "list_string".equals(filtro.getType())
                            ? " LIKE "
                            : " = ";
                    for (int j = 0; j < filtro.getValues().size(); j++) {
                        value = filtro.getValues().get(j);
                        where.append("(");
                        where.append(filtro.getName());
                        if ("OTROS".equals(value)) {
                            where.append(" NOT IN ('A','B','C','D','E','F','G')");
                        } else {
                            where.append(operator);
                            where.append(prefix);
                            where.append(value);
                            where.append(posfix);
                        }
                        where.append(")");
                        if ((j+1) < filtro.getValues().size()) {
                            where.append(" OR ");
                        }
                    }
                    where.append(") ");
                    break;
                case "NULL":
                    where.append(") IS NOT NULL ");
                    break;
                case "GE": // <-- GREATER or EQUAL
                    where.append(") >= (");
                    where.append(prefix);
                    where.append(filtro.getValues().get(0));
                    where.append(posfix);
                    where.append(") ");
                    break;
                case "LE": // <-- LOWER or EQUAL
                    where.append(") <= (");
                    where.append(prefix);
                    where.append(filtro.getValues().get(0));
                    where.append(posfix);
                    where.append(") ");
                    break;
                case "EQUAL":
                default:
                    where.append(") = (");
                    where.append(prefix);
                    where.append(filtro.getValues().get(0));
                    where.append(posfix);
                    where.append(") ");
                    break;
            }
            if ((i+1) < filtrosWithouIgnoreNames.size()) {
                where.append(separator);
            }
        }
        return where.toString();
    }
    
    public static String getWhere(List<Filtro> filtros) {
        return getWhereWithIgnoreNames(filtros);
    }
}
