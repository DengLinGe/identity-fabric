package scut.deng.didservice.pojo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import scut.deng.didservice.pojo.constant.Constant;
import scut.deng.didservice.pojo.constant.EncryptType;
import scut.deng.didservice.util.EncUtil;

import java.util.List;

@Data
@Schema(name = "DID文档类")
public class DidDoc {

    @NotNull
    @Schema(title = "DID标识")
    private String didID;
    @NotNull
    @Schema(title = "版本号")
    private Integer version;

    @Schema(title = "创建时间")
    private String createTime;

    @Schema(title = "更新时间")
    private String updateTime;

    @Schema(title = "公钥列表")
    private List<PublicKey> keyList;

    @Schema(title = "文档类型")
    private String type;

    @Schema(title = "备注")
    private String comment;
    @NotNull(message = "公钥对应的私钥的用户就是此 DID 的拥有者，不能为空")
    @Schema(title = "认证公钥")
    private String authentication;

    @NotNull(message = "恢复公钥不能为空")
    @Schema(title = "恢复公钥")
    private String recovery;

    @Schema(title = "服务列表")
    private List<DidService> serviceList;

    @Schema(title = "证明")
    private Proof proof;
    public static DidDoc createNewDID(String did, BaseDidDoc baseDidDoc, String sk){
        DidDoc didDoc = new DidDoc();
        // 设置did标识
        didDoc.setDidID(did);
        // 设置时间
        String now = DateUtil.now();
        didDoc.setCreateTime(now);
        didDoc.setUpdateTime(now);
        // 设置版本
        didDoc.setVersion(1);
        // 设置认证以及恢复私钥
        didDoc.setAuthentication(did + baseDidDoc.getAuthentication());
        didDoc.setRecovery(did + baseDidDoc.getRecovery());
        //
        baseDidDoc.getKeyList().stream().forEach( x -> {
            x.setId(did + x.getId());
        });
        // 设置公钥列表
        didDoc.setKeyList(baseDidDoc.getKeyList());
        // 使用私钥将密文加密
        System.out.println(didDoc);
        String docString = JSONUtil.toJsonStr(BeanUtil.beanToMap(didDoc));
        String encstring = EncUtil.digestMsgUseSK(docString, sk);
        Proof proof = new Proof();
        proof.setType(EncryptType.RSA.getType());
        proof.setCreator(did + "#key-1");
        proof.setSignatureValue(encstring);
        didDoc.setProof(proof);

        return didDoc;
    }
}
