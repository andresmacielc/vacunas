package com.app.utils.tables.facade;


/*import com.google.inject.Guice;
 import com.google.inject.Injector;
 import com.google.inject.Module;*/
import com.app.utils.Util;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.app.utils.tables.dao.GenericDao;
import com.app.utils.tables.filter.BaseFilter;
import com.app.utils.tables.filter.StringFilter;


public class DatatablesFacadeImpl<T> implements DatatablesFacade<T> {

    protected GenericDao<T> dao;
    protected static Logger log = Logger.getLogger(DatatablesFacadeImpl.class.getName());

    @Override
    public ObjectNode getResult(Map<String, String[]> queryString, Map<String, String> defaultFilters) {
        //regular expression for /data url
        List<String> attributes = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[data\\]");
        //regular expression for /search url
        List<String> searches = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[search\\]\\[value\\]");
        //regular expression for order
        List<Integer> sortIndexes = toIntList(getParamsByIndex(queryString, "order\\[([0-9]*)\\]\\[column\\]"));
        //regular expression for custom search by 1 or more filters
        List<String> extraData = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[extraData\\]");
        //if word sorted if not define
        if (!isSorted(sortIndexes)) {
            String msg = "DatatablesFacadeImpl<" + this.getEntityClass() + ">: La lista de indices de ordenamiento debe estar ordenada";
            //log.error(msg);
            log.info(msg);
            throw new RuntimeException(msg);
        }
        List<String> sortDirs = getParamsByIndex(queryString, "order\\[([0-9]*)\\]\\[dir\\]");
        System.out.println("sortIndexes : " + sortIndexes.toString());
        System.out.println("orders : " + sortDirs.toString());
        String globalSearch = queryString.get("search[value]")[0];
        System.out.println("globalSearch : " + globalSearch);
        //Integer sortIndex = Integer.parseInt(queryString.get("order[0][column]")[0]);
        //String sortDir = queryString.get("order[0][dir]")[0];
        Integer pageSize = Integer.parseInt(queryString.get("length")[0]);
        Integer offset = Integer.parseInt(queryString.get("start")[0]);
        Integer draw = Integer.parseInt(queryString.get("draw")[0]);
        List<List<BaseFilter<?>>> filters = getFilters(attributes, searches, extraData, sortIndexes, sortDirs);
        filters.add(getGlobalFilters(attributes, globalSearch));
        System.out.println("filters: "+ filters.toString());
        //filters = removeEmptyFilters(filters);
        System.out.println("invoking select entities..");
        List<T> entities = dao.getEntities(attributes, filters, pageSize, offset, defaultFilters);
        System.out.println("invoking total..");
        Integer total = dao.getEntitiesCount(defaultFilters);
        System.out.println("invoking total filtered..");
        Integer totalFiltered = dao.getFilteredEntitiesCount(attributes, filters, defaultFilters);

        return serializeResults(entities, total, draw, totalFiltered);
    }

    @Override
    public ObjectNode getAllFilteredEntities(Map<String, String[]> queryString) {
        List<String> attributes = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[data\\]");
        List<String> searches = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[search\\]\\[value\\]");
        List<Integer> sortIndexes = toIntList(getParamsByIndex(queryString, "order\\[([0-9]*)\\]\\[column\\]"));
        List<String> extraData = getParamsByIndex(queryString, "columns\\[([0-9]*)\\]\\[extraData\\]");
        if (!isSorted(sortIndexes)) {
            String msg = "DatatablesFacadeImpl<" + this.getEntityClass() + ">: La lista de indices de ordenamiento debe estar ordenada";
            //log.error(msg);
            log.info(msg);
            throw new RuntimeException(msg);
        }
        List<String> sortDirs = getParamsByIndex(queryString, "order\\[([0-9]*)\\]\\[dir\\]");

        String globalSearch = queryString.get("search[value]")[0];
        //Integer sortIndex = Integer.parseInt(queryString.get("order[0][column]")[0]);
        //String sortDir = queryString.get("order[0][dir]")[0];
        List<List<BaseFilter<?>>> filters = getFilters(attributes, searches, extraData, sortIndexes, sortDirs);
        filters.add(getGlobalFilters(attributes, globalSearch));
        List<T> entities = dao.getAllFilteredEntities(filters);
        return serializeResultsFiltered(entities);
    }

    public ObjectNode getAllFilteredWithoutRegex(Map<String, String[]> queryString) {
        List<String> attributes = new ArrayList();
        List<String> searches = new ArrayList<>();
        for (Map.Entry<String, String[]> entryMap: queryString.entrySet()) {
            attributes.add(entryMap.getKey());
            searches.add(entryMap.getValue()[0]);
        }
        List<List<BaseFilter<?>>> filters = getFilters(attributes, searches, null, null, null);
        List<T> entities = dao.getAllFilteredEntities(filters);
        return serializeResultsFiltered(entities);
    }

    private List<BaseFilter<?>> getGlobalFilters(List<String> attributes, String globalSearch) {
        List<BaseFilter<?>> result = new ArrayList<>();
        for (String attr : attributes) {
            StringFilter f = new StringFilter(getEntityClass(), attr);
            f.setLike(globalSearch);
            if (!f.isEmpty()) {
                result.add(f);
            }
        }
        return result;
    }

    private ObjectNode serializeResults(List<T> entities, Integer total, Integer draw, Integer totalFiltered) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode result = new ObjectNode(nodeFactory);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        ArrayNode jsonEntities = mapper.valueToTree(entities);
        result.put("data", jsonEntities);
        result.put("draw", draw);
        result.put("recordsTotal", total);
        result.put("recordsFiltered", totalFiltered);

        return result;
    }

    private ObjectNode serializeResultsFiltered(List<T> entities) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode result = new ObjectNode(nodeFactory);
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ArrayNode jsonEntities = mapper.valueToTree(entities);
        //String json = new Gson().toJson(entities);
        //System.out.println("++++++++++++++++++ jsonEntities " + jsonEntities);

        result.put("data", jsonEntities);

        return result;
    }

    @Override
    public ObjectNode getWords(Map<String, String[]> queryString) {
        String column = queryString.get("column")[0];
        String filter = queryString.get("filter")[0];
        Integer page = Integer.parseInt(queryString.get("page")[0]);
        Integer pageSize = Integer.parseInt(queryString.get("pageSize")[0]);
        Long draw = Long.parseLong(queryString.get("_")[0]);

        Integer offset = (page - 1) * pageSize;
        List<String> attributes = new ArrayList<String>();
        attributes.add(column);

        StringFilter f = new StringFilter(getEntityClass(), column);
        f.setLike(filter);

        List<String> words = dao.getWords(column, f, pageSize, offset);
        Integer total = dao.getWordsCount(column, f);

        return serializeWordsResults(words, total, draw, page);
    }

    private ObjectNode serializeWordsResults(List<String> words, Integer total,
            Long draw, Integer page) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
        ObjectNode result = new ObjectNode(nodeFactory);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
                false);
        ArrayNode jsonEntities = mapper.valueToTree(words);

        result.put("items", jsonEntities);
        result.put("_", draw);
        result.put("total_count", total);
        result.put("page", page);

        return result;
    }

    private List<BaseFilter<?>> removeEmptyFilters(List<BaseFilter<?>> filters) {
        List<BaseFilter<?>> result = new ArrayList<>();
        for (int i = 0; i < filters.size(); i++) {
            BaseFilter<?> f = filters.get(i);
            if (!f.isEmpty()) {
                result.add(f);
            }
        }
        return result;
    }

    private List<List<BaseFilter<?>>> getFilters(List<String> attrs,
            List<String> searches, List<String> extraData,
            List<Integer> sortIndexes, List<String> sortDirs) {
        
        Pattern p = Pattern.compile("_[><=]");
        List<List<BaseFilter<?>>> result = new ArrayList<>();
        
        for (int i = 0; i < attrs.size(); i++) {
            StringFilter f = new StringFilter(getEntityClass(), attrs.get(i));
            if (!searches.get(i).isEmpty()) {
                //check is the attribute have the pattern p for perfomance
                Matcher m = p.matcher(searches.get(i));
                if (m.find()) {
                    //separate value and operator
                    String[] parts = searches.get(i).split("_");
                    if (parts.length == 2) {
                        String value = parts[0];
                        String operator = parts[1];
                        if ( operator.equals(">") ) {
                            f.setUpper(value);
                        } else if ( operator.equals("<") ) {
                            f.setLower(value);
                        } else {
                            //like do the = operator for default
                            f.setLike(value);
                        }
                    } 
                    log.info(f.toString());
                } else {
                    f.setLike(searches.get(i));
                }
            }
            
            System.out.println("f = " + f.toString());
            if (sortIndexes != null && sortIndexes.indexOf(i) >= 0) {
                f.setSortAsc(sortDirs.get(sortIndexes.indexOf(i)).equals("asc"));
                f.setSortDesc(sortDirs.get(sortIndexes.indexOf(i)).equals("desc"));
            }
            List<BaseFilter<?>> singleFilter = new ArrayList<>();
            singleFilter.add(f);
            if (extraData != null && !extraData.get(i).isEmpty()) {
                Object value = Util.valueOf(
                        Util.getType(getEntityClass(), extraData.get(i)),
                        f.getLike());
                if (value != null) {
                    StringFilter fExtra
                            = new StringFilter(getEntityClass(), extraData.get(i));
                    fExtra.setLike(String.valueOf(value));
                    singleFilter.add(fExtra);
                }
            }
            System.out.println("singleFilter = " + singleFilter);
            if (!f.isEmpty()) {
                result.add(singleFilter);
            }
        }
        return result;
    }

    private List<String> getColumnPaths(List<BaseFilter<?>> filters) {
        List<String> result = new ArrayList<>();
        for (BaseFilter<?> f : filters) {
            result.add(f.getPath());
        }
        return null;
    }

    private List<String> getParamsByIndex(Map<String, String[]> queryString, String regex) {
        return getParamsByIndex(queryString, regex, false);
    }

    private List<String> getParamsByIndex(Map<String, String[]> queryString, String regex, Boolean ignoreBlanks) {
        Pattern p = Pattern.compile(regex);
        Map<Integer, String> elemsByIndex = new TreeMap<>();

        for (String key : queryString.keySet()) {
            Matcher m = p.matcher(key);
            //System.out.println(key);
            if (m.find()) {
                elemsByIndex.put(Integer.parseInt(m.group(1)), queryString.get(key)[0]);
            }

        }

        List<String> result = new ArrayList<>();
        for (Integer i : elemsByIndex.keySet()) {
            if (!(elemsByIndex.get(i).isEmpty() && ignoreBlanks)) {
                result.add(elemsByIndex.get(i));
            }
        }

        return result;
    }

    private Boolean isSorted(List<? extends Comparable> list) {
        boolean sorted = true;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).compareTo(list.get(i)) > 0) {
                sorted = false;
            }
        }

        return sorted;
    }

    private List<Integer> toIntList(List<String> list) {
        List<Integer> result = new ArrayList<>();
        for (String e : list) {
            result.add(Integer.parseInt(e));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {

        ParameterizedType genericSuperclass = (ParameterizedType) getClass()
                .getGenericSuperclass();
        return (Class<T>) genericSuperclass
                .getActualTypeArguments()[0];
    }
}
