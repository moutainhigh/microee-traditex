package com.microee.traditex.archive.app.actions;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microee.plugin.response.R;
import com.microee.traditex.archive.app.service.archive.LiquiArchiveDiskOrder;
import com.microee.traditex.archive.app.service.archive.LiquiArchiveOrderBook;

@RestController
@RequestMapping("/archive")
public class ArchiveRestful {

    @Autowired
    private LiquiArchiveOrderBook liquiArchiveOrderBook;

    @Autowired
    private LiquiArchiveDiskOrder liquiArchiveDiskOrder;

    @RequestMapping(value = "/orderbook", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> orderbook(
            @RequestParam(value="size", required=false, defaultValue="10") Integer size) throws IOException {
        return R.ok(liquiArchiveOrderBook.archiveOrderBook(size));
    }

    @RequestMapping(value = "/diskorder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> diskorder(
            @RequestParam(value="size", required=false, defaultValue="10") Integer size) throws IOException {
        return R.ok(liquiArchiveDiskOrder.archiveDiskOrder(size)); 
    }

    @RequestMapping(value = "/solrorder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> solrorder(
            @RequestParam(value="size", required=false, defaultValue="10") Integer size) throws IOException {
        return null;
    }

    @RequestMapping(value = "/hdegorder", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R<String> hdegorder(
            @RequestParam(value="size", required=false, defaultValue="10") Integer size) throws IOException {
        return null;
    }
    
}
