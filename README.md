# Trash Cleaner Addon

## Description

The goal is to clean up regularly the trash based upon a cron job and the time spend in the trash.

## Installation
```shell script
nuxeoctl mp-install nuxeo-trash-cleanup-package-x.x.x
```
## Setup

Default cron contribution:
```xml
<?xml version="1.0"?>
<component name="org.nuxeo.ecm.platform.cron.trashcleanup">
    <extension target="org.nuxeo.ecm.core.scheduler.SchedulerService" point="schedule">
        <schedule id="monthly_trash_cleanup">
            <eventId>trashCleanUp</eventId>
            <eventCategory>default</eventCategory>
            <!-- Every first of the month at 3am -->
            <cronExpression>0 0 3 1 * ?</cronExpression>
        </schedule>
    </extension>
</component>
```

Example of trash contribution to setup cleanup:
```xml
<?xml version="1.0"?>
<component name="org.nuxeo.ecm.platform.cleanup.service.trashcleanup.configuration.test">
    <require>org.nuxeo.ecm.platform.cleanup.service.trashcleanup.configuration</require>
    <extension target="org.nuxeo.ecm.platform.cleanup.service.TrashCleanUp" point="trashcleanup">
        <trashCleanUpConfig useWorker="true" years="3" months="1" days="2" hours="9" minutes="4" seconds="8"/>
    </extension>
</component>
```
Contribution property descriptions:

| Property name | Description                        | Default |
| ------------- | ---------------------------------- | ------- | 
| useWorker     | Use BAF or worker (default is BAF) | false   | 
| years         | Years to clean up after trashed    | 0       | 
| months        | Months to clean up after trashed   | 0       | 
| days          | Days to clean up after trashed     | 0       |
| hours         | Hours to clean up after trashed    | 0       | 
| minutes       | Minutes to clean up after trashed  | 0       | 
| seconds       | Seconds to clean up after trashed  | 0       |
