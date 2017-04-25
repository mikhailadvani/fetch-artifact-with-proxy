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

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.HashMap;
import java.util.Map;

public class ValidateRequest {
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
        HashMap<String, Object> validationResult = new HashMap<>();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
        Map configMap = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        HashMap errorMap = new HashMap();

//        if (isEmptyText(configMap, TaskPlugin.PIPELINE_NAME)){
//            errorMap.put(TaskPlugin.PIPELINE_NAME, "Pipeline cannot be empty");
//        }
//
//        if (isEmptyText(configMap, TaskPlugin.STAGE_NAME)){
//            errorMap.put(TaskPlugin.STAGE_NAME, "Stage cannot be empty");
//        }
//
//        if (isEmptyText(configMap, TaskPlugin.JOB_NAME)){
//            errorMap.put(TaskPlugin.JOB_NAME, "Job cannot be empty");
//        }
//
//        if (isEmptyText(configMap, TaskPlugin.SOURCE)){
//            errorMap.put(TaskPlugin.SOURCE, "Source cannot be empty");
//        }

        validationResult.put("errors", errorMap);
        return new DefaultGoPluginApiResponse(responseCode, TaskPlugin.GSON.toJson(validationResult));
    }

    private Boolean isEmptyText(Map configMap, String property) {
        if (!configMap.containsKey(property) || ((Map) configMap.get(property)).get("value") == null || ((String) ((Map) configMap.get(property)).get("value")).trim().isEmpty()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
