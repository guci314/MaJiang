package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.AIUtil;

import java.util.Collections;

public class SmartPengGangAction extends BasicPengGangAction{
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
//        if (!me.hasQue()){
//            if (Collections.frequency(me.cards,(Integer)input)==3){
//                return gang(input,me);
//            }
//        }

        boolean b= AIUtil.gangAI(me.cards,gui,input,0);
        if(b) return gang(input,me);
        return processPeng(input,me);
    }
}
