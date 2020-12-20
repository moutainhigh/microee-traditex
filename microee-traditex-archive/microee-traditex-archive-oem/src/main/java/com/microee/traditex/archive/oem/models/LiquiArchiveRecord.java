package com.microee.traditex.archive.oem.models;

import java.io.Serializable;
import java.util.Date;

public class LiquiArchiveRecord implements Serializable {
    private String id;

    private String theTableName;

    private String lastId;

    private Integer rowsCount;

    private Date lastTimestamp;

    private String fileId;

    private String fileUrl;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTheTableName() {
        return theTableName;
    }

    public void setTheTableName(String theTableName) {
        this.theTableName = theTableName == null ? null : theTableName.trim();
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId == null ? null : lastId.trim();
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Date getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Date lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId == null ? null : fileId.trim();
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl == null ? null : fileUrl.trim();
    }
}