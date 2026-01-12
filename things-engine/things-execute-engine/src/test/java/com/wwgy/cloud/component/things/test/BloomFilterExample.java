//package cn.huangdayu.things.engine.test;
//
//import cn.huangdayu.things.engine.async.BloomFilter;
//
//import java.io.IOException;
//
//public class BloomFilterExample {
//    public static void main(String[] args) {
//        try {
//            // 创建布隆过滤器实例
//            BloomFilter bloomFilter = new BloomFilter(10000, 0.01);
//
//            // 添加元素
//            String[] elementsToAdd = {"apple", "banana", "cherry", "date"};
//            for (String element : elementsToAdd) {
//                bloomFilter.add(element);
//            }
//
//            // 序列化
//            bloomFilter.serialize("temp/files/serialize/bloomfilter.ser");
//
//            // 反序列化
//            BloomFilter deserializedBloomFilter = BloomFilter.deserialize("temp/files/serialize/bloomfilter.ser");
//
//            // 查询元素是否存在
//            System.out.println(deserializedBloomFilter.mightContain("apple")); // 应该返回 true
//            System.out.println(deserializedBloomFilter.mightContain("orange")); // 可能返回 true 或 false
//
//
//            // 序列化为字符串
//            String serialized = bloomFilter.serializeToString();
//            System.out.println("Serialized: " + serialized);
//
//            // 从字符串反序列化
//            BloomFilter deserializedBloomFilter1 = BloomFilter.deserializeFromString(serialized);
//
//            // 查询元素是否存在
//            System.out.println(deserializedBloomFilter1.mightContain("apple")); // 应该返回 true
//            System.out.println(deserializedBloomFilter1.mightContain("orange")); // 可能返回 true 或 false
//
//
//
//
//            // 序列化为16进制字符串
//            String serializedHex = bloomFilter.serializeToHex();
//            System.out.println("Serialized Hex: " + serializedHex);
//
//            // 从16进制字符串反序列化
//            BloomFilter deserializedBloomFilter2 = BloomFilter.deserializeFromHex(serializedHex);
//
//            // 查询元素是否存在
//            System.out.println(deserializedBloomFilter2.mightContain("apple")); // 应该返回 true
//            System.out.println(deserializedBloomFilter2.mightContain("orange")); // 可能返回 true 或 false
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//}
