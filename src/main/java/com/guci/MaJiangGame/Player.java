package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class Player {
    public int id;
    public List<Integer> cards=new ArrayList<>();
    public List<Integer> cardsOnTable=new ArrayList<>();
    public Status status;
    public ActionList<MoPaiAction> moPaiActionList=new ActionList<>();
    public ActionList<HuPaiAction> huPaiActionList=new ActionList<>();
    public ActionList<BasicPengAction> pengActionList=new ActionList<>();
    public Player nextPlayer;
    public HuaShe DingQue;
    public Matrix matrix;

    @Override
    public java.lang.String toString() {
        Collections.sort(cards);
        return "Player{" +
                "id=" + id +
                ",status="+status+
                ", cards=" + MaJiangDef.cardsToString(cards) +
                ",cardsOnTable="+MaJiangDef.cardsToString(cardsOnTable)+
                ",定缺="+DingQue+
                '}';
    }

    public int mopai(int pai){
        if (status==Status.Hu) return -1;
        int r=moPaiActionList.go(pai,this);
        if (r==0) status=Status.Hu;
        return r;
    }

    public int hupai(int pai){
        if (status==Status.Hu) return -1;
        int r = huPaiActionList.go(pai,this);
        if (r==0) status=Status.Hu;
        return r;
    }

    /**
     *
     * @param pai
     * @return -1 表示不碰  其它值表示碰后打出的牌
     */
    public int peng(int pai){
        if (status==Status.Hu) return -1;
        int r=pengActionList.go(pai,this);
        if (r != -1){
            cardsOnTable.add(pai);
            cardsOnTable.add(pai);
            cardsOnTable.add(pai);
            cards.remove((Integer) pai);
            cards.remove((Integer) pai);
            cards.remove((Integer) r);
        }
        return r;
    }
}
