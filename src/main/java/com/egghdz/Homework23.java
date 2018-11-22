package com.egghdz;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Homework23 {
    public static void main(String[] args) {
        MongoCollection mgCollection = getCollection("students", "grades");

        List<Document> allHomework = (List<Document>) mgCollection.find(new Document("type", "homework"))
                .sort(Sorts.ascending("student_id", "score"))
                .into(new ArrayList<Document>());

        int deletionCount = 0;
        int previousStudentId = -1;

        for (Document cur : allHomework) {
            int currentStudentId = cur.getInteger("student_id");

            if (previousStudentId != currentStudentId) {
                deletionCount++;
                System.out.println("\nDeleting one document...");
                mgCollection.deleteOne(Filters.eq("_id", cur.getObjectId("_id")));

                previousStudentId = currentStudentId;
            }
        }

        System.out.println("Process completed. " + deletionCount + " document(s) has/have been deleted.");
    }

    private static MongoCollection getCollection(String dbName, String collectionName) {
        MongoClient mgClient = MongoClients.create();
        MongoDatabase mgDb = mgClient.getDatabase(dbName);

        return mgDb.getCollection(collectionName);
    }
}
