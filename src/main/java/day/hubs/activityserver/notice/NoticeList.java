package day.hubs.activityserver.notice;

import java.util.ArrayList;
import java.util.List;

public class NoticeList {

    private List<Notice> hubNotices = new ArrayList<>();

    private int nextNoticeId;

    public int getNextNoticeId() {
        return this.nextNoticeId++;
    }

    public List<Notice> getNotices() {
        return new ArrayList<>(this.hubNotices);
    }

    public void setNotices(final List<Notice> taskHubPostItNotes) {
        this.hubNotices = new ArrayList<>(taskHubPostItNotes);
    }

    public void addNotice(final Notice newPostItNote) {
        this.hubNotices.add(newPostItNote);
    }

    public void removeNotice(final Notice postItNote) {
        this.hubNotices.remove(postItNote);
    }

    public void clearNotices() {
        this.hubNotices.clear();
    }
}
