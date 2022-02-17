package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;
import com.guci.MaJiangGame.QingYiSe.QingYiSePengGangAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByActivePlayerNumber;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByCardsNumber;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByKeGangPai;
import com.guci.MaJiangGame.RenPao.RenPaoAction_ByXiaJiaoZhangShu;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStrategy {
    @BeforeClass
    public static void setup(){
        HuUtil.load();
        AIUtil.load();
    }

    /**
     * 测试碰杠策略
     */
    @Test
    public void testSmartPengGang(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.players.get(0).pengGangActionList.clear();
        matrix.players.get(0).pengGangActionList.add(new SmartPengGangAction());

        //matrix.showDetail=true;
        for(int i=0;i<10000;i++){
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 测试暗杠策略
     */
    @Test
    public void testSmartAnGang(){
        // TODO: 2022/2/15 测试清一色的暗杠策略
        Matrix matrix=new Matrix();
        matrix.init();
        Player p=matrix.players.get(0);
        p.moPaiActionList.clear();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new SmartMoPaiAction());
        //matrix.showDetail=true;
        for(int i=0;i<10000;i++){
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
    }

    /**
     * 测试忍炮  根据活动用户
     *  结论 不能忍炮
     */
    @Test
    public void testRenPao(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p=matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByActivePlayerNumber rp=new RenPaoAction_ByActivePlayerNumber();
        p.dianPaoHuActionList.add(rp);
        for(int n=4;n<5;n++) {
            rp.threshold_active_player=n;
            System.out.println("活动用户阈值="+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
            }
            for (int i = 0; i < 10000; i++) {
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
    public void testRenPao1(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p=matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByCardsNumber rp=new RenPaoAction_ByCardsNumber();
        p.dianPaoHuActionList.add(rp);
        for(int n=26;n<29;n++) {
            rp.threshold_cards_number=n;
            System.out.println("剩余张数阈值="+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
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
     *   6是最优阈值
     */
    @Test
    public void testRenPao2(){
        Matrix matrix=new Matrix();
        matrix.init();
        Player p=matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByXiaJiaoZhangShu rp=new RenPaoAction_ByXiaJiaoZhangShu();
        p.dianPaoHuActionList.add(rp);
        for(int n=1;n<15;n++) {
            rp.threshold=n;
            System.out.println("胡牌张数阈值="+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
            }
            for (int i = 0; i < 10000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
    }

    /**
     * 根据手上坎牌数量决定是否忍炮
     * 结论 手上有一坎可杠牌 且玩家数目为4时要忍炮
     * 胡牌张数最优值 4
     */
    @Test
    public void testRenPao3(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p=matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        RenPaoAction_ByKeGangPai rp=new RenPaoAction_ByKeGangPai();
        p.dianPaoHuActionList.add(rp);
        for (int n=1;n<10;n++) {
            p.threshold_mopaicishu=n;
            System.out.println("胡牌张数阈值:"+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }

    }

    /**
     * 打一万盘
     * 2号玩家基准值是 -800
     */
    @Test
    public void testRenPaoJiZunZi(){
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
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
    public void testQingYiSeStrategy(){
        //int n=2;
        Matrix matrix=new Matrix();
        matrix.init();
        Player p=matrix.players.get(2);
        //p.threshold=4;
        p.moPaiActionList.clear();
        p.moPaiActionList.add(new DingQueMoPaiAction());
        p.moPaiActionList.add(new IsolatingMoPaiAction());
        p.moPaiActionList.add(new QingYiSheMoPaiAction());
        p.pengGangActionList.clear();
        p.pengGangActionList.add(new QingYiSePengGangAction());
        p.dianPaoHuActionList.clear();
        //p.dianPaoHuActionList.add(new QingYiSeDianPaoHuAction());
        //p.dianPaoHuActionList.add(new RenPaoAction_ByActivePlayerNumber());
        p.dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

//        matrix.players.get(0).dianPaoHuActionList.clear();
//        matrix.players.get(0).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(1).dianPaoHuActionList.clear();
//        matrix.players.get(1).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());
//
//        matrix.players.get(3).dianPaoHuActionList.clear();
//        matrix.players.get(3).dianPaoHuActionList.add(new RenPaoAction_ByCardsNumber());

        for(int n=2;n<7;n++) {
            p.threshold_duanzhang =n;
            System.out.println("阈值:"+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
    }

    /**
     * 测试清一色 剩余摸牌次数最佳值 4
     *
     */
    @Test
    public void testQingYiSeStrategy1(){
        Matrix matrix=new Matrix();
        matrix.init();
        Player p=matrix.players.get(2);
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

        for(int n=3;n<20;n++) {
            p.threshold_mopaicishu =n;
            System.out.println("摸牌次数阈值:"+n);
            for (Player p1 :matrix.players){
                p1.jinE=0;
            }
            for (int i = 0; i < 100000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
    }
}
