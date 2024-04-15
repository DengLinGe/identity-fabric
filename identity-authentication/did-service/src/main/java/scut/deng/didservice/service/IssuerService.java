package scut.deng.didservice.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.response.BaseResponse;

public interface IssuerService {


    public BaseResponse getIssuerLists();


    BaseResponse applyForVC(JSONObject didInfo, JSONObject provideData) throws MyException;

}
