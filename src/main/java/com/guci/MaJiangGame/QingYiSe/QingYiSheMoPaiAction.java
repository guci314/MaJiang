package com.guci.MaJiangGame.QingYiSe;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;

import java.util.*;
import java.util.stream.Collectors;

public class QingYiSheMoPaiAction extends BasicMoPaiAction {
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        //是否自摸
        if (HuUtil.isHuExtra(me.cards, gui, 0)){
            // TODO: 2022/2/16 delete this
            //System.out.println("自摸 player"+me.id);
            ActionResult result=new ActionResult(ResultCode.ZiMo,input);
            result.from=me.matrix;
            result.to=me;
            result.in=input;
            //System.out.println(result.value);
            return result;
        }
        //是否明杠. 默认算法始终明杠
        if (Collections.frequency(me.cardsOnTable, input)==3){
            return mingGang(input, me);
        }
        //是否暗杠 默认算法始终暗杠
        if (Collections.frequency(me.cards,(Integer)input)==4){
            ActionResult r= anGang(input, me);
            if (r.code==ResultCode.AnGang) return r;
        }
        //出牌
        HuaShe qingyishe=me.keZuoQingYiShe();
        if (qingyishe != null) {
            // TODO: 2022/2/15 delete this
//            if (me.matrix !=null) {
//                System.out.println("启动清一色程序 player" + me.id + " " + qingyishe + " 剩余牌 " + me.matrix.cards.size());
//            }
            // TODO: 2022/2/14 短张出牌应调用AIUtil
//            Optional<Integer> c=null;
//            switch (qingyishe){
//                case TIAO:
//                    c= me.cards.stream().filter(x-> x> MaJiangDef.TIAO9 || x<MaJiangDef.TIAO1).findFirst();
//                    break;
//                case TONG:
//                    c= me.cards.stream().filter(x-> x> MaJiangDef.TONG9 || x<MaJiangDef.TONG1).findFirst();
//                    break;
//                case WAN:
//                    c= me.cards.stream().filter(x-> x> MaJiangDef.WAN9 || x<MaJiangDef.WAN1).findFirst();
//                    break;
//            }
//            if (c.isPresent()) {
//                Integer x=c.get();
//                ActionResult result = new ActionResult(ResultCode.ChuPai, x);
//                result.from = me;
//                me.cards.remove(x);
//                return result;
//            }

            List<Integer> duanPai=new ArrayList<>();
            switch (qingyishe){
                case TIAO:
                    duanPai= me.cards.stream().filter(x-> x> MaJiangDef.TIAO9 || x<MaJiangDef.TIAO1).collect(Collectors.toList());
                    break;
                case TONG:
                    duanPai= me.cards.stream().filter(x-> x> MaJiangDef.TONG9 || x<MaJiangDef.TONG1).collect(Collectors.toList());
                    break;
                case WAN:
                    duanPai= me.cards.stream().filter(x-> x> MaJiangDef.WAN9 || x<MaJiangDef.WAN1).collect(Collectors.toList());
                    break;
            }
            if (!duanPai.isEmpty()){
                int x = AIUtil.outAI(duanPai, gui);
                me.cards.remove((Integer) x);
                ActionResult result = new ActionResult(ResultCode.ChuPai, x);
                result.from = me;
                result.in=input;
                return result;
            }
        }
        // TODO: 2022/2/16 outAI返回一个出牌列表,以便保留做清一色的可能
        int out = AIUtil.outAI(me.cards, gui);
        me.cards.remove((Integer) out);
        //根据可见张数下叫
//        double value=AIUtil.calc(me.cards,new ArrayList<>());
//        if (value>10){
//            Map<Integer,Integer> zhangShuMap=new HashMap<>();
//            me.cards.add((Integer) out);
//            HashSet<Integer> cardsSet=new HashSet<>(me.cards);
//            for(Integer card : cardsSet){
//                List<Integer> temp=new ArrayList<>(me.cards);
//                temp.remove(card);
//                List<Integer> ting = HuUtil.isTingExtra(temp, gui);
//                //List<Integer> ting = HuUtil.isTingCard(temp,0);
//                int zhangShu=ting.size()*4;
//                if (!ting.isEmpty()){
//                    for (Integer i : ting){
//                        zhangShu=zhangShu-Collections.frequency(me.cards,i);
//                        zhangShu=zhangShu-Collections.frequency(me.cardsOnTable,i);
//                        zhangShu=zhangShu-Collections.frequency(me.matrix.cardsOnTable,i);
//                    }
//                }
//                zhangShuMap.put(card,zhangShu);
//            }
//            Integer bestKey=zhangShuMap.keySet().stream().findFirst().get();
//            for (Integer key:zhangShuMap.keySet()){
//                if(zhangShuMap.get(key)>zhangShuMap.get(bestKey)) bestKey=key;
//            }
//            if (zhangShuMap.get(bestKey) !=0) {
//                me.cards.remove(bestKey);
//                out = bestKey;
//            }
//            else {
//                me.cards.remove((Integer) out);
//            }
//        }
        ActionResult result = new ActionResult(ResultCode.ChuPai, out);
        result.from = me;
        result.in=input;
        return result;
    }
}
