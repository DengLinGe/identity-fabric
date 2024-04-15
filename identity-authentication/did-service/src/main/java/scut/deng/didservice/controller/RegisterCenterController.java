package scut.deng.didservice.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.issue.DIDInfo;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.RegisterCenterService;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/registerCenter")
@Validated
@Tag(name = "RegisterCenterController", description = "注册中心功能")
public class RegisterCenterController {


    @Autowired
    public RegisterCenterService registerCenterService;

    @Operation(summary = "注册发证机构")
    @PostMapping("/register")
    public BaseResponse registerIssuer( @RequestBody @Validated DIDInfo data) throws MyException {

        return registerCenterService.registerIssuer(data);
    }

    @Operation(summary = "获取发证方注册所需熟悉")
    @GetMapping ("/getIssuerInfo")
    public BaseResponse getIssuerInfo(@NotNull @RequestParam("website") String website){
        return registerCenterService.getIssuerInfo(website);

    }

}
