package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicMoPaiAction implements MoPaiAction {
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        me.cards.add(input);
        //是否自摸
        if (HuUtil.isHuExtra(me.cards, gui, 0)){
            System.out.println("自摸");
            ActionResult result=new ActionResult(ResultCode.ZiMo,input);
            result.from=me.matrix;
            result.to=me;
            System.out.println(result.value);
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
        //打牌
        int out = AIUtil.outAI(me.cards, gui);
        me.cards.remove((Integer) out);
        ActionResult result=new ActionResult(ResultCode.ChuPai,(Integer)out);
        result.from=me;
        return result;
    }

    ActionResult mingGang(int input, Player me) {
        me.cards.remove((Integer) input);
        me.cardsOnTable.add((Integer) input);
        ActionResult result= new ActionResult(ResultCode.MingGang, input);
        result.from= me.matrix;
        result.to= me;
        return result;
    }

    ActionResult anGang(int input, Player me) {
        for (int i=0;i<4;i++){
            me.cards.remove((Integer) input);
            me.anGangCards.add((Integer) input);
        }
        ActionResult result= new ActionResult(ResultCode.AnGang, input);
        result.from= me.matrix;
        result.to= me;
        return result;
    }
}
