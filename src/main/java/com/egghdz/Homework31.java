package com.egghdz;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Homework31 {
    public static void main(String[] args) {
        MongoCollection mgCollection = getCollection("school", "students");

        List<Document> allStudents = (List<Document>) mgCollection.find()
                .into(new ArrayList<Document>());

        for (Document student : allStudents) {
            double lowestHomeworkScore = 100;
            int lowestHomeworkIndex = 0;
            int i = 0;
            List<Document> scores = (ArrayList<Document>) student.get("scores");

            for (Document score : scores) {
                if (score.getString("type").equals("homework")
                        && score.getDouble("score") <= lowestHomeworkScore) {
                    lowestHomeworkScore = score.getDouble("score");
                    lowestHomeworkIndex = i;
                }

                i++;
            }

            scores.remove(lowestHomeworkIndex);

            mgCollection.updateOne(
                    eq("_id", student.getInteger("_id")),
                    set("scores", scores)
            );
        }

    }

    private static MongoCollection getCollection(String dbName, String collectionName) {
        MongoClient mgClient = MongoClients.create();
        MongoDatabase mgDb = mgClient.getDatabase(dbName);

        return mgDb.getCollection(collectionName);
    }
}
