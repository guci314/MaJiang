package com.guci.MaJiangGame.test;

import com.alibaba.fastjson.JSONObject;
import com.guci.MaJiangGame.GameUtil;
import com.guci.MaJiangGame.HuaShe;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;
import static com.mongodb.client.model.Filters.eq;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TestJava {
    static MongoCollection<Document> collection;
    static MongoCollection<Document> dataForBasicAction;
    static MongoDatabase database;

    @BeforeClass
    public static void setup() {
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("majiang");
        collection = database.getCollection("test");
        dataForBasicAction = database.getCollection("dataForBasicAction");
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

        Consumer<Document> processDocument = document -> {
            String to = document.get("action", Document.class).get("to").toString();
            if (to.equals("1")) {
                System.out.println(document.get("action"));
                System.out.println(document.get("player1"));
            }
        };
        findIterable.forEach(processDocument::accept);
    }

    @Test
    public void gen_xiaJiaoZhangSu_Collection() {
        AtomicInteger counter= new AtomicInteger(0);
        AtomicInteger total= new AtomicInteger(0);
        MongoCollection<Document> xiaJiaoZhangSu_Collection = database.getCollection("xiaJiaoZhangSu");

        for (int j=3;j<5;j++) {
            for (int i = 1; i < 30; i++) {
                Document query = new Document();
                query.put("activePlayerNumber", j);
                query.put("player3.xiaJiaoZhangSu", i);

                HashSet<String> gameIds = new HashSet<>();

                FindIterable<Document> findIterable = dataForBasicAction.find(query)
                        .projection(new Document("gameId", 1));
                int finalI = i;
                int finalJ = j;
                findIterable.forEach(document -> {
                    String gid = document.getString("gameId");
                    if (!gameIds.contains(gid)) {
                        gameIds.add(gid);
                        Document d = new Document("gameId", gid);
                        d.put("activePlayerNum", finalJ);
                        d.put("player3_xjzs", finalI);
                        xiaJiaoZhangSu_Collection.insertOne(d);
                        int c = counter.getAndIncrement();
                        int t = total.getAndIncrement();
                        if (c > 500) {
                            System.out.println("total:" + t);
                            counter.set(0);
                        }
                    }
                });

            }
        }

    }

    @Test
    public void test1(){
        MongoCollection<Document> xiaJiaoZhangSu_Collection = database.getCollection("xiaJiaoZhangSu");
        xiaJiaoZhangSu_Collection.deleteMany(new Document("player3_xjzs",0));
    }

    @Test
    public void test() {
        long l=dataForBasicAction.estimatedDocumentCount();
        System.out.println(l);
    }
}
