package org.denglg.hyperledgerfabric.app.javademo.controller;


import org.apache.commons.codec.binary.StringUtils;
import org.denglg.hyperledgerfabric.app.javademo.pojo.BaseResponse;
import org.denglg.hyperledgerfabric.app.javademo.pojo.constant.ErrorCode;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/issuer")

public class IssuerController {
    @Autowired
    public Gateway gateway;
    @Autowired
    public Contract contract;
    @Autowired
    public Network network;


    @PostMapping("/add")
    public BaseResponse addIssuer(@RequestBody Map<String, String> data) throws InterruptedException, TimeoutException {
        String uuid = data.get("uuid");
        String issuerInfo = data.get("issuerInfo");
        try {
            byte[] bytes = contract.createTransaction("AddIssuer")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(uuid, issuerInfo);
            return BaseResponse.success(StringUtils.newStringUtf8(bytes));
        } catch (ContractException e) {
            return BaseResponse.failure(ErrorCode.REMOTE_ERROR, e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public String  getAllIssuers() throws InterruptedException, TimeoutException {
        try {
            byte[] bytes = contract.createTransaction("GetIssuerList")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit();
            return StringUtils.newStringUtf8(bytes);
        } catch (ContractException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    
    
}
