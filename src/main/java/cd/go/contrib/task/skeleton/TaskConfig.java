/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.task.skeleton;

import java.util.Map;

// TODO: edit this to map to the fields in your task configuration
public class TaskConfig {
    private final String pipelineName;
    private final String stageName;
    private final String jobName;
    private final String source;
    private final String sourceIsAFile;
    private final String destination;

    public TaskConfig(Map config) {
        pipelineName = getValue(config, TaskPlugin.PIPELINE_NAME);
        stageName = getValue(config, TaskPlugin.STAGE_NAME);
        jobName = getValue(config, TaskPlugin.JOB_NAME);
        source = getValue(config, TaskPlugin.SOURCE);
        sourceIsAFile = getValue(config, TaskPlugin.SOURCE_IS_A_FILE);
        destination = getValue(config, TaskPlugin.DESTINATION);
    }

    private String getValue(Map config, String property) {
        return (String) ((Map) config.get(property)).get("value");
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public String getStageName() {
        return stageName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getSource() {
        return source;
    }

    public String getSourceIsAFile() {
        return sourceIsAFile;
    }

    public String getDestination() {
        return destination;
    }
}
