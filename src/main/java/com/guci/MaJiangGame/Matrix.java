package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Matrix {
    public boolean showDetail=false;
    public List<Player> players=new ArrayList<>();
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
    public void init(){
        Player p1=new Player();
        p1.id=1;
        addBasicAction(p1);

        Player p2=new Player();
        p2.id=2;
        addBasicAction(p2);

        Player p3=new Player();
        p3.id=3;
        addBasicAction(p3);

        Player p4=new Player();
        p4.id=4;
        addBasicAction(p4);

        p1.nextPlayer=p2;
        p2.nextPlayer=p3;
        p3.nextPlayer=p4;
        p4.nextPlayer=p1;
    }

    private void addBasicAction(Player p) {
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new BasicMoPaiAction());
        p.dianPaoHuActionList.add(new BasicDianPaoHuAction());
        p.pengGangActionList.add(new BasicPengGangAction());
        p.matrix=this;
        players.add(p);
    }

    /**
     * 重置
     */
    public void reset(){
        cardsOnTable =new ArrayList<>();
        cards=new ArrayList<>();
        for (int i = MaJiangDef.WAN1; i <= MaJiangDef.TIAO9; i++)
        {
            cards.add(i);
            cards.add(i);
            cards.add(i);
            cards.add(i);
        }
        Collections.shuffle(cards);
        for(Player p:players){
            p.status=Status.Playing;
            ArrayList<Integer> c = new ArrayList<>();
            for (int i = 0; i < 13; i++)
            {
                c.add(cards.remove(0));
            }
            //Collections.sort(c);
            p.cards=c;
            p.cardsOnTable=new ArrayList<>();
            //定缺
            long numOfWan=p.cards.stream().filter(x->x>=MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
            long numOfTiao=p.cards.stream().filter(x->x>=MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
            long numOfTong=p.cards.stream().filter(x->x>=MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
            if (numOfTong<=numOfTiao && numOfTong<=numOfWan) p.DingQue=HuaShe.TONG;
            if (numOfTiao<=numOfTong && numOfTiao<=numOfWan) p.DingQue=HuaShe.TIAO;
            if (numOfWan<=numOfTiao && numOfWan<=numOfTong) p.DingQue=HuaShe.WAN;
        }
    }

    /**
     * 游戏结束
     * @return
     */
    public boolean gameover(){
        if (cards.isEmpty()) return true;
        return players.stream().filter(p->p.status==Status.Playing).count()<2;
    }


    /**
     * 运行一步
     */
    public void step(){
        int pai=cards.remove(0);
        if (showDetail){
            System.out.println("----------------------------------------------------------------");
            System.out.println("Player"+currentPlayer.id);
            System.out.println("status: "+currentPlayer.status);
            Collections.sort(currentPlayer.cards);
            System.out.println(MaJiangDef.cardsToString(currentPlayer.cards));
            System.out.println("摸到的牌:"+MaJiangDef.cardToString(pai));
            double n= AIUtil.calc(currentPlayer.cards,new ArrayList<>());
            System.out.println(n);
        }
        ActionResult out=currentPlayer.mopai(pai);
        if (showDetail){
            //if (out.code==ResultCode.ChuPai) System.out.println("打出的牌:"+MaJiangDef.cardToString(out.value));
            System.out.println("返回代码:"+out.code);
            if (out.value != null) System.out.println("打出的牌:"+MaJiangDef.cardToString(out.value));
        }
        //此玩家已经胡牌
        if (out.code==ResultCode.NoAction){
            cards.add(0,pai);
            currentPlayer=currentPlayer.nextPlayer;
        }
        else if (out.code==ResultCode.ZiMo) {
            //自摸
            out.from=this;
            out.to=currentPlayer;
            settle(out);
            cardsOnTable.add(out.value);
            currentPlayer=currentPlayer.nextPlayer;
        }
        else if (out.code==ResultCode.MingGang){
            return;
        }
        else if (out.code==ResultCode.AnGang){
            return;
        }
        //出牌
        else{
            otherPlayerLookCard(out);
        }
    }

    /**
     * 其它玩家看牌
     * @param out
     */
    private void otherPlayerLookCard(ActionResult out) {
        //是否有人胡牌
        boolean somebodyHu=false;
        for (Player p : players){
            if (p != currentPlayer){
                ActionResult r=p.dianPaoHu(out.value);
                if (r.code==ResultCode.DianPaoHu) {
                    somebodyHu=true;
                    r.from=currentPlayer;
                    r.to=p;
                    settle(r);
                }
            }
        }
        //是否有人碰杠牌
        if (!somebodyHu){
            pengGang(out);
        }
        cardsOnTable.add(out.value);
        currentPlayer=currentPlayer.nextPlayer;
    }

    /**
     * 其它玩家碰杠牌
     * @param out
     */
    private void pengGang(ActionResult out){
        // TODO: 2022/2/12 添加杠牌算法
        while (true) {
            boolean somebodyPengGang = false; //是否有人碰牌或杠牌
            for (Player p : players) {
                if (p != currentPlayer) {
                    ActionResult r = p.pengGang(out.value);
                    if (r.code == ResultCode.Peng || r.code==ResultCode.PengGang) {
                        if (showDetail) {
                            System.out.println("碰 Player" + p.id + "  " + MaJiangDef.cardToString(out.value));
                        }
                        out.value = r.value;
                        out.from=p;
                        somebodyPengGang = true;
                        break;
                    }
                }
            }
            if (!somebodyPengGang) break;
        }
    }

    /**
     * 玩一局游戏
     */
    public void play(){
        currentPlayer=players.get(0);
        while (!gameover()){
            step();
        }
    }

    /**
     * 结账
     */
    void settle(ActionResult result){
        //if (showDetail) System.out.println("胡牌 "+MaJiangDef.cardToString(pai)+" from:"+from.id+" to:"+to.id);
    }

    public void print(){
        for (Player p : players){
            System.out.println(p);
        }
    }
}
