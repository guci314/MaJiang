package com.guci.MaJiangGame.QingYiSe;

import com.guci.MaJiangGame.*;

public class QingYiSePengGangAction extends BasicPengGangAction {

    @Override
    protected ActionResult processPeng(int input, Player me) {
        HuaShe qingYiShe=me.keZuoQingYiShe();
        //如果可做清一色
        if (qingYiShe !=null){
            if (GameUtil.type(input) != qingYiShe) return new ActionResult(ResultCode.NoAction,null);
        }
        return super.processPeng(input, me);
    }
}
