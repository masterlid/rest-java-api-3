package tv.lid.cinema.api3.models;

import java.beans.ConstructorProperties;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static org.jooq.impl.DSL.*;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.SQLDataType;

// класс модели киносеанса
@JsonIgnoreProperties(value = "movieId", allowSetters = true)
public class ScheduleModel extends CommonModel {
    // имя SQL-таблицы с сеансами
    private static final Table<Record> TABLE_SCHEDULES = table(name("api3_schedules"));

    // идентификатор фильма
    @JsonProperty(value = "movieId", required = true)
    public final int movieId;

    // дата и время начала
    @JsonProperty(value = "dateAndTime", required = true)
    public final String dateAndTime;

    // номер зала
    @JsonProperty(value = "auditorium", required = false, defaultValue = "1")
    public final byte auditorium;

    // конструктор #1 -- используется для создания экземпляра из входящего запроса
    @JsonCreator
    @ConstructorProperties({"id", "movie_id", "date_time", "auditorium"})
    public ScheduleModel(
        @JsonProperty("id")          final int    id,
        @JsonProperty("movieId")     final int    movieId,
        @JsonProperty("dateAndTime") final String dateAndTime,
        @JsonProperty("auditorium")  final byte   auditorium
    ) {
        super(id);

        this.movieId     = movieId;
        this.dateAndTime = dateAndTime;
        this.auditorium  = auditorium;
    }

    // конструктор #2 -- используется для создания экземпляра с нуля
    public ScheduleModel(
        final int    movieId,
        final String dateAndTime,
        final byte   auditorium
    ) {
        this(0, movieId, dateAndTime, auditorium);
    }

    // создание таблицы в БД
    public static void createTable() throws SQLException {
        CommonModel.dslContext
            .createTableIfNotExists​(ScheduleModel.TABLE_SCHEDULES)
            .column("id",         SQLDataType.INTEGER.identity(true).nullable(false))
            .column("movie_id",   SQLDataType.INTEGER.nullable(false))
            .column("date_time",  SQLDataType.VARCHAR(50).nullable(false))
            .column("auditorium", SQLDataType.TINYINT.nullable(false))
            .constraints(
                primaryKey("id"),
                foreignKey("movie_id").references(MovieModel.tableName(), "id").onDeleteCascade()
            )
            .execute();
    }

    // удаление таблицы из БД
    public static void dropTable() throws SQLException {
        CommonModel.dslContext
            .dropTable(ScheduleModel.TABLE_SCHEDULES)
            .execute();
    }

    // имя таблицы в БД
    public static String tableName() {
        return ScheduleModel.TABLE_SCHEDULES.getName();
    }

    // подсчет количества записей в БД по заданному идентификатору фильма
    public static int count(final int movieId) throws SQLException {
        int cnt = CommonModel.dslContext
            .selectCount()
            .from(ScheduleModel.TABLE_SCHEDULES)
            .where(
                field(name("movie_id"), int.class).equal(movieId)
            )
            .fetchOne(0, int.class);
        return cnt;
    }

    // проверка существования в БД записи с заданным идентификатором
    public static boolean exists(final int id) throws SQLException {
        int cnt = CommonModel.dslContext
            .selectCount()
            .from(ScheduleModel.TABLE_SCHEDULES)
            .where(
                field(name("id"), int.class).equal(id)
            )
            .fetchOne(0, int.class);
        return cnt != 0;
    }

    // чтение записи из БД по заданному идентификатору
    public static ScheduleModel find(final int id) throws SQLException {
        ScheduleModel result = CommonModel.dslContext
            .select(
                field(name("id"),         int.class),
                field(name("movie_id"),   int.class),
                field(name("date_time"),  String.class),
                field(name("auditorium"), byte.class)
            )
            .from(ScheduleModel.TABLE_SCHEDULES)
            .where(
                field(name("id"), int.class).equal(id)
            )
            .fetchOne()
            .into(ScheduleModel.class);
        return result;

    }

    // получить список записей из БД в соответствии с заданными параметрами
    public static List<ScheduleModel> list(
        final int movieId,
        final int page,
        final int numb
    ) throws SQLException {
        List<ScheduleModel> result = CommonModel.dslContext
            .select(
                field(name("id"),         int.class),
                field(name("movie_id"),   int.class),
                field(name("date_time"),  String.class),
                field(name("auditorium"), byte.class)
            )
            .from(ScheduleModel.TABLE_SCHEDULES)
            .where(
                field(name("movie_id"), int.class).equal(movieId)
            )
            .orderBy(
                field(name("date_time"), String.class).desc()
            )
            .limit(numb)
            .offset((page - 1) * numb)
            .fetchInto(ScheduleModel.class);
        return result;
    }

    // удаление записи из БД по заданному идентификатору
    public static void kill(final int id) throws SQLException {
        CommonModel.dslContext
            .deleteFrom(ScheduleModel.TABLE_SCHEDULES)
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
                    ScheduleModel.TABLE_SCHEDULES,
                    field(name("movie_id"),   int.class),
                    field(name("date_time"),  String.class),
                    field(name("auditorium"), byte.class)
                )
                .values(
                    this.movieId,
                    this.dateAndTime,
                    this.auditorium
                )
                .execute();
        } else { // изменение ранее созданной
            CommonModel.dslContext
                .update(ScheduleModel.TABLE_SCHEDULES)
                .set(field(name("movie_id"),   int.class),    this.movieId)
                .set(field(name("date_time"),  String.class), this.dateAndTime)
                .set(field(name("auditorium"), byte.class),   this.auditorium)
                .where(
                    field(name("id"), int.class).equal(this.id)
                )
                .execute();
        };
    }
}
