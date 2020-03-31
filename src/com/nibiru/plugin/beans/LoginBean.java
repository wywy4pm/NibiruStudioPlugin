package com.nibiru.plugin.beans;

public class LoginBean {
    /**
     * resCode : 0
     * account : {"id":156,"cerUrl":"http","name":"adminruiyue","licenseBalance":0,"activeStatus":false}
     */

    private int resCode;
    private AccountBean account;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

    public static class AccountBean {
        /**
         * id : 156
         * cerUrl : http
         * name : adminruiyue
         * licenseBalance : 0
         * activeStatus : false
         */

        private int id;
        private String cerUrl;
        private String name;
        private int licenseBalance;
        private boolean activeStatus;

        @Override
        public String toString() {
            return "AccountBean{" +
                    "id=" + id +
                    ", cerUrl='" + cerUrl + '\'' +
                    ", name='" + name + '\'' +
                    ", licenseBalance=" + licenseBalance +
                    ", activeStatus=" + activeStatus +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCerUrl() {
            return cerUrl;
        }

        public void setCerUrl(String cerUrl) {
            this.cerUrl = cerUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLicenseBalance() {
            return licenseBalance;
        }

        public void setLicenseBalance(int licenseBalance) {
            this.licenseBalance = licenseBalance;
        }

        public boolean isActiveStatus() {
            return activeStatus;
        }

        public void setActiveStatus(boolean activeStatus) {
            this.activeStatus = activeStatus;
        }
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "resCode=" + resCode +
                ", account=" + account +
                '}';
    }
}
