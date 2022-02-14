package com.guci.MaJiangGame.QingYiSe;

import com.github.esrrhs.majiang_algorithm.AIUtil;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import com.guci.MaJiangGame.*;

import java.util.Collections;
import java.util.Optional;

public class QingYiSheMoPaiAction extends BasicMoPaiAction {
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
        if (Collections.frequency(me.cardsOnTable, input)==3){
            return mingGang(input, me);
        }
        //是否暗杠 默认算法始终暗杠
        if (Collections.frequency(me.cards,(Integer)input)==4){
            ActionResult r= anGang(input, me);
            if (r.code==ResultCode.AnGang) return r;
        }
        //出牌
        HuaShe qingyishe=me.zuoQingYiShe();
        if (qingyishe != null) {
            // TODO: 2022/2/15 delete this
//            if (me.matrix !=null) {
//                System.out.println("启动清一色程序 player" + me.id + " " + qingyishe + " 剩余牌 " + me.matrix.cards.size());
//            }
            // TODO: 2022/2/14 短张出牌应调用AIUtil
            Optional<Integer> c=null;
            switch (qingyishe){
                case TIAO:
                    c= me.cards.stream().filter(x-> x> MaJiangDef.TIAO9 || x<MaJiangDef.TIAO1).findFirst();
                    break;
                case TONG:
                    c= me.cards.stream().filter(x-> x> MaJiangDef.TONG9 || x<MaJiangDef.TONG1).findFirst();
                    break;
                case WAN:
                    c= me.cards.stream().filter(x-> x> MaJiangDef.WAN9 || x<MaJiangDef.WAN1).findFirst();
                    break;
            }
            if (c.isPresent()) {
                Integer x=c.get();
                ActionResult result = new ActionResult(ResultCode.ChuPai, x);
                result.from = me;
                me.cards.remove(x);
                return result;
            }
        }
        int out = AIUtil.outAI(me.cards, gui);
        me.cards.remove((Integer) out);
        ActionResult result = new ActionResult(ResultCode.ChuPai, out);
        result.from = me;
        return result;
    }
}
