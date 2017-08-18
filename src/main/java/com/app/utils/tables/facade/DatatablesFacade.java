package com.app.utils.tables.facade;

import java.util.Map;

import org.codehaus.jackson.node.ObjectNode;


public interface DatatablesFacade<T> {
    ObjectNode getResult(Map<String,String[]> queryString, Map<String,String> defaultFilters);
    ObjectNode getAllFilteredEntities(Map<String,String[]> queryString);
    ObjectNode getWords(Map<String,String[]> queryString);
}
