package scut.deng.didservice.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "DID标识")
public class DidProof {
    @Schema(title = "类型")
    private String type;
    @Schema(title = "签名者")
    private String creator;

    @Schema(title = "签名摘要")
    private String signatureValue;
}
