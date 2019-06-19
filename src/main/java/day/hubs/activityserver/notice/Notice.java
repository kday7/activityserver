package day.hubs.activityserver.notice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Notice {

    private static final Logger LOG = LoggerFactory.getLogger(Notice.class);

    private int id;
    private String datePosted;
    private String detail;

    public Notice() {
        // this("");
    }

    /*public Notice(final String detail) {
        final UUID uuid = UUID.randomUUID();
        // this.id = uuid.toString();
        this.detail = detail;

        final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.datePosted = format.format(new Date());
    }*/

    /*public Notice(final Notice note) {
        this(note.detail);
        this.id = note.id;
        this.datePosted = note.datePosted;
    }*/

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getDatePosted() {
        return this.datePosted;
    }

    public void setDatePosted(final String datePosted) {
        this.datePosted = datePosted;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

    /*public String getDetailForDisplay() {
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("getDetailForDisplay: %s", this.id));
        }

        return this.detail.replace("\r\n", " <br/> ");
    }*/

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        final Notice that = (Notice) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
