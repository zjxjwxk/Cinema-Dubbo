package com.stylefeng.guns.rest.modular.example;

import com.stylefeng.guns.rest.common.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 常规控制器
 *
 * @author fengshuonan
 * @date 2017-08-23 16:02
 */
@Controller
@RequestMapping("/hello")
public class ExampleController {

    @RequestMapping("")
    public ResponseEntity hello() {

        System.out.println(CurrentUser.getCurrentUser());

        // 这里将以userId为key，userInfo为value存在redis中，TTL为30分钟
        // 或以userId为条件去数据库取出（和redis只有快慢的区别）

        return ResponseEntity.ok("请求成功!");
    }
}
