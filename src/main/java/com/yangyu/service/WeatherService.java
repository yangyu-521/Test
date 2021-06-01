package com.yangyu.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.yangyu.common.Constant;
import com.yangyu.common.CustomRestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class WeatherService {
    @Value("${com.yangyu.weather.get_province}")
    private String getProvince;

    @Value("${com.yangyu.weather.get_city}")
    private String getCity;

    @Value("${com.yangyu.weather.get_country}")
    private String getCountry;

    @Value("${com.yangyu.weather.get_weather}")
    private String getWeather;

    @Autowired
    private CustomRestTemplate restTemplate;

    public String getProvinceCodeByName(String provinceName) {
        JSONObject data = restTemplate.getInfo(getProvince);
        return getKeyFromJsonObject(data, provinceName);
    }

    public String getCityCodeByName(String provinceCode, String cityName) {
        JSONObject data = restTemplate.getInfo(getCity + provinceCode + Constant.SUFFIX_HTML);
        return getKeyFromJsonObject(data, cityName);
    }

    public String getCountryCodeByName(String provinceCode, String cityCode, String countryName) {
        JSONObject data = restTemplate.getInfo(getCountry + provinceCode + cityCode + Constant.SUFFIX_HTML);
        return getKeyFromJsonObject(data, countryName);
    }

    public String getTemperature(String provinceName, String cityName, String countryName) {
        String provinceCode = getProvinceCodeByName(provinceName);
        if (null == provinceCode) {
            log.error("Invalid province name:{}", provinceName);
            return null;
        }

        String cityCode = getCityCodeByName(provinceCode, cityName);
        if (null == cityCode) {
            log.error("Invalid province name:{} or city name:{}", provinceName, cityName);
            return null;
        }

        String countryCode = getCountryCodeByName(provinceCode, cityCode, countryName);
        if (null == countryCode) {
            log.error("Invalid province name:{} or city name:{} or country name:{}", provinceName, cityName, countryName);
            return null;
        }

        JSONObject data = restTemplate.getInfo(getWeather + provinceCode + cityCode + countryCode + Constant.SUFFIX_HTML);
        if (null == data) {
            log.error("Fetch weather info failed");
            return null;
        }
        return data.getJSONObject("weatherinfo").getString("temp");
    }

    private String getKeyFromJsonObject(JSONObject data, String value) {
        for (String key: data.keySet()) {
            if (data.getString(key).equals(value)) {
                return key;
            }
        }
        return null;
    }
}
