package scut.deng.didservice.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import scut.deng.didservice.exception.MyException;

import java.security.Key;

public class EncUtil {




    /*将Key转换为Base64格式*/
    public static String keyToString(Key key){
        byte[] encoded_key = key.getEncoded();
        String string_key = Base64.encode(encoded_key);
        return string_key;
    }

    public static String digestMsgUseSK(String msg, String sk){
        RSA rsa = new RSA(sk, null);
//        String str = rsa.encryptBase64(msg, KeyType.PrivateKey);
        String str = rsa.encryptHex(msg, KeyType.PrivateKey);
        return str;
    }

    public static String deDigestMsgUsePK(String msg,String pk){
        RSA rsa = new RSA(null, pk);
        String str = rsa.decryptStr(msg, KeyType.PublicKey);

        return str;
    }

    public static String encoderMsgUsePK(String msg, String pk){
        byte[] decode = Base64.decode(pk);
        RSA rsa = new RSA(null, pk);
        String str = rsa.encryptBase64(msg, KeyType.PublicKey);
        return str;
    }

    public static String decodeMsgUsesk(String msg,String sk) throws MyException {
        try{
            byte[] decode = Base64.decode(sk);
            RSA rsa = new RSA(sk, null);
            String str = rsa.decryptStr(msg, KeyType.PrivateKey);
            return str;
        }catch (CryptoException e){
            throw new MyException("该密文无法用该私钥进行解密，解密出错！");
        }



    }
}
