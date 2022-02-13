package com.guci.MaJiangGame;

import java.util.*;

/**
 * 记账
 */
public class JiZhang {
    Matrix matrix;
    /**
     * 结账
     */
    void settle(ActionResult result){
        //System.out.println(result);
        if (result.code==ResultCode.ZiMo){
            int fan=jiFan(result.to, result.value);
            fan++;
            int n=(int) Math.pow(2,fan);
            result.to.jinE=result.to.jinE+n*3;
            for(Player p : result.to.matrix.players){
                if (p != result.to){
                    p.jinE=p.jinE-n;
                }
            }
        }

        if (result.code==ResultCode.DianPaoHu){
            int fan=jiFan(result.to, result.value);
            int n=(int) Math.pow(2,fan);
            ((Player)result.from).jinE=((Player)result.from).jinE-n;
            ((Player)result.to).jinE=((Player)result.to).jinE+n;
        }

        if (result.code==ResultCode.AnGang){
            result.to.jinE=result.to.jinE+6;
            for (Player p : result.to.matrix.players){
                if (p != result.to){
                    p.jinE=p.jinE-2;
                }
            }
        }

        if (result.code==ResultCode.MingGang){
            result.to.jinE=result.to.jinE+3;
            for (Player p : result.to.matrix.players){
                if (p != result.to){
                    p.jinE=p.jinE-1;
                }
            }
        }

        if (result.code==ResultCode.PengGang){
            result.to.jinE=result.to.jinE+4;
            ((Player)result.from).jinE=((Player)result.from).jinE-2;
            for(Player p : result.to.matrix.players){
                if (p != result.to && p != result.from){
                    p.jinE=p.jinE-1;
                }
            }
        }
    }

    /**
     * 计番
     * @param p
     * @return
     */
    int jiFan(Player p,int input){
        List<Integer> all=new ArrayList<>();
        all.add((Integer) input);
        all.addAll(p.cards);
        all.addAll(p.cardsOnTable);
        all.addAll(p.anGangCards);
        Set<Integer> allSet = new HashSet<>();
        allSet.addAll(all);
        int fan=0;
        for(Integer i : allSet){
            if (Collections.frequency(all,i)==4) fan++;
        }
        return fan;
    }
}
