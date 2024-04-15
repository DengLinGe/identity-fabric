package scut.deng.didservice.pojo;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MerkleTreeNode {
    private String data=null;
    private String hash=null;

    private MerkleTreeNode left=null;
    private MerkleTreeNode right=null;

    public MerkleTreeNode(String data, MerkleTreeNode left, MerkleTreeNode right) {
        this.left = left;
        this.right = right;
        String hash = SmUtil.sm3(data);
        this.hash = hash;
        this.data = data;
    }
}
