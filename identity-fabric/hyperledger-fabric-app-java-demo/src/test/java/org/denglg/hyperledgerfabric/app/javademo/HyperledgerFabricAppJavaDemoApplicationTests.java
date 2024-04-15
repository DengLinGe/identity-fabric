package org.denglg.hyperledgerfabric.app.javademo;

import org.denglg.hyperledgerfabric.app.javademo.controller.DIDController;
import org.denglg.hyperledgerfabric.app.javademo.pojo.request.DIDRequest;
import org.hyperledger.fabric.gateway.ContractException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeoutException;

@SpringBootTest
class HyperledgerFabricAppJavaDemoApplicationTests {

    @Autowired
    public DIDController didController;
    @Test
    void contextLoads() throws ContractException, InterruptedException, TimeoutException {
        DIDRequest diddoc = new DIDRequest();
        diddoc.setDid("b");
        diddoc.setDoc("12345");

            didController.saveDoc(diddoc);

    }

}
