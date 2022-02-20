package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.*;

public class BasicMoPaiAction implements MoPaiAction {
    protected List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        //是否自摸
        if (HuUtil.isHuExtra(me.cards, gui, 0)){
            ActionResult result=new ActionResult(ResultCode.ZiMo,input);
            result.from=me.matrix;
            result.to=me;
            result.in=input;
            return result;
        }
        //是否明杠. 默认算法始终明杠
        if (Collections.frequency(me.cardsOnTable,(Integer)input)==3){
            return mingGang(input, me);
        }
        //是否暗杠 默认算法始终暗杠
        if (Collections.frequency(me.cards,(Integer)input)==4){
            ActionResult r= anGang(input, me);
            if (r.code==ResultCode.AnGang) return r;
        }
        //出牌
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
        ActionResult result=new ActionResult(ResultCode.ChuPai,(Integer)out);
        result.from=me;
        result.in=input;
        return result;
    }

    protected ActionResult mingGang(int input, Player me) {
        me.cards.remove((Integer) input);
        me.cardsOnTable.add((Integer) input);
        ActionResult result= new ActionResult(ResultCode.MingGang, input);
        result.from= me.matrix;
        result.to= me;
        result.in=input;
        return result;
    }

    protected ActionResult anGang(int input, Player me) {
        for (int i=0;i<4;i++){
            me.cards.remove((Integer) input);
            me.cardsOnTable.add((Integer) input);
        }
        ActionResult result= new ActionResult(ResultCode.AnGang, input);
        result.from= me.matrix;
        result.to= me;
        result.in=input;
        return result;
    }
}
