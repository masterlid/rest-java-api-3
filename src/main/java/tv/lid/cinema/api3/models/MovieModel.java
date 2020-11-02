package tv.lid.cinema.api3.models;

import java.beans.ConstructorProperties;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.jooq.impl.DSL.*;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;

// класс модели кинофильма
public class MovieModel extends CommonModel {
    // имя SQL-таблицы с фильмами
    private static final Table<Record> TABLE_MOVIES = table(name("api3_movies"));

    // название
    @JsonProperty(value = "title", required = true)
    public final String title;

    // длительность в минутах
    @JsonProperty(value = "duration", required = true)
    public final short duration;

    // год выхода
    @JsonProperty(value = "year", required = true)
    public final short year;

    // конструктор #1 -- используется для создания экземпляра из входящего запроса
    @JsonCreator
    @ConstructorProperties({"id", "title", "duration", "year"})
    public MovieModel(
        @JsonProperty("id")       final int    id,
        @JsonProperty("title")    final String title,
        @JsonProperty("duration") final short  duration,
        @JsonProperty("year")     final short  year
    ) {
        super(id);

        this.title    = title;
        this.duration = duration;
        this.year     = year;
    }

    // конструктор #2 -- используется для создания экземпляра с нуля
    public MovieModel(
        final String title,
        final short  duration,
        final short  year
    ) {
        this(0, title, duration, year);
    }

    // создание таблицы в БД
    public static void createTable() throws SQLException {
        CommonModel.dslContext
            .createTableIfNotExists​(MovieModel.TABLE_MOVIES)
            .column("id",       SQLDataType.INTEGER.identity(true).nullable(false))
            .column("title",    SQLDataType.VARCHAR(300).nullable(false))
            .column("duration", SQLDataType.SMALLINT.nullable(false))
            .column("year",     SQLDataType.SMALLINT.nullable(false))
            .constraints(
                primaryKey("id")
            )
            .execute();
    }

    // удаление таблицы из БД
    public static void dropTable() throws SQLException {
        CommonModel.dslContext
            .dropTable(MovieModel.TABLE_MOVIES)
            .execute();
    }

    // имя таблицы в БД
    public static String tableName() {
        return MovieModel.TABLE_MOVIES.getName();
    }

    // подсчет количества записей в БД
    public static int count() throws SQLException {
        int cnt = CommonModel.dslContext
            .selectCount()
            .from(MovieModel.TABLE_MOVIES)
            .fetchOne(0, int.class);
        return cnt;
    }

    // проверка существования в БД записи с заданным идентификатором
    public static boolean exists(final int id) throws SQLException {
        int cnt = CommonModel.dslContext
            .selectCount()
            .from(MovieModel.TABLE_MOVIES)
            .where(
                field(name("id"), int.class).equal(id)
            )
            .fetchOne(0, int.class);
        return cnt != 0;
    }

    // чтение записи из БД по заданному идентификатору
    public static MovieModel find(final int id) throws SQLException {
        MovieModel result = CommonModel.dslContext
            .select(
                field(name("id"),       int.class),
                field(name("title"),    String.class),
                field(name("duration"), short.class),
                field(name("year"),     short.class)
            )
            .from(MovieModel.TABLE_MOVIES)
            .where(
                field(name("id"), int.class).equal(id)
            )
            .fetchOne()
            .into(MovieModel.class);
        return result;
    }

    // получить список записей из БД с постраничным выводом
    public static List<MovieModel> list(final int page, final int numb) throws SQLException {
        List<MovieModel> result = CommonModel.dslContext
            .select(
                field(name("id"),       int.class),
                field(name("title"),    String.class),
                field(name("duration"), short.class),
                field(name("year"),     short.class)
            )
            .from(MovieModel.TABLE_MOVIES)
            .orderBy(
                field(name("year"), short.class).desc()
            )
            .limit(numb)
            .offset((page - 1) * numb)
            .fetchInto(MovieModel.class);
        return result;
    }

    // удаление записи из БД по заданному идентификатору
    public static void kill(final int id) throws SQLException {
        CommonModel.dslContext
            .deleteFrom(MovieModel.TABLE_MOVIES)
            .where(
                field(name("id"), int.class).equal(id)
            )
            .execute();
    }

    // сохранение данной записи в БД
    public void save() throws SQLException {
        if (this.id == 0) { // создание новой
            CommonModel.dslContext
                .insertInto(
                    MovieModel.TABLE_MOVIES,
                    field(name("title"),    String.class),
                    field(name("duration"), short.class),
                    field(name("year"),     short.class)
                )
                .values(
                    this.title,
                    this.duration,
                    this.year
                )
                .execute();
        } else { // изменение ранее созданной
            CommonModel.dslContext
                .update(MovieModel.TABLE_MOVIES)
                .set(field(name("title"),    String.class), this.title)
                .set(field(name("duration"), short.class),  this.duration)
                .set(field(name("year"),     short.class),  this.year)
                .where(
                    field(name("id"), int.class).equal(this.id)
                )
                .execute();
        };
    }
}
