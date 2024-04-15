package scut.deng.didservice.util;

import cn.hutool.crypto.SmUtil;
import scut.deng.didservice.pojo.MerkleTreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MerkleTreeUtil {

    private static final String addConstant = "supplement";

    private List<String> data;

    private String[] tree;
    private int oriLen;
    public MerkleTreeUtil(List<String> data){

        oriLen = data.size();
        this.data = data;
        if (oriLen%2!=0){
            this.data =data;
            this.data.add(addConstant);
            oriLen = oriLen+1;
        }
        tree =new String[2*oriLen - 1];
        int hashLen = tree.length - oriLen;
        for (int i = 0; i < data.size(); i++) {
            tree[i+hashLen] = getHash(data.get(i));
        }
        for (int i=oriLen-2; i>=0; i--){
            tree[i] = getHash(tree[2 * i + 1] + tree[2 * i + 2]);
        }
    }

    public String returnRoot(){
        return tree[0];
    }

    public String[]  returnTree(){
        return tree;
    }
/*1).首先获取需要进行md5普摘要算法的明文text。
2).随机生成加盐值（可以由字母，数字，字符组成，位数可以自己决定，这里使用16位的字符串）salt字符串.
3)将生成的salt字符串追加到明文text中，得到组合的字符串 mergeText = text +salt。
4）使用md5普通摘要算法获取mergeText 的hash值得到一个32位的字符串decodeText。
5).将盐salt字符串的每个字符逐个插入到字符串decodeText是 i *3+1（i = 0,1,2,…16）的位置上,得到一个新的48位的字符串 decode。
6）最终的decode字符串就是所要的密文，位长为48个字符。


如何加盐其实主要指的是如何选择盐，通常盐的长度需要较长，短盐的效果可能不是那么好。其实通常各种资料里会建议用一个随机生成的盐。这样能确保每个密文的盐尽量不同，增加破解难度。
但是Sunny思考了一下，使用随机生成的盐也是有一定的弊端，较大的弊端就是，这个盐也必须存储，所以也是有机会获取的。所以，我在这里就提出一种思路，至于好不好大家可以讨论一下。
首先，考虑生成的盐是每个用户有所区别的。这一点很重要，作用类似于随机生成的盐。
然后，考虑这个盐不能进行存储，而是可以用现有的用户信息进行生成。这样其实已经很明显了，可以用某种算法利用当前用户的信息（必须是固定不会修改的信息），比如用户id、用户注册时间等。这个算法也可以是哈希加密算法，比如将用户的几个信息进行一定的排序处理之后利用哈希生成盐。

*/
    public static String getHash(String data){
        String hash = SmUtil.sm3(data);
        return hash;
    }



    public static ArrayList<String> getMerklePath(String[] data, String[] hashVec, int index){
       index = index - 1;
       index = index + data.length;
        ArrayList<String> path = new ArrayList<>();
        while (index>0){
           if (index%2 != 0){ //左节点
               path.add(hashVec[index+1]);
               index = (index-1) / 2;
           }else {
               path.add(hashVec[index-1]);
               index = (index-2) / 2;
           }
       }

        return path;
    }

    public static boolean verifyPath(String root, String data, int dataSize, int index, String[] path){
        String cur = getHash(data);
        int treeIndex = dataSize+index-2;
        for (int i = 0; i < path.length; i++) {
            if (treeIndex%2 == 0){ //右节点
                cur = getHash(path[i]+cur);
                treeIndex = treeIndex/2 -1;
            }else {
                cur = getHash(cur+path[i]);
                treeIndex = treeIndex/2;
            }

        }

        return root.equals(cur);
    }

}
