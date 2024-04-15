package scut.deng.didservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.request.VerifyVCRequest;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.AppService;

@RestController
@RequestMapping("/app")
@Validated
@Tag(name = "AppController", description = "模拟验证方应用功能")
public class AppController {


//    1. 先认证did是属于自己的
//    2.再认证vc
    @Autowired
    public AppService appService;


    @Operation(summary = "登录应用")
    @GetMapping("/login")
    public BaseResponse loginApp(@RequestParam(name = "did") String did) throws MyException {
        return appService.loginApp(did);

    }

    @Operation(summary = "验证用户VC")
    @PostMapping("/verifyVC")
    public BaseResponse verifyVC(@RequestBody VerifyVCRequest encodeMsg) throws MyException {
        return appService.verifyVC(encodeMsg);
    }

    @Operation(summary = "应用验证VP")
    @PostMapping("/verifyVP")
    public BaseResponse verifyVP(@RequestBody VerifyVCRequest encodeMsg) throws MyException {
        return appService.verifyVP(encodeMsg);
    }

}
