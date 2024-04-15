package scut.deng.didservice.pojo.constant;


public enum EncryptType {

    RSA("rsa"),
    SECP256K1("Secp256k1");

    private String type;



    EncryptType(String type){
        this.type = type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

}
