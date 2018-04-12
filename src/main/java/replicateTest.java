import com.couchbase.client.core.tracing.ThresholdLogReporter;
import com.couchbase.client.core.tracing.ThresholdLogTracer;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.ReplicateTo;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import io.jaegertracing.Configuration;
import io.opentracing.*;

import java.util.concurrent.TimeUnit;

public class replicateTest {

    public static void main(String... args) throws Exception {



        Tracer tracer = new Configuration(
                "my_app",
                new Configuration.SamplerConfiguration("const", 1),
                new Configuration.ReporterConfiguration(
                        true, "172.17.0.2", 5775, 1000, 10000)
        ).getTracer();


        CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder()
                .tracer(tracer)
                .build();

        Cluster cluster = CouchbaseCluster.create(env, "172.17.0.3");
        cluster.authenticate("tom", "g0ne8ang");

        Bucket bucket = cluster.openBucket("travel-sample");

        // Load a couple of docs and write them back
        JsonDocument doc = bucket.get("airline_1");

        for(int i = 0; i < 10; i++) {
            if (doc != null) {
                bucket.upsert(doc, ReplicateTo.ONE);
            }
        }

        Thread.sleep(TimeUnit.MINUTES.toMillis(1));


    }

}
