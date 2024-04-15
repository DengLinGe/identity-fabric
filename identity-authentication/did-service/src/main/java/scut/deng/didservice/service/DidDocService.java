package scut.deng.didservice.service;

import scut.deng.didservice.exception.MyException;
import scut.deng.didservice.pojo.response.BaseResponse;

public interface DidDocService {
    public BaseResponse createDID(String type, String comment) throws MyException;
    public BaseResponse getDIDDoc(String did);
    BaseResponse getAllDoc();
    BaseResponse getMyVC();
    BaseResponse applyForVP(String uuid, String[] indexVec) throws MyException;
}
