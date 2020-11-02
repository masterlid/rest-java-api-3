package tv.lid.cinema.api3.controllers;

import java.util.List;

import io.jooby.Context;
import io.jooby.Route;

import tv.lid.cinema.api3.models.MovieModel;

// класс контроллера управления фильмами
public final class MovieController extends CommonController {
    private static final int MOVIES_PER_PAGE = 10; // количество записей на страницу

    // список фильмов
    public final Route.Handler list;

    // новый фильм
    public final Route.Handler create;

    // найти фильм
    public final Route.Handler find;

    // изменить фильм
    public final Route.Handler modify;

    // удалить фильм
    public final Route.Handler kill;

    // конструктор
    public MovieController() {
        // запрос списка фильмов
        this.list = (Context ctx) -> {
            // считываем номер страницы во входных параметрах
            int page = 1;

            try {
                page = Integer.parseInt(ctx.path("page").value());
            } catch (Exception exc) {}

            // запрашиваем количество записей и вычисляем число страниц
            int total, pages;
            try {
                total = MovieModel.count();
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось получить количество записей в таблице фильмов!");
            }
            pages = (int) Math.ceil(total / MovieController.MOVIES_PER_PAGE);

            // запрашиваем список записей в соответствии с номером страницы
            List<MovieModel> list;
            try {
                list = MovieModel.list(page, MovieController.MOVIES_PER_PAGE);
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось получить список фильмов по заданным параметрам!");
            }

            // возвращаем результат в обёртке списка
            return ok(new ListWrapper(
                list,
                total,
                pages
            ));
        };

        // создать новый фильм
        this.create = (Context ctx) -> {
            // преобразовываем входные данные в модель
            MovieModel movie = ctx.body(MovieModel.class);

            // проверка корректности полученных данных
            if (movie == null || movie.id != 0) {
                return error(Code.BAD_REQUEST, "Заданы некорректные входные данные запроса!");
            }

            // сохраняем фильм в БД
            try {
                movie.save();
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось сохранить информацию о фильме в базе данных!");
            }

            // сообщаем об успехе
            return ok();
        };

        // найти фильм по заданному идентификатору
        this.find = (Context ctx) -> {
            MovieModel movie;
            int        id;

            // считываем идентификатор фильма во входных параметрах
            try {
                id = Integer.parseInt(ctx.path("id").value());

                // ищем фильм по заданному идентификатору
                movie = MovieModel.find(id);
                if (movie == null) {
                    throw new Exception();
                }
            } catch (Exception exc) {
                return error(Code.BAD_REQUEST, "Задан некорректный идентификатор фильма!");
            }

            // возвращаем фильм
            return ok(movie);
        };

        // изменить ранее созданный фильм
        this.modify = (Context ctx) -> {
            MovieModel movie = ctx.body(MovieModel.class);

            // проверка корректности полученных данных
            if (movie == null || !MovieModel.exists(movie.id)) {
                return error(Code.BAD_REQUEST, "Заданы некорректные входные данные запроса!");
            }

            // сохраняем фильм в БД
            try {
                movie.save();
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось сохранить информацию о фильме в базе данных!");
            }

            // сообщаем об успехе
            return ok();
        };

        // удалить фильм по заданному идентификатору
        this.kill = (Context ctx) -> {
            int id;

            // считываем идентификатор фильма во входных параметрах
            try {
                id = Integer.parseInt(ctx.path("id").value());

                // проверяем существование фильма по заданному идентификатору
                if (!MovieModel.exists(id)) {
                    throw new Exception();
                }
            } catch (Exception exc) {
                return error(Code.BAD_REQUEST, "Задан некорректный идентификатор фильма!");
            }

            // удаляем фильм из БД
            try {
                MovieModel.kill(id);
            } catch (Exception exc) {
                return error(Code.INTERNAL_SERVER_ERROR, "Не удалось удалить информацию о фильме из базы данных!");
            }

            // сообщаем об успехе
            return ok();
        };
    }
}
