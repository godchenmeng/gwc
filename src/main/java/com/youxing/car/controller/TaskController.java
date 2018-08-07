package com.youxing.car.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.youxing.car.service.TaskService;
@Controller
public class TaskController {
         @Resource
         private TaskService taskService;

}
