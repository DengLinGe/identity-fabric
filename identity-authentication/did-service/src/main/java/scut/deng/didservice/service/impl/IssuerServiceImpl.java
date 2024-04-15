package scut.deng.didservice.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.Proof;
import scut.deng.didservice.pojo.constant.Constant;
import scut.deng.didservice.pojo.constant.ErrorCode;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.IssuerService;
import scut.deng.didservice.util.EncUtil;
import scut.deng.didservice.util.MerkleTreeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static scut.deng.didservice.pojo.constant.Constant.*;


@Service
@Slf4j
public class IssuerServiceImpl implements IssuerService {


    @Resource(name ="DIDdict" )
    public Dict DIDdict;

    @Resource(name ="Enterprisedict" )
    public Dict Enterprisedict;
    @Resource(name ="VCdict" )
    public Dict VCdict;

    @Autowired
    public RestTemplate restTemplate;


    @Override
    public BaseResponse getIssuerLists() {
        ResponseEntity<String> response = restTemplate.getForEntity( ISSUER_CLIENT+ "getAll", String.class);
        if (response.getStatusCodeValue()!=200){
            return BaseResponse.failure(ErrorCode.REQUEST_ERROR);
        }
        String body = response.getBody();
        if (body == null || body.equals("")){
            return new BaseResponse<>(999, "请求不到任何发证方数据");
        }
        return BaseResponse.success(JSONUtil.parseArray(body));
    }

    @Override
    public BaseResponse applyForVC(JSONObject didInfo, JSONObject provideData) throws MyException {
        String did_id = didInfo.getStr("did");
        ResponseEntity<Boolean> entity = restTemplate.getForEntity(Constant.FABRIC_CLIENT + "getIfDocExist?did={did}", Boolean.class, did_id);
        if(!entity.getBody()){
            throw new MyException(ErrorCode.NO_DIDDOC, "did不存在，无法进行注册");
        }
        String idCard = provideData.get("IdCard").toString();
        if (!IdcardUtil.isValidCard(idCard)) {
            throw new MyException("身份证格式不正确/数据库查询不到相关记录");
        }
        JSONObject VC = new JSONObject(true);
        String uuid = IdUtil.simpleUUID();
        VC.set("uuid", uuid);
        VC.set("type", "ProofClaim");
        VC.set("issuer", "did:scut:computer");
        LocalDateTime now = LocalDateTime.now();
        VC.set("issuanceDate", now.toString());
        VC.set("expirationDate", now.plusYears(30).toString());
        JSONObject jsonObject = new JSONObject(true);
        jsonObject.set("id", didInfo.getStr("did"));
        String hideCard = StrUtil.hide(idCard, 4, 4);
        jsonObject.set("idCard", hideCard);
        jsonObject.set("school", "华南理工大学");
        jsonObject.set("degree", "本科");
        jsonObject.set("degreeType", "工科");
        jsonObject.set("college", "计算机学院");
        jsonObject.set("date","2023-6-19");
        ArrayList<String> attributeList = CollUtil.newArrayList("idCard", "school", "degree", "degreeType", "college", "date");


        /*取出学校DID的私钥用于加密*/
        if(!Enterprisedict.containsKey(KEY_1)){
            throw new MyException("学校的DID还没有生成！");
        }
        String sk = Enterprisedict.getStr(KEY_1);
        String did = Enterprisedict.getStr(DID);


        /*生成默克尔树*/
        ArrayList<String> list = CollUtil.newArrayList(hideCard,"华南理工大学", "本科", "工科", "计算机学院", "2023-6-19");
        MerkleTreeUtil merkleTreeUtil = new MerkleTreeUtil(list);
        //默克尔根哈希
        jsonObject.set("merkleRoot", merkleTreeUtil.returnRoot());
        //对默克尔根的签名
        jsonObject.set("rootSignature",EncUtil.digestMsgUseSK(merkleTreeUtil.returnRoot(), sk));
        jsonObject.set("signer", did);

        VC.set("credentialSubject", jsonObject);
        VC.set("revocation", "https://www.scut.edu.cn");



        Proof proof = new Proof();
        // 使用私钥将密文加密
        String docString = JSONUtil.toJsonStr(BeanUtil.beanToMap(VC));
        String encstring = EncUtil.digestMsgUseSK(docString, sk);
        proof.setType("rsa");
        proof.setCreator(did);
        proof.setSignatureValue(encstring);
        VC.set("proof", proof);




        VCdict.set(uuid, VC);
        VCdict.set(uuid+ MERKLE, merkleTreeUtil.returnTree()); //存储VC属性的全部默克尔树节点数据
        VCdict.set(uuid+ ATTRIBUTE, attributeList); //存储VC属性
        return BaseResponse.success(VC);
    }
}
