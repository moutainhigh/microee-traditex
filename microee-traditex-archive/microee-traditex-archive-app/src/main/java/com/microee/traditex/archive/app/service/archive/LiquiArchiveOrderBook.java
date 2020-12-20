package com.microee.traditex.archive.app.service.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.microee.plugin.commons.UUIDObject;
import com.microee.plugin.excel.Excel;
import com.microee.plugin.jodad.JODAd;
import com.microee.traditex.archive.app.mappers.LiquiArchiveRecordMapper;
import com.microee.traditex.archive.app.mappers.OrderbookStreamMapper;
import com.microee.traditex.archive.app.service.UploadService;
import com.microee.traditex.archive.oem.models.LiquiArchiveRecord;
import com.microee.traditex.archive.oem.models.OrderbookStream;

@Component
@EnableScheduling
public class LiquiArchiveOrderBook {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiArchiveOrderBook.class);
    
    @Autowired
    private UploadService uploadService;
    
    @Autowired
    private OrderbookStreamMapper orderbookStreamMapper;

    @Autowired
    private LiquiArchiveRecordMapper archiveRecordMapper;
    
    @Value("${archive.orderbook.size:200000}")
    private Integer ARCHIVE_ORDERBOOK_SIZE = 200000;

    @Async
    @Scheduled(cron = "0/15 * * * * ?")
    public void run() throws IOException {
        this.archiveOrderBook(ARCHIVE_ORDERBOOK_SIZE);
    }
    
    public String archiveOrderBook(Integer size) throws IOException {
        Long s1 = Instant.now().toEpochMilli();
        List<OrderbookStream> list = orderbookStreamMapper.archive(size); 
        Long s2 = Instant.now().toEpochMilli();
        if (list == null || list.size() == 0 || list.size() < size) {
            return null;
        }
        File file = exportOrderbook(list);
        if (file == null) {
            return null;
        }
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            Long s4 = Instant.now().toEpochMilli();
            String fileId = uploadService.uploadToS3(null, file.getName(), input, file.length());
            Long s5 = Instant.now().toEpochMilli();
            String newLastId = list.get(list.size() - 1).getOrderBookId();
            LiquiArchiveRecord archive = new LiquiArchiveRecord();
            archive.setFileId(fileId);
            archive.setFileUrl(uploadService.getS3Url(fileId, false).toString());
            archive.setId(UUIDObject.get().toString());
            archive.setLastId(newLastId);
            archive.setRowsCount(list.size()); 
            archive.setTheTableName("t_orderbook_stream");
            archive.setLastTimestamp(new Date(Instant.now().toEpochMilli()));
            archiveRecordMapper.insertSelective(archive);
            orderbookStreamMapper.delete(newLastId); 
            Long s6 = Instant.now().toEpochMilli();
            LOGGER.info("`{}`定时任务归档orderbook: count={}, fileSize={}, 总耗时={}, 查询={}, 导出={}, 上传={}, 删除={}, archiveFileId={}", 
                    JODAd.format(new Date(s1), JODAd.STANDARD_FORMAT_MS), list.size(), file.length(), s6 - s1, s2 - s1, s4 - s2, s5 - s4, s6 - s5, fileId);
            return fileId;  
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 导出orderbook
     * 
     * @throws IOException
     */
    public File exportOrderbook(List<OrderbookStream> list) throws IOException {
        if (list == null || list.size() == 0) {
            return null;
        }
        String[] header = new String[] {"id", "order_book", "symbol", "side", "amount", "price", "vender", "skip_code", "created_at"};
        List<LinkedHashMap<String, Object>> excelData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            OrderbookStream q = list.get(i);
            map.put("id", i + 1);
            map.put("order_book_id", q.getOrderBookId());
            map.put("symbol", q.getSymbol());
            map.put("side", q.getSide());
            map.put("amount", q.getAmount());
            map.put("price", q.getPrice());
            map.put("vender", q.getVender());
            map.put("skip_code", q.getSkipCode());
            map.put("created_at", JODAd.format(q.getCreatedAt(), JODAd.STANDARD_FORMAT_MS));
            excelData.add(map);
        }
        return Excel.exportXlsx("orderbook-" + list.get(0).getOrderBookId(), header, excelData);
    }
    
}
