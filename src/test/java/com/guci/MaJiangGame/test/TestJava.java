package com.guci.MaJiangGame.test;

import com.guci.MaJiangGame.HuaShe;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        int x= (int) Math.pow(2,4);
        System.out.println(x);
    }
}
