package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
import com.guci.MaJiangGame.special.LiangDuiDianPaoHuAction;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;

public class TestMatrix {
    static MongoCollection<Document> collection;
    static MongoCollection<Document>  dataForBasicAction;
    static MongoCollection<Document> xiaJiaoZhangSuCollection;
    @BeforeClass
    public static void setup(){
        HuUtil.load();
        AIUtil.load();
        String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("majiang");
        collection = database.getCollection("test");
        dataForBasicAction=database.getCollection("dataForBasicAction");
        xiaJiaoZhangSuCollection=database.getCollection("xiaJiaoZhangSu");
    }

    @Test public void test(){
        Document query=new Document("_id",new ObjectId("6214a575ac4d7b13e021adb2"));
//        List<Document> docs=new ArrayList<>();
//        docs.add(new Document("ddd","dff"));
//        dataForBasicAction.find(query).into(docs);
//        for(Document doc:docs){
//            System.out.println(doc);
//        }
        Document doc= dataForBasicAction
                .find(eq("_id",new ObjectId("6214a575ac4d7b13e021adb2")))
                .first();
        System.out.println(doc);
    }

    @Test
    public void testLoadFromDatabase(){
        Document query=new Document("player3_xjzs",1);
        FindIterable<Document> findIterable=xiaJiaoZhangSuCollection.find(query).limit(1);
        AtomicReference<String> gameId=new AtomicReference<>();
        findIterable.forEach(document -> {
            System.out.println(document);
            gameId.set(document.getString("gameId"));
        });
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.collection=dataForBasicAction;
        boolean b=matrix.loadFromDatabase(gameId.get(),1);
        //if (!b) System.out.println("??????????????????");
        matrix.play();
        matrix.print();
    }

    @Test
    public void testLoadStatus(){
        String docId="6214a575ac4d7b13e021adb2";
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.collection=dataForBasicAction;
        matrix.loadFromDatabase(docId);
        matrix.print();
        System.out.println("------------------------------");
        matrix.play();
        matrix.print();
    }

    @Test
    public void testReset(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        Assert.assertTrue(matrix.players.size()==4);
        for(Player p : matrix.players){
            Assert.assertTrue(p.cards.size()==13);
            System.out.println(p);
        }
        Assert.assertTrue(matrix.cards.size()==3*4*9-4*13);
    }

    @Test
    public void testMoPai(){
        Player player=new Player();
        player.moPaiActionList.add(new BasicMoPaiAction());
        player.status=Status.Playing;
        String init = "2???,6???,3???,5???";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="3???";
        int pai=MaJiangDef.stringToCard(spai);
        ActionResult result=player.mopai(pai);
        int out=result.out;
        String sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("6???",sout);

        init = "2???,6???,3???,5???";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="3???";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).out;
        sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("5???",sout);

        //??????
        init = "2???,3???,3???,3???";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="1???";
        pai=MaJiangDef.stringToCard(spai);
        result=player.mopai(pai);
        Assert.assertEquals(ResultCode.ZiMo,result.code);
        Assert.assertEquals("1???",MaJiangDef.cardToString(result.out));
        Assert.assertTrue(result.to==player);
        Assert.assertEquals(Status.Hu,player.status);

        //???????????????
        spai="1???";
        pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.NoAction,player.mopai(pai).code);
    }

    @Test
    public void testMopai1(){
        Player player=new Player();
        player.moPaiActionList.add(new DingQueMoPaiAction());
        player.moPaiActionList.add(new IsolatingMoPaiAction());
        player.moPaiActionList.add(new BasicMoPaiAction());
        player.status=Status.Playing;
        player.dingQue =HuaShe.TIAO;
        String init = "4???,8???,5???,6???,6???,8???,9???,1???,1???,2???,4???,5???,9???";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="7???";
        int pai=MaJiangDef.stringToCard(spai);
        int out=player.mopai(pai).out;
        String sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("1???",sout);

        init = "4???,4???,5???,6???";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="7???";
        pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.ZiMo,player.mopai(pai).code);
        Assert.assertEquals(Status.Hu,player.status);

        //????????????????????????
        init = "2???,3???,5???,5???";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        player.status=Status.Playing;
        spai="2???";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).out;
        sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("2???,3???,5???,5??? ?????? 2??? ????????????","2???",sout);

        //?????????????????????  4???,4???,5???,6???,8???,8???,6???,7???,8???,9???,9???,3???,5???,   7???
        //3???,5???,6???,7???,8???,2???,2???,4???,5???,6???,6???,7???,9???  5???
        init = "3???,5???,6???,7???,8???,6???,7???,9???";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        player.status=Status.Playing;
        spai="5???";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).out;
        sout=MaJiangDef.cardToString(out);
        System.out.println(sout);
        Collections.sort(player.cards);
        System.out.println(MaJiangDef.cardsToString(player.cards));
        //Assert.assertEquals("4???,4???,5???,6???,8???,8???,6???,7???,8???,9???,9???,3???,5??? ?????? 7??? ????????????","3???",sout);
    }

    @Test
    public void testHuPai(){
        Player player=new Player();
        player.dingQue =HuaShe.TIAO;
        player.dianPaoHuActionList.add(new BasicDianPaoHuAction());
        player.status=Status.Playing;
        String init = "2???,3???,3???,3???";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="1???";
        int pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.DianPaoHu,player.dianPaoHu(pai).code);

        spai="6???";
        pai=MaJiangDef.stringToCard(spai);
        ActionResult r=player.dianPaoHu(pai);
        Assert.assertEquals(ResultCode.NoAction,r.code);
    }

    @Test
    public void testPeng(){
        Player player=new Player();
        player.dingQue =HuaShe.TIAO;
        player.pengGangActionList.add(new BasicPengGangAction());
        player.status=Status.Playing;
        String init = "1???,1???,2???,2???,3???,4???,9???";
        player.cards=MaJiangDef.stringToCards(init);
        String spai="1???";
        int pai=MaJiangDef.stringToCard(spai);
        ActionResult r=player.pengGang(pai);
        Assert.assertEquals(3,player.cardsOnTable.size());
        Assert.assertEquals(4,player.cards.size());
        int n=Collections.frequency(player.cardsOnTable,(Integer)MaJiangDef.WAN1);
        Assert.assertEquals(3,n);
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void testMingGang(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="9???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        spai="1???,1???,1???";
        matrix.players.get(0).cardsOnTable=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        spai="1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        spai="1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        spai="1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;

        matrix.currentPlayer=matrix.players.get(0);
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.TIAO1);
        matrix.cards.add(MaJiangDef.WAN9);
        matrix.step();
        matrix.step();

        Assert.assertEquals(4,
                Collections.frequency(matrix.players.get(0).cardsOnTable,(Integer)MaJiangDef.TIAO1));
        Assert.assertEquals(1,
                Collections.frequency(matrix.cardsOnTable,(Integer)MaJiangDef.WAN9));
    }

    @Test
    public void testPengGang(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="1???";//"1???,1???,1???,3???"
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        spai="1???,1???,1???,3???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TONG;
        spai="1???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.WAN;
        spai="1???";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.WAN;

        matrix.cards.clear();
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.cards.add(MaJiangDef.stringToCard("9???"));
        matrix.currentPlayer=matrix.players.get(0);
        matrix.step();

        Assert.assertEquals(4,matrix.players.get(1).cardsOnTable.size());
    }

    /**
     * ????????????????????????
     */
    @Test
    public void testMingGang1(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="9???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        spai="1???,1???,1???";
        matrix.players.get(0).cardsOnTable=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        spai="1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        spai="1???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.TIAO;
        spai="1???";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.TIAO;

        matrix.currentPlayer=matrix.players.get(0);
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.TIAO1);
        matrix.cards.add(MaJiangDef.TIAO9);
        matrix.step();
        matrix.step();

        Assert.assertEquals(4,
                Collections.frequency(matrix.players.get(0).cardsOnTable,(Integer)MaJiangDef.TIAO1));
        Assert.assertEquals(1,
                Collections.frequency(matrix.cardsOnTable,(Integer)MaJiangDef.WAN9));
    }

    @Test
    public void testStep(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="1???,3???,9???,9???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        spai="1???,3???,9???,9???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        spai="1???,3???,9???,9???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.WAN;
        spai="1???,4???,7???,9???";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.WAN;
        matrix.currentPlayer=matrix.players.get(0);

        //??????
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.TIAO2);
        matrix.step();
        Assert.assertEquals(Status.Hu,matrix.players.get(0).status);
        Assert.assertEquals(Status.Playing,matrix.players.get(1).status);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(1));

        //??????
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.TONG2);
        matrix.step();
        Assert.assertEquals(Status.Playing,matrix.players.get(1).status);
        Assert.assertEquals(Status.Hu,matrix.players.get(2).status);
    }

    @Test
    public void testDianPao(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="1???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        matrix.players.get(1).cards=new ArrayList<>();
        matrix.players.get(1).cards.add(MaJiangDef.WAN2);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        matrix.players.get(2).cards=new ArrayList<>();
        matrix.players.get(2).cards.add(MaJiangDef.WAN2);
        matrix.players.get(2).dingQue =HuaShe.TIAO;
        matrix.players.get(3).cards=new ArrayList<>();
        matrix.players.get(3).cards.add(MaJiangDef.WAN3);
        matrix.players.get(3).dingQue =HuaShe.TIAO;
        matrix.currentPlayer=matrix.players.get(0);

        //????????????
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.WAN2);
        matrix.step();
        Assert.assertEquals(Status.Playing,matrix.players.get(0).status);
        Assert.assertEquals(Status.Hu,matrix.players.get(1).status);
        Assert.assertEquals(Status.Hu,matrix.players.get(2).status);
        Assert.assertEquals(Status.Playing,matrix.players.get(3).status);
        Assert.assertEquals(1,matrix.cardsOnTable.size());
        Assert.assertTrue(matrix.cardsOnTable.contains(MaJiangDef.WAN2));
    }

    @Test
    public void testPeng1(){
        Matrix matrix = createMatrix();
        String spai="1???,1???,2???,2???,3???,4???,9???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.cards.add(0,MaJiangDef.WAN1);
        matrix.currentPlayer=matrix.players.get(1);
        matrix.step();
        Assert.assertEquals(3,matrix.players.get(0).cardsOnTable.size());
        Assert.assertEquals(1,matrix.cardsOnTable.size());
        Assert.assertTrue(matrix.cardsOnTable.contains(MaJiangDef.stringToCard("9???")));
    }

    private Matrix createMatrix() {
        Matrix matrix=new Matrix();
        matrix.showDetail=true;
        matrix.init();
        matrix.reset();
        String spai="1???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.TIAO;
        spai="1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.WAN;
        matrix.players.get(2).cards=new ArrayList<>();
        matrix.players.get(2).cards.add(MaJiangDef.WAN2);
        matrix.players.get(2).dingQue =HuaShe.TONG;
        matrix.players.get(3).cards=new ArrayList<>();
        matrix.players.get(3).cards.add(MaJiangDef.WAN3);
        matrix.players.get(3).dingQue =HuaShe.TONG;
        matrix.currentPlayer=matrix.players.get(0);

        matrix.cards=new ArrayList<>();
        return matrix;
    }

    /**
     * ??????????????????
     */
    @Test
    public void testPeng2(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="1???,1???,3???,3???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;
        //???1??? ??????1???
        spai="1???,1???,3???,3???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        //???1??? ??????3???
        spai="9???,9???,3???,3???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.WAN;
        //???3??? ??????9???
        spai="9???,9???,3???,3???";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.TIAO;
        //???9???  ??????3???
        matrix.currentPlayer=matrix.players.get(0);
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.step();

        Assert.assertEquals(3,Collections.frequency(matrix.players.get(1).cardsOnTable,
                MaJiangDef.stringToCard("1???")));
        Assert.assertEquals(3,Collections.frequency(matrix.players.get(2).cardsOnTable,
                MaJiangDef.stringToCard("3???")));
        Assert.assertEquals(3,Collections.frequency(matrix.players.get(3).cardsOnTable,
                MaJiangDef.stringToCard("9???")));
        Assert.assertEquals(1,matrix.cardsOnTable.size());
        Assert.assertTrue(matrix.cardsOnTable.contains((Integer) MaJiangDef.stringToCard("3???")));
    }

    /**
     * ?????????????????????
     */
    @Test
    public void testPengHu(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        //???1??? ??????1???
        String spai="1???,1???,3???,3???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.WAN;

        //???1??? ??????3???,?????????3???
        spai="1???,1???,3???,3???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;

        spai="1???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.TIAO;

        spai="1???";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.TIAO;

        matrix.currentPlayer=matrix.players.get(0);
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.step();

        Assert.assertEquals(3,Collections.frequency(matrix.players.get(1).cardsOnTable,
                MaJiangDef.stringToCard("1???")));
        Assert.assertEquals(Status.Hu,matrix.players.get(0).status);
    }


    @Test
    public void testPlay(){
        Matrix matrix=new Matrix();

        //matrix.showDetail=true;
        matrix.init();
        matrix.reset();
        System.out.println("??????????????????");
        matrix.print();
        matrix.play();
        System.out.println("??????????????????");
        matrix.print();
    }

    @Test
    public void testPlayQingYiSe(){
        Matrix matrix=new Matrix();
        //matrix.showDetail=true;
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        System.out.println("??????????????????");
        matrix.print();
        matrix.play();
        System.out.println("??????????????????");
        matrix.print();
        //matrix.printJingE();
    }

    //@Test
    public void genDatabase(){
        int total=0;
        Matrix matrix=new Matrix();
        matrix.savetodb=true;
        matrix.init();
        matrix.collection=dataForBasicAction;

        matrix.createQingYiSeAction();
//        for (Player p: matrix.players){
//            p.dianPaoHuActionList.clear();
//            p.dianPaoHuActionList.add(new RenPaoAction_ByActivePlayerNumber());
//        }
        int couter=0;
        for(int i=0;i<1000000;i++){
            couter++;
            if (couter>300){
                System.out.println("??????:"+i);
                couter=0;
            }
            matrix.reset();
            matrix.play();
            for (Player p:matrix.players){
                if (p.status==Status.Hu) total++;
            }
        }
        System.out.println(total);
        //matrix.printJingE();
    }

    @Test
    public void testAnGang(){
        String spai="2???,2???,4???,4???,7???,8???,1???,1???,1???,2???,5???,5???,5???";
        Player p=new Player();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new BasicMoPaiAction());
        p.status=Status.Playing;
        p.dingQue =HuaShe.TIAO;
        p.cards=MaJiangDef.stringToCards(spai);
        p.cardsOnTable=new ArrayList<>();
        p.mopai(MaJiangDef.stringToCard("5???"));
        Assert.assertEquals(4,Collections.frequency(p.cardsOnTable,MaJiangDef.stringToCard("5???")));
    }

    // TODO: 2022/2/13 ?????????
    @Test
    public void testGangShangHua(){

    }

    // TODO: 2022/2/13 ?????????
    @Test
    public void testGangShangPao(){

    }

    @Test
    public void testSettle_Hu(){
        Matrix matrix=createSimpleMatrix();
        String spai="1???,1???,1???,1???,2???,3???,5???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue=HuaShe.WAN;
        spai="2???";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue=HuaShe.WAN;
        matrix.cards.add(MaJiangDef.stringToCard("5???"));
        matrix.cards.add(MaJiangDef.stringToCard("2???"));
        matrix.step();

        Assert.assertEquals(Status.Hu,matrix.players.get(0).status);
        Assert.assertEquals(24,matrix.players.get(0).jinE);
        Assert.assertEquals(-8,matrix.players.get(1).jinE);
        Assert.assertEquals(-8,matrix.players.get(2).jinE);
        Assert.assertEquals(-8,matrix.players.get(3).jinE);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(1));

        matrix.step();

        Assert.assertEquals(Status.Hu,matrix.players.get(2).status);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(2));
        Assert.assertEquals(-24,matrix.players.get(1).jinE);
        Assert.assertEquals(8,matrix.players.get(2).jinE);
        //System.out.println(matrix.currentPlayer.id);
    }

    @Test
    public void testSettle_AnGang(){
        Matrix matrix=createSimpleMatrix();
        String spai="1???,1???,1???,1???,2???,3???,5???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue=HuaShe.WAN;
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.step();

        Assert.assertEquals(6,matrix.players.get(0).jinE);
        Assert.assertEquals(-2,matrix.players.get(1).jinE);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(0));

    }

    @Test
    public void testSettle_MingGang(){
        Matrix matrix=createSimpleMatrix();
        String spai="1???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        for (int i=0;i<3;i++){
            matrix.players.get(0).cardsOnTable.add(MaJiangDef.stringToCard("1???"));
        }
        matrix.players.get(0).dingQue=HuaShe.WAN;
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.step();

        Assert.assertEquals(3,matrix.players.get(0).jinE);
        Assert.assertEquals(-1,matrix.players.get(1).jinE);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(0));

    }

    @Test
    public void testSettle_PengGang(){
        Matrix matrix=createSimpleMatrix();
        String spai="1???,1???,1???,1???";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue=HuaShe.WAN;
        matrix.cards.add(MaJiangDef.stringToCard("1???"));
        matrix.cards.add(MaJiangDef.stringToCard("2???"));
        matrix.step();

        Assert.assertEquals(4,matrix.players.get(1).cardsOnTable.size());
        Assert.assertEquals(4,matrix.players.get(1).jinE);
        Assert.assertEquals(-2,matrix.players.get(0).jinE);
        Assert.assertEquals(-1,matrix.players.get(2).jinE);
        Assert.assertEquals(-1,matrix.players.get(3).jinE);
    }

    private Matrix createSimpleMatrix() {
        Matrix matrix=new Matrix();
        matrix.showDetail=true;
        matrix.init();
        matrix.reset();
        String spai="1???";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).dingQue =HuaShe.TIAO;
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).dingQue =HuaShe.TIAO;
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).dingQue =HuaShe.TIAO;
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).dingQue =HuaShe.TIAO;
        matrix.currentPlayer=matrix.players.get(0);

        matrix.cards=new ArrayList<>();
        return matrix;
    }

    @Test
    public void testQingYiSheMoPaiAction(){
        Player p=new Player();
        p.dingQue=HuaShe.WAN;
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new QingYiSheMoPaiAction());
        p.cardsOnTable=MaJiangDef.stringToCards("1???,1???,1???,2???,2???,2???");
        p.cards=MaJiangDef.stringToCards("5???,2???,3???,5???"); //1???,2???,3???,5???
        ActionResult result= p.mopai(MaJiangDef.stringToCard("7???"));
        Assert.assertEquals(MaJiangDef.TYPE_TONG,MaJiangDef.type(result.out));

        p.cards=MaJiangDef.stringToCards("5???,2???,3???,5???");
        result= p.mopai(MaJiangDef.stringToCard("9???"));
        Assert.assertEquals(MaJiangDef.TYPE_TONG,MaJiangDef.type(result.out));

        p.cards=MaJiangDef.stringToCards("9???,2???,3???,5???");
        result= p.mopai(MaJiangDef.stringToCard("5???"));
        Assert.assertEquals(MaJiangDef.TYPE_TONG,MaJiangDef.type(result.out));
    }

    @Test
    public void testJiFan(){
        Player p=new Player();
        p.cards=MaJiangDef.stringToCards("1???,1???.1???,2???");
        p.cardsOnTable=MaJiangDef.stringToCards("9???,9???,9???,9???");
        JiZhang jiZhang=new JiZhang();
        int fan=jiZhang.jiFan(p,MaJiangDef.stringToCard("3???"));
        Assert.assertEquals(3,fan);
    }

    // TODO: 2022/2/28 ?????????
    @Test
    public void testHuanSanZhang(){
        Player p=new Player();
        p.cards=MaJiangDef.stringToCards("2???,3???,4???,6???,3???,3???,6???,7???,7???,8???,8???,4???,6???");
        List<Integer> sanZhang=p.huanSanZhang();
        System.out.println(MaJiangDef.cardsToString(sanZhang));
    }

    @Test
    public void testGetDuanZhang(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        Player player3=matrix.players.get(2);
        player3.cards= MaJiangDef.stringToCards("5???,5???,8???,3???,1???,1???,2???,3???,3???,5???,7???,8???,8???,9???");
        player3.dingQue=HuaShe.TIAO;
        List<Integer> duanZhang= player3.getDuanZhang();
        System.out.println(MaJiangDef.cardsToString(duanZhang).contains("3???"));
    }

    //@Test
    public void testLiangDuiDianPaoHuAction(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        matrix.reset();
        Player player2=matrix.players.get(1);
        player2.cards=MaJiangDef.stringToCards("1???");
        player2.dingQue=HuaShe.WAN;

        Player player3=matrix.players.get(2);
        player3.dianPaoHuActionList.add(0,new LiangDuiDianPaoHuAction());
        player3.cards=MaJiangDef.stringToCards("1???,1???,1???,1???");
        player3.dingQue=HuaShe.TIAO;

        matrix.cards.add(0,MaJiangDef.stringToCard("1???"));
        matrix.currentPlayer=player2;
        matrix.step();
        System.out.println(MaJiangDef.cardsToString(player3.cards));
    }
}
