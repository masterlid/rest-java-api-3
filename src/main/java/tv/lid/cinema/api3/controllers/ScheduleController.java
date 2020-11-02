package tv.lid.cinema.api3.controllers;

import java.util.List;

import io.jooby.Context;
import io.jooby.Route;

import tv.lid.cinema.api3.models.MovieModel;
import tv.lid.cinema.api3.models.ScheduleModel;

// класс контроллера управления сеансами
public final class ScheduleController extends CommonController {
    private static final int SCHEDULES_PER_PAGE = 10; // количество записей на страницу

    // список сеансов
    public final Route.Handler list;

    // новый сеанс
    public final Route.Handler create;

    // найти сеанс
    public final Route.Handler find;

    // изменить сеанс
    public final Route.Handler modify;

    // удалить сеанс
    public final Route.Handler kill;

    // конструктор
    public ScheduleController() {
        // запрос списка сеансов
        this.list = (Context ctx) -> {
            // считываем идентификатор фильма во входных параметрах
            int movieId;
            try {
                movieId = Integer.parseInt(ctx.path("movieId").value());
            } catch (Exception exc) {
                return error(Code.BAD_REQUEST, "Задан некорректный идентификатор фильма!");
            }

            // считываем номер страницы во входных параметрах
            int page = 1;

            try {
                page = Integer.parseInt(ctx.path("page").value());
            } catch (Exception exc) {}

            // запрашиваем количество записей и вычисляем число страниц
            int total, pages;
            try {
                total = ScheduleModel.count(movieId);
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось получить количество записей в таблице сеансов!");
            }
            pages = (int) Math.ceil(total / ScheduleController.SCHEDULES_PER_PAGE);

            // запрашиваем список записей в соответствии с номером страницы
            List<ScheduleModel> list;
            try {
                list = ScheduleModel.list(movieId, page, ScheduleController.SCHEDULES_PER_PAGE);
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось получить список сеансов по заданным параметрам!");
            }

            // возвращаем результат в обёртке списка
            return ok(new ListWrapper(
                list,
                total,
                pages
            ));
        };

        // создать новый сеанс
        this.create = (Context ctx) -> {
            // преобразовываем входные данные в модель
            ScheduleModel schedule = ctx.body(ScheduleModel.class);

            // проверка корректности полученных данных
            if (schedule == null || !MovieModel.exists(schedule.movieId)) {
                return error(Code.BAD_REQUEST, "Заданы некорректные входные данные запроса!");
            }

            // сохраняем фильм в БД
            try {
                schedule.save();
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось сохранить информацию о сеансе в базе данных!");
            }

            // сообщаем об успехе
            return ok();
        };

        // найти сеанс по заданному идентификатору
        this.find = (Context ctx) -> {
            ScheduleModel schedule;
            int           id;

            // считываем идентификатор сеанса во входных параметрах
            try {
                id = Integer.parseInt(ctx.path("id").value());

                // ищем сеанс по заданному идентификатору
                schedule = ScheduleModel.find(id);
                if (schedule == null) {
                    throw new Exception();
                }
            } catch (Exception exc) {
                return error(Code.BAD_REQUEST, "Задан некорректный идентификатор сеанса!");
            }

            // возвращаем сеанс
            return ok(schedule);
        };

        // изменить ранее созданный сеанс
        this.modify = (Context ctx) -> {
            ScheduleModel schedule = ctx.body(ScheduleModel.class);

            // проверка корректности полученных данных
            if (schedule == null || !ScheduleModel.exists(schedule.id) || !MovieModel.exists(schedule.movieId)) {
                return error(Code.BAD_REQUEST, "Заданы некорректные входные данные запроса!");
            }

            // сохраняем сеанс в БД
            try {
                schedule.save();
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось сохранить информацию о сеансе в базе данных!");
            }

            // сообщаем об успехе
            return ok();
        };

        // удалить сеанс по заданному идентификатору
        this.kill = (Context ctx) -> {
            int id;

            // считываем идентификатор сеанса во входных параметрах
            try {
                id = Integer.parseInt(ctx.path("id").value());

                // проверяем существование сеанса по заданному идентификатору
                if (!ScheduleModel.exists(id)) {
                    throw new Exception();
                }
            } catch (Exception exc) {
                return error(Code.BAD_REQUEST, "Задан некорректный идентификатор сеанса!");
            }

            // удаляем сеанс из БД
            try {
                ScheduleModel.kill(id);
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось удалить информацию о сеансе из базы данных!");
            }

            // сообщаем об успехе
            return ok();
        };
    }
}
