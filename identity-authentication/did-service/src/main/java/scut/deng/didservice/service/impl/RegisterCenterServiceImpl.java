package scut.deng.didservice.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.constant.Constant;
import scut.deng.didservice.pojo.constant.ErrorCode;
import scut.deng.didservice.pojo.issue.DIDInfo;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.RegisterCenterService;

import java.time.LocalDateTime;
import java.util.HashMap;

import static scut.deng.didservice.pojo.constant.Constant.ISSUER_CLIENT;


@Service

public class RegisterCenterServiceImpl implements RegisterCenterService {
    @Resource(name = "web")
    public Dict dict;
    @Autowired
    public RestTemplate restTemplate;


    @Override
    public BaseResponse registerIssuer(DIDInfo data) throws MyException {

        /*
         * 1.获得目前id数量，然后递增加1
         * 2.填入对应的信息
         * 3.
         * */
        String did = data.getDid();
        ResponseEntity<Boolean> entity = restTemplate.getForEntity(Constant.FABRIC_CLIENT + "getIfDocExist?did={did}", Boolean.class, did);
        if(!entity.getBody()){
            throw new MyException(ErrorCode.NO_DIDDOC, "该企业的did不存在，无法进行注册");
        }


        JSONObject didInfo = JSONUtil.parseObj(data);

        Integer issuerNum = dict.getInt("issuerNum");

        didInfo.set("id",issuerNum+1);
        String uuid = IdUtil.simpleUUID();
        didInfo.set("uuid", uuid);

        /*
         * didInfo包含：website\endpoint\Description\serviceType
         * */

        didInfo.set("deleted", false);
        didInfo.set("createTime", LocalDateTime.now().toString());
        didInfo.set("updateTime", LocalDateTime.now().toString());
        dict.replace("issuerNum", issuerNum+1);
        HashMap<String, JSONObject> webServicePoint = (HashMap<String, JSONObject>) dict.get("webServicePoint");
        webServicePoint.put(didInfo.getStr("website"), didInfo);
        dict.replace("webServicePoint", webServicePoint);
        HashMap<String, Object> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("issuerInfo", JSONUtil.toJsonStr(didInfo));
        ResponseEntity<String> response = restTemplate.postForEntity( ISSUER_CLIENT+ "add", params, String.class);
        if (response.getStatusCodeValue()!=200){
            return BaseResponse.failure(ErrorCode.REQUEST_ERROR);
        }

        return BaseResponse.success(didInfo);

    }

    @Override
    public BaseResponse getIssuerInfo(String website) {

        HashMap<String, JSONObject> webServicePoint = (HashMap<String, JSONObject>) dict.get("webServicePoint");
        if (!webServicePoint.containsKey(website)){
            return BaseResponse.failure(ErrorCode.REQ_ERROR);
        }
        JSONObject jsonObject = webServicePoint.get(website);
        String endpoint = (String) jsonObject.get("endpoint");
        Object requestData = jsonObject.get("requestData");
        JSONObject re = new JSONObject();
        re.set("endpoint", endpoint);
        re.set("requestData", requestData);
        return BaseResponse.success(re);
    }
}
