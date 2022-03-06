package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;
import com.guci.MaJiangGame.QingYiSe.QingYiSeDianPaoHuAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSePengGangAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
import com.guci.MaJiangGame.RenPao.*;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import lombok.var;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

public class TestStrategy {
    static MongoCollection<Document> dataForBasicAction;
    static MongoCollection<Document> xiaJiaoZhangSuCollection;
    static MongoDatabase database;
    static MongoCollection<Document> qysCollection;
    static MongoCollection<Document> paiXing1Collection;
    static MongoCollection<Document> kuanChuangCollection;
    static MongoCollection<Document> player3XiajaoCollection;

    @BeforeClass
    public static void setup() {
        HuUtil.load();
        AIUtil.load();
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("majiang");
        dataForBasicAction = database.getCollection("dataForBasicAction");
        xiaJiaoZhangSuCollection = database.getCollection("xiaJiaoZhangSu");
        qysCollection = database.getCollection("qingyise");
        paiXing1Collection = database.getCollection("paixing1");
        kuanChuangCollection = database.getCollection("kuanChuang");
        player3XiajaoCollection = database.getCollection("player3XiaJiao");
    }

    /**
     * 测试碰杠策略
     */
    //@Test
    public void testSmartPengGang() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.players.get(0).pengGangActionList.clear();
        matrix.players.get(0).pengGangActionList.add(new SmartPengGangAction());

        //matrix.showDetail=true;
        for (int i = 0; i < 10000; i++) {
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 测试暗杠策略
     */
    //@Test
    public void testSmartAnGang() {
        // TODO: 2022/2/15 测试清一色的暗杠策略
        Matrix matrix = new Matrix();
        matrix.init();
        Player p = matrix.players.get(0);
        p.moPaiActionList.clear();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new SmartMoPaiAction());
        //matrix.showDetail=true;
        for (int i = 0; i < 10000; i++) {
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 测试忍炮  根据活动用户
     * 百万盘基准值 -81087  忍炮 -60580
     * 结论 活动用户为4的时候忍炮比不忍炮略好.  如果牌有成长空间则忍炮
     * 活动玩家小于4时不能忍炮
     */
    @Test
    public void testRenPao_activePlayerNum() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByActivePlayerNumber rp = new RenPaoAction_ByActivePlayerNumber();
        p.dianPaoHuActionList.add(rp);
        for (int n = 3; n < 4; n++) {
            rp.threshold_active_player = n;
            System.out.println("活动用户阈值=" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
//                matrix.players.get(0).status=Status.Hu;
//                matrix.players.get(1).status=Status.Hu;
                matrix.play();
            }
            matrix.printJingE();
        }
        //matrix.print();
    }

    /**
     * 测试忍炮  根据剩余牌张数
     * 结论  剩余玩家为4时  剩余张数最优值是 27
     */
    @Test
    public void testRenPao_remainCardsNum() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByCardsNumber rp = new RenPaoAction_ByCardsNumber();
        p.dianPaoHuActionList.add(rp);
        for (int n = 5; n < 50; n++) {
            //int n=9;
            rp.threshold_cards_number = n;
            System.out.println("剩余张数阈值=" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 10000; i++) {
                matrix.reset();
                matrix.play();
            }
            //matrix.printJingE();
            System.out.println(matrix.players.get(2).jinE);
        }

    }

    /**
     * 测试忍炮  根据胡牌张数
     * 5是最优阈值
     *
     * @see #genRenPaoYuZiData()
     */
    @Test
    public void testRenPao2() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByXiaJiaoZhangShu rp = new RenPaoAction_ByXiaJiaoZhangShu();
        p.dianPaoHuActionList.add(rp);
        for (int n = 1; n < 10; n++) {
            rp.xjzs_threshold = n;
            System.out.println("胡牌张数阈值=" + n);
            matrix.resetJinE();
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
            //matrix.print();
        }
    }

    @Test
    public void testRenPaoByQys() {
        Document query = new Document("player3.isQingYiShe", false);
        List<Document> docs = player3XiajaoCollection.find(query).limit(10000).into(new ArrayList<>());
        System.out.println("documents size:" + docs.size());
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByXiaJiaoZhangShu rp = new RenPaoAction_ByXiaJiaoZhangShu();
        p.dianPaoHuActionList.add(rp);
        matrix.reset();

        for (int n = 2; n < 10; n++) {
            rp.xjzs_threshold = n;
            System.out.println("胡牌张数阈值=" + n);
            matrix.resetJinE();
            for (Document doc : docs) {
                matrix.loadFromDocument(doc);
                matrix.play();
            }
            matrix.printJingE();
            //matrix.print();
        }
    }

    @Test
    public void testRenPao_xjzs_remainCardsNum() {
        int xjzs_threshold = 1;
        //System.out.println("胡牌张数阈值:"+xjzs_threshold);
        List<Document> docs = dataForBasicAction.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.and(
                                Filters.eq("activePlayerNumber", 4),
                                //Filters.eq("player3.xiaJiaoZhangSu", xjzs_threshold)))
                                Filters.gt("player3.xiaJiaoZhangSu", 0)))
                        , Aggregates.limit(10)
                        , Aggregates.project(Projections.include(Arrays.asList("gameId", "index")))
                        , Aggregates.group("$gameId", Accumulators.min("minIndex", "$index"))

                )
        ).into(new ArrayList<>());
        System.out.println("局面总数:" + docs.size());
        System.out.println(docs.get(0));
    }

    /**
     * 根据下叫张数忍炮
     */
    @Test
    public void testRenPao_xjzs1() {
        for (int i = 1; i < 10; i++) {
            int xjzs_threshold = i;
            System.out.println("胡牌张数阈值:" + xjzs_threshold);
            List<Document> docs = dataForBasicAction.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.and(
                                    Filters.eq("activePlayerNumber", 4),
                                    Filters.eq("player3.xiaJiaoZhangSu", xjzs_threshold)))
                            , Aggregates.limit(100000)
                            , Aggregates.project(Projections.include(Arrays.asList("gameId", "index")))
                            , Aggregates.group("$gameId", Accumulators.min("minIndex", "$index"))

                    )
            ).into(new ArrayList<>());
            System.out.println("局面总数:" + docs.size());
            Matrix matrix = new Matrix();
            matrix.collection = dataForBasicAction;
            matrix.init();
            matrix.createQingYiSeAction();
            for (Document document : docs) {
                String gameId = document.getString("_id");
                int index = document.getInteger("minIndex");
                matrix.loadFromDatabase(gameId, index);
                matrix.play();
            }
            matrix.printJingE();
            matrix.resetJinE();
            System.out.println("---------------------------------");

            Player p = matrix.players.get(2);
            p.dianPaoHuActionList.clear();
            RenPaoAction_ByXiaJiaoZhangShu rp = new RenPaoAction_ByXiaJiaoZhangShu();
            rp.xjzs_threshold = xjzs_threshold;
            p.dianPaoHuActionList.add(rp);

            for (Document document : docs) {
                String gameId = document.getString("_id");
                int index = document.getInteger("minIndex");
                matrix.loadFromDatabase(gameId, index);
                matrix.play();
            }
            matrix.printJingE();
        }
    }

    @Test
    public void testRenPao_xjzs() {
        for (int x = 1; x < 2; x++) {
            int xjzs = x;
            System.out.println("张数阈值:" + x);
            HashSet<String> gameIds = new HashSet<>();
            Document query = new Document("activePlayerNum", 4);
            query.put("player3_xjzs", xjzs);
            FindIterable<Document> findIterable = xiaJiaoZhangSuCollection.find(query).limit(10);
            findIterable.forEach(document -> {
                gameIds.add(document.getString("gameId"));
            });

            System.out.println(gameIds.size());

            Matrix matrix = new Matrix();
            matrix.collection = dataForBasicAction;
            matrix.init();
            matrix.createQingYiSeAction();
            matrix.reset();
            for (String id : gameIds) {
                matrix.loadFromDatabase(id, 1);
                matrix.play();
            }
            matrix.printJingE();

            System.out.println("---------------------------");

            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            Player p = matrix.players.get(2);
            p.dianPaoHuActionList.clear();
            RenPaoAction_ByXiaJiaoZhangShu rp = new RenPaoAction_ByXiaJiaoZhangShu();
            rp.xjzs_threshold = xjzs;
            p.dianPaoHuActionList.add(rp);

            for (String id : gameIds) {
                matrix.loadFromDatabase(id, 1);
                matrix.play();
            }
            matrix.printJingE();
            //matrix.print();
        }
    }

    /**
     * 根据手上坎牌数量决定是否忍炮
     * 结论 手上有一坎可杠牌 且玩家数目为4时要忍炮
     * 胡牌张数最优值 4
     */
    @Test
    public void testRenPao_byKanPai() {
        for (int i = 1; i < 10; i++) {
            System.out.println("胡牌张数值:" + i);
            List<Document> docs = dataForBasicAction.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.and(
                                    Filters.eq("activePlayerNumber", 4),
                                    Filters.gte("player3.xiaJiaoZhangSu", i),
                                    Filters.gt("player3.numberOfKanPai", 0),
                                    Filters.eq("player3.status", "Playing")
                            ))

                            , Aggregates.limit(110000)
                            , Aggregates.project(Projections.include(Arrays.asList("gameId", "index")))
                            , Aggregates.group("$gameId", Accumulators.min("minIndex", "$index"))

                    )
            ).into(new ArrayList<>());
            System.out.println("局面总数:" + docs.size());
            Matrix matrix = new Matrix();
            matrix.collection = dataForBasicAction;
            matrix.init();
            matrix.createQingYiSeAction();
            for (Document document : docs) {
                String gameId = document.getString("_id");
                int index = document.getInteger("minIndex");
                matrix.loadFromDatabase(gameId, index);
                if (matrix.players.get(2).numberOfKanPai() > 0) matrix.play();
            }
            matrix.printJingE();
            float jingE1 = matrix.players.get(2).jinE;
            matrix.resetJinE();
            System.out.println("---------------------------------");

            Player p = matrix.players.get(2);
            p.dianPaoHuActionList.clear();
            RenPaoAction_ByKeGangPai rp = new RenPaoAction_ByKeGangPai();
            p.dianPaoHuActionList.add(rp);

            for (Document document : docs) {
                String gameId = document.getString("_id");
                int index = document.getInteger("minIndex");
                matrix.loadFromDatabase(gameId, index);
                if (matrix.players.get(2).numberOfKanPai() > 0) matrix.play();
            }
            matrix.printJingE();
            float jingE2 = matrix.players.get(2).jinE;
            System.out.println("提升率:" + (jingE2 - jingE1) / jingE1);
        }
    }

    /**
     * 打一万盘
     * 2号玩家基准值是 -800
     */
    @Test
    public void testRenPaoJiZunZi() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        for (int i = 0; i < 100000; i++) {
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 生成牌型1的测试数据
     * {@link TestStrategy#testPaiXing1()}
     */
    @Test
    public void genPaiXing1() {
        Matrix.memoryDb = new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.savetodb = true;
        matrix.createQingYiSeAction();
        int counter = 0;
        while (true) {
            counter++;
            if (counter > 300) {
                counter = 0;
                System.out.println(Matrix.memoryDb.size());
            }
            matrix.reset();
            matrix.play();
            if (Matrix.memoryDb.size() > 50000) break;
        }
        database.getCollection("paixing1").insertMany(Matrix.memoryDb);
    }

    @Test
    public void genKuanChuang() {
        Matrix.memoryDb = new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.savetodb = true;
        matrix.createQingYiSeAction();
        int counter = 0;
        while (true) {
            counter++;
            if (counter > 1000) {
                counter = 0;
                System.out.println(Matrix.memoryDb.size());
            }
            matrix.reset();
            matrix.play();
            if (Matrix.memoryDb.size() > 100000) break;
        }
        database.getCollection("kuanChuang").insertMany(Matrix.memoryDb);
    }

    @Test
    public void genKuanChuangXiaJiao() {
        //AtomicReference<Document> currentDoc=null;
        //var kuan
        MongoCollection<Document> kuanChuangXiaJiaoCollection =
                database.getCollection("kuanChuangXiaJiao");
        BiConsumer<Matrix, Document> callback = (matrix, doc) -> {
            int xjzs = matrix.players.get(2).xiaJiaoZhangSu();
            if (xjzs > 0 && matrix.players.get(2).status == Status.Playing) {
                kuanChuangXiaJiaoCollection.insertOne(doc);
                matrix.players.forEach(p -> p.status = Status.Hu);
            }
        };
        Matrix matrix = new Matrix();
        matrix.saveStatusConsumer = callback;
        matrix.init();
        matrix.savetodb = true;
        matrix.createQingYiSeAction();
        matrix.reset();
        AtomicInteger total = new AtomicInteger();
        AtomicInteger counter = new AtomicInteger();
        List<Document> docs = kuanChuangCollection.find(new Document()).into(new ArrayList<>());
        System.out.println("total size:" + docs.size());
        docs.forEach(document -> {
            total.getAndIncrement();
            counter.getAndIncrement();
            if (counter.get() > 300) {
                counter.set(0);
                System.out.println("total:" + total.get());
            }
            matrix.loadFromDocument(document);
            matrix.play();
        });
    }

    @Test
    public void loadOneGame() {
        //Document query =new Document("gameId","fdbbe63e-20ab-4c39-b8bd-110641abbbfc");
        Document query = new Document("_id", new ObjectId("6218666c19c2fd5c10d0df54"));
        Document document = player3XiajaoCollection.find(query).first();
        Matrix matrix = new Matrix();
        //matrix.savetodb=true;
//        matrix.saveStatusConsumer=(matrix1, doc) -> {
//            int xjzs=matrix1.players.get(2).xiaJiaoZhangSu();
//            if (xjzs>0) {
//                System.out.println(xjzs);
//                matrix1.print();
//            }
//        };
        matrix.init();
        matrix.jiZhang.settleConsumer = result -> {
            System.out.println(result);
        };
        matrix.createQingYiSeAction();
        matrix.reset();
        matrix.loadFromDocument(document);
        matrix.play();
        //matrix.print();
        matrix.printJingE();
    }

    //region player3XiaJiao
    @Test
    public void genPlayer3XiaJiao() {
        BiConsumer<Matrix, Document> callback = (matrix, doc) -> {
            int xjzs = matrix.players.get(2).xiaJiaoZhangSu();
            if (xjzs > 0 && matrix.players.get(2).status == Status.Playing) {
                doc.put("matrix_cards_size", matrix.cards.size());
                player3XiajaoCollection.insertOne(doc);
                //Matrix.memoryDb.add(doc);
                matrix.players.forEach(p -> p.status = Status.Hu);
            }
        };
        //Matrix.memoryDb=new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.savetodb = true;
        matrix.saveStatusConsumer = callback;
        matrix.createQingYiSeAction();
        int counter = 0;
        long total = 0;
        while (true) {
            counter++;
            total++;
            if (total > 3000000) break;
            if (counter > 1000) {
                counter = 0;
                System.out.println(total);
            }
            matrix.reset();
            matrix.play();
            //if (Matrix.memoryDb.size()>100000) break;
        }
        //player3XiajaoCollection.insertMany(Matrix.memoryDb);
//        System.out.println("playNormalAndRenPao");
//        playNormalAndRenPao();
    }

    @Test
    public void playNormal() {
        AtomicLong counter = new AtomicLong();
        AtomicLong total = new AtomicLong();
        var query1 = new Document("player3_jinE_normal", new BasicDBObject("$exists", false));
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.reset();
        matrix.createQingYiSeAction();
        player3XiajaoCollection.find(query1)
                .forEach(doc -> {
                    var id = doc.get("_id").toString();
                    matrix.loadFromDocument(doc);
                    matrix.resetJinE();
                    matrix.play();
                    Document query = new Document("_id", new ObjectId(id));
                    Bson updates = Updates.set("player3_jinE_normal", matrix.players.get(2).jinE);
                    player3XiajaoCollection.updateOne(query, updates);
                    total.getAndIncrement();
                    if (counter.getAndIncrement() > 1000) {
                        counter.set(0);
                        System.out.println(total.get());
                    }
                });

    }

    @Test
    public void playRenPao() {
        AtomicLong counter = new AtomicLong();
        AtomicLong total = new AtomicLong();
        var query1 = new Document("player3_jinE_renpao", new BasicDBObject("$exists", false));
        //new Document("activePlayerNumber",4);
        //new Document("player3_jinE_normal",new BasicDBObject("$exists",false));
        //query1.put("player3_jinE_normal",new BasicDBObject("$exists",false));
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.reset();
        matrix.createQingYiSeAction();
//        player3XiajaoCollection.find(query1)
//                .forEach(doc -> {
//            var id=doc.get("_id").toString();
//            matrix.loadFromDocument(doc);
//            matrix.resetJinE();
//            matrix.play();
//            Document query = new Document("_id",new ObjectId(id));
//            Bson updates=Updates.set("player3_jinE_normal",matrix.players.get(2).jinE);
//            player3XiajaoCollection.updateOne(query,updates);
//            total.getAndIncrement();
//            if (counter.getAndIncrement()>1000){
//                counter.set(0);
//                System.out.println(total.get());
//            }
//        });

        matrix.players.get(2).dianPaoHuActionList.clear();
        matrix.players.get(2).dianPaoHuActionList.add(new RenPaoAction_Simple());
        player3XiajaoCollection.find(query1)
                .forEach(doc -> {
                    var id = doc.get("_id").toString();
                    matrix.loadFromDocument(doc);
                    matrix.resetJinE();
                    matrix.play();
                    Document query = new Document("_id", new ObjectId(id));
                    Bson updates = Updates.set("player3_jinE_renpao", matrix.players.get(2).jinE);
                    player3XiajaoCollection.updateOne(query, updates);
                    total.getAndIncrement();
                    if (counter.getAndIncrement() > 1000) {
                        counter.set(0);
                        System.out.println(total.get());
                    }
                });

    }

    @Test
    public void genShangBanCangFlag() {
        Document query = new Document("matrix_cards_size",new BasicDBObject("$lt",35));
        Bson updates=Updates.set("ShangBanChang",false);
        player3XiajaoCollection.updateMany(query,updates);

        query = new Document("matrix_cards_size",new BasicDBObject("$gte",35));
        updates=Updates.set("ShangBanChang",true);
        player3XiajaoCollection.updateMany(query,updates);
        //player3_jinE_normal player3_jinE_renpao
        //var query1 = new Document("player3_jinE_normal", new BasicDBObject("$exists", false));
        //query1.put("activePlayerNumber", 4);
        //query1.put("player3_jinE_renpao",new BasicDBObject("$exists",false));
        //System.out.println(player3XiajaoCollection.countDocuments(query1));
//        player3XiajaoCollection.find(query1).limit(1).forEach(document -> {
//            System.out.println(document);
//        });
    }

    /**
     * @see #testRenPao2()
     */
    @Test
    public void genRenPaoYuZiData() {
        AtomicLong counter = new AtomicLong();
        AtomicLong total = new AtomicLong();
        var query1 = new Document("activePlayerNumber", 3);
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.reset();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByXiaJiaoZhangShu renPaoAction = new RenPaoAction_ByXiaJiaoZhangShu();
        p.dianPaoHuActionList.add(renPaoAction);
        player3XiajaoCollection.find(query1).forEach(doc -> {
            for (int n = 1; n < 11; n++) {
                renPaoAction.xjzs_threshold = n;
                var id = doc.get("_id").toString();
                matrix.loadFromDocument(doc);
                matrix.resetJinE();
                matrix.play();
                Document query = new Document("_id", new ObjectId(id));
                Bson updates = Updates.set("renpao_threshold_" + n, matrix.players.get(2).jinE);
                player3XiajaoCollection.updateOne(query, updates);
            }
            total.getAndIncrement();
            if (counter.getAndIncrement() > 1000) {
                counter.set(0);
                System.out.println(total.get());
            }
        });
    }

    @Test
    public void genFan() {
        AtomicLong counter = new AtomicLong();
        AtomicLong total = new AtomicLong();
        var query1 = new Document("activePlayerNumber", 2);
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.reset();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByXiaJiaoZhangShu renPaoAction = new RenPaoAction_ByXiaJiaoZhangShu();
        p.dianPaoHuActionList.add(renPaoAction);
        player3XiajaoCollection.find(query1).forEach(doc -> {
            var id = doc.get("_id").toString();
            matrix.loadFromDocument(doc);
            int fan = p.getMaxFan();
            Document query = new Document("_id", new ObjectId(id));
            Bson updates = Updates.set("player3_fan", fan);
            player3XiajaoCollection.updateOne(query, updates);
            total.getAndIncrement();
            if (counter.getAndIncrement() > 1000) {
                counter.set(0);
                System.out.println(total.get());
            }
        });
    }

    @Test
    public void viewPlayer3XiaoJiao() {
        for (int i = 1; i < 20; i++) {
            System.out.println("下叫张数:" + i);
            long l = player3XiajaoCollection.countDocuments(Filters.eq("player3.xiaJiaoZhangSu", i));
            System.out.println(l);
        }
    }

    //endregion

    @Test
    public void genQys() {
        Matrix.memoryDb = new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.players.get(2).threshold_duanzhang = 5;
        matrix.players.get(2).threshold_mopaicishu = 5;
        matrix.savetodb = true;
        matrix.createQingYiSeAction();
        int counter = 0;
        while (true) {
            counter++;
            if (counter > 300) {
                counter = 0;
                System.out.println(Matrix.memoryDb.size());
            }
            matrix.reset();
            matrix.play();
            if (Matrix.memoryDb.size() > 200000) break;
        }
        database.getCollection("qingyise").insertMany(Matrix.memoryDb);
    }

    /**
     * 清一色阈值2和3的比较
     */
    @Test
    public void twoVsTree() {
        Matrix matrix = new Matrix();
        //matrix.showDetail=true;
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.players.forEach(player -> player.threshold_duanzhang = 3);
        for (int i = 0; i < 100000; i++) {
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
        matrix.resetJinE();
        matrix.players.get(2).threshold_duanzhang = 2;
        for (int i = 0; i < 100000; i++) {
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 测试清一色短牌阈值
     * 结论最佳阈值为3
     */
    @Test
    public void testQingYiSeStrategy() {
        //int n=2;
        Matrix matrix = new Matrix();
        matrix.init();
        Player p = matrix.players.get(2);
        //p.threshold=4;
        p.moPaiActionList.clear();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new QingYiSheMoPaiAction());
        p.pengGangActionList.clear();
        p.pengGangActionList.add(new QingYiSePengGangAction());
        p.dianPaoHuActionList.clear();
        p.dianPaoHuActionList.add(new QingYiSeDianPaoHuAction());
        //p.dianPaoHuActionList.add(new RenPaoAction_ByActivePlayerNumber());
        //p.dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

//        matrix.players.get(0).dianPaoHuActionList.clear();
//        matrix.players.get(0).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(1).dianPaoHuActionList.clear();
//        matrix.players.get(1).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(3).dianPaoHuActionList.clear();
//        matrix.players.get(3).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

        for (int n = 2; n < 4; n++) {
            p.threshold_duanzhang = n;
            System.out.println("阈值:" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 1000000; i++) {
                matrix.reset();
                // TODO: 2022/2/22 检查定缺状况
                matrix.play();
            }
            matrix.printJingE();
        }
    }

    /**
     * 测试清一色 剩余摸牌次数最佳值 4
     */
    //@Test
    public void testQingYiSeStrategy1() {
        Matrix matrix = new Matrix();
        matrix.init();
        Player p = matrix.players.get(2);
        p.moPaiActionList.clear();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new QingYiSheMoPaiAction());
        p.pengGangActionList.clear();
        p.pengGangActionList.add(new QingYiSePengGangAction());
        p.dianPaoHuActionList.clear();
        p.dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

//        matrix.players.get(0).dianPaoHuActionList.clear();
//        matrix.players.get(0).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(1).dianPaoHuActionList.clear();
//        matrix.players.get(1).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(3).dianPaoHuActionList.clear();
//        matrix.players.get(3).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

        for (int n = 3; n < 20; n++) {
            p.threshold_mopaicishu = n;
            System.out.println("摸牌次数阈值:" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
    }

    /**
     * 根据定缺数求最优清一色阈值
     */
    @Test
    public void testQingYiSeStrategy2() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        for (int dingQueSu = 0; dingQueSu < 3; dingQueSu++) {
            System.out.println("定缺数:" + dingQueSu);
            List<Document> docs = qysCollection.find(new Document("dingQueSu", dingQueSu))
                    .limit(50000)
                    .into(new ArrayList<>());

            for (int dzs = 2; dzs < 3; dzs++) {
                System.out.println("短张阈值:" + dzs);
                matrix.players.get(2).threshold_duanzhang = dzs;
                matrix.resetJinE();
                for (Document doc : docs) {
                    matrix.loadFromDocument(doc);
                    matrix.play();
                }
                System.out.println(matrix.players.get(2).jinE);
            }
        }
    }

    // TODO: 2022/3/4 研究短张为3是否碰牌

    /**
     * 短张为3是否碰牌
     */
    @Test
    public void duanZhangPengPai() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        Player player3 = matrix.players.get(2);
        player3.cards = MaJiangDef.stringToCards("5筒,5筒,8筒,1万,1万,2万,3万,3万,5万,7万,8万,8万,9万");
        player3.dingQue = HuaShe.TIAO;
        ActionResult result = player3.pengGang(MaJiangDef.stringToCard("5筒"));
        Assert.assertEquals(ResultCode.Peng, result.code);
    }

    /**
     * 牌型1的打法
     * {@link Matrix#saveStatus(ActionResult)}
     *
     * @see #genPaiXing1()
     * @see com.guci.MaJiangGame.special.LiangDuiDianPaoHuAction#go(int, Player)
     */
    @Test
    public void testPaiXing1() {
        List<Document> paiXing1Docs = paiXing1Collection.find(new Document()).into(new ArrayList<>());
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        for (Document doc : paiXing1Docs) {
            matrix.loadFromDocument(doc);
            matrix.play();
        }
        matrix.printJingE();
    }
}
