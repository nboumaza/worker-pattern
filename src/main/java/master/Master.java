package master;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import util.JobHelper;
import worker.Worker;

@Slf4j
public class Master extends AbstractVerticle {


    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setOptional(true)
                .setConfig(new JsonObject().put("path", "conf/default.json"));

        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore)
                .addStore(sysPropsStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(json -> {
            if (json.succeeded()) {
                JsonObject config = json.result();
                log.debug("+++ Master: Starting with config: " + config.encodePrettily());

                //deploy master verticle : Single Thread Event Loop
                vertx.deployVerticle(Master.class.getName(), new DeploymentOptions().setConfig(config));


                //setup worker pool size and deploy
                //worker verticles use a separate thread pool, ( max of 20 thread pool) .
                vertx.deployVerticle(Worker.class.getName(), new DeploymentOptions()
                        .setConfig(config)
                        .setWorkerPoolName(config.getString("workers.pool.name", "barrister-pool"))
                        .setWorkerPoolSize(config.getInteger("workers.pool.size", 2))
                        .setInstances(config.getInteger("workers.instances.count", 1))
                        .setWorker(true)

                );
                log.info("+++ Master: Deployment completed! " + config.encodePrettily());

            } else {
                log.error("+++ Master: Error retrieving configuration.");
            }
        });
    }


    @Override
    public void start(Future<Void> done) {

        JsonObject config = config();
        int port = config.getInteger("http.port", 7070);

        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/jobs")
                .produces("application/json")
                .handler(this::dispatchJob);

        server.requestHandler(router).listen(port);
        log.info("*** Master: Listening for job requests on: http:/localhost"+port+"/jobs" );

        done.complete();
    }

    /**
     * @param routingContext routing context for posting jobs
     */
    private void dispatchJob(RoutingContext routingContext) {
        //iter 2:  send to the appropriate  queue in function of the job type
        JsonObject requestBody = routingContext.getBodyAsJson();

        EventBus eb = vertx.eventBus();

        JsonObject message = new JsonObject();
        String jobId = JobHelper.getNexJobID();

        //for now - Iteration 2 will create an actual Job instance
        message.put("id", jobId);
        message.put("customerId", requestBody.getValue("customerId"));
        message.put("coffee", requestBody.getValue("coffee"));
        message.put("size", requestBody.getValue("size"));

        /**
         * dispatch job request to workers pool -
         */
        //for now - Iteration 2 will infer from the job type
        log.info("*** Master: Dispatching ["+jobId+"] to Barrister Worker Pool\n" + requestBody.encodePrettily());
        eb.send("workers.jobs", message);


        //for now - Iter 2 will post the result to job queue
        routingContext.response()
                .setChunked(true)
                .putHeader("Content-Type", "application/json")
                .end(Json.encode(new JsonObject()
                        .put("status", "OK")
                        .put("id", jobId)
                ));

    }


}
