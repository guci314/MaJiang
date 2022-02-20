package com.guci.MaJiangGame.test;

import com.alibaba.fastjson.JSONObject;
import com.guci.MaJiangGame.HuaShe;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;
import java.util.function.Consumer;

public class TestJava {
    static MongoCollection<Document> collection;
    @BeforeClass
    public static void setup(){
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("majiang");
        collection = database.getCollection("test");
    }
    @Test
    public void testSwitch() {
        HuaShe x = HuaShe.TIAO;
        switch (x) {
            case WAN:
                System.out.println("wan");
                break;
            case TIAO:
                System.out.println("tiao");
                break;
            case TONG:
                System.out.println("tong");
                break;
        }
    }

    @Test
    public void testJson() {
        JSONObject object = new JSONObject();
        //string
        object.put("string", "string");
        //int
        object.put("int", 2);
        //boolean
        object.put("boolean", true);
        //array
        List<Integer> integers = Arrays.asList(1, 2, 3);
        object.put("list", integers);
        //null
        object.put("null", null);
        System.out.println(object.toJSONString());
    }

    @Test
    public void testMongo() {
        String uri = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("majiang");
            MongoCollection<Document> collection = database.getCollection("test");
            try {
                Document document = new Document("ddd", "ggg")
                        .append("_id", new ObjectId())
                        .append("title", "Ski Bloopers")
                        .append("genres", Arrays.asList("Documentary", "Comedy"));
                document.put("aaa", "bbb");
                InsertOneResult result = collection.insertOne(document);
                System.out.println("Success! Inserted document id: " + result.getInsertedId());
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }

    }

    @Test
    public void testPlayer() {
        FindIterable<Document> findIterable = collection.find(new Document());
        findIterable.forEach(document -> {
            String to=document.get("action",Document.class).get("to").toString();
            if (to.equals("1")) {
                System.out.println(document.get("action"));
                System.out.println(document.get("player1"));
            }
        });
    }

    @Test
    public void viewData(){
        FindIterable<Document> findIterable = collection.find(new Document());
        findIterable.forEach(document -> {
            System.out.println(document);
            //System.out.println(document.get("action",Document.class).get("code"));
//            if (document.get("action",Document.class).get("code").equals("Init")){
//                System.out.println(document);
//            }
        });
    }

    @Test
    public void test() {

    }
}
