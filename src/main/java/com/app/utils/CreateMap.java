package com.app.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMap {

	public List<Map<String, Object>> createMapFromList(List<Object> list) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		for (Object object : list) {
			Object[] fila = (Object[]) object;
			Map<String, Object> filaMap = new HashMap<String, Object>();
			for (int i = 0; i < fila.length; i++) {
				filaMap.put("col" + i, fila[i]);
			}
			result.add(filaMap);
		}

		return result;
	}

	public List<Map<String, Object>> createMapFromResulSet(ResultSet result)
			throws SQLException {

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		// Obtener el nombre de las columnas
		ResultSetMetaData meta = result.getMetaData();

		String[] columnsName = new String[meta.getColumnCount()];
		for (int i = 0; i < columnsName.length; i++) {
			columnsName[i] = meta.getColumnName(i + 1);
		}

		while (result.next()) {
			int i = 1;
			Map<String, Object> filaMap = new HashMap<String, Object>();
			while (i <= columnsName.length) {
				Object o;
				o = result.getObject(i);
				filaMap.put(columnsName[i - 1], o);
				i++;
			}
			listMap.add(filaMap);

		}

		return listMap;
	}
}
