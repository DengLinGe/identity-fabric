package scut.deng.didservice.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.IssuerService;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/scutIssuer")
@Tag(name = "IssuerController", description = "发证方操作功能")
public class IssuerController {


    @Autowired
    public IssuerService issuerService;

    @Operation(summary = "获得发证方列表")
    @GetMapping("/getAll")
    public BaseResponse getAllIssuers(){
        return issuerService.getIssuerLists();
    }

    @Operation(summary = "用户申请VC")
    @PostMapping("/applyVC")
    public BaseResponse applyVC(@RequestBody Map<String, Object> map) throws MyException {
        JSONObject didInfo = JSONUtil.parseObj(map.get("didInfo"));
        JSONObject provideData = JSONUtil.parseObj(map.get("provideData"));
        return issuerService.applyForVC(didInfo,provideData);
    }

}
