package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;
import com.guci.MaJiangGame.QingYiSe.QingYiSeDianPaoHuAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSePengGangAction;
import com.guci.MaJiangGame.QingYiSe.QingYiSheMoPaiAction;
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
     * 测试忍炮
     */
    @Test
    public void testRenPao(){
        // TODO: 2022/2/15 测试清一色时的忍炮策略
        Matrix matrix=new Matrix();
        matrix.init();
        matrix.createQingYiSeAction();
        Player p=matrix.players.get(2);
        p.dianPaoHuActionList.clear();
        p.dianPaoHuActionList.add(new RenPaoAction());
        for(int i=0;i<10000;i++){
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
        //matrix.print();
    }
    
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
        p.dianPaoHuActionList.add(new RenPaoAction());

        matrix.players.get(0).dianPaoHuActionList.clear();
        matrix.players.get(0).dianPaoHuActionList.add(new RenPaoAction());

        matrix.players.get(1).dianPaoHuActionList.clear();
        matrix.players.get(1).dianPaoHuActionList.add(new RenPaoAction());

        matrix.players.get(3).dianPaoHuActionList.clear();
        matrix.players.get(3).dianPaoHuActionList.add(new RenPaoAction());

        for(int n=2;n<13;n++) {
            p.threshold=n;
            System.out.println("阈值:"+n);
            for (int i = 0; i < 10000; i++) {
                matrix.reset();
                matrix.play();
            }
            matrix.printJingE();
        }
    }
}
