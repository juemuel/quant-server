package com.juemuel.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {
    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> listIndexData(String code){
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);

        for (IndexData indexData : result) {
            System.out.println(indexData.getDate());
        }

        return result;
    }
    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {
        // 初始化利润和交易列表
        List<Profit> profits =new ArrayList<>();
        List<Trade> trades = new ArrayList<>();
        List<MaData> maDatas = new ArrayList<>(); // 存储 MA 均线数据
        // 初始现金
        float initCash = 1000;
        float cash = initCash;
        float share = 0; // 持有的股票数量
        float value = 0;  // 当前持有资产的价值
        // 初始化交易统计变量
        int winCount = 0;  // 赢利次数
        float totalWinRate = 0;  // 总赢利率
        float avgWinRate = 0;  // 平均赢利率
        float totalLossRate = 0;  // 总亏损率
        int lossCount = 0;  // 亏损次数
        float avgLossRate = 0;  // 平均亏损率

        // 遍历所有数据
        float init =0; // 初始化收盘价
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();

        for (int i = 0; i<indexDatas.size() ; i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i, ma, indexDatas);  // 计算移动平均线
            float max = getMax(i, ma, indexDatas);  // 计算最大值
            float increase_rate = closePoint / avg;  // 上涨比例
            float decrease_rate = closePoint / max;  // 下跌比例
            // 操作逻辑
            if(avg!=0) {
                // 买入：收盘价超过买入阈值
                if(increase_rate>buyRate  ) {
                    if (share == 0) {  // 如果没有持股
                        share = cash / closePoint;  // 用现金购买股票
                        cash = 0;  // 现金清零

                        // 创建交易记录
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(indexData.getClosePoint());
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                }
                // 卖出：收盘价低于卖出阈值
                else if(decrease_rate<sellRate ) {
                    if (share != 0) {  // 如果持有股票
                        cash = closePoint * share * (1 - serviceCharge);  // 出售股票并扣除手续费
                        share = 0;  // 清空持有的股票

                        // 更新最后一个交易记录
                        Trade trade = trades.get(trades.size() - 1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        float rate = cash / initCash;  // 计算收益率
                        trade.setRate(rate);

                        // 计算盈亏情况
                        if (trade.getSellClosePoint() - trade.getBuyClosePoint() > 0) {
                            totalWinRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                            winCount++;
                        } else {
                            totalLossRate += (trade.getSellClosePoint() - trade.getBuyClosePoint()) / trade.getBuyClosePoint();
                            lossCount++;
                        }
                    }
                }
                // 不操作
                else{

                }
            }
            // 计算当前持有的资产价值
            if(share!=0) {
                value = closePoint * share;
            }
            else {
                value = cash;
            }
            float rate = value/initCash;
            // 创建利润记录
            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate*init);
            profits.add(profit);
            // 添加 MA 值
            MaData maData = new MaData(indexData.getDate(), avg);
            maDatas.add(maData);
        }
        // 计算平均赢利率和平均亏损率、年收益
        avgWinRate = winCount > 0 ? totalWinRate / winCount : 0;
        avgLossRate = lossCount > 0 ? totalLossRate / lossCount : 0;
        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);

        // 构建结果映射
        Map<String, Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);
        map.put("maDatas", maDatas); // 添加 MA 值
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        map.put("annualProfits", annualProfits);
        return map;
    }

    /**
     * 计算日期范围内，指数的最大值
     * @param i
     * @param day
     * @param list
     * @return
     */
    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i-1-day;
        if(start<0)
            start = 0;
        int now = i-1;

        if(start<0)
            return 0;

        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }

    /**
     * 获取一年的指数投资收益
     * @param year
     * @param indexDatas
     * @return
     */
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
//			Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }
    /**
     * 获取一年的趋势投资收益
     * @param year
     * @param profits
     * @return
     */
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }
    /**
     * 计算MA
     * @param i
     * @param ma
     * @param list
     * @return
     */
    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i-1-ma;
        int now = i-1;

        if(start<0)
            return 0;

        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }

    /**
     * 计算完整时间范围内，每年的指数投资年收益和趋势投资年收益
     * @param indexDatas
     * @param profits
     * @return
     */
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
        }
        return result;
    }

    /**
     * 计算当前的时间范围有多少年
     * @param allIndexDatas
     * @return
     */
    public float getYear(List<IndexData> allIndexDatas) {
        float years;
        String sDateStart = allIndexDatas.get(0).getDate();
        String sDateEnd = allIndexDatas.get(allIndexDatas.size()-1).getDate();

        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);

        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days/365f;
        return years;
    }

    /**
     * 获取某日期的年份
     * @param date
     * @return
     */
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }
}