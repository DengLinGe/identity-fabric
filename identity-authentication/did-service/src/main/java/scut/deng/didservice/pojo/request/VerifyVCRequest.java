package scut.deng.didservice.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "VerifyVCRequest", title ="验证VC请求" )
public class VerifyVCRequest {
    @Schema(name = "did", title= "did标识")
    @NotNull
    public String did;
    @NotNull
    @Schema(name = "uuid",title = "VC编号")
    public String uuid;
    @NotNull
    @Schema(name = "encodeMsg", title = "加密密文")
    public String encodeMsg;
}
