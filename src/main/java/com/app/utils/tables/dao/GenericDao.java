package com.app.utils.tables.dao;

import com.app.utils.tables.filter.BaseFilter;

import java.util.List;
import java.util.Map;


public interface GenericDao<T> {
	List<T> getEntities(List<String> names, List<List<BaseFilter<?>>> filters,
			Integer pageSize, Integer offset, Map<String, String> defaultFilters);

	Integer getEntitiesCount(Map<String, String> defaultFilters);

	Integer getFilteredEntitiesCount(List<String> names,
			List<List<BaseFilter<?>>> filters,
			Map<String, String> defaultFilters);

	List<T> getAllFilteredEntities(List<List<BaseFilter<?>>> filters);

	List<String> getWords(String column, BaseFilter<?> filter,
			Integer pageSize, Integer offset);

	Integer getWordsCount(String column, BaseFilter<?> filter);
}
