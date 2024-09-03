package cn.huangdayu.things.engine.async;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.BitSet;
import java.util.LinkedList;

/**
 * 布隆过滤器
 *
 * @author huangdayu
 */
public class BloomFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final BitSet bitSet;
    private final LinkedList<String> linkedList;
    private final int numHashFunctions;


    public BloomFilter(int expectedElements, double falsePositiveProbability) {
        // 计算位数组大小
        int optimalSize = optimalBitSetSize(expectedElements, falsePositiveProbability);
        // 计算哈希函数数量
        numHashFunctions = optimalNumHashFunctions(expectedElements, optimalSize);

        // 初始化位数组和链表
        bitSet = new BitSet(optimalSize);
        linkedList = new LinkedList<>();
    }

    /**
     * 添加元素到布隆过滤器
     *
     * @param element
     */
    public void add(String element) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(element, i);
            bitSet.set(hash, true);
        }
        linkedList.add(element);
    }

    /**
     * 移除元素
     *
     * @param element
     */
    public void remove(String element) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(element, i);
            bitSet.clear(hash);
        }
        linkedList.remove(element);
    }

    /**
     * 查询元素是否存在
     *
     * @param element
     * @return
     */
    public boolean mightContain(String element) {
        for (int i = 0; i < numHashFunctions; i++) {
            int hash = hash(element, i);
            if (!bitSet.get(hash)) {
                return false;
            }
        }
        return linkedList.contains(element);
    }

    /**
     * 序列化
     *
     * @param filePath
     * @throws IOException
     */
    public void serialize(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this);
        }
    }

    /**
     * 反序列化
     *
     * @param filePath
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static BloomFilter deserialize(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (BloomFilter) ois.readObject();
        }
    }

    /**
     * 序列化为字符串
     *
     * @return
     * @throws IOException
     */
    public String serializeToString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 从字符串反序列化
     *
     * @param serialized
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static BloomFilter deserializeFromString(String serialized) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(serialized);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (BloomFilter) ois.readObject();
    }


    /**
     * 序列化为16进制字符串
     *
     * @return
     * @throws IOException
     */
    public String serializeToHex() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();

        byte[] bytes = baos.toByteArray();
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    /**
     * 从16进制字符串反序列化
     *
     * @param hexString
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static BloomFilter deserializeFromHex(String hexString) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (BloomFilter) ois.readObject();
    }

    /**
     * 计算最优的位数组大小
     *
     * @param expectedElements
     * @param falsePositiveProbability
     * @return
     */
    private int optimalBitSetSize(int expectedElements, double falsePositiveProbability) {
        return (int) (-expectedElements * Math.log(falsePositiveProbability) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算最优的哈希函数数量
     *
     * @param expectedElements
     * @param optimalSize
     * @return
     */
    private int optimalNumHashFunctions(int expectedElements, int optimalSize) {
        return (int) (optimalSize / expectedElements * Math.log(2));
    }

    /**
     * 生成哈希值
     *
     * @param element
     * @param index
     * @return
     */
    private int hash(String element, int index) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(element.getBytes());
            int hash = (digest[index % digest.length] & 0xFF) << (index * 8);
            return Math.abs(hash) % bitSet.size();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
