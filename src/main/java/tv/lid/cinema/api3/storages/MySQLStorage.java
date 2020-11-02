package tv.lid.cinema.api3.storages;

import java.sql.SQLException;
import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import tv.lid.cinema.api3.config.Config;

// класс доступа к базе данных MySQL
public final class MySQLStorage extends DatabaseStorage {
    // дефолтные параметры подключения к базе данных
    private static final int    DEFAULT_DB_PORT     = 3306;        // дефолтный порт для подключения к серверу БД

    private static final String DEFAULT_DB_HOST     = "localhost", // дефолтный хост для подключения к серверу БД
                                DEFAULT_DB_NAME     = "cinema",    // дефолтное имя БД
                                DEFAULT_DB_USERNAME = "root",      // дефолтное имя пользователя БД
                                DEFAULT_DB_PASSWORD = "";          // дефолтный пароль пользователя БД

    // параметры подключения к базе данных
    private final int    dbPort;
    private final String dbHost;
    private final String dbName;
    private final String dbUsername;
    private final String dbPassword;

    // data source
    private MysqlDataSource ds = null;

    // конструктор #1
    public MySQLStorage(
        int    dbPort,
        String dbHost,
        String dbName,
        String dbUsername,
        String dbPassword
    ) {
        this.dbPort     = dbPort;
        this.dbHost     = dbHost;
        this.dbName     = dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    // конструктор #2
    public MySQLStorage() {
        this(
            MySQLStorage.DEFAULT_DB_PORT,
            MySQLStorage.DEFAULT_DB_HOST,
            MySQLStorage.DEFAULT_DB_NAME,
            MySQLStorage.DEFAULT_DB_USERNAME,
            MySQLStorage.DEFAULT_DB_PASSWORD
        );
    }

    // конструктор #3
    public MySQLStorage(final Config.Database cfgDb) {
        this(
            cfgDb.port,
            cfgDb.host,
            cfgDb.name,
            cfgDb.user,
            cfgDb.pass
        );
    }

    // установление соединения с базой данных
    public void connect() throws SQLException {
        this.ds = new MysqlDataSource();
        this.ds.setPortNumber(this.dbPort);
        this.ds.setServerName(this.dbHost);
        this.ds.setDatabaseName(this.dbName);
        this.ds.setUser(this.dbUsername);
        this.ds.setPassword(this.dbPassword);
    }

    // разрыв соединения с базой данных
    public void disconnect() throws SQLException {
        if (this.ds != null) {
            this.ds = null;
        } else {
            throw new SQLException();
        }
    }

    // получить DSL context
    public DSLContext dslContext() throws SQLException {
        if (this.ds != null) {
            return DSL.using(this.ds, SQLDialect.MYSQL);
        } else {
            throw new SQLException();
        }
    }
}
