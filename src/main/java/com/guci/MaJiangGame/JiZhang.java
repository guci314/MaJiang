package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 记账
 */
public class JiZhang {
    Matrix matrix;
    public Consumer<ActionResult> settleConsumer=null;
    /**
     * 结账
     */
    void settle(ActionResult result){
        if (settleConsumer != null) settleConsumer.accept(result);
        if (result.code==ResultCode.ZiMo){
            //System.out.println("自摸 player"+result.to.id);
            int fan=jiFan(result.to, result.out);
            fan++;
            if (fan>5) fan=5;
            int n=(int) Math.pow(2,fan);
            int yingsou=0;
            for(Player p : result.to.matrix.players){
                if (p.status==Status.Hu) continue;
                if (p != result.to){
                    p.jinE=p.jinE-n;
                    yingsou=yingsou+n;
                }
            }
            result.to.jinE=result.to.jinE+yingsou;
        }

        if (result.code==ResultCode.DianPaoHu){
            //System.out.println("点炮胡 player"+result.to.id+"  点炮者 "+((Player)result.from).id);
            int fan=jiFan(result.to, result.out);
            int n=(int) Math.pow(2,fan);
            ((Player)result.from).jinE=((Player)result.from).jinE-n;
            ((Player)result.to).jinE=((Player)result.to).jinE+n;
        }

        if (result.code==ResultCode.AnGang){
            //System.out.println("暗杠 "+result.to.id);
            int yingsou=0;
            for (Player p : result.to.matrix.players){
                if (p.status==Status.Hu) continue;
                if (p != result.to){
                    p.jinE=p.jinE-2;
                    yingsou=yingsou+2;
                }
            }
            result.to.jinE=result.to.jinE+yingsou;
        }

        if (result.code==ResultCode.MingGang){
            //System.out.println("明杠 "+result.to.id);
            int yingsou=0;
            for (Player p : result.to.matrix.players){
                if (p.status==Status.Hu) continue;
                if (p != result.to){
                    p.jinE=p.jinE-1;
                    yingsou=yingsou+1;
                }
            }
            result.to.jinE=result.to.jinE+yingsou;
        }

        if (result.code==ResultCode.PengGang){
            //System.out.println("碰杠 "+result.to.id);
            int yingsou=0;
            ((Player)result.from).jinE=((Player)result.from).jinE-1;
            yingsou=yingsou+1;
            for(Player p : result.to.matrix.players){
                if (p.status==Status.Hu) continue;
                if (p != result.to ){
                    p.jinE=p.jinE-1;
                    yingsou=yingsou+1;
                }
            }
            result.to.jinE=result.to.jinE+yingsou;
        }
    }

    /**
     * 计番
     * @param p
     * @return
     */
    public int jiFan(Player p,int input){
        List<Integer> all=new ArrayList<>();
        all.add((Integer) input);
        all.addAll(p.cards);
        all.addAll(p.cardsOnTable);
        //all.addAll(p.anGangCards);
        //计算有几个根
        Set<Integer> allSet = new HashSet<>();
        allSet.addAll(all);
        int fan=0;
        for(Integer i : allSet){
            if (Collections.frequency(all,i)==4) fan++;
        }
        //清一色
        boolean isQingYiShe=false;
        if(all.stream().allMatch(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9)) isQingYiShe=true;
        if(all.stream().allMatch(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9)) isQingYiShe=true;
        if(all.stream().allMatch(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9)) isQingYiShe=true;
        if (isQingYiShe) fan=fan+2;
        //大对子
        if (GameUtil.isDaduizi(all)) fan++;
        //暗七对
        if (GameUtil.isQiDui(all)) fan=fan+2;
        //门清
        if (p.cardsOnTable.size()==0) fan++;
        // TODO: 2022/3/5 添加卡心五
        // TODO: 2022/3/5 添加中张
        if (fan >5) fan=5;
        return fan;
    }

}
