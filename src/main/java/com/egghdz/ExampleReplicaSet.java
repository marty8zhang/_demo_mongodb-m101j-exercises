package com.egghdz;

import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import static com.egghdz.util.Helpers.getReplicaSetCollection;
import static java.util.Arrays.asList;

public class ExampleReplicaSet {
    public static void main(String[] args) {
        testConnection();
    }

    private static void testConnection() {
        MongoCollection<Document> mongoCollection = getReplicaSetCollection(
                    asList(
                        new ServerAddress("localhost", 27017),
                        new ServerAddress("localhost", 27018),
                        new ServerAddress("localhost", 27019)
                    ),
                    "example-replica-set",
                    "test",
                    "exampleReplicaSet"
                );
//        mongoCollection.withReadPreference(ReadPreference.nearest());
//        mongoCollection.withWriteConcern(WriteConcern.ACKNOWLEDGED); // See `WriteConcern` for more details.

        mongoCollection.drop();

        for (int i = 0; i < 100; i++) {
            try {
                mongoCollection.insertOne(new Document("_id", i));

                System.out.println("Inserted document: " + i);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
