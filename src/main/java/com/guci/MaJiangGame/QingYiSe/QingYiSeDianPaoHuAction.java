package com.guci.MaJiangGame.QingYiSe;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;

import java.util.ArrayList;
import java.util.List;

public class QingYiSeDianPaoHuAction implements DianPaoHuAction {
    List<Integer> gui = new ArrayList<>();
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status== Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        //检查是否已定缺
        if (!me.hasQue()) return new ActionResult(ResultCode.NoAction,null);
        //当前牌是否是定缺
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        //做清一色时,有人点炮非清一色,不胡
        HuaShe qys=me.keZuoQingYiShe();
        if (qys != null){
            if (GameUtil.type(input) != qys) return new ActionResult(ResultCode.NoAction,null);
            if (!me.isQingYiShe()) return new ActionResult(ResultCode.NoAction,null);

        }
        List<Integer> tmp = new ArrayList<>(me.cards);
        tmp.add(input);
        if (HuUtil.isHuExtra(tmp, gui, 0)){
            // TODO: 2022/2/16 delete this
            //System.out.println("点炮胡 player"+me.id+" 胡牌 "+ MaJiangDef.cardToString(input));
//            if (qys != null){
//                System.out.println("可做清一色 "+qys);
//            }
            return new ActionResult(ResultCode.DianPaoHu,input);
        }
        else return new ActionResult(ResultCode.NoAction,null);
    }
}
