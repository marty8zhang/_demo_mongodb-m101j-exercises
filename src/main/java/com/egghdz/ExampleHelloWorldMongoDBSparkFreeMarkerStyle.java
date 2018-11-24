package com.egghdz;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.bson.Document;

import java.io.StringWriter;

import static spark.Spark.*;

public class ExampleHelloWorldMongoDBSparkFreeMarkerStyle {
    public static void main(String[] args) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setClassForTemplateLoading(ExampleHelloWorldMongoDBSparkFreeMarkerStyle.class, "/");

        MongoCollection mgCollection = getCollection("m101j", "ch2");
        mgCollection.drop();
        mgCollection.insertOne(new Document("name", "MongoDB"));

        get("/", (request, response) -> {
            StringWriter writer = new StringWriter();

            try {
                Template helloWorldTemplate = configuration.getTemplate("hello_world.ftl");

                Document doc = (Document) mgCollection.find().first();

                helloWorldTemplate.process(doc, writer);

                return writer;
            } catch (Exception e) {
                halt(500);
                e.printStackTrace();

                return null;
            }
        });
    }

    private static MongoCollection getCollection(String dbName, String collectionName) {
        MongoClient mgClient = MongoClients.create();
        MongoDatabase mgDb = mgClient.getDatabase(dbName);

        return mgDb.getCollection(collectionName);
    }
}
