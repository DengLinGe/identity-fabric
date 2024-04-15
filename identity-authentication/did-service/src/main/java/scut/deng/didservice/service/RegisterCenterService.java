package scut.deng.didservice.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.issue.DIDInfo;
import scut.deng.didservice.pojo.response.BaseResponse;

public interface RegisterCenterService {

    public BaseResponse registerIssuer(DIDInfo data) throws MyException;

    BaseResponse getIssuerInfo(String website);
}
