package com.yangyu.controller;

import com.yangyu.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WeatherController {
    @Autowired
    private WeatherService weatherService;
    @RequestMapping("/")
    public String index(){
        weatherService.getTemperature("湖北", "武汉", "新洲");
        return "hello";
    }
}
