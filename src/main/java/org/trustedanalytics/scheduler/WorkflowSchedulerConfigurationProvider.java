/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.scheduler;

import org.trustedanalytics.scheduler.client.OozieClient;
import org.trustedanalytics.scheduler.config.Database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import rx.Observable;

@Service
public class WorkflowSchedulerConfigurationProvider {

    private final List<Database> databases;

    private final List<String> zones;

    @Autowired
    public WorkflowSchedulerConfigurationProvider(Observable<Database> databases, OozieClient oozieClient) {
        this.databases = databases.toList().toBlocking().single();
        this.zones = Arrays.stream(TimeZone.getAvailableIDs()).filter(timezone -> timezone.contains("GMT+")
                || timezone.contains("GMT-")
                || timezone.contains("GMT")
                || timezone.contains("UTC")
                || timezone.contains("US")
                || timezone.contains("Europe/Warsaw")).collect(Collectors.toList());
    }

    public WorkflowSchedulerConfigurationEntity getConfiguration(UUID orgId) {
        return WorkflowSchedulerConfigurationEntity.builder()
            .databases(databases)
            .timezones(zones)
            .organizationDirectory(
                String.format("hdfs://nameservice1/org/%s/brokers/userspace/", orgId))
            .build();
    }
}
