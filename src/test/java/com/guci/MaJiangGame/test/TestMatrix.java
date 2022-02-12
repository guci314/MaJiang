package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMatrix {
    @BeforeClass
    public static void setup(){
        HuUtil.load();
        AIUtil.load();
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
        String init = "2万,6万,3筒,5筒";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="3万";
        int pai=MaJiangDef.stringToCard(spai);
        int out=player.mopai(pai).value;
        String sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("6万",sout);

        init = "2万,6万,3筒,5筒";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="3筒";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).value;
        sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("5筒",sout);

        //自摸
        init = "2万,3万,3筒,3筒";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="1万";
        pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.ZiMo,player.mopai(pai).code);
        Assert.assertEquals(Status.Hu,player.status);

        //玩家已胡牌
        spai="1万";
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
        player.DingQue=HuaShe.TIAO;
        String init = "4万,8万,5筒,6筒,6筒,8筒,9筒,1条,1条,2条,4条,5条,9条";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="7万";
        int pai=MaJiangDef.stringToCard(spai);
        int out=player.mopai(pai).value;
        String sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("1条",sout);

        init = "4万,4万,5筒,6筒";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        spai="7筒";
        pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.ZiMo,player.mopai(pai).code);
        Assert.assertEquals(Status.Hu,player.status);

        //测试正确下叫方式
        init = "2万,3万,5筒,5筒";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        player.status=Status.Playing;
        spai="2万";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).value;
        sout=MaJiangDef.cardToString(out);
        Assert.assertEquals("2万,3万,5筒,5筒 摸到 2万 应该打出","2万",sout);

        //测试正确的打法  4万,4万,5万,6万,8万,8万,6筒,7筒,8筒,9筒,9筒,3条,5条,   7万
        //3万,5万,6万,7万,8万,2筒,2筒,4筒,5筒,6筒,6条,7条,9条  5万
        init = "3万,5万,6万,7万,8万,6条,7条,9条";
        cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        player.status=Status.Playing;
        spai="5万";
        pai=MaJiangDef.stringToCard(spai);
        out=player.mopai(pai).value;
        sout=MaJiangDef.cardToString(out);
        System.out.println(sout);
        Collections.sort(player.cards);
        System.out.println(MaJiangDef.cardsToString(player.cards));
        //Assert.assertEquals("4万,4万,5万,6万,8万,8万,6筒,7筒,8筒,9筒,9筒,3条,5条 摸到 7万 应该打出","3条",sout);
    }

    @Test
    public void testHuPai(){
        Player player=new Player();
        player.DingQue=HuaShe.TIAO;
        player.dianPaoHuActionList.add(new BasicDianPaoHuAction());
        player.status=Status.Playing;
        String init = "2万,3万,3筒,3筒";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        player.cards=cards;
        String spai="1万";
        int pai=MaJiangDef.stringToCard(spai);
        Assert.assertEquals(ResultCode.DianPaoHu,player.dianPaoHu(pai).code);

        spai="6万";
        pai=MaJiangDef.stringToCard(spai);
        ActionResult r=player.dianPaoHu(pai);
        Assert.assertEquals(ResultCode.NoAction,r.code);
    }

    @Test
    public void testPeng(){
        Player player=new Player();
        player.DingQue=HuaShe.TIAO;
        player.pengGangActionList.add(new BasicPengGangAction());
        player.status=Status.Playing;
        String init = "1万,1万,2万,2万,3筒,4筒,9筒";
        player.cards=MaJiangDef.stringToCards(init);
        String spai="1万";
        int pai=MaJiangDef.stringToCard(spai);
        ActionResult r=player.pengGang(pai);
        Assert.assertEquals(3,player.cardsOnTable.size());
        Assert.assertEquals(4,player.cards.size());
        int n=Collections.frequency(player.cardsOnTable,(Integer)MaJiangDef.WAN1);
        Assert.assertEquals(3,n);
    }

    /**
     * 测试带幺九时的明杠
     */
    @Test
    public void testMingGang(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="9筒";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        spai="1条,1条,1条";
        matrix.players.get(0).cardsOnTable=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        spai="1万";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        spai="1万";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        spai="1万";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;

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
        String spai="1条";//"1万,1万,1万,3条"
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        spai="1万,1万,1万,3条";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TONG;
        spai="1筒";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).DingQue=HuaShe.WAN;
        spai="1筒";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).DingQue=HuaShe.WAN;

        matrix.cards.clear();
        matrix.cards.add(MaJiangDef.stringToCard("1万"));
        matrix.cards.add(MaJiangDef.stringToCard("9筒"));
        matrix.currentPlayer=matrix.players.get(0);
        matrix.step();

        Assert.assertEquals(4,matrix.players.get(1).cardsOnTable.size());
    }

    /**
     * 测试带缺张时明杠
     */
    @Test
    public void testMingGang1(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="9万";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        spai="1条,1条,1条";
        matrix.players.get(0).cardsOnTable=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        spai="1万";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        spai="1万";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).DingQue=HuaShe.TIAO;
        spai="1万";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).DingQue=HuaShe.TIAO;

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
        String spai="1条,3条,9筒,9筒";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        spai="1万,3万,9筒,9筒";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        spai="1筒,3筒,9筒,9筒";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).DingQue=HuaShe.WAN;
        spai="1筒,4筒,7筒,9条";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).DingQue=HuaShe.WAN;
        matrix.currentPlayer=matrix.players.get(0);

        //自摸
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.TIAO2);
        matrix.step();
        Assert.assertEquals(Status.Hu,matrix.players.get(0).status);
        Assert.assertEquals(Status.Playing,matrix.players.get(1).status);
        Assert.assertTrue(matrix.currentPlayer==matrix.players.get(1));

        //点炮
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
        String spai="1筒";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        matrix.players.get(1).cards=new ArrayList<>();
        matrix.players.get(1).cards.add(MaJiangDef.WAN2);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        matrix.players.get(2).cards=new ArrayList<>();
        matrix.players.get(2).cards.add(MaJiangDef.WAN2);
        matrix.players.get(2).DingQue=HuaShe.TIAO;
        matrix.players.get(3).cards=new ArrayList<>();
        matrix.players.get(3).cards.add(MaJiangDef.WAN3);
        matrix.players.get(3).DingQue=HuaShe.TIAO;
        matrix.currentPlayer=matrix.players.get(0);

        //一炮两响
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
        String spai="1万,1万,2万,2万,3筒,4筒,9筒";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.cards.add(0,MaJiangDef.WAN1);
        matrix.currentPlayer=matrix.players.get(1);
        matrix.step();
        Assert.assertEquals(3,matrix.players.get(0).cardsOnTable.size());
        Assert.assertEquals(1,matrix.cardsOnTable.size());
        Assert.assertTrue(matrix.cardsOnTable.contains(MaJiangDef.stringToCard("9筒")));
    }

    private Matrix createMatrix() {
        Matrix matrix=new Matrix();
        matrix.showDetail=true;
        matrix.init();
        matrix.reset();
        String spai="1万";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.TIAO;
        spai="1筒";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.WAN;
        matrix.players.get(2).cards=new ArrayList<>();
        matrix.players.get(2).cards.add(MaJiangDef.WAN2);
        matrix.players.get(2).DingQue=HuaShe.TONG;
        matrix.players.get(3).cards=new ArrayList<>();
        matrix.players.get(3).cards.add(MaJiangDef.WAN3);
        matrix.players.get(3).DingQue=HuaShe.TONG;
        matrix.currentPlayer=matrix.players.get(0);

        matrix.cards=new ArrayList<>();
        return matrix;
    }

    /**
     * 测试级联碰牌
     */
    @Test
    public void testPeng2(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.reset();
        String spai="1筒,1筒,3条,3条";
        matrix.players.get(0).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(0).DingQue=HuaShe.WAN;
        //打出1万
        spai="1万,1万,3条,3条";
        matrix.players.get(1).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(1).DingQue=HuaShe.TIAO;
        //碰1万 打出3条
        spai="9万,9万,3条,3条";
        matrix.players.get(2).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(2).DingQue=HuaShe.WAN;
        //碰3条 打出9万
        spai="9万,9万,3条,3条";
        matrix.players.get(3).cards=MaJiangDef.stringToCards(spai);
        matrix.players.get(3).DingQue=HuaShe.TIAO;
        //碰9万  打出3条
        matrix.currentPlayer=matrix.players.get(0);
        matrix.cards=new ArrayList<>();
        matrix.cards.add(MaJiangDef.stringToCard("1万"));
        matrix.step();

        Assert.assertEquals(3,Collections.frequency(matrix.players.get(1).cardsOnTable,
                MaJiangDef.stringToCard("1万")));
        Assert.assertEquals(3,Collections.frequency(matrix.players.get(2).cardsOnTable,
                MaJiangDef.stringToCard("3条")));
        Assert.assertEquals(3,Collections.frequency(matrix.players.get(3).cardsOnTable,
                MaJiangDef.stringToCard("9万")));
        Assert.assertEquals(1,matrix.cardsOnTable.size());
        Assert.assertTrue(matrix.cardsOnTable.contains((Integer) MaJiangDef.stringToCard("3条")));
    }

    @Test
    public void testPlay(){
        Matrix matrix=new Matrix();
        //matrix.showDetail=true;
        matrix.init();
        matrix.reset();
        System.out.println("游戏开始之前");
        matrix.print();
        matrix.play();
        System.out.println("游戏结束之后");
        matrix.print();
    }

    @Test
    public void testPerformance(){
        Matrix matrix=new Matrix();
        matrix.init();
        //matrix.showDetail=true;
        for(int i=0;i<10;i++){
            matrix.reset();
            matrix.play();
        }
    }

    @Test
    public void testAnGang(){
        String spai="2万,2万,4万,4万,7万,8万,1筒,1筒,1筒,2筒,5筒,5筒,5筒";
        Player p=new Player();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new BasicMoPaiAction());
        p.status=Status.Playing;
        p.DingQue=HuaShe.TIAO;
        p.cards=MaJiangDef.stringToCards(spai);
        p.cardsOnTable=new ArrayList<>();
        p.mopai(MaJiangDef.stringToCard("5筒"));
        Assert.assertEquals(4,Collections.frequency(p.anGangCards,MaJiangDef.stringToCard("5筒")));
    }


}
