package scut.deng.didservice.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "DID服务")
public class DidService {
    @Schema(title = "服务ID")
    private String id;

    @Schema(title = "服务类型")
    private String type;

    @Schema(title = "服务端点")
    private String serviceEndpoint;
}
