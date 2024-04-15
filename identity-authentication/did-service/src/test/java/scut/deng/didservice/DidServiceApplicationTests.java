package scut.deng.didservice;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import scut.deng.didservice.controller.DidController;
import scut.deng.didservice.service.DidDocService;
import scut.deng.didservice.util.EncUtil;
import scut.deng.didservice.util.MerkleTreeUtil;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.ArrayList;

@SpringBootTest
class DidServiceApplicationTests {


    @Autowired
    public DidController didController;

    @Autowired
    private DidDocService didDocService;
    @Test
    void contextLoads() {
        KeyPair pair = SecureUtil.generateKeyPair("RSA");
        String sk = Base64.encode(pair.getPrivate().getEncoded());
        String pk = Base64.encode(pair.getPublic().getEncoded());
        String msg = "我爱你";
        String msgUseSK = EncUtil.digestMsgUseSK(msg, sk);
        System.out.println(msgUseSK);
        String s = EncUtil.deDigestMsgUsePK(msgUseSK, pk);
        System.out.println(s);
    }

    @Test
    void test1(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("2002-7-22");
        arrayList.add("440902200007220816");
        arrayList.add("abc");
        MerkleTreeUtil treeUtil = new MerkleTreeUtil(arrayList);
    }



}
