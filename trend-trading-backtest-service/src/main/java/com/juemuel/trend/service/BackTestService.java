package com.juemuel.trend.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.juemuel.trend.client.IndexDataClient;
import com.juemuel.trend.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BackTestService {
    private static final Logger log = LoggerFactory.getLogger(BackTestService.class);
    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> listIndexData(String code) {
        List<IndexData> result = indexDataClient.getIndexData(code);

        if (result == null || result.isEmpty()) {
            log.warn("从数据源获取数据为空: code={}", code);
            return new ArrayList<>();
        }

        log.info("获取到原始数据: size={}", result.size());
        // 检查数据有效性
        result = result.stream()
                .filter(data -> data != null && data.getDate() != null && !"0000-00-00".equals(data.getDate()))
                .collect(Collectors.toList());

        log.info("过滤后的有效数据: size={}", result.size());
        Collections.reverse(result);

        return result;
    }
    public Map<String,Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {
        // 初始化利润和交易列表
        List<Profit> profits =new ArrayList<>();
        List<Trade> trades = new ArrayList<>();
        List<MaData> allMaDatas = new ArrayList<>();
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
        log.info("初始的indexDatas{}", indexDatas);
        if(!indexDatas.isEmpty())
            init = indexDatas.get(0).getClosePoint();
        // 计算多个移动平均线
        List<List<Float>> maLists = calculateMultipleMAs(indexDatas, Arrays.asList(10, 20, 30, 50, 100));
        for (int i = 0; i<indexDatas.size() ; i++) {
            // 获取当天的多个移动平均线值
            List<Float> mAs = new ArrayList<>();
            for (int j = 0; j < maLists.size(); j++) {
                mAs.add(maLists.get(j).get(i));
            }

            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i, ma, indexDatas);  // 根据传入的ma，计算算法参考的移动平均线
            float max = getMax(i, ma, indexDatas);  // 根据传入的ma，计算算法参考的最大值
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
            // 添加 选择的MA值 到 选择MA数组中
            List<Float> maValues = mAs;
            MaData maData = new MaData(indexData.getDate(), maValues);
            allMaDatas.add(maData);
        }
        // 计算平均赢利率和平均亏损率、年收益
        avgWinRate = winCount > 0 ? totalWinRate / winCount : 0;
        avgLossRate = lossCount > 0 ? totalLossRate / lossCount : 0;
        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);

        // 构建结果映射
        Map<String, Object> map = new HashMap<>();
        map.put("profits", profits);
        map.put("trades", trades);
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        map.put("annualProfits", annualProfits);
        map.put("allMaDatas", allMaDatas);
        return map;
    }
    private List<List<Float>> calculateMultipleMAs(List<IndexData> indexDatas, List<Integer> maPeriods) {
        List<List<Float>> maLists = new ArrayList<>();
        for (int period : maPeriods) {
            List<Float> maList = new ArrayList<>();
            for (int i = 0; i < indexDatas.size(); i++) {
                float ma = getMA(i, period, indexDatas);
                maList.add(ma);
            }
            maLists.add(maList);
        }
        return maLists;
    }
    /**
     * 计算日期范围内，指数的最大值
     * @param currentIndex
     * @param ma
     * @param indexDatas
     * @return
     */
    private float getMax(int currentIndex, int ma, List<IndexData> indexDatas) {
        if (currentIndex < ma - 1) {
            return 0;
        }
        float max = Float.MIN_VALUE;
        for (int i = currentIndex - ma + 1; i <= currentIndex; i++) {
            max = Math.max(max, indexDatas.get(i).getClosePoint());
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
        log.info("getIndexIncome: {}", indexDatas);
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
            if (strDate == null || "0000-00-00".equals(strDate)) {
                log.info("无效日期：" + strDate);
                continue;  // 跳过无效日期
            }
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
     * @param currentIndex
     * @param ma
     * @param indexDatas
     * @return
     */
    private float getMA(int currentIndex, int ma, List<IndexData> indexDatas) {
        if (currentIndex < ma - 1) {
            return 0;
        }
        float sum = 0;
        for (int i = currentIndex - ma + 1; i <= currentIndex; i++) {
            sum += indexDatas.get(i).getClosePoint();
        }
        return sum / ma;
    }

    /**
     * 计算完整时间范围内，每年的指数投资年收益和趋势投资年收益
     * @param indexDatas
     * @param profits
     * @return
     */
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        if (indexDatas == null || indexDatas.isEmpty() || profits == null || profits.isEmpty()) {
            log.info("计算年度收益时数据为空 {}", indexDatas);
            return result;
        }
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
            System.out.println(year + ":"+indexDatas);
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