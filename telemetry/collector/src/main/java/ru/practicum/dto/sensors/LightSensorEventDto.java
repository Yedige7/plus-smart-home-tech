package ru.practicum.dto.sensors;

public class LightSensorEventDto extends SensorEventDto {


    private Integer linkQuality;
    private Integer luminosity;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Integer getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(Integer luminosity) {
        this.luminosity = luminosity;
    }

    @Override
    public String toString() {
        return "LightSensorEventDto{" +
                "id='" + getId() + '\'' +
                ", hubId='" + getHubId() + '\'' +
                ", timestamp=" + getTimestamp() +
                ", type=" + getType() +
                ", linkQuality=" + linkQuality +
                ", luminosity=" + luminosity +
                '}';
    }
}
