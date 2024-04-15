package scut.deng.didservice.pojo.issue;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Issuer {
    private int id;

    private String uuid;
    private String did;
    private String website;
    private String endpoint;

    private String shortDescription;

    private String longDescription;

    /*issuer提供的认证类型*/
    private String serviceType;

    /*需要向 issuer 提供的信息*/
//    private RequestData requestData;
    private boolean deleted;

    private LocalDateTime createtime;

    private LocalDateTime updatetime;
}
