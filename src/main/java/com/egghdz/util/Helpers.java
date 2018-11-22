/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.egghdz.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;

import java.io.StringWriter;

public class Helpers {
    public static void printJson(Document document) {
        printJson(document, true);
    }

    public static void printJson(Document document, boolean indent) {
        JsonWriterSettings writerSettings = JsonWriterSettings.builder()
                .outputMode(JsonMode.SHELL)
                .indent(indent)
                .build();
        JsonWriter jsonWriter = new JsonWriter(new StringWriter(), writerSettings);
        new DocumentCodec().encode(
                jsonWriter,
                document,
                EncoderContext.builder()
                        .isEncodingCollectibleDocument(true)
                        .build()
        );

        System.out.println(jsonWriter.getWriter());
        System.out.flush();
    }

    public static MongoCollection<Document> getCollection(String dbName, String collectionName) {
        MongoClient mgClient = MongoClients.create();
        MongoDatabase mgDb = mgClient.getDatabase(dbName);

        return mgDb.getCollection(collectionName);
    }
}
