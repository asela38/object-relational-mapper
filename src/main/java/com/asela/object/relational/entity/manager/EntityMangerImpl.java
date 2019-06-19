package com.asela.object.relational.entity.manager;

import com.asela.object.relational.entity.Person;
import com.asela.object.relational.meta.ColumnField;
import com.asela.object.relational.meta.MetaModel;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityMangerImpl<T> implements EntityManger<T> {


    private AtomicInteger idGen = new AtomicInteger(10);

    private Class<T> clss;

    public EntityMangerImpl(Class<T> clss) {
        this.clss = clss;
    }

    @Override
    public void persist(T entity) throws SQLException, IllegalAccessException {

        MetaModel model = MetaModel.of(entity.getClass());
        String sql = model.buildInsertRequest();

        System.out.println("sql = " + sql);
        PreparedStatement statement = prepareStatement(sql).addParameters(entity);

        statement.executeUpdate();

        statement.getConnection().close();


    }

    @Override
    public T find(Object i) {

        MetaModel model = MetaModel.of(clss);

        return  null;
    }

    private PreparedStatementWrapper prepareStatement(String sql) throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:h2:/Users/asela/Workspace/github/object-relational-mapper/db-files/db-persons",
                "sa", "");
        PreparedStatement statement = connection.prepareStatement(sql);
        return  new PreparedStatementWrapper(statement);
    }

    private class PreparedStatementWrapper {
        PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }


        public PreparedStatement addParameters(T entity) throws SQLException, IllegalAccessException {
            MetaModel model = MetaModel.of(entity.getClass());
            Class<?> type = model.getPrimaryKeysField().getType();

            if(type == int.class) {
                int  id= idGen.getAndDecrement();
                statement.setInt(1, id);

                Field field = model.getPrimaryKeysField().getField();
                field.setAccessible(true);
                field.set(entity, id);
            }

            List<ColumnField> columnFields = model.getColumnFields();
            for (int i = 0; i < columnFields.size(); i++) {

                ColumnField columnField = columnFields.get(i);
                Class<?> fieldType = columnField.getType();
                Field field = columnField.getField();
                field.setAccessible(true);
                Object object = field.get(entity);

                if(fieldType == int.class) {
                    statement.setInt(i + 2, (int)object);
                }
                if(fieldType == String.class) {
                    statement.setString(i + 2, (String) object);
                }
            }
            return statement;
        }
    }
}
