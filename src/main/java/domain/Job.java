package domain;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.redis.RedisClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import util.JobHelper;


@Slf4j
@Data
public abstract class Job {


    private static Vertx vertx;
    private static RedisClient client;
    private static EventBus eventBus;
    private final String addressId;
    protected String id = JobHelper.getNexJobID();
    protected State state = State.CREATED;
    protected Type type;
    protected Object response = null;
    protected RuntimeProfile profile;

    /**
     * Set vertx context of this Job
     *
     * @param v           vertx
     * @param redisClient redis client
     */
    protected static void setVertx(Vertx v, RedisClient redisClient) {
        vertx = v;
        client = redisClient;
        eventBus = vertx.eventBus();
    }

    /**
     * Process the task associated with this job
     */
    protected abstract void start();

    /**
     * Invoked when some compute resources threshold have been reached
     * ex: Throttling, exceeded budgeted allocated runtime, etc.
     */
    protected abstract void stop();

    public enum State {
        CREATED, QUEUED, PROCESSING, COMPLETED, FAILED, DELAYED
    }

    protected int maxConcurrentRequest;
    protected long startedAt;
    protected Object taskDef;    // task definition is required to run Docker containers for container orchestration service providers.


    public enum Type {
        WORKFLOW, SINGLE
    }

    public enum RuntimeProfile {
        SHORT, LONG
    }

    public enum Priority {

        LOW(4),
        NORMAL(3),
        MEDIUM(1),
        HIGH(1),
        CRITICAL(0);

        private int value;

        Priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


}
