package com.microee.traditex.inbox.oem.cumberland.apiresult;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class CBApiResultForReferenceData extends CBApiResultBase
        implements Serializable {

    private static final long serialVersionUID = 3607590180748167494L;

    private ReferenceDataResponseId referenceDataResponseId;
    private ReferenceDataRequest referenceDataRequest;
    private List<CBTickers> availableTickers;
    private List<String> availableCurrencies;
    private String status;

    public ReferenceDataResponseId getReferenceDataResponseId() {
        return referenceDataResponseId;
    }

    public void setReferenceDataResponseId(ReferenceDataResponseId referenceDataResponseId) {
        this.referenceDataResponseId = referenceDataResponseId;
    }

    public ReferenceDataRequest getReferenceDataRequest() {
        return referenceDataRequest;
    }

    public void setReferenceDataRequest(ReferenceDataRequest referenceDataRequest) {
        this.referenceDataRequest = referenceDataRequest;
    }

    public List<CBTickers> getAvailableTickers() {
        return availableTickers;
    }

    public void setAvailableTickers(List<CBTickers> availableTickers) {
        this.availableTickers = availableTickers;
    }

    public List<String> getAvailableCurrencies() {
        return availableCurrencies;
    }

    public void setAvailableCurrencies(List<String> availableCurrencies) {
        this.availableCurrencies = availableCurrencies;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> availableTickersId() {
        if (this.getAvailableTickers() == null) {
            return null;
        }
        return this.getAvailableTickers().stream().map(mapper -> mapper.getId()).collect(Collectors.toList());
    }
    
    public class ReferenceDataResponseId implements Serializable {

        private static final long serialVersionUID = 3064196928063683670L;

        private Integer version;
        private String id;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public class ReferenceDataRequest implements Serializable {

        private static final long serialVersionUID = 4588292842583285176L;

        private String messageType;
        private String counterpartyRequestId;
        private String counterpartyId;

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getCounterpartyRequestId() {
            return counterpartyRequestId;
        }

        public void setCounterpartyRequestId(String counterpartyRequestId) {
            this.counterpartyRequestId = counterpartyRequestId;
        }

        public String getCounterpartyId() {
            return counterpartyId;
        }

        public void setCounterpartyId(String counterpartyId) {
            this.counterpartyId = counterpartyId;
        }

    }

}
