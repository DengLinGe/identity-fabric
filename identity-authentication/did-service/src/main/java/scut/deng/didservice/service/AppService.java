package scut.deng.didservice.service;

import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.request.VerifyVCRequest;
import scut.deng.didservice.pojo.response.BaseResponse;

public interface AppService {
    BaseResponse loginApp(String did) throws MyException;
    BaseResponse verifyVC(VerifyVCRequest encodeMsg) throws MyException;
    BaseResponse verifyVP(VerifyVCRequest encodeMsg) throws MyException;
}
