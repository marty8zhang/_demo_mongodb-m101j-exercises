package com.egghdz;

import com.egghdz.util.Helpers;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.gte;

public class ExampleAggregationFramework {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        MongoCollection<Document> collection = Helpers.getCollection("test", "zips");

        List<Document> pipelineOne = Arrays.asList(
                    new Document(
                        "$group", new Document(
                            "_id", "$state"
                        ).append(
                            "totalPopulation", new Document(
                                "$sum", "$pop"
                            )
                        )
                    ),
                    new Document(
                        "$match", new Document(
                            "totalPopulation", new Document(
                                "$gt", 10000000
                            )
                        )
                    )
                );
        // The same pipeline (as above) created using the aggregation builder. Note: The type is `List<Bson>` instead
        // of `List<Document>`.
        List<Bson> pipelineTwo = Arrays.asList(
                    Aggregates.group(
                        "$state", Accumulators.sum("totalPopulation", "$pop")
                    ),
                    Aggregates.match(
                        gte("totalPopulation", 10000000)
                    )
                );
        // Another way to create a pipeline same as above.
        List<Document> pipeLineThree = Arrays.asList(
                    Document.parse("{'$group': {'_id': '$state', 'totalPopulation': {'$sum': '$pop'}}}"),
                    Document.parse("{'$match': {'totalPopulation': {'$gt': 10000000}}}")
                );

        List<Document> all = collection.aggregate(pipelineTwo)
                .into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur, false);
        }
    }
}
