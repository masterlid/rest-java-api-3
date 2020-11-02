package tv.lid.cinema.api3;

import io.jooby.Jooby;
import io.jooby.MediaType;

import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tv.lid.cinema.api3.config.Config;
import tv.lid.cinema.api3.controllers.MovieController;
import tv.lid.cinema.api3.controllers.ScheduleController;
import tv.lid.cinema.api3.models.CommonModel;
import tv.lid.cinema.api3.models.MovieModel;
import tv.lid.cinema.api3.models.ScheduleModel;
import tv.lid.cinema.api3.storages.DatabaseStorage;

// главный класс приложения
public class App extends Jooby {
    // различные варианты запуска приложения
    private static final String CMD_OPERATE   = "operate",
                                CMD_INSTALL   = "install",
                                CMD_UNINSTALL = "uninstall";

    // инициализация класса
    {
        decoder(MediaType.json, (ctx, type) -> {
            try {
                return (new ObjectMapper())
                    .readValue(
                        ctx.body().bytes(),
                        Class.forName(type.getTypeName())
                    );
            } catch (ClassNotFoundException | JsonProcessingException exc) {
                return null;
            }
        });

        encoder(MediaType.json, (ctx, result) -> {
            ctx.setDefaultResponseType(MediaType.json);

            try {
                return (new ObjectMapper()).writeValueAsBytes(result);
            } catch (JsonProcessingException exc) {
                return null;
            }
        });

        path("/api3", () -> {
            // фильмы
            final MovieController movCtr = new MovieController();

            get("/movies",        movCtr.list);
            get("/movies/{page}", movCtr.list);
            post("/movie",        movCtr.create);
            get("/movie/{id}",    movCtr.find);
            put("/movie",         movCtr.modify);
            delete("/movie/{id}", movCtr.kill);

            // сеансы
            final ScheduleController schCtr = new ScheduleController();

            get("/schedules/{movieId}",        schCtr.list);
            get("/schedules/{movieId}/{page}", schCtr.list);
            post("/schedule",                  schCtr.create);
            get("/schedule/{id}",              schCtr.find);
            put("/schedule",                   schCtr.modify);
            delete("/schedule/{id}",           schCtr.kill);
        });
    }

    // создание таблиц в базе данных
    private static void install() throws SQLException {
        MovieModel.createTable();
        ScheduleModel.createTable();
    }

    // удаление таблиц из базы данных
    private static void uninstall() throws SQLException {
        ScheduleModel.dropTable();
        MovieModel.dropTable();
    }

    // нормальная работа приложения
    private static void operate(final String[] args) {
        runApp(args, App::new);
    }

    public static void main(final String[] args) {
        // читаем конфигурацию приложения
        final Config cfg = Config.load();
        if (cfg == null) {
            System.out.println("Unable to interpret the configuration file! Exiting...\n\n");
            return;
        }

        // инициализация соединения с БД и подключение
        final DatabaseStorage dbs;

        try {
            // подключение к серверу БД
            dbs = DatabaseStorage.initialize(cfg.database);
            dbs.connect();

            // инициализация моделей
            CommonModel.initialize(dbs.dslContext());
        } catch (SQLException exc) {
            System.out.println("Unable to initialize the database storage! Exiting...\n\n");
            return;
        }

        // хук завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // разрыв соединения с БД
                try {
                    dbs.disconnect();
                } catch (SQLException exc) {
                    System.out.println("Unable to finalize the database storage!\n\n");
                }
            }
        });

        // разбор командной строки
        try {
            if (args.length == 0 || args.length == 1) {
                if (args.length == 0 || args[0].equals(App.CMD_OPERATE)) { // обычный режим
                    App.operate(args);
                } else if (args[0].equals(App.CMD_INSTALL)) { // создание таблиц
                    App.install();
                } else if (args[0].equals(App.CMD_UNINSTALL)) { // удаление таблиц
                    App.uninstall();
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (SQLException exc) {
            System.out.println("SQL exception occured during the execution! Exiting...\n\n");
        } catch (Exception exc) {
            System.out.println("Incorrect command line arguments were specified! Exiting...\n\n");
        }
    }
}
