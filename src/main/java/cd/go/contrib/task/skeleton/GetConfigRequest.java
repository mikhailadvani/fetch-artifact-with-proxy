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

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;

// TODO: change this to allow configuration options in your configuration
public class GetConfigRequest {

    public GoPluginApiResponse execute() {
        HashMap<String, Object> config = new HashMap<>();

        HashMap<String, Object> pipeline = new HashMap<>();
        pipeline.put("display-order", "0");
        pipeline.put("display-name", "Pipeline");
        pipeline.put("required", false);
        config.put(TaskPlugin.PIPELINE_NAME, pipeline);

        HashMap<String, Object> stage = new HashMap<>();
        stage.put("display-order", "1");
        stage.put("display-name", "Stage");
        stage.put("required", true);
        config.put(TaskPlugin.STAGE_NAME, stage);

        HashMap<String, Object> job = new HashMap<>();
        job.put("display-order", "2");
        job.put("display-name", "Job");
        job.put("required", true);
        config.put(TaskPlugin.JOB_NAME, job);

        HashMap<String, Object> source = new HashMap<>();
        source.put("display-order", "3");
        source.put("display-name", "Source");
        source.put("required", true);
        config.put(TaskPlugin.SOURCE, source);

        HashMap<String, Object> sourceIsAFile = new HashMap<>();
        sourceIsAFile.put("display-order", "4");
        sourceIsAFile.put("display-name", "SourceIsAFile");
        sourceIsAFile.put("required", true);
        config.put(TaskPlugin.SOURCE_IS_A_FILE, sourceIsAFile);

        HashMap<String, Object> destination = new HashMap<>();
        destination.put("display-order", "5");
        destination.put("display-name", "Destination");
        destination.put("required", false);
        config.put(TaskPlugin.DESTINATION, destination);

        return DefaultGoPluginApiResponse.success(TaskPlugin.GSON.toJson(config));
    }
}
