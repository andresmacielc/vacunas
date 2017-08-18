package com.app.utils.tables.filter;



import java.util.logging.Logger;

import com.app.utils.tables.annotation.AttributeDescriptor;

import java.lang.reflect.Field;


/**
 * Clase abstracta que indice un filtro aplicado sobre un atributo. Contiene las
 * condiciones que pueden ser aplicadas a atributos de cualquier tipo de dato.
 *
 * @param <T>
 */
public abstract class BaseFilter<T> {

    /**
     * Indica si el path correspondiente no debe ser null
     */
    private Boolean isNotNull;

    /**
     * Indica si el path correspondiente debe ser igual a null
     */
    private Boolean isNull;

    /**
     * Indica si se ordena ascendentemente sobre este atributo
     */
    private Boolean sortAsc;

    /**
     * Indica si se ordena descendentemente sobre este atributo
     */
    private Boolean sortDesc;

    /**
     * Indica una condicion de igualdad del tipo T sobre el path
     */
    private T equals;

    /**
     * Indica una condicion de no igualdad del tipo T sobre el path
     */
    private T notEquals;

    protected String entityClass;

    /**
     * La clase del modelo base sobre el cual se define el filtro
     */
    private Class<?> filteredEntityClass;

    /**
     * Indica el nombre de la columna sobre el cual se debe realizar la condicion
     */
    private String path;

    /**
     * Indica el nombre del atributo sobre el cual se debe realizar la condicion
     * */
    private String attributeName;

    protected static Logger log = Logger.getLogger(BaseFilter.class.getName());

    protected BaseFilter() {
        this.entityClass = this.getClass().getCanonicalName();
        this.isNotNull = false;
        this.isNull = false;
        this.notEquals = null;
        this.setSortAsc(false);
        this.setSortDesc(false);
    }

    public BaseFilter(Class<?> clazz, String name) {
        this();
        this.filteredEntityClass = clazz;
        this.attributeName = name;
        this.setPath(name);
    }

    public BaseFilter(Class<?> clazz, String... args) {
        this();
        this.filteredEntityClass = clazz;
        this.setAttributeName(args);
        this.setPath(args);
    }

    public Boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(Boolean isNotNull) {
        this.isNotNull = isNotNull;
    }

    public Boolean isNull() {
        return isNull;
    }

    public void setNull(Boolean isNull) {
        this.isNull = isNull;
    }

    public T getEquals() {
        return equals;
    }

    public void setEquals(T equals) {
        this.equals = equals;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setAttributeName(String... args) {
        this.attributeName = "";
        for (String elem : args) {
            this.attributeName = this.attributeName + "." + elem;
        }
        this.attributeName = this.attributeName.substring(1);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String name) {
        this.path = getPathByClazz(this.filteredEntityClass, name);
    }

    public void setPath(String... args) {
        //Probar, solo seria util en caso de querer inferir un join
        Class<?> currentClazz = this.filteredEntityClass;
        path = "";
        for (String elem : args) {
            path = path + "." + getPathByClazz(currentClazz, elem);
            try {
                currentClazz = currentClazz.getDeclaredField(elem).getDeclaringClass();
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("No existe el atributo " + elem + " en la clase " + this.filteredEntityClass.getCanonicalName());
            }
        }
        if(args.length > 0) path = path.substring(1);
    }

    private String getPathByClazz(Class<?> clazz, String attrName){
        try {
            Field f = clazz.getDeclaredField(attrName);
            AttributeDescriptor descriptor = f.getAnnotation(AttributeDescriptor.class);
            if (descriptor == null || descriptor.path() == null) {
                /*Convencion sobre configuracion, se asume que la columna de la BD se llama como el atributo, pero
                pasando de camelCase a camel_case.*/
                return attrName.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
            } else {
                return descriptor.path();
            }
        } catch (NoSuchFieldException e) {
            String msg = "No existe el atributo " + attrName + " en la clase " + this.filteredEntityClass.getCanonicalName();
            //log.error(msg);
            log.info(msg);
            throw new RuntimeException(msg);
        }
    }

    public T getNotEquals() {
        return notEquals;
    }

    public void setNotEquals(T notEquals) {
        this.notEquals = notEquals;
    }

    public Boolean getSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(Boolean sortDesc) {
        if (sortDesc) {
            this.sortAsc = false;
        }
        this.sortDesc = sortDesc;
    }

    public Boolean getSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(Boolean sortAsc) {
        if (sortAsc) {
            this.sortDesc = false;
        }
        this.sortAsc = sortAsc;
    }

    public Class<?> getFilteredEntityClass() {
        return filteredEntityClass;
    }

    public void setFilteredEntityClass(Class<?> filteredEntityClass) {
        this.filteredEntityClass = filteredEntityClass;
    }

    /**
     * Metodo que pasa a todos los atributos booleanos a false, y el resto de
     * los objetos a null. No reinicializa los elementos de ordenamiento
     */
    public void cleanFilters() {
        Field[] campos = this.getClass().getDeclaredFields();

        for (Field field : campos) {
            field.setAccessible(true);
            try {
                if (field.getType().equals(Boolean.class)) {

                    field.set(this, new Boolean(false));

                } else {

                    field.set(this, null);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error al limpiar filtros");
            }
        }
        setEquals(null);
        setNull(false);
        setNotNull(false);
        setNotEquals(null);
    }

    /**
     * Metodo abstracto que define el getter de las condiciones impuestas por
     * cada filtro especifico
     *
     * @return el string correspondiente a la condicion
     */
    public abstract String getCondition();
    public abstract String getCondition(Boolean isCount);

    public String getSorting() {
        String result = "";
        if (sortAsc) {
            result += path + " ASC";
        }
        if (sortDesc) {
            result += path + " DESC";
        }
        return result;
    }

    /**
     * Metodo que sirve para aplicar la condicion pasada como parametro al
     * filtro, decidiendo cada implementacion de filtro que tipo de condicion
     * agregara.
     *
     * @param condition una cadena que representala condicion
     */
    public abstract void setGeneralCondition(String condition);

    /**
     * Metodo que sirve para que cada implementacion de filtro parsee una cadena
     * a su tipo de dato. En caso de no poder parsear, deberian retornar null
     *
     * @param parse el string que se parseara el tipo de dato del filtro
     */
    public abstract T parse(String parse);

    /**
     * Sirve para setear un equals cuando el parametro a agregar es un string. Se
     * intenta parsear al tipo T, y se retorna
     *
     * @param s
     */
    public void setEqualString(String s) {
        this.setEquals(this.parse(s));
    }

    public boolean evaluate(T value) {
        boolean result = true;
        if (this.getEquals() != null && !this.getEquals().equals(value)) {
            result = false;
        }

        if (this.getNotEquals() != null && this.getNotEquals().equals(value)) {
            result = false;
        }

        if (this.isNull() != null && this.isNull() && value != null) {
            result = false;
        }

        if (this.isNotNull() != null && this.isNotNull() && value == null) {
            result = false;
        }
        return result;
    }

    /**
     * Returna true si no existe ninguna condicion de filtrado definida
     */
    public Boolean isEmpty(){
        return (this.getEquals() == null &&  this.getNotEquals() == null && !this.isNull && !this.isNotNull() && !this.getSortAsc() && !this.getSortDesc());
    }

    @Override
    public String toString() {
        return "BaseFilter{" + "isNotNull=" + isNotNull + ", isNull=" + isNull + ", sortAsc=" + sortAsc + ", sortDesc=" + sortDesc + ", equals=" + equals + ", notEquals=" + notEquals + ", entityClass=" + entityClass + ", filteredEntityClass=" + filteredEntityClass + ", path=" + path + ", attributeName=" + attributeName + '}';
    }

}
