package com.guci.MaJiangGame.test;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.guci.MaJiangGame.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStratege {
    @BeforeClass
    public static void setup(){
        HuUtil.load();
        AIUtil.load();
    }

    @Test
    public void testGang(){
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

    @Test
    public void testAnGang(){
        Matrix matrix=new Matrix();
        matrix.init();
        Player p=matrix.players.get(0);
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

    @Test
    public void testRenPao(){
        Matrix matrix=new Matrix();
        matrix.init();
        //matrix.showDetail=true;
        Player p=matrix.players.get(0);
        p.dianPaoHuActionList.clear();
        p.dianPaoHuActionList.add(new RenPaoAction());
        for(int i=0;i<10000;i++){
            matrix.reset();
            matrix.play();
        }
        matrix.printJingE();
        matrix.print();
    }
}
