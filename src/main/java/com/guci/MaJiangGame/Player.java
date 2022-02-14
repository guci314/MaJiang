package com.guci.MaJiangGame;

import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Player {
    public int id;
    public List<Integer> cards=new ArrayList<>();
    public List<Integer> cardsOnTable=new ArrayList<>();
    //public List<Integer> anGangCards=new ArrayList<>();
    public Status status;
    public ActionList<MoPaiAction> moPaiActionList=new ActionList<>();
    public ActionList<DianPaoHuAction> dianPaoHuActionList =new ActionList<>();
    public ActionList<BasicPengGangAction> pengGangActionList =new ActionList<>();
    public Player nextPlayer;
    public HuaShe dingQue;
    public Matrix matrix;
    public int jinE;
    /**
     * 清一色短张阈值
     */
    int threshold=4;

    @Override
    public java.lang.String toString() {
        Collections.sort(cards);
        return "Player{" +
                "id=" + id +
                ",status="+status+
                ", cards=" + MaJiangDef.cardsToString(cards) +
                ",cardsOnTable="+MaJiangDef.cardsToString(cardsOnTable)+
                //",anGangCards="+MaJiangDef.cardsToString(anGangCards)+
                ",定缺="+ dingQue +
                '}';
    }

    public ActionResult mopai(int pai){
        if (status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        ActionResult r=moPaiActionList.go(pai,this);
        if (r.code==ResultCode.ZiMo) status=Status.Hu;
        return r;
    }

    public ActionResult dianPaoHu(int pai){
        if (status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        ActionResult r = dianPaoHuActionList.go(pai,this);
        if (r.code==ResultCode.DianPaoHu) status=Status.Hu;
        return r;
    }

    /**
     *
     * @param pai
     * @return
     */
    public ActionResult pengGang(int pai){
        if (status==Status.Hu) return new ActionResult(ResultCode.NoAction,null);
        ActionResult r= pengGangActionList.go(pai,this);
        return r;
    }

    /**
     * 检查给定的牌是否是定缺牌
     * @param input
     * @return
     */
    public boolean isQue(int input){
        boolean isQue=false;
        switch (dingQue){
            case WAN:
                isQue=input>= MaJiangDef.WAN1 && input<=MaJiangDef.WAN9;
                break;
            case TIAO:
                isQue=input>= MaJiangDef.TIAO1 && input<=MaJiangDef.TIAO9;
                break;
            case TONG:
                isQue=input>= MaJiangDef.TONG1 && input<=MaJiangDef.TONG9;
                break;
        }
        return isQue;
    }

    /**
     * 检查是否已经定缺
     * @return
     */
    public boolean hasQue(){
        return !cards.stream().anyMatch(x->isQue(x));
    }

    /**
     * 检查牌是否可杠
     * @param input
     * @return
     */
    public boolean keGang(Integer input){
        if (isQue(input)) return false;
        if (Collections.frequency(cardsOnTable,input)==3) return true;
        if (Collections.frequency(cards,input)==3) return true;
        return false;
    }


    /**
     * 判定是否做清一色. 如果是则返回花色.如果不做则返回null
     * @return
     */
    public HuaShe zuoQingYiShe(){
        // TODO: 2022/2/15 根据剩余张数和短牌张数的比值决策清一色
        if (cardsOnTable.stream().anyMatch(x->isQue(x))) return null;
        List<Integer> all=new ArrayList<>();
        all.addAll(cards);
        all.addAll(cardsOnTable);
        long lengthOfWAN=all.stream().filter(x->x>=MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
        long lengthOfTIAO=all.stream().filter(x->x>=MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
        long lengthOfTONG=all.stream().filter(x->x>=MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
        if (dingQue==HuaShe.TIAO){
            if (lengthOfWAN<=threshold) return HuaShe.TONG;
            if (lengthOfTONG<=threshold) return HuaShe.WAN;
        }
        if (dingQue==HuaShe.TONG){
            if (lengthOfTIAO<=threshold) return HuaShe.WAN;
            if (lengthOfWAN<=threshold) return HuaShe.TIAO;
        }
        if (dingQue==HuaShe.WAN){
            if (lengthOfTIAO<=threshold) return HuaShe.TONG;
            if (lengthOfTONG<=threshold) return HuaShe.TIAO;
        }
        return null;
    }
}
