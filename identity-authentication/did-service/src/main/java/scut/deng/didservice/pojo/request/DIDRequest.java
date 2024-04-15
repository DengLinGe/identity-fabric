package scut.deng.didservice.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "DIDRequest", title ="DID请求" )
public class DIDRequest {
    public String did;
}
