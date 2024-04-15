package scut.deng.didservice.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "公钥类")
public class PublicKey {

    @Schema(title = "公钥id")
    private String id;

    @Schema(title = "公钥类型")
    private String type;

    @Schema(title = "公钥")
    private String keyString; // base64格式


}
