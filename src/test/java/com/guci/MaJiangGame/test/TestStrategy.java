package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;
import com.guci.MaJiangGame.QingYiSe.QingYiSeDianPaoHuAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSePengGangAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByActivePlayerNumber;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByCardsNumber;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByKeGangPai;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByXiaJiaoZhangShu;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TestStrategy {
    static MongoCollection<Document> dataForBasicAction;
    static MongoCollection<Document> xiaJiaoZhangSuCollection;
    static MongoDatabase database;
    static MongoCollection<Document> qysCollection;
    @BeforeClass
    public static void setup() {
        HuUtil.load();
        AIUtil.load();
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("majiang");
        dataForBasicAction=database.getCollection("dataForBasicAction");
        xiaJiaoZhangSuCollection=database.getCollection("xiaJiaoZhangSu");
        qysCollection=database.getCollection("qingyise");
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
    public void testRenPao() {
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
    //@Test
    public void testRenPao1() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p = matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByCardsNumber rp = new RenPaoAction_ByCardsNumber();
        p.dianPaoHuActionList.add(rp);
        for (int n = 26; n < 29; n++) {
            rp.threshold_cards_number = n;
            System.out.println("剩余张数阈值=" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
        //matrix.print();
    }

    /**
     * 测试忍炮  根据胡牌张数
     * 6是最优阈值
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
            //rp.threshold=n;
            System.out.println("胡牌张数阈值=" + n);
            for (Player p1 : matrix.players) {
                p1.jinE = 0;
            }
            for (int i = 0; i < 10000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
            //matrix.print();
        }
    }

    @Test
    public void testRenPao_xjzs1(){
        for (int i=1;i<10;i++) {
            int xjzs_threshold = i;
            System.out.println("胡牌张数阈值:"+xjzs_threshold);
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
    public void testRenPao_xjzs(){
        for (int x=1;x<2;x++) {
            int xjzs = x;
            System.out.println("张数阈值:"+x);
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
                matrix.loadFromDatabase(id,1);
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
                matrix.loadFromDatabase(id,1);
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
        for(int i=1;i<10;i++) {
            System.out.println("胡牌张数值:"+i);
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
            float jingE1=matrix.players.get(2).jinE;
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
            float jingE2=matrix.players.get(2).jinE;
            System.out.println("提升率:"+(jingE2-jingE1)/jingE1);
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

    @Test
    public void genPaiXing1() {
        Matrix.memoryDb=new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.savetodb=true;
        matrix.createQingYiSeAction();
        while (true){
            matrix.reset();
            matrix.play();
            if (Matrix.memoryDb.size()>10000) break;
        }
        database.getCollection("paixing1").insertMany(Matrix.memoryDb);
    }

    @Test
    public void genQys() {
        Matrix.memoryDb=new ArrayList<>();
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.players.get(2).threshold_duanzhang=5;
        matrix.players.get(2).threshold_mopaicishu=5;
        matrix.savetodb=true;
        matrix.createQingYiSeAction();
        int counter=0;
        while (true){
            counter++;
            if (counter>300){
                counter=0;
                System.out.println(Matrix.memoryDb.size());
            }
            matrix.reset();
            matrix.play();
            if (Matrix.memoryDb.size()>200000) break;
        }
        database.getCollection("qingyise").insertMany(Matrix.memoryDb);
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

    @Test
    public void testQingYiSeStrategy2() {
        Matrix matrix = new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        for (int dingQueSu = 3; dingQueSu < 4; dingQueSu++) {
            System.out.println("定缺数:"+dingQueSu);
            List<Document> docs = qysCollection.find(new Document("dingQueSu", dingQueSu))
                    .limit(50000).into(new ArrayList<>());

            for(int dzs=4;dzs<9;dzs++) {
                System.out.println("短张数:"+dzs);
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
}
