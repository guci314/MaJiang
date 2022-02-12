package com.guci.MaJiangGame.test;

import com.guci.MaJiangGame.HuaShe;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJava {
    @Test
    public void testSwitch(){
        HuaShe x=HuaShe.TONG;
        switch (x){
            case WAN:
                System.out.println("wan");
                break;
            case TIAO:
                System.out.println("tiao");
                break;
            case TONG:
                System.out.println("tong");
                break;
        }
    }

    @Test
    public void testLoop(){
        List<Integer> l=new ArrayList<>();
        l.add(1);
        l.add(2);
        l.add(3);
        System.out.println(l.remove(0));
        System.out.println(l.remove(0));
    }
}
