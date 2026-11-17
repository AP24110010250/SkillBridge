package com.skillbridge.model;

import java.sql.Timestamp;

// Model representing a peer-to-peer skill exchange request
public class ExchangeRequest {

    private int requestId;
    private int senderId;
    private String senderName;
    private int receiverId;
    private String receiverName;
    private int offeredSkillId;
    private String offeredSkillName;
    private int wantedSkillId;
    private String wantedSkillName;
    private String status; // PENDING, ACCEPTED, REJECTED, CANCELLED
    private String message;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ExchangeRequest() {
        this.status = "PENDING";
    }

    public ExchangeRequest(int requestId, int senderId, int receiverId, int offeredSkillId, int wantedSkillId, String status, String message) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.offeredSkillId = offeredSkillId;
        this.wantedSkillId = wantedSkillId;
        this.status = status != null ? status : "PENDING";
        this.message = message;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getOfferedSkillId() {
        return offeredSkillId;
    }

    public void setOfferedSkillId(int offeredSkillId) {
        this.offeredSkillId = offeredSkillId;
    }

    public String getOfferedSkillName() {
        return offeredSkillName;
    }

    public void setOfferedSkillName(String offeredSkillName) {
        this.offeredSkillName = offeredSkillName;
    }

    public int getWantedSkillId() {
        return wantedSkillId;
    }

    public void setWantedSkillId(int wantedSkillId) {
        this.wantedSkillId = wantedSkillId;
    }

    public String getWantedSkillName() {
        return wantedSkillName;
    }

    public void setWantedSkillName(String wantedSkillName) {
        this.wantedSkillName = wantedSkillName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
