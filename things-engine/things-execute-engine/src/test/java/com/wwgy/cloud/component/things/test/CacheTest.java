//package cn.huangdayu.things.engine.test;
//
//import cn.huangdayu.things.common.annotation.ThingsBean;
//import cn.hutool.core.builder.Builder;
//import cn.hutool.core.map.multi.RowKeyTable;
//import cn.hutool.core.map.multi.Table;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cache.CacheManager;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
///**
// * @author huangdayu
// */
//@Slf4j
//@RequiredArgsConstructor
//@ThingsBean
//public class CacheTest {
//
//    private final CacheManager cacheManager;
//
//    private Table<String, String, String> CACHE_TABLE = null;
//
//
//    @PostConstruct
//    public void init() {
//        CACHE_TABLE = new RowKeyTable<>(new cn.huangdayu.things.engine.test.SpringCacheBackedMap<>(cacheManager, "productCode")
//                , (Builder<Map<String, String>>) () -> new cn.huangdayu.things.engine.test.SpringCacheBackedMap<>(cacheManager, "deviceCode"));
//    }
//
//    @Scheduled(initialDelay = 10, fixedDelay = 60_000, scheduler = "thingsTaskScheduler")
//    public void test() {
//        CACHE_TABLE.put("a", "b", "c");
//        log.info("CacheTest : " + CACHE_TABLE.get("a", "b"));
//        for (Table.Cell<String, String, String> cell : CACHE_TABLE.cellSet()) {
//            log.info("CacheTest : " + cell.getRowKey() + " " + cell.getColumnKey() + " " + cell.getValue());
//        }
//    }
//
//}
