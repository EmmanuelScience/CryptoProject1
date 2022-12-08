package com.ticketswap.cryptoproject1.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("serial")
@Document(collection = "ticketrequests")
@JsonPropertyOrder({"emailBuyer", "emailSeller","ticketId"})
public class TicketRequest {

        @Id
        private String id;

        private String emailBuyer;

        private String aliasBuyer;

        private String aliasSeller;

        private String emailSeller;

        private int ticketId;

        private byte[] signature;

        private byte[] certificate;

        private String publicKey;

        private int quantity;

        public TicketRequest() {

        }

        @PersistenceConstructor
        public TicketRequest(String emailBuyer, String aliasBuyer, String emailSeller, String aliasSeller, int ticketId, byte[] signature, byte[] certificate, String publicKey, int quantity) {
            this.emailBuyer = emailBuyer;
            this.aliasBuyer = aliasBuyer;
            this.emailSeller = emailSeller;
            this.aliasSeller = aliasSeller;
            this.ticketId = ticketId;
            this.signature = signature;
            this.certificate = certificate;
            this.publicKey = publicKey;
            this.quantity = quantity;

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAliasBuyer() {
            return aliasBuyer;
        }

        public void setAliasBuyer(String aliasBuyer) {
            this.aliasBuyer = aliasBuyer;
        }

        public String getAliasSeller() {
            return aliasSeller;
        }

        public void setAliasSeller(String aliasSeller) {
            this.aliasSeller = aliasSeller;
        }

        public String getEmailBuyer() {
            return emailBuyer;
        }

        public void setEmailBuyer(String emailBuyer) {
            this.emailBuyer = emailBuyer;
        }

        public String getEmailSeller() {
            return emailSeller;
        }

        public void setEmailSeller(String emailSeller) {
            this.emailSeller = emailSeller;
        }

        public int getTicketId() {
            return ticketId;
        }

        public void setTicketId(int ticketId) {
            this.ticketId = ticketId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public byte[] getSignature() {
            return signature;
        }

        public void setSignature(byte[] signature) {
            this.signature = signature;
        }

        public byte[] getCertificate() {
            return certificate;
        }

        public void setCertificate(byte[] certificate) {
            this.certificate = certificate;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

}
