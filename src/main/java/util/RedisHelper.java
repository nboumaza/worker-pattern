package util;


import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import static domain.Job.State;

//Iteration 2
public class RedisHelper {
    private static final String VERTX_Q_REDIS_PREFIX = "vertx_q";

    private RedisHelper() {
    }

    /**
     * Factory method for creating a Redis client in Vert.x context.
     *
     * @param vertx  Vertx instance
     * @param config configuration
     * @return the new Redis client instance
     */
    public static io.vertx.redis.RedisClient client(Vertx vertx, JsonObject config) {
        return RedisClient.create(vertx, options(config));
    }

    /**
     * Factory method for creating a default local Redis client configuration.
     *
     * @param config configuration from Vert.x context
     * @return the new configuration instance
     */
    public static io.vertx.redis.RedisOptions options(JsonObject config) {
        return new RedisOptions()
                .setHost(config.getString("redis.host", "127.0.0.1"))
                .setPort(config.getInteger("redis.port", 6379));
    }


    /**
     * Wrap the key with prefix of Vert.x JobQueue namespace.
     *
     * @param key the key to wrap
     * @return the wrapped key
     */
    public static String getKey(String key) {
        return VERTX_Q_REDIS_PREFIX + ":" + key;
    }


    /**
     * Generate the key of a certain task state with prefix of Vert.x JobQueue namespace.
     *
     * @param state job state
     * @return the generated key
     */
    public static String getStateKey(State state) {

        return VERTX_Q_REDIS_PREFIX + ":jobs:" + state.name();
    }

    /**
     * Create an id for the zset to preserve FIFO order.
     *
     * @param id id
     */
    public static String createFIFO(long id) {
        String idLen = "" + ("" + id).length();
        int len = 2 - idLen.length();
        while (len-- > 0)
            idLen = "0" + idLen;
        return idLen + "|" + id;
    }

    /**
     * Parse out original ID from zid.
     *
     * @param zid zid
     */
    public static String stripFIFO(String zid) {
        return zid.substring(zid.indexOf('|') + 1);
    }

    /**
     * Parse out original ID from zid.
     *
     * @param zid zid
     */
    public static long numStripFIFO(String zid) {
        return Long.parseLong(zid.substring(zid.indexOf('|') + 1));
    }
}
