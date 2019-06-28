package worker;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import util.JobHelper;

@Slf4j
public class Worker extends AbstractVerticle {

    @Override
    public void start(Future<Void> done) {

        EventBus eb = vertx.eventBus();

        // <T> MessageConsumer<T> consumer(String address, Handler<Message<T>> handler)
        eb.consumer("workers.jobs", message -> {

            JsonObject body = (JsonObject) message.body();

            try {
                //Estimated processing time - for now bypass job profile
                long execTime = JobHelper.getRandomShortTime();
                log.info(">>> Worker: Processing " + body.getString("id") +" Estimated completion time: " + execTime+"ms");
                Thread.sleep(execTime);

            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }

            log.info(">>> Worker: " + body.getString("id") + " complete!");
        });

        done.complete();
    }


}