package org.denglg.hyperledgerfabric.app.javademo.pojo.request;


import lombok.Data;

@Data
public class DIDRequest {
    private String did;
    private String doc;

    private String key;

    private String newValue;
}
