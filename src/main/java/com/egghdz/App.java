package com.egghdz;

import com.egghdz.util.Helpers;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Updates.*;

public class App {
    public static void main(String[] args) {
        runExampleDocument();

        runExampleFind();

        runExampleFindWithFilters();

        runExampleFindWithProjection();

        runExampleFindWithSortSkipLimit();

        runExampleInsert();

        runExampleUpdateReplace();

        runExampleDelete();
    }

    private static MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoClient mgClient = MongoClients.create();
        MongoDatabase mgDb = mgClient.getDatabase(dbName);

        return mgDb.getCollection(collectionName);
    }

    private static void runExampleDocument() {
        // Creating and accessing a document.
        Document document = new Document()
                .append("objectId", new ObjectId())
                .append("greeting", "Hello, BSON!")
                .append("nonsenseInt", 42)
                .append("nonsenseLong", 1L)
                .append("nonsenseDouble", 1.1)
                .append("nonsenseBoolean", true)
                .append("nonsenseNull", null)
                .append("nonsenseDate", new Date())
                .append("nonsenseEmbeddedDoc", new Document("nonsenseKey", "nonsenseValue"))
                .append("nonsenseList", Arrays.asList(1, 2, 3));

        String greeting = document.getString("greeting");
        int nonsenseInt = document.getInteger("nonsenseInt");
        // ...

        Helpers.printJson(document);

        // Creating a BSON document which is type-safe.
        BsonDocument bsonDocument = new BsonDocument("greeting", new BsonString("Hello, BSON!"));
    }

    private static void runExampleFind() {
        MongoCollection mgCollection = getCollection("school", "temp");
        mgCollection.drop();

        for (int i = 0; i < 10; i++) {
            mgCollection.insertOne(new Document("x", i));
        }

        System.out.println("\nFind one:");
        Document first = (Document) mgCollection.find().first();
        Helpers.printJson(first);

        System.out.println("\nFind all with into():");
        List<Document> all = (List<Document>) mgCollection.find().into(new ArrayList<Document>());
        for (Document cur : all) {
            Helpers.printJson(cur);
        }

        System.out.println("\nFind all with iteration:");
        // The try-with-resources version. Because MongoCursor implements the interface java.lang.AutoCloseable, it will
        // be closed regardless of whether the try statement completes normally or abruptly
        try (MongoCursor<Document> cursor = mgCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document cur = cursor.next();
                Helpers.printJson(cur);
            }
        }

        System.out.println("\nCount: ");
        long count = mgCollection.countDocuments();
        System.out.println(count);
    }

    private static void runExampleFindWithFilters() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        for (int i = 0; i < 10; i++) {
            mgCollection.insertOne(
                    new Document()
                            .append("x", new Random().nextInt(2))
                            .append("y", new Random().nextInt(100))
            );
        }

        // Create a filter with a raw document.
        Bson filterOne = new Document("x", 0)
                .append(
                        "y",
                        new Document("$gt", 10)
                                .append("$lt", 90)
                );
        // Create the same filter with the filter builder.
        Bson filterTwo = and(
                eq("x", 0),
                gt("y", 10),
                lt("y", 90)
        );

        List<Document> all = (List<Document>) mgCollection.find(filterTwo).into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur);
        }

        long count = mgCollection.countDocuments(filterTwo);
        System.out.println("\nCount: " + count);
    }

    private static void runExampleFindWithProjection() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        for (int i = 0; i < 10; i++) {
            mgCollection.insertOne(
                    new Document()
                            .append("x", new Random().nextInt(2))
                            .append("y", new Random().nextInt(100))
                            .append("i", i)
            );
        }

        Bson filterTwo = and(
                eq("x", 0),
                gt("y", 10),
                lt("y", 90)
        );

        // Create projection rules with a raw document.
        Bson projectionOne = new Document("x", 0)
                .append("_id", 0);
        // The projection builder version of the above projection.
        Bson projectionTwo = exclude("x", "_id");
        // The inclusion version of the above projection.
        Bson projectionThree = fields(
                include("y", "i"),
                exclude("_id") // You can also use Projections.excludeId() here.
        );

        List<Document> all = (List<Document>) mgCollection
                .find(filterTwo)
                .projection(projectionTwo)
                .into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur);
        }
    }

    private static void runExampleFindWithSortSkipLimit() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                mgCollection.insertOne(
                        new Document()
                                .append("i", i)
                                .append("j", j)
                );
            }
        }

        Bson projection = exclude("_id");

        Bson sortOne = new Document("i", 1).append("j", 1);
        Bson sortTwo = ascending("i", "j");
        // Sorts.orderBy() is only needed when there are more than one sort criteria.
        Bson sortThree = orderBy(ascending("i"), ascending("j"));

        List<Document> all = (List<Document>) mgCollection
                .find()
                .projection(projection)
                .sort(sortTwo)
                .skip(10) // Note: The order of skip() and limit() doesn't matter.
                .limit(10)
                .into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur, false);
        }
    }

    private static void runExampleInsert() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        Document student = new Document("name", "Marty")
                .append("age", 17)
                .append("major", "Software Engineering");
        Helpers.printJson(student);

        // Note: insertOne() will update the given document with the returned "_id".
        mgCollection.insertOne(student);
        Helpers.printJson(student);

        student.remove("_id");
        Helpers.printJson(student);

        // insertMany().
        Document studentOne = new Document("name", "John");
        Document studentTwo = new Document("name", "Smith");
        mgCollection.insertMany(Arrays.asList(studentOne, studentTwo));
    }

    private static void runExampleUpdateReplace() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        for (int i = 0; i < 8; i++) {
            mgCollection.insertOne(
                    new Document()
                            .append("_id", i)
                            .append("x", i)
                            .append("y", true)
            );
        }

        // Update a document with a raw document.
        mgCollection.updateOne(
                eq("x", 4),
                new Document("$set", new Document("x", 20)
                        .append("updated", true))
        );
        // Achieve the same thing with the update builder.
        mgCollection.updateOne(
                eq("x", 4),
                // Note: Updates.combine() is only needed when you want to set more than one field.
                combine(
                        set("x", 20),
                        set("updated", true)
                )
        );

        mgCollection.replaceOne(
                eq("x", 5),
                new Document("x", 20)
                        .append("updated", true)
        );

        // Upsert.
        mgCollection.updateOne(
                eq("_id", 9),
                combine(
                        set("x", 20),
                        set("updated", true)
                ),
                new UpdateOptions().upsert(true)
        );

        mgCollection.updateMany(
                lt("x", 5),
                inc("x", 1)
        );

        List<Document> all = (List<Document>) mgCollection.find()
                .into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur, false);
        }
    }

    private static void runExampleDelete() {
        MongoCollection mgCollection = getCollection("school", "students");
        mgCollection.drop();

        for (int i = 0; i < 8; i++) {
            mgCollection.insertOne(new Document("_id", i));
        }

        mgCollection.deleteOne(eq("_id", 1));

        mgCollection.deleteMany(gt("_id", 4));

        List<Document> all = (List<Document>) mgCollection.find()
                .into(new ArrayList<Document>());

        for (Document cur : all) {
            Helpers.printJson(cur, false);
        }
    }
}
