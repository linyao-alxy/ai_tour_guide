package com.guide.avatar.controller;

import com.guide.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AvatarAdminController {

    @PostMapping("/avatar/config")
    public R<String> configAvatar(@RequestBody Map<String, Object> config) {
        // TODO: 保存数字人配置
        return R.ok("保存成功");
    }

    @GetMapping("/avatar/regions")
    public R<List<Map<String, String>>> getRegions() {
        return R.ok(List.of(
                Map.of("name","江南水乡","base","gentle_female"),
                Map.of("name","唐风遗址","base","plump_female"),
                Map.of("name","佛教圣地","base","kind_elder")
        ));
    }
}
