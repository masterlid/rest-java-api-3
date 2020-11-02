package tv.lid.cinema.api3.storages;

import java.sql.SQLException;
import javax.sql.DataSource;

import org.jooq.DSLContext;

import tv.lid.cinema.api3.config.Config;

// базовый абстрактный класс базы данных
public abstract class DatabaseStorage {
    // возможные значения типов базы данных
    public static final String DB_TYPE_H2         = "h2",         // H2
                               DB_TYPE_MYSQL      = "mysql",      // MySQL
                               DB_TYPE_POSTGRES   = "postgres",   // PostgreSQL, вариант 1
                               DB_TYPE_POSTGRESQL = "postgresql"; // PostgreSQL, вариант 2

    // инициализация DatabaseStorage по заданной конфигурации
    public static DatabaseStorage initialize(final Config.Database cfgDb) throws SQLException {
        final DatabaseStorage dbs;

        if (cfgDb.type.equalsIgnoreCase(DB_TYPE_H2)) {
            dbs = new H2Storage(cfgDb);
        } else if (cfgDb.type.equalsIgnoreCase(DB_TYPE_MYSQL)) {
            dbs = new MySQLStorage(cfgDb);
        } else if (cfgDb.type.equalsIgnoreCase(DB_TYPE_POSTGRES) ||
                   cfgDb.type.equalsIgnoreCase(DB_TYPE_POSTGRESQL)) {
            dbs = new PostgreSQLStorage(cfgDb);
        } else {
            throw new SQLException();
        }

        return dbs;
    }

    // установление соединения с базой данных
    public abstract void connect() throws SQLException;

    // разрыв соединения с базой данных
    public abstract void disconnect() throws SQLException;

    // получить DSL context
    public abstract DSLContext dslContext() throws SQLException;
}
