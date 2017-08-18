package com.app.utils.tables.filter;


import com.app.utils.tables.annotation.LikeMode;
import com.app.utils.tables.annotation.LikeMode.LikeType;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define un filtro aplicado sobre un atributo del tipo String heredando los
 * atributos base de {@link utils.filter.BaseFilter}
 */
public class StringFilter extends BaseFilter<String> {

    /**
     * Agrega la condicion de like
     */
    private String like;

    /**
     * Agrega condicion de notLike
     */
    private String notLike;

    private String lower;

    private String upper;

    protected static Logger log = Logger.getLogger(StringFilter.class.getName());

    private StringFilter() {
        super();
        this.like = null;
        this.notLike = null;
    }

    public StringFilter(Class<?> clazz, String path) {
        super(clazz, path);
        this.like = null;
        this.notLike = null;
    }

    public StringFilter(Class<?> clazz, String... args) {
        super(clazz, args);
        this.like = null;
        this.notLike = null;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getNotLike() {
        return notLike;
    }

    public void setNotLike(String notLike) {
        this.notLike = notLike;
    }

    public String getLower() {
        return lower;
    }

    public void setLower(String lower) {
        this.lower = lower;
    }

    public String getUpper() {
        return upper;
    }

    public void setUpper(String upper) {
        this.upper = upper;
    }

    @Override
    public void setGeneralCondition(String condition) {
        if (condition != null) {
            this.setLike(condition);
        }

    }

    @Override
    public String parse(String parse) {
        return parse;
    }

    @Override
    public Boolean isEmpty() {
        return (super.isEmpty() && (this.getUpper()== null || this.getUpper().isEmpty())  && (this.getLower()== null || this.getLower().isEmpty()) && (this.getLike() == null || this.getLike().isEmpty()) && (this.getNotLike() == null || this.getNotLike().isEmpty()));
    }

    @Override
    public String getCondition() {
        List<String> conditions = new ArrayList<>();
        Boolean needsCasting = false;
        LikeType likeType = LikeType.EQUAL;
        Field f = null;
        try {
            f = this.getFilteredEntityClass().getDeclaredField(this.getAttributeName());
            System.out.println("field: " + f);
            System.out.println("type: " + f.getType());
            needsCasting = !f.getType().isAssignableFrom(String.class);
            System.out.println("needsCasting: " + needsCasting);
            if (f.isAnnotationPresent(LikeMode.class)) {
                likeType = f.getAnnotation(LikeMode.class).type();
                System.out.println("likeType: " + likeType);
            }
        } catch (NoSuchFieldException e) {
            String msg = "No existe el atributo " + this.getPath() + " en la clase " + this.getFilteredEntityClass().getCanonicalName();
            log.info(msg);
            throw new RuntimeException(msg);
        }

        String castPrefix = (needsCasting) ? " cast( " : "";
        String castExpr = (needsCasting) ? " as text )" : "";
        String prefix;
        String sufix;

        if (LikeType.START.equals(likeType)) {
            prefix = "%";
            sufix = "";
        } else if (LikeType.END.equals(likeType)) {
            prefix = "";
            sufix = "%";
        } else if (LikeType.ANYWHERE.equals(likeType)) {
            prefix = "%";
            sufix = "%";
        } else if (LikeType.EQUAL.equals(likeType)) {
            prefix = "";
            sufix = "";
            this.setEquals(this.getLike());
        } else { // by default
            prefix = "";
            sufix = "%";
        }

        if (needsCasting) {
            if (f.getType().isAssignableFrom(Integer.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = " + this.getLike());
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < " + this.getLower());
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > " + this.getUpper());
                }
            } else if (f.getType().isAssignableFrom(Date.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = '" + this.getLike() + "'");
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < '" + this.getLower() + "'");
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > '" + this.getUpper() + "'");
                }
            } else if (f.getType().isAssignableFrom(BigDecimal.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = '" + this.getLike() + "'");
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < '" + this.getLower() + "'");
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > '" + this.getUpper() + "'");
                }
            }
        } else {
            if (this.getLike() != null) {
                if (LikeType.EQUAL.equals(likeType)) {
                    conditions.add("lower(unaccent(" + castPrefix + this.getPath() + castExpr + ")) = lower(unaccent('" + prefix + this.getLike().toLowerCase() + sufix + "'" + "))");
                }
                else {
                    conditions.add("lower(unaccent(" + castPrefix + this.getPath() + castExpr + ")) LIKE lower(unaccent('" + prefix + this.getLike().toLowerCase() + sufix + "'" + "))");
                }
            }
        }

        String result = "";
        for (int i = 0; i < conditions.size(); i++) {
            if (i == conditions.size() - 1) {
                result += conditions.get(i);
            } else {
                result += conditions.get(i) + " AND ";
            }
        }
        return result;
    }

    @Override
    public String getCondition(Boolean isCount) {
        List<String> conditions = new ArrayList<>();
        Boolean needsCasting = false;
        LikeType likeType = LikeType.EQUAL;
        Field f = null;
        try {
            f = this.getFilteredEntityClass().getDeclaredField(this.getAttributeName());
            System.out.println("field: " + f);
            System.out.println("type: " + f.getType());
            needsCasting = !f.getType().isAssignableFrom(String.class);
            System.out.println("needsCasting: " + needsCasting);
            if (f.isAnnotationPresent(LikeMode.class)) {
                likeType = f.getAnnotation(LikeMode.class).type();
                System.out.println("likeType: " + likeType);
            }
        } catch (NoSuchFieldException e) {
            String msg = "No existe el atributo " + this.getPath() + " en la clase " + this.getFilteredEntityClass().getCanonicalName();
            log.info(msg);
            throw new RuntimeException(msg);
        }

        String castPrefix = (needsCasting) ? " cast( " : "";
        String castExpr = (needsCasting) ? " as text )" : "";
        String prefix;
        String sufix;

        if (LikeType.START.equals(likeType)) {
            prefix = "%";
            sufix = "";
        } else if (LikeType.END.equals(likeType)) {
            prefix = "";
            sufix = "%";
        } else if (LikeType.ANYWHERE.equals(likeType)) {
            prefix = "%";
            sufix = "%";
        } else if (LikeType.EQUAL.equals(likeType)) {
            prefix = "";
            sufix = "";
            this.setEquals(this.getLike());
        } else { // by default
            prefix = "";
            sufix = "%";
        }

        if (needsCasting) {
            if (f.getType().isAssignableFrom(Integer.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = " + this.getLike());
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < " + this.getLower());
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > " + this.getUpper());
                }
            } else if (f.getType().isAssignableFrom(Date.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = '" + this.getLike() + "'");
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < '" + this.getLower() + "'");
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > '" + this.getUpper() + "'");
                }
            } else if (f.getType().isAssignableFrom(BigDecimal.class)) {
                if (this.getLike() != null) {
                    conditions.add(this.getPath() + " = '" + this.getLike() + "'");
                }
                if (this.getLower() != null) {
                    conditions.add(this.getPath() + " < '" + this.getLower() + "'");
                }
                if (this.getUpper() != null) {
                    conditions.add(this.getPath() + " > '" + this.getUpper() + "'");
                }
            }
        } else {
            if (this.getLike() != null) {
                if (LikeType.EQUAL.equals(likeType)) {
                    conditions.add("lower(unaccent(" + castPrefix + this.getPath() + castExpr + ")) = lower(unaccent('" + prefix + this.getLike().toLowerCase() + sufix + "'" + "))");
                }
                else {
                    conditions.add("lower(unaccent(" + castPrefix + this.getPath() + castExpr + ")) LIKE lower(unaccent('" + prefix + this.getLike().toLowerCase() + sufix + "'" + "))");
                }
            }
        }

        String result = "";
        for (int i = 0; i < conditions.size(); i++) {
            if (i == conditions.size() - 1) {
                result += conditions.get(i);
            } else {
                result += conditions.get(i) + " AND ";
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "StringFilter{" + "like=" + like + ", notLike=" + notLike + ", lower=" + lower + ", upper=" + upper + '}';
    }
}
