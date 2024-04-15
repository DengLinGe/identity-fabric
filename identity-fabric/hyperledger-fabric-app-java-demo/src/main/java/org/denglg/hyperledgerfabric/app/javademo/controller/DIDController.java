package org.denglg.hyperledgerfabric.app.javademo.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.denglg.hyperledgerfabric.app.javademo.pojo.BaseResponse;
import org.denglg.hyperledgerfabric.app.javademo.pojo.constant.ErrorCode;
import org.denglg.hyperledgerfabric.app.javademo.pojo.request.DIDRequest;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Peer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/fabric")
@Slf4j
public class DIDController {
    @Autowired
    public Gateway gateway;
    @Autowired
    public Contract contract;
    @Autowired
    public Network network;

    @GetMapping("/getDoc")
    public String queryDocByKey(@RequestParam(name = "did") String did) throws GatewayException {


        byte[] bytes = contract.evaluateTransaction("GetDoc", did);
        if (bytes.length == 0) {
            return "";
        }
        return StringUtils.newStringUtf8(bytes);
    }

    @GetMapping("/getIfDocExist")
    public Boolean getIfDocExist(@RequestParam(name = "did") String did) throws GatewayException {


        byte[] bytes = contract.evaluateTransaction("GetDoc", did);
        if (bytes.length == 0) {
            return false;
        }
        return true;
    }

    @PostMapping("/saveDoc")
    public BaseResponse saveDoc(@RequestBody DIDRequest didDoc) throws InterruptedException, TimeoutException {
        log.info("============start to save the DID doc==================");
        log.info("DID is '{}'", didDoc.getDid());

        Network network = gateway.getNetwork("mychannel");
        try {
            byte[] bytes = contract.createTransaction("SaveDoc")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(didDoc.getDid(), didDoc.getDoc());
            log.info("============successfully save the DID doc==============");
            return BaseResponse.success(StringUtils.newStringUtf8(bytes));
        } catch (ContractException e) {
            return BaseResponse.failure(ErrorCode.EXIST_DOC);
        }


    }

    @PostMapping("/changDoc")
    public BaseResponse changDoc(@RequestBody DIDRequest didRequest) throws ContractException, InterruptedException, TimeoutException {

        byte[] bytes = contract.createTransaction("ChangDoc")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(didRequest.getDid(), didRequest.getKey(), didRequest.getNewValue());
        System.out.println(bytes.length);
        if (bytes.length == 0) {
            return BaseResponse.failure(ErrorCode.EXIST_DOC);
        }
        return BaseResponse.success(StringUtils.newStringUtf8(bytes));
    }

    @PostMapping("/deleteDoc")
    public BaseResponse deleteDoc(@RequestBody DIDRequest didRequest) throws InterruptedException, TimeoutException {
        try {
            byte[] bytes = contract.createTransaction("DeleteDoc")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(didRequest.getDid());
            return BaseResponse.success(StringUtils.newStringUtf8(bytes));
        } catch (ContractException e) {
            return BaseResponse.failure(ErrorCode.NO_DOC);
        }


    }

    @GetMapping("/DeleteAndInit")
    public BaseResponse deleteAllDoc() throws InterruptedException, TimeoutException {
        try {
            byte[] bytes = contract.createTransaction("DeleteAndInit")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit();
            return BaseResponse.success(StringUtils.newStringUtf8(bytes));
        } catch (ContractException e) {
            return BaseResponse.failure(ErrorCode.NO_DOC);
        }


    }


    @GetMapping("/getAllDoc")
    public String getAllDoc() {
        try {
            byte[] bytes = contract.evaluateTransaction("GetAllDoc");
            return StringUtils.newStringUtf8(bytes);
        } catch (ContractException e) {
            e.printStackTrace();
            return "";
        }

    }


    @GetMapping("/deleteAndInit")
    public BaseResponse deleteAndInit() {
        try {
            byte[] bytes = contract.createTransaction("DeleteAndInit")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit();
            return BaseResponse.success(StringUtils.newStringUtf8(bytes));
        } catch (ContractException e) {
            return new BaseResponse(ErrorCode.NO_DOC, e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("test")
    public void test() {
        log.info("12344444444444444444444");
    }
}
