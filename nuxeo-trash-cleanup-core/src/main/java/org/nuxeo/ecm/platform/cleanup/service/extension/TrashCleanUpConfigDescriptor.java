package org.nuxeo.ecm.platform.cleanup.service.extension;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

import java.io.Serializable;

@XObject("trashCleanUpConfig")
public class TrashCleanUpConfigDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    public TrashCleanUpConfigDescriptor() {
        //DO NOTHING
    }

    @XNode("@useWorker")
    protected Boolean useWorker;

    public Boolean getUseWorker() {
        return useWorker;
    }

    @XNode("@years")
    protected Integer years;

    public Integer getYears() {
        return years;
    }

    @XNode("@months")
    protected Integer months;

    public Integer getMonths() {
        return months;
    }

    @XNode("@days")
    protected Integer days;

    public Integer getDays() {
        return days;
    }

    @XNode("@hours")
    protected Integer hours;

    public Integer getHours() {
        return hours;
    }

    @XNode("@minutes")
    protected Integer minutes;

    public Integer getMinutes() {
        return minutes;
    }

    @XNode("@seconds")
    protected Integer seconds;

    public Integer getSeconds() {
        return seconds;
    }
}
