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
import com.microee.traditex.archive.app.mappers.DiskOrderTableMapper;
import com.microee.traditex.archive.app.mappers.LiquiArchiveRecordMapper;
import com.microee.traditex.archive.app.service.UploadService;
import com.microee.traditex.archive.oem.models.DiskOrderTable;
import com.microee.traditex.archive.oem.models.LiquiArchiveRecord;

@Component
@EnableScheduling
public class LiquiArchiveDiskOrder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquiArchiveDiskOrder.class);

    @Autowired
    private UploadService uploadService;

    @Autowired
    private DiskOrderTableMapper diskOrderTableMapper;

    @Autowired
    private LiquiArchiveRecordMapper archiveRecordMapper;
    
    @Value("${archive.diskorder.size:200000}")
    private Integer ARCHIVE_DISKORDER_SIZE = 2;

    @Async
    @Scheduled(cron = "0/15 * * * * ?")
    public void run() throws IOException {
        this.archiveDiskOrder(ARCHIVE_DISKORDER_SIZE);
    }
    
    public String archiveDiskOrder(Integer size) throws IOException {
        Long s1 = Instant.now().toEpochMilli();
        List<DiskOrderTable> list = diskOrderTableMapper.archive(size);  
        Long s2 = Instant.now().toEpochMilli();
        if (list == null || list.size() == 0 || list.size() < size) {
            return null;
        }
        File file = exportDiskOrder(list);
        if (file == null) {
            return null;
        }
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            Long s4 = Instant.now().toEpochMilli();
            String fileId = uploadService.uploadToS3(null, file.getName(), input, file.length());
            Long s5 = Instant.now().toEpochMilli();
            String newLastId = list.get(list.size() - 1).getDiskClientOrderId();
            LiquiArchiveRecord archive = new LiquiArchiveRecord();
            archive.setFileId(fileId);
            archive.setFileUrl(uploadService.getS3Url(fileId, false).toString());
            archive.setId(UUIDObject.get().toString());
            archive.setLastId(newLastId);
            archive.setRowsCount(list.size()); 
            archive.setTheTableName("t_order_disk_table");
            archive.setLastTimestamp(new Date(Instant.now().toEpochMilli()));
            archiveRecordMapper.insertSelective(archive);
            diskOrderTableMapper.delete(newLastId); 
            Long s6 = Instant.now().toEpochMilli();
            LOGGER.info("`{}`定时任务归档diskorder: count={}, fileSize={}, 总耗时={}, 查询={}, 导出={}, 上传={}, 删除={}, archiveFileId={}", 
                    JODAd.format(new Date(s1), JODAd.STANDARD_FORMAT_MS), list.size(), file.length(), s6 - s1, s2 - s1, s4 - s2, s5 - s4, s6 - s5, fileId);
            return fileId;  
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 导出diskorder
     * 
     * @throws IOException
     */
    public File exportDiskOrder(List<DiskOrderTable> list) throws IOException {
        if (list == null || list.size() == 0) {
            return null;
        }
        String[] header = new String[] {"id", "disk_client_order_id", "disk_result_order_id", "order_book_id", "vender", "target_symbol", "target_side", "target_amount", "target_price"
                , "usdt_price", "usdt_usd_rate", "disk_amount", "disk_price", "disk_price_prec", "disk_amount_prec", "disk_order_type", "disk_account", "disk_order_result", "created_at"};
        List<LinkedHashMap<String, Object>> excelData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            DiskOrderTable q = list.get(i);
            map.put("id", i + 1);
            map.put("disk_client_order_id", q.getDiskClientOrderId());
            map.put("disk_result_order_id", q.getDiskResultOrderId());
            map.put("order_book_id", q.getOrderBookId());
            map.put("vender", q.getVender());
            map.put("target_symbol", q.getTargetSymbol());
            map.put("target_side", q.getTargetSide());
            map.put("target_amount", q.getTargetAmount());
            map.put("target_price", q.getTargetPrice());
            map.put("usdt_price", q.getUsdtPrice());
            map.put("usdt_usd_rate", q.getUsdtUsdRate());
            map.put("disk_amount", q.getDiskAmount());
            map.put("disk_price", q.getDiskPrice());
            map.put("disk_price_prec", q.getDiskPricePrec());
            map.put("disk_amount_prec", q.getDiskAmountPrec());
            map.put("disk_order_type", q.getDiskOrderType());
            map.put("disk_account", q.getDiskAccount());
            map.put("disk_order_result", q.getDiskOrderResult());
            map.put("created_at", JODAd.format(q.getCreatedAt(), JODAd.STANDARD_FORMAT_MS));
            excelData.add(map);
        }
        return Excel.exportXlsx("diskorder-" + list.get(0).getDiskClientOrderId(), header, excelData);
    }
}
