/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengoofy.index12306.biz.ticketservice.toolkit;

import org.opengoofy.index12306.biz.ticketservice.dto.domain.RouteDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 站点计算工具
 *
 */
public final class StationCalculateUtil {

    /**
     * 计算某车次从startStation到endStation能组合成的所有路线段（包含出发站和终点站）
     *
     * @param stations     某车次所有站点数据
     * @param startStation 某车次出发站
     * @param endStation   某车次终点站
     * @return 计算某车次从startStation到endStation能组合成的所有路线段（包含出发站和终点站）
     */
    public static List<RouteDTO> throughStation(List<String> stations, String startStation, String endStation) {
        List<RouteDTO> routesToDeduct = new ArrayList<>();
        int startIndex = stations.indexOf(startStation);
        int endIndex = stations.indexOf(endStation);
        if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex) {
            return routesToDeduct;
        }
        for (int i = startIndex; i < endIndex; i++) {
            for (int j = i + 1; j <= endIndex; j++) {
                String currentStation = stations.get(i);
                String nextStation = stations.get(j);
                RouteDTO routeDTO = new RouteDTO(currentStation, nextStation);
                routesToDeduct.add(routeDTO);
            }
        }
        return routesToDeduct;
    }

    /**
     * TODO：目前的扣减逻辑有点问题，比如abcde，有人买了bc，但目前有扣减ae表示不能买ae全程的票，但之后有人买了同个位置的ce，又会再扣一次ae，但实际上只需要扣一次。
     * 计算购买从出发站到终点站的票之后其他需要扣减余票的路线段（包含出发站和终点站）
     * 实际上就是所有和(出发站到终点站的路线段)有交集的所有路线段。
     * @param stations     所有站点数据
     * @param startStation 出发站
     * @param endStation   终点站
     * @return 计算购买从出发站到终点站的票之后其他需要扣减余票的路线段（包含出发站和终点站）
     */
    public static List<RouteDTO> takeoutStation(List<String> stations, String startStation, String endStation) {
        List<RouteDTO> takeoutStationList = new ArrayList<>();
        int startIndex = stations.indexOf(startStation);
        int endIndex = stations.indexOf(endStation);
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return takeoutStationList;
        }
        if (startIndex != 0) {
            for (int i = 0; i < startIndex; i++) {
                for (int j = 1; j < stations.size() - startIndex; j++) {
                    takeoutStationList.add(new RouteDTO(stations.get(i), stations.get(startIndex + j)));
                }
            }
        }
        for (int i = startIndex; i <= endIndex; i++) {
            for (int j = i + 1; j < stations.size() && i < endIndex; j++) {
                takeoutStationList.add(new RouteDTO(stations.get(i), stations.get(j)));
            }
        }
        return takeoutStationList;
    }

    public static void main(String[] args) {
        List<String> stations = Arrays.asList("北京南", "济南西", "南京南", "杭州东", "宁波");
        String startStation = "济南西";
        String endStation = "杭州东";
        StationCalculateUtil.takeoutStation(stations, startStation, endStation).forEach(System.out::println);
    }
}
