package scut.deng.didservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.DidDoc;
import scut.deng.didservice.pojo.Proof;
import scut.deng.didservice.pojo.PublicKey;
import scut.deng.didservice.pojo.constant.Constant;
import scut.deng.didservice.pojo.constant.ErrorCode;
import scut.deng.didservice.pojo.request.VerifyVCRequest;
import scut.deng.didservice.pojo.response.BaseResponse;
import scut.deng.didservice.service.AppService;
import scut.deng.didservice.util.EncUtil;
import scut.deng.didservice.util.MerkleTreeUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static scut.deng.didservice.pojo.constant.Constant.*;


@Service
public class AppServiceImpl implements AppService {

    @Autowired
    public RestTemplate restTemplate;

    @Resource(name = "DIDdict")
    public Dict DIDdict;

    @Resource(name = "Enterprisedict")
    public Dict Enterprisedictdict;


    @Resource(name = "VCdict")
    public Dict VCdict;
    @Autowired
    public HashMap<String, Integer> hashMap;

    @Override
    public BaseResponse loginApp(String did) throws MyException {
        /*
         * 1. 在区块链上获得did文档
         * 2. 取得did公钥，并且利用did公钥加密一段随机数，然后加密一段随机数发送回给客户端
         * 3. 客户端用私钥解密后再发发回
         * */

        ResponseEntity<String> entity = restTemplate.getForEntity(Constant.FABRIC_CLIENT+"getDoc?did={did}", String.class, did);


        if (entity.getStatusCodeValue() != 200||entity.getBody() == null) {
            throw new MyException(ErrorCode.NO_DIDDOC, "请求fabric端接口失败");
        }

        DidDoc diddoc = JSONUtil.toBean(entity.getBody(), DidDoc.class);

        PublicKey publicKey = diddoc.getKeyList().get(0);
        String pk = publicKey.getKeyString();
        int nonce = RandomUtil.randomInt();
        String encodeMsg = EncUtil.encoderMsgUsePK(String.valueOf(nonce), pk);
        hashMap.put(did, nonce);

        return BaseResponse.success(encodeMsg);
    }

    @Override
    public BaseResponse verifyVC(VerifyVCRequest encodeMsg) throws MyException {
        Integer oriNonce = hashMap.get(encodeMsg.getDid());
        String sk = DIDdict.getStr(KEY_1);
        String nonce = EncUtil.decodeMsgUsesk(encodeMsg.getEncodeMsg(), sk);
        if (Integer.parseInt(nonce)!=oriNonce){
            throw new MyException(ErrorCode.ENC_ERROR, "与公钥加密的原始密文不相同！");
        }
        if (!VCdict.containsKey(encodeMsg.getUuid())) {
            throw new MyException(ErrorCode.REQ_ERROR, "不存在该UUID对应的VC文档");
        }
        String VCStr = VCdict.getStr(encodeMsg.getUuid());
        JSONObject VC = JSONUtil.parseObj(VCStr, true, true);

        Proof proof = JSONUtil.toBean(VC.getJSONObject("proof"), Proof.class);
        DidDoc issuer_doc = getDoc(proof.getCreator());
        String issuer_pk = issuer_doc.getKeyList().get(0).getKeyString();
        VC.remove("proof");
        String VCString = JSONUtil.toJsonStr(BeanUtil.beanToMap(VC));
        String VCString_decode = EncUtil.deDigestMsgUsePK(proof.getSignatureValue(), issuer_pk);


        if (VCString_decode.equals(VCString)){
            return BaseResponse.success(null, "验证通过，允许登录");
        }

        return new BaseResponse(999, "验证不通过，与摘要不同");
    }

    @Override
    public BaseResponse verifyVP(VerifyVCRequest encodeMsg) throws MyException {
        Integer oriNonce = hashMap.get(encodeMsg.getDid());
        String sk = DIDdict.getStr(KEY_1);
        String nonce = EncUtil.decodeMsgUsesk(encodeMsg.getEncodeMsg(), sk);
        if (Integer.parseInt(nonce)!=oriNonce){
            throw new MyException(ErrorCode.ENC_ERROR, "与公钥加密的原始密文不相同！");
        }
        if (!VCdict.containsKey(encodeMsg.getUuid())) {
            throw new MyException(ErrorCode.REQ_ERROR, "不存在该UUID对应的VC文档");
        }
        JSONObject oriVP = (JSONObject) VCdict.get(encodeMsg.getUuid() + Constant.VP);
        JSONObject VP = JSONUtil.parseObj(oriVP,true, true);
        /*验证VP步骤:
        * 1.验证VP用户自身的签名是有效的
        * 2.获取到公安部的DID，验证公安部的did是有效的
        * 3.验证公安部的公钥对默克尔根的签名是否正确
        * 4.对纰漏字段验证*/

//        阶段二
        Proof proof = JSONUtil.toBean(VP.getJSONObject("proof"), Proof.class);
        DidDoc user_doc = getDoc(proof.getCreator());
        String user_pk = user_doc.getKeyList().get(0).getKeyString();
        VP.remove("proof");
        String VPString = JSONUtil.toJsonStr(BeanUtil.beanToMap(VP));
        String VPString_decode = EncUtil.deDigestMsgUsePK(proof.getSignatureValue(), user_pk);
        if (!VPString_decode.equals(VPString)){
            return new BaseResponse(999, "用户公钥验证不通过！！");
        }
//        阶段三
        JSONObject credentialSubject = VP.getJSONObject("credentialSubject");
        String root = credentialSubject.getStr("merkleRoot");
        String rootSignature = credentialSubject.getStr("rootSignature");
        DidDoc issuer_doc = getDoc(credentialSubject.getStr("signer"));
        String issuer_pk = issuer_doc.getKeyList().get(0).getKeyString();
        String decodeRoot = EncUtil.deDigestMsgUsePK(rootSignature, issuer_pk);
        if(!decodeRoot.equals(root)){
            return new BaseResponse(999, "公钥验证不通过，默克尔根遭遇篡改。");
        }
//        阶段四
        JSONArray propertiesArray = credentialSubject.getJSONArray("properties");
        ArrayList<String> attribute = (ArrayList<String>) VCdict.get(encodeMsg.getUuid() + ATTRIBUTE);
        for (int i = 0; i < propertiesArray.size(); i++) {
            JSONObject property = (JSONObject) propertiesArray.get(i);
            String[] merklePath = property.getJSONArray("merklePath").toArray(new String[0]);
            if(!MerkleTreeUtil.verifyPath(root, property.getStr(attribute.get(property.getInt("dataIndex") - 1)), attribute.size(), property.getInt("dataIndex"), merklePath)){
                return new BaseResponse(999, "默克尔路径验证不通过，属性遭遇篡改");
            }
        }
        return BaseResponse.success(null, "验证通过，允许登录");
    }




    public DidDoc getDoc(String issuer_did) throws MyException {
        ResponseEntity<String> entity = restTemplate.getForEntity(Constant.FABRIC_CLIENT+"getDoc?did={did}", String.class, issuer_did);
        if (entity.getStatusCodeValue() != 200||entity.getBody()==null) {
            throw new MyException(ErrorCode.NO_DIDDOC,"该VC中的证明proof对应的did无效");
        }
        DidDoc issuer_doc = JSONUtil.toBean(entity.getBody(), DidDoc.class);
        return issuer_doc;
    }
    

}
