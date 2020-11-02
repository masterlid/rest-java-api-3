package tv.lid.cinema.api3.models;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jooq.DSLContext;

// базовый абстрактный класс модели
public abstract class CommonModel {
    // DSL context
    protected static DSLContext dslContext = null;

    // идентификатор записи
    @JsonProperty(value = "id", required = false, defaultValue = "0")
    public final int id;

    // конструктор #1
    @JsonCreator
    protected CommonModel(@JsonProperty("id") int id) {
        this.id = id;
    }

    // конструктор #2
    protected CommonModel() {
        this(0);
    }

    // инициализация
    public static final void initialize(final DSLContext dslContext) {
        CommonModel.dslContext = dslContext;
    }

    // создание таблицы в БД
    public static void createTable() throws SQLException {
        throw new SQLException();
    }

    // удаление таблицы из БД
    public static void dropTable() throws SQLException {
        throw new SQLException();
    }

    // имя таблицы в БД
    public static String tableName() throws SQLException {
        throw new SQLException();
    }

    // подсчет количества записей в БД
    public static int count() throws SQLException {
        throw new SQLException();
    }

    // проверка существования в БД записи с заданным идентификатором
    public static boolean exists(final int id) throws SQLException {
        throw new SQLException();
    }

    // чтение записи из БД по заданному идентификатору
    public static CommonModel find(final int id) throws SQLException {
        throw new SQLException();
    }

    // удаление записи из БД по заданному идентификатору
    public static void kill(final int id) throws SQLException {
        throw new SQLException();
    }

    // сохранение данной записи в БД
    public void save() throws SQLException {
        throw new SQLException();
    }
}
