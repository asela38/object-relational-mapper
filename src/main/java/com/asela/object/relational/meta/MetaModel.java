package com.asela.object.relational.meta;

import com.asela.object.relational.anno.Column;
import com.asela.object.relational.anno.PrimaryKey;
import com.asela.object.relational.entity.Person;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MetaModel<T> {

    private Class<T> clss;

    public MetaModel(Class<T> clss) {
        this.clss = clss;
    }

    public static <T> MetaModel of(Class<T> clss) {
        return new MetaModel(clss);
    }

    public PrimaryKeyField getPrimaryKeysField() {

        Field[] fields = clss.getDeclaredFields();
        for (Field field : fields) {
            PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
                return new PrimaryKeyField(field);
            }
        }

        throw new IllegalStateException("Entity has no private keys : " + clss.getSimpleName());
    }

    public List<ColumnField> getColumnFields() {

        List<ColumnField> fields = new ArrayList<>();

        for (Field field : clss.getDeclaredFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                fields.add(new ColumnField(field));
            }
        }

        return fields;
    }
}
