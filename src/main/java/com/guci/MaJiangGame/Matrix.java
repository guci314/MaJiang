package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.google.gson.Gson;
import com.guci.MaJiangGame.QingYiSe.QingYiSeDianPaoHuAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSePengGangAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.bson.types.ObjectId;

import java.util.*;

public class Matrix {
    public static Gson gson=new Gson();
    public boolean showDetail = false;
    public List<Player> players = new ArrayList<>();
    public JiZhang jiZhang;
    List<Document> statusHistory=new ArrayList<>();
    public MongoCollection<Document> collection;

    UUID gameId;

    /**
     * 未显的牌
     */
    public List<Integer> cards;
    /**
     * 已显的牌
     */
    public List<Integer> cardsOnTable;
    /**
     * 当前玩家
     */
    public Player currentPlayer;

    /**
     * 初始化
     */
    public void init() {
        gameId=UUID.randomUUID();
        jiZhang = new JiZhang();
        jiZhang.matrix = this;
        Player p1 = new Player();
        p1.id = 1;
        addBasicAction(p1);

        Player p2 = new Player();
        p2.id = 2;
        addBasicAction(p2);

        Player p3 = new Player();
        p3.id = 3;
        addBasicAction(p3);

        Player p4 = new Player();
        p4.id = 4;
        addBasicAction(p4);

        p1.nextPlayer = p2;
        p2.nextPlayer = p3;
        p3.nextPlayer = p4;
        p4.nextPlayer = p1;

    }

    private void addBasicAction(Player p) {
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new BasicMoPaiAction());
        p.dianPaoHuActionList.add(new BasicDianPaoHuAction());
        p.pengGangActionList.add(new BasicPengGangAction());
        p.matrix = this;
        players.add(p);
    }

    public void createQingYiSeAction() {
        for (Player p : players) {
            p.moPaiActionList.clear();
            p.moPaiActionList.add(new DingQueMoPaiAction());
            p.moPaiActionList.add(new IsolatingMoPaiAction());
            p.moPaiActionList.add(new QingYiSheMoPaiAction());
            p.dianPaoHuActionList.clear();
            p.dianPaoHuActionList.add(new QingYiSeDianPaoHuAction());
            p.pengGangActionList.clear();
            p.pengGangActionList.add(new QingYiSePengGangAction());
        }
    }

    /**
     * 重置
     */
    public void reset() {
        gameId=UUID.randomUUID();
        statusHistory.clear();
        cardsOnTable = new ArrayList<>();
        cards = new ArrayList<>();
        for (int i = MaJiangDef.WAN1; i <= MaJiangDef.TIAO9; i++) {
            cards.add(i);
            cards.add(i);
            cards.add(i);
            cards.add(i);
        }
        Collections.shuffle(cards);
        for (Player p : players) {
            p.status = Status.Playing;
            //p.jinE=0;
            ArrayList<Integer> c = new ArrayList<>();
            for (int i = 0; i < 13; i++) {
                c.add(cards.remove(0));
            }
            //Collections.sort(c);
            p.cards = c;
            p.cardsOnTable.clear();
            //p.anGangCards.clear();
            //定缺
            long numOfWan = p.cards.stream().filter(x -> x >= MaJiangDef.WAN1 && x <= MaJiangDef.WAN9).count();
            long numOfTiao = p.cards.stream().filter(x -> x >= MaJiangDef.TIAO1 && x <= MaJiangDef.TIAO9).count();
            long numOfTong = p.cards.stream().filter(x -> x >= MaJiangDef.TONG1 && x <= MaJiangDef.TONG9).count();
            if (numOfTong <= numOfTiao && numOfTong <= numOfWan) p.dingQue = HuaShe.TONG;
            if (numOfTiao <= numOfTong && numOfTiao <= numOfWan) p.dingQue = HuaShe.TIAO;
            if (numOfWan <= numOfTiao && numOfWan <= numOfTong) p.dingQue = HuaShe.WAN;
        }
    }

    /**
     * 游戏结束
     *
     * @return
     */
    public boolean gameover() {
        if (cards.isEmpty()) return true;
        return players.stream().filter(p -> p.status == Status.Playing).count() < 2;
    }

    public void saveStatus(ActionResult action){
        if (action.code==ResultCode.NoAction) return;
        Document document=new Document();//.append("_id", );
        document.put("gameId",gameId.toString());
        document.put("matrix_cards",MaJiangDef.cardsToString(cards));
        document.put("matrix_cardsOnTable",MaJiangDef.cardsToString(cardsOnTable));
        for (Player p:players){
            JsonObject object=new JsonObject(p.toString());
            document.put("player"+p.id,object);
        }
        JsonObject jo=new JsonObject(action.toString());
        document.put("action",jo);
        document.put("activePlayerNumber",players.stream().filter(p->p.status== Status.Playing).count());
        statusHistory.add(document);
    }

    /**
     * 运行一步
     */
    public void step() {
        // TODO: 2022/2/14 重构step使其具有可测试性
        int pai = cards.remove(0);
        if (showDetail) {
            System.out.println("----------------------------------------------------------------");
            System.out.println(currentPlayer);
            System.out.println("摸到的牌:" + MaJiangDef.cardToString(pai));
            double n = AIUtil.calc(currentPlayer.cards, new ArrayList<>());
            System.out.println("牌面价值" + n);
        }
        ActionResult out = currentPlayer.mopai(pai);
        saveStatus(out);
        if (showDetail) {
            //if (out.code==ResultCode.ChuPai) System.out.println("打出的牌:"+MaJiangDef.cardToString(out.value));
            System.out.println("返回代码:" + out.code);
            if (out.out != null) System.out.println("打出的牌:" + MaJiangDef.cardToString(out.out));
            System.out.println(currentPlayer);
            System.out.println("手上的牌张数:" + currentPlayer.cards.size());
            double n = AIUtil.calc(currentPlayer.cards, new ArrayList<>());
            System.out.println("牌面价值" + n);
        }

        //此玩家已经胡牌
        if (out.code == ResultCode.NoAction) {
            cards.add(0, pai);
            currentPlayer = currentPlayer.nextPlayer;
        } else if (out.code == ResultCode.ZiMo) {
            //自摸
            out.from = this;
            out.to = currentPlayer;
            cardsOnTable.add(out.out);
            currentPlayer.cards.remove((Integer) out.out);
            if (showDetail) {
                System.out.println("自摸 player" + currentPlayer.id + " 牌:" + MaJiangDef.cardToString(pai));
            }
            settle(out);
            currentPlayer = currentPlayer.nextPlayer;

        } else if (out.code == ResultCode.MingGang) {
            if (showDetail) {
                System.out.println("明杠 player" + currentPlayer.id + " 牌:" + MaJiangDef.cardToString(pai));
            }
            settle(out);
            return;
        } else if (out.code == ResultCode.AnGang) {
            if (showDetail) {
                System.out.println("暗杠 player" + currentPlayer.id + " 牌:" + MaJiangDef.cardToString(pai));
            }
            settle(out);
            return;
        }
        //出牌
        else {
            while (true) {
                ActionResult r = otherPlayerLookCard(out);
                out = r;
                if (r.code == ResultCode.NoAction) {
                    cardsOnTable.add(out.out);
                    break;
                }else {

                }
            }
            currentPlayer = currentPlayer.nextPlayer;
        }


    }

    /**
     * 其它玩家看牌
     *
     * @param out
     */
    private ActionResult otherPlayerLookCard(ActionResult out) {
        //是否有人胡牌
        for (Player p : players) {
            if (p != currentPlayer) {
                ActionResult r = p.dianPaoHu(out.out);
                if (r.code == ResultCode.DianPaoHu) {
                    if (showDetail) {
                        System.out.println("-----------------------------------------------------------");
                        System.out.println("点炮胡 from player" + currentPlayer.id + " to player" + p.id + " 牌" + MaJiangDef.cardToString(out.out));
                    }
                    r.from = currentPlayer;
                    r.to = p;
                    settle(r);
                    saveStatus(r);
                    return out;
                }
            }
        }
        //是否有人碰杠牌
        return pengGang(out);

        //cardsOnTable.add(out.value);
        //currentPlayer=currentPlayer.nextPlayer;
    }


    /**
     * 其它玩家碰杠牌
     *
     * @param out
     */
    private ActionResult pengGang(ActionResult out) {
        for (Player p : players) {
            if (p != currentPlayer) {
                ActionResult r = p.pengGang(out.out);
                if (r.code == ResultCode.Peng || r.code == ResultCode.PengGang) {
                    if (showDetail) {
                        if (r.code == ResultCode.Peng) {
                            System.out.println("----------------------------------------------------------------");
                            System.out.println("碰 player" + p.id + " from player" + currentPlayer.id + " 碰牌:"
                                    + MaJiangDef.cardToString(out.out)
                                    + "  打出的牌" + MaJiangDef.cardToString(r.out));
                            System.out.println(p);
                        }
                        if (r.code == ResultCode.PengGang) {
                            System.out.println("----------------------------------------------------------------");
                            System.out.println("碰杠 player" + p.id + " from player" + currentPlayer.id + " 碰杠牌:"
                                    + MaJiangDef.cardToString(out.out)
                                    + "  打出的牌" + MaJiangDef.cardToString(r.out));
                        }
                    }
                    //out.value = r.value;
                    //out.from = p;
                    //somebodyPengGang = true;
                    //if (r.code==ResultCode.PengGang) settle(out);
                    currentPlayer = p;
                    saveStatus(r);
                    return r;
                }
            }
        }
        return new ActionResult(ResultCode.NoAction, out.out);
    }

    /**
     * 玩一局游戏
     */
    public void play() {
        currentPlayer = players.get(0);
        saveStatus(new ActionResult(ResultCode.Init,null));
        while (!gameover()) {
            step();
        }
        saveToDatabase();
    }

    public void saveToDatabase(){
        if (collection != null){
            try {
                collection.insertMany(statusHistory);
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }
//        String uri = "mongodb://localhost:27017";
//        try (MongoClient mongoClient = MongoClients.create(uri)) {
//            MongoDatabase database = mongoClient.getDatabase("majiang");
//            MongoCollection<Document> collection = database.getCollection("test");
//            try {
//                collection.insertMany(statusHistory);
//            } catch (MongoException me) {
//                System.err.println("Unable to insert due to an error: " + me);
//            }
//        }
    }

    /**
     * 结账
     */
    void settle(ActionResult result) {
        jiZhang.settle(result);
    }

    public void print() {
        for (Player p : players) {
            System.out.println(p);
        }
    }

    public void printJingE() {
        for (Player p : players) {
            System.out.println(String.format("Player%d %d", p.id, p.jinE));
        }
    }
}
