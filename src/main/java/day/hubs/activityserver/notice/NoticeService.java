package day.hubs.activityserver.notice;

import day.hubs.activityserver.ActivityServerApplication;
import day.hubs.activityserver.document.DocumentUtilities;
import day.hubs.activityserver.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NoticeService {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeService.class);

    protected String hub;

    private NoticeList noticeList;
    private String noticesFile;
    private final List<Notice> notices = new ArrayList<>();

    @Autowired
    public NoticeService(final String noticesFile, final String hub) {
        super();
        this.noticesFile = noticesFile;
        this.hub = hub;
        initialise();
    }

    private void initialise() {
        if (StringUtils.isEmpty(this.noticesFile)) {
            return;
        }

        final Optional<Object> notices = JsonService.readJsonDataFile(new File(this.noticesFile), NoticeList.class);
        if (notices.isPresent()) {
            this.noticeList = (NoticeList)notices.get();
            this.notices.addAll( this.noticeList.getNotices() );
        }
    }

    public int getNextNoticeId() {
        return this.noticeList.getNextNoticeId();
    }

    public void addNotice(final Notice newNotice) {
        if (newNotice.getId() == -1) {
            newNotice.setId(getNextNoticeId());
        }

        this.notices.add(newNotice);
    }

    public Notice createNotice(final Notice newNoticeDetails) {
        final Notice newNotice = new Notice();
        newNotice.setId(this.getNextNoticeId());
        newNotice.setDetail(newNoticeDetails.getDetail());
        newNotice.setDatePosted(newNoticeDetails.getDatePosted());

        addNotice(newNotice);

        // Save projects
        saveNotices();

        return newNotice;
    }

    public Notice updateNotice(final String hub, final Notice updateNotice, final Notice newNoticeDetails) {
        LOG.debug("NoticeService: Updating notice: {}", updateNotice.getId());

        updateNotice.setDetail(newNoticeDetails.getDetail());
        updateNotice.setDatePosted(newNoticeDetails.getDatePosted());

        saveNotices();

        return updateNotice;
    }

    public boolean deleteNotice(final int noticeId) {
        // Remove notice from list of notices
        final Optional<Notice> notice = getNotice(noticeId);
        if (notice.isEmpty()) {
            return false;
        }
        this.notices.remove(notice.get());

        // Save notices
        saveNotices();

        return true;
    }

    public List<Notice> reloadNotices() {
        this.noticeList.clearNotices();
        this.notices.clear();
        initialise();
        return this.notices;
    }

    public boolean openNoticesFile() {
        final File file = new File(this.noticesFile);
        if (!file.exists()) {
            return false;
        }
        DocumentUtilities.openCodeFile(file);
        return true;
    }

    public List<Notice> getNotices() {
        return this.notices;
    }

    public Optional<Notice> getNotice(final int id) {
        return this.notices.stream().filter(notice -> id == notice.getId())
                .findFirst();
    }

    public void saveNotices() {
        this.noticeList.clearNotices();
        this.noticeList.setNotices(this.notices);
        JsonService.writeJsonDataFile(ActivityServerApplication.getApplicationContext().getNoticesFile(this.hub), this.noticeList);
    }
}
