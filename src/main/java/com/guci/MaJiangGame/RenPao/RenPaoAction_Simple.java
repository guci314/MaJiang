package com.guci.MaJiangGame.RenPao;

import com.guci.MaJiangGame.*;

public class RenPaoAction_Simple extends BasicDianPaoHuAction {
    @Override
    public ActionResult go(int input, Player me) {
        if (me.xiaJiaoZhangSu()>1) {
            return new ActionResult(ResultCode.NoAction, null);
        }
        else return super.go(input,me);
    }
}
