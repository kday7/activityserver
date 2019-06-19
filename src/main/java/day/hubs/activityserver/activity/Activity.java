package day.hubs.activityserver.activity;

public class Activity {

    private int id = -1;
    private String name;
    private String link;
    private String icon;

    public Activity() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(final String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
