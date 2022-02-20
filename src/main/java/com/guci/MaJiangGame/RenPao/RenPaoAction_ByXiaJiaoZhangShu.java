package com.guci.MaJiangGame.RenPao;

import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;

import java.util.ArrayList;
import java.util.List;

public class RenPaoAction_ByXiaJiaoZhangShu extends BasicDianPaoHuAction {
    public int threshold=4;
    @Override
    public ActionResult go(int input, Player me) {
        if (me.status== Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        //检查是否已定缺
        if (!me.hasQue()) return new ActionResult(ResultCode.NoAction,null);
        //当前牌是否是定缺
        if (me.isQue(input)) return new ActionResult(ResultCode.NoAction,null);
        List<Integer> tmp = new ArrayList<>(me.cards);
        tmp.add(input);
        if (HuUtil.isHuExtra(tmp, gui, 0)){
            //检测是否忍炮

            //System.out.println("点炮胡");
            //如果活动玩家小于4 则不忍炮
            if (me.matrix.players.stream().filter(x->x.status==Status.Playing).count() != 4)
                return new ActionResult(ResultCode.DianPaoHu,input);
//            if (me.matrix.cards.size()<28)
//                return new ActionResult(ResultCode.NoAction,null);
            int total=me.xiaJiaoZhangSu();

            // TODO: 2022/2/18 delete this
            //System.out.println("胡牌张数:"+total);
            //System.out.println(me);
//            for(Player player:me.matrix.players){
//                System.out.println(MaJiangDef.cardsToString(player.cardsOnTable));
//            }
//            System.out.println(MaJiangDef.cardsToString(me.matrix.cardsOnTable));
            if (total>=threshold) return new ActionResult(ResultCode.NoAction,null);
            return new ActionResult(ResultCode.DianPaoHu,input);
        }
        else return new ActionResult(ResultCode.NoAction,null);
    }
}
