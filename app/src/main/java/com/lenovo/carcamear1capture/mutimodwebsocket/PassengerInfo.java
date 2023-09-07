package com.lenovo.mutimodeshow.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PassengerInfo {

    @SerializedName("status")
    private Integer status;
    @SerializedName("message")
    private MessageDTO message;
    @SerializedName("timestamp")
    private String timestamp;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public static class MessageDTO {
        @SerializedName("count")
        private Integer count;
        @SerializedName("member")
        private List<MemberDTO> member;
        @SerializedName("weather")
        private String weather;
        @SerializedName("temperature")
        private Integer temperature;
        @SerializedName("scene")
        private String scene;

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public List<MemberDTO> getMember() {
            return member;
        }

        public void setMember(List<MemberDTO> member) {
            this.member = member;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public Integer getTemperature() {
            return temperature;
        }

        public void setTemperature(Integer temperature) {
            this.temperature = temperature;
        }

        public String getScene() {
            return scene;
        }

        public void setScene(String scene) {
            this.scene = scene;
        }

        public static class MemberDTO {
            @SerializedName("seat")
            private Integer seat;
            @SerializedName("gender")
            private String gender;
            @SerializedName("action")
            private String action;
            @SerializedName("wearing")
            private String wearing;
            @SerializedName("emotion")
            private String emotion;
            @SerializedName("belt")
            private Object belt;
            @SerializedName("respiratory")
            private Object respiratory;
            @SerializedName("heartRate")
            private Object heartRate;

            public Integer getSeat() {
                return seat;
            }

            public void setSeat(Integer seat) {
                this.seat = seat;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getWearing() {
                return wearing;
            }

            public void setWearing(String wearing) {
                this.wearing = wearing;
            }

            public String getEmotion() {
                return emotion;
            }

            public void setEmotion(String emotion) {
                this.emotion = emotion;
            }

            public Object getBelt() {
                return belt;
            }

            public void setBelt(Object belt) {
                this.belt = belt;
            }

            public Object getRespiratory() {
                return respiratory;
            }

            public void setRespiratory(Object respiratory) {
                this.respiratory = respiratory;
            }

            public Object getHeartRate() {
                return heartRate;
            }

            public void setHeartRate(Object heartRate) {
                this.heartRate = heartRate;
            }
        }
    }
}
