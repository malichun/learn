package com.atguigu.project.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AdsClickLog {
    private Long userId;
    private Long adId;
    private String province;
    private String city;
    private Long timestamp;
}