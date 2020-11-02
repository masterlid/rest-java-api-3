package tv.lid.cinema.api3.config;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

// класс конфигурации приложения
public final class Config {
    // имя файла с настройками
    private static final String CONFIG_FILE = "config.json";

    // внутренний класс конфигурации базы данных
    public static class Database {
        // тип базы данных
        @JsonProperty(value = "type", required = true)
        public final String type;
    
        // имя базы данных
        @JsonProperty(value = "name", required = false, defaultValue = "")
        public final String name;
    
        // имя каталога, где расположен файл базы данных
        @JsonProperty(value = "home", required = false, defaultValue = "")
        public final String home;
    
        // имя файла базы данных
        @JsonProperty(value = "file", required = false, defaultValue = "")
        public final String file;
    
        // хост для подключения к серверу базы данных
        @JsonProperty(value = "host", required = true)
        public final String host;
    
        // порт для подключения к серверу базы данных
        @JsonProperty(value = "port", required = true)
        public final int port;
    
        // имя пользователя для подключения к серверу базы данных
        @JsonProperty(value = "user", required = true)
        public final String user;
    
        // пароль пользователя для подключения к серверу базы данных
        @JsonProperty(value = "pass", required = true)
        public final String pass;
    
        // конструктор
        @JsonCreator
        public Database(
            @JsonProperty("type") final String type,
            @JsonProperty("name") final String name,
            @JsonProperty("home") final String home,
            @JsonProperty("file") final String file,
            @JsonProperty("host") final String host,
            @JsonProperty("port") final int    port,
            @JsonProperty("user") final String user,
            @JsonProperty("pass") final String pass
        ) {
            this.type = type;
            this.name = name;
            this.home = home;
            this.file = file;
            this.host = host;
            this.port = port;
            this.user = user;
            this.pass = pass;
        }
    }

    // конфигурация базы данных
    @JsonProperty(value = "database", required = true)
    public final Config.Database database;

    // конструктор
    @JsonCreator
    public Config(
        @JsonProperty("database") final Config.Database database
    ) {
        this.database = database;
    }

    // статический метод считывает конфигурацию из заданного файла
    public static Config load(final String fileName) {
        try {
            return (new ObjectMapper()).readValue(new File(fileName), Config.class);
        } catch (Exception exc) {
            return null;
        }
    }

    // статический метод считывает конфигурацию из дефолтного файла
    public static Config load() {
        return Config.load(CONFIG_FILE);
    }
}
