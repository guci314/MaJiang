package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.HuUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 点炮胡,非自摸
 */
public class BasicDianPaoHuAction implements DianPaoHuAction {
    protected List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        //检查是否已定缺
        if (!me.hasQue()) return new ActionResult(ResultCode.NoAction,null);
        //当前牌是否是定缺
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        List<Integer> tmp = new ArrayList<>(me.cards);
        tmp.add(input);
        if (HuUtil.isHuExtra(tmp, gui, 0)){
            return new ActionResult(ResultCode.DianPaoHu,input);
        }
        else return new ActionResult(ResultCode.NoAction,null);
    }
}
