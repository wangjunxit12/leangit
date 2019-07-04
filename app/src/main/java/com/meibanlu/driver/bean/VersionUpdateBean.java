package com.meibanlu.driver.bean;

/**
 * 版本更新
 * Created by lhq on 2016/10/9.
 */
public class VersionUpdateBean {


    /**
     * code : 200
     * message : 请求成功
     * data : {"moduleName":"apk","versionCode":"921","versionName":"1.231","createTime":1475983571000,"description":"更新的时 ","filePath":"http://localhost:8080/moduleFile/meibanlu_v1.231.apk"}
     */

    private int code;
    private String message;
    /**
     * moduleName : apk
     * versionCode : 921
     * versionName : 1.231
     * createTime : 1475983571000
     * description : 更新的时
     * filePath : http://localhost:8080/moduleFile/meibanlu_v1.231.apk
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String moduleName;
        private String versionCode;
        private String versionName;
        private String createTime;
        private String description;
        private String filePath;

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getCreateTime() {
            return createTime;
        }

            public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
