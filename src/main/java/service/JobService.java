package service;


import domain.Job;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static domain.Job.State;


/**
 * Service interface for task operations.
 */
//TODO re-enable for iter 2
//@ProxyGen // Generate service proxies
//@VertxGen // Generate the clients
public interface JobService {


    /**
     * Factory method for creating a {@link JobService} instance.
     *
     * @param vertx  Vertx instance
     * @param config configuration
     * @return the new {@link JobService} instance
     */
    static JobService create(Vertx vertx, JsonObject config) {
        //Iter 2
        // return new JobServiceImpl(vertx, config);
        return null;
    }

    /**
     * Factory method for creating a {@link JobService} service proxy.
     * This is useful for doing RPCs.
     *
     * @param vertx   Vertx instance
     * @param address event bus address of RPC
     * @return the new {@link JobService} service proxy
     */
    static JobService createProxy(Vertx vertx, String address) {
        //TODO return new JobServiceVertxEBProxy(vertx, address);
        return null;
    }

    /**
     * Get the certain from redis backend by id.
     *
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService getJob(long id, Handler<AsyncResult<Job>> handler);

    /**
     * Remove a job by id.
     *
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService removeJob(long id, Handler<AsyncResult<Void>> handler);

    /**
     * Determine if a job with certain id exists.
     *
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService existsJob(long id, Handler<AsyncResult<Boolean>> handler);

    /**
     * Get job log by id.
     *
     * @param id      job id
     * @param handler async result handler
     */
    @Fluent
    JobService getJobLog(long id, Handler<AsyncResult<JsonArray>> handler);

    /**
     * metrics or UI client purpose
     *
     */

    /**
     * Get cardinality of failed jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService failedCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * //metrics or UI client
     * Get cardinality of inactive jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService inactiveCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of active jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService activeCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get cardinality of delayed jobs.
     *
     * @param type job type; if null, then return global metrics
     */
    @Fluent
    JobService delayedCount(String type, Handler<AsyncResult<Long>> handler);

    /**
     * Get the job types present.
     *
     * @param handler async result handler
     */
    @Fluent
    JobService getAllTypes(Handler<AsyncResult<List<String>>> handler);

    /**
     * Return job ids with the given {@link State}.
     *
     * @param state   job state
     * @param handler async result handler
     */
    @Fluent
    JobService getIdsByState(State state, Handler<AsyncResult<List<Long>>> handler);


}
