package com.github.esrrhs.majiang_algorithm.test;

import com.github.esrrhs.majiang_algorithm.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestMaJiang {

    @BeforeClass
    public static void setup(){
        HuUtil.load();
        AIUtil.load();
    }
    @Test
    public void testCalc(){
        double n = getN("2万,7万,1筒,1筒,4条,5条,6条,7筒,9筒,1筒");
        System.out.println(n);

//        init = "1万,2万,3万,4条,4条";
//        cards = MaJiangDef.stringToCards(init);
//        n= AIUtil.calc(cards,gui);
//        System.out.println(n);
//
//        init = "2万,3万,5万,5万";
//        cards = MaJiangDef.stringToCards(init);
//        n= AIUtil.calc(cards,gui);
//        System.out.println(n);
    }

    private double getN(String s) {
        //String init = "2万,3万,1条,1筒,1筒";
        List<Integer> cards = MaJiangDef.stringToCards(s);
        List<Integer> gui = new ArrayList<>();
        double n= AIUtil.calc(cards,gui);
        return n;
    }

    @Test
    public void testOut(){
        //String init = "1万,1万,8万,9万,5万";
        String init="1万,4万,8万,5筒,6筒,6筒,8筒,9筒";
        //String init="5筒,6筒,2筒,8筒,9筒";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        //List<Integer> gui = MaJiangDef.stringToCards(guiStr);
        List<Integer> gui =new ArrayList<>();
        int out = AIUtil.outAI(cards, gui);
        System.out.println(MaJiangDef.cardToString(out));
    }
    @Test
    public void peng(){
        String init = "1万,2万,2万,1条,1条,2筒,4筒,4筒";
        String guiStr = "1万";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        List<Integer> gui = MaJiangDef.stringToCards(guiStr);

        System.out.println(AIUtil.pengAI(cards, gui, MaJiangDef.stringToCard("2万"), 0.d));
    }
    @Test
    public void ting(){
        String init = "2筒,3筒,4筒,5筒,5筒,6筒,6筒,7筒,7筒,7筒";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        List<Integer>gui=new ArrayList<>();
        System.out.println(MaJiangDef.cardsToString(HuUtil.isTingExtra(cards, gui)));
    }
    @Test
    public void hu(){
        String init = "1万,2万,3万";

        //String gui = "东";
        List<Integer> cards = MaJiangDef.stringToCards(init);
        cards.add(MaJiangDef.FENG_DONG);
        cards.add(MaJiangDef.FENG_DONG);
        List<Integer> gui=new ArrayList<>();
        System.out.println(HuUtil.isHuExtra(cards, gui,0)); //MaJiangDef.stringToCard(gui)
    }
    @Test
    public void hu1(){
        ArrayList<Integer> total = new ArrayList<>();
        for (int i = MaJiangDef.WAN1; i <= MaJiangDef.TIAO9; i++)
        {
            total.add(i);
            total.add(i);
            total.add(i);
            total.add(i);
        }
        Collections.shuffle(total);

        ArrayList<Integer> cards = new ArrayList<>();
        for (int i = 0; i < 14; i++)
        {
            cards.add(total.remove(0));
        }

        Collections.sort(cards);
        System.out.println("before " + MaJiangDef.cardsToString(cards));

        List<Integer> gui = new ArrayList<>();

        int step = 0;
        while (!total.isEmpty())
        {
            if (HuUtil.isHuExtra(cards, gui, 0))
            {
                Collections.sort(cards);
                System.out.println("after " + MaJiangDef.cardsToString(cards));
                System.out.println("step " + step);
                break;
            }
            step++;
            int out = AIUtil.outAI(cards, gui);
            cards.remove((Integer) out);
            cards.add(total.remove(0));
        }
    }
}
