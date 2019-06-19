package day.hubs.activityserver.domain;

public class ApplicationProperty {

    private String key;
    private String value;

    public ApplicationProperty(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}
