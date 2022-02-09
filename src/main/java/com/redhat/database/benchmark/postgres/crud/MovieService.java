package com.redhat.database.benchmark.postgres.crud;


import com.redhat.database.benchmark.mongo.crud.Fruit;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class MovieService {

    @Inject
    Instance<PgPool> clientInstance;

    @PostConstruct
    void config() {
        initdb();
    }

    public Multi<Movie> findAll() {
        return clientInstance
                .get()
                .query("SELECT id, title FROM movies ORDER BY title DESC")
                .execute()
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem()
                .transform(MovieService::from);
    }

    public Uni<Movie> findById(Long id) {
        return clientInstance
                .get()
                .preparedQuery("SELECT id, title FROM movies WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem()
                .transform(m -> m.iterator().hasNext() ? from(m.iterator().next()) : null);
    }

    public Fruit save(String title) {
        clientInstance
                .get()
                .preparedQuery("INSERT INTO movies (title) VALUES ($1) RETURNING id")
                .execute(Tuple.of(title))
                .onItem()
                .transform(m -> m.iterator().next().getLong("id"));

        return new Fruit("sampleUid","Sample name", "Sample Desc");
    }

    public Uni<Boolean> delete(Long id) {
        return clientInstance
                .get()
                .preparedQuery("DELETE FROM movies WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem()
                .transform(m -> m.rowCount() == 1);
    }

    private static Movie from(Row row) {
        return new Movie(row.getLong("id"), row.getString("title"));
    }

    private void initdb() {
        clientInstance
                .get()
                .query("DROP TABLE IF EXISTS movies").execute()
                .flatMap(m -> clientInstance.get().query("CREATE TABLE movies (id SERIAL PRIMARY KEY, " +
                        "title TEXT NOT NULL)").execute())
                .flatMap(m -> clientInstance.get().query("INSERT INTO movies (title) VALUES('The Lord of the Rings')").execute())
                .flatMap(m -> clientInstance.get().query("INSERT INTO movies (title) VALUES('Harry Potter')").execute())
                .await()
                .indefinitely();
    }

}