package com.guci.MaJiangGame.QingYiSe;

import com.guci.MaJiangGame.*;

public class QingYiSePengGangAction extends BasicPengGangAction {

    @Override
    protected ActionResult processPeng(int input, Player me) {
        HuaShe qingYiShe=me.keZuoQingYiShe();
        // TODO: 2022/2/28 delete this
        //如果短张长度为3,则不碰短张
//        HuaShe inputType=GameUtil.type(input);
//        if (inputType != qingYiShe && inputType != me.dingQue && (me.getDuanZhang().size()==3 || me.getDuanZhang().size()==4))
//            return new ActionResult(ResultCode.NoAction,null);
        //如果可做清一色
        if (qingYiShe !=null){
            if (GameUtil.type(input) != qingYiShe) return new ActionResult(ResultCode.NoAction,null);
        }
        return super.processPeng(input, me);
    }
}
