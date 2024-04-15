package scut.deng.didservice.controller;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.text.StrSpliter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.BaseDidDoc;
import scut.deng.didservice.pojo.constant.Constant;
import scut.deng.didservice.pojo.constant.EncryptType;
import scut.deng.didservice.pojo.request.DIDRequest;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.pojo.DidDoc;
import scut.deng.didservice.pojo.constant.ErrorCode;
import scut.deng.didservice.service.DidDocService;
import scut.deng.didservice.util.EncUtil;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.Map;

@Validated
@RequestMapping("did")
@RestController
@Tag(name = "DidController", description = "DID身份操作功能")
public class DidController {

    @Autowired
    public DidDocService docService;
    @Resource(name ="VCdict" )
    public Dict VCdict;

    @Operation(summary = "创建DID身份")
    @GetMapping(value = "/create")
    public BaseResponse createDID(@RequestParam(value = "type", defaultValue = "user") String type, @RequestParam(value = "comment", defaultValue = "默认评论")String comment) throws MyException {
        if (type=="user" || type=="enterprise"){
            return BaseResponse.failure(ErrorCode.REQ_ERROR);
        }
        return docService.createDID(type, comment);

    }

    @Operation(summary = "查询Fabric网络中存储的DID文档")
    @PostMapping(value = "/getDoc")
    public BaseResponse getDIDDoc(@RequestBody DIDRequest didRequest){
        return docService.getDIDDoc(didRequest.getDid());
    }

    @Operation(summary = "获得本地存储VC")
    @GetMapping(value = "getMyVC")
    public BaseResponse getMyVC(){
        return docService.getMyVC();
    }

    @Operation(summary = "查询区块链所有DID文档")
    @GetMapping("/getAllDoc")
    public BaseResponse getAllDoc()  {
        return docService.getAllDoc();
    }

    @Operation(summary = "针对VC生成VP")
    @GetMapping("applyVP")
    public BaseResponse applyVP(@RequestParam("uuid") String uuid, @RequestParam("index") String index) throws MyException {
        String[] indexVec = StrSpliter.splitToArray(index, ",", 0, true, true);
        return docService.applyForVP(uuid, indexVec);
    }

}
