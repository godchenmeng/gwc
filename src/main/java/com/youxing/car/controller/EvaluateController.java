package com.youxing.car.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.youxing.car.service.EvaluateService;
@Controller
public class EvaluateController {
         @Resource
         private EvaluateService evaluateService;

}
