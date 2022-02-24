package com.guci.MaJiangGame;

import com.alibaba.fastjson.JSONObject;
import com.github.esrrhs.majiang_algorithm.HuUtil;
import com.github.esrrhs.majiang_algorithm.MaJiangDef;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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
    public int threshold_duanzhang =3;

    public int threshold_mopaicishu=4;

    @Override
    public java.lang.String toString() {
        Collections.sort(cards);
        JSONObject object = new JSONObject();
        object.put("id",id);
        object.put("status",status);
        object.put("cards",MaJiangDef.cardsToString(cards));
        object.put("cardsOnTable",MaJiangDef.cardsToString(cardsOnTable));
        object.put("DingQue",dingQue);
        object.put("xiaJiaoZhangSu",xiaJiaoZhangSu());
        object.put("numberOfKanPai",numberOfKanPai());
        object.put("isQingYiShe",isQingYiShe());
        object.put("zuoQingYiShe",keZuoQingYiShe());
        return object.toJSONString();
    }

    /**
     * 有几坎牌可杠
     * @return
     */
    public int numberOfKanPai(){
        int number=0;
        HashSet<Integer> cardsSet=new HashSet<>(cards);
        for (Integer c:cardsSet){
            long l=Collections.frequency(cards,c);
            if (l==3) {
                int x=0;
                x=x+Collections.frequency(matrix.cardsOnTable,c);
                for (Player p:matrix.players){
                    x=x+Collections.frequency(p.cardsOnTable,c);
                }
                if (x==0) {
                    number++;
                }
            }
        }
        return number;
    }

    public int xiaJiaoZhangSu(){
        List<Integer> gui = new ArrayList<>();
        List<Integer> ting= HuUtil.isTingExtra(cards,gui);
        int total=ting.size()*4;
        for (Integer card:ting){
            long x=cards.stream().filter(x1->x1==card).count();
            total= (int) (total-x);
            x=matrix.cardsOnTable.stream().filter(x1->x1==card).count();
            total= (int) (total-x);
            for(Player p:matrix.players){
                x=p.cardsOnTable.stream().filter(x1->x1==card).count();
                total= (int) (total-x);
            }
        }
        return total;
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
     * 已经是清一色
     * @return
     */
    public boolean isQingYiShe(){
        List<Integer> all=new ArrayList<>();
        all.addAll(cards);
        all.addAll(cardsOnTable);
        if(all.stream().allMatch(x->x>= MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9)) return true;
        if(all.stream().allMatch(x->x>= MaJiangDef.WAN1 && x<=MaJiangDef.WAN9)) return true;
        if(all.stream().allMatch(x->x>= MaJiangDef.TONG1 && x<=MaJiangDef.TONG9)) return true;
        return false;
    }

    /**
     * 判定是否做清一色. 如果是则返回花色.如果不做则返回null
     * @return
     */
    public HuaShe keZuoQingYiShe(){
        // TODO: 2022/2/15 根据剩余张数和短牌张数的比值决策清一色
        List<Integer> all=new ArrayList<>();
        all.addAll(cards);
        all.addAll(cardsOnTable);
        long lengthOfWAN=all.stream().filter(x->x>=MaJiangDef.WAN1 && x<=MaJiangDef.WAN9).count();
        long lengthOfTIAO=all.stream().filter(x->x>=MaJiangDef.TIAO1 && x<=MaJiangDef.TIAO9).count();
        long lengthOfTONG=all.stream().filter(x->x>=MaJiangDef.TONG1 && x<=MaJiangDef.TONG9).count();
        HuaShe changHuaShe = null;
        if (dingQue==HuaShe.TIAO){
            if (lengthOfWAN<= threshold_duanzhang) changHuaShe= HuaShe.TONG;
            if (lengthOfTONG<= threshold_duanzhang) changHuaShe= HuaShe.WAN;
        }
        if (dingQue==HuaShe.TONG){
            if (lengthOfTIAO<= threshold_duanzhang) changHuaShe= HuaShe.WAN;
            if (lengthOfWAN<= threshold_duanzhang) changHuaShe= HuaShe.TIAO;
        }
        if (dingQue==HuaShe.WAN){
            if (lengthOfTIAO<= threshold_duanzhang) changHuaShe= HuaShe.TONG;
            if (lengthOfTONG<= threshold_duanzhang) changHuaShe= HuaShe.TIAO;
        }
        HuaShe finalChangHuaShe = changHuaShe;
        if (cardsOnTable.stream().anyMatch(x->GameUtil.type(x) != finalChangHuaShe)) return null;
        int cisu= (int) (matrix.cards.size()/matrix.players.stream().filter(x->x.status==Status.Playing).count());
        if (cisu<threshold_mopaicishu) return null;
        // TODO: 2022/2/21 delete this
//        if(changHuaShe !=null) {
//            System.out.println("可做清一色 ");
//            System.out.println(this);
//            System.out.println("长张花色:" + changHuaShe);
//        }
        return changHuaShe;
    }
}
