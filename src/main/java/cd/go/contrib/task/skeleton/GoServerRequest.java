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

import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import org.apache.commons.io.IOUtils;
import com.google.gson.GsonBuilder;

//import org.json.JSONException;
//import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mikhailadvani on 4/26/17.
 */
public class GoServerRequest {
    private Context context;

//    public GoServerRequest(Context taskContext) throws IOException , JSONException {
//        context = taskContext;
//    }

    public Map getJson(String path, JobConsoleLogger console) {
        Map returnObject = new HashMap();
        try {
            console.printLine("Going to make HTTP call to: " + goServerUrl() + path);
//            returnObject = (Map) new GsonBuilder().create().fromJson(body, Object.class);
        }
        catch (Exception e) {
            console.printLine(e.getStackTrace().toString());
//            e.printStackTrace();
        }
        return returnObject;
    }

    private <T> T fromJSON(String json, Class<T> targetType) {
        return new GsonBuilder().create().fromJson(json, targetType);
    }

    private String getEnvironmentVariable(String variableName) {
        Map environmentVariablesMap = this.context.getEnvironmentVariables();
        return environmentVariablesMap.get(variableName).toString();
    }
    private String goServerUrl() {
        return getEnvironmentVariable("GO_SERVER_URL").replace("/go","/");
    }
    private ProcessBuilder createCurlCommand(Context taskContext, TaskConfig taskTaskConfig, String requestPath, Boolean useProxy) {
        String destinationFilePath = taskContext.getWorkingDir() + "/" + taskTaskConfig.getDestination();
        String proxy_url = getEnvironmentVariable("proxy_url");
        List<String> command = new ArrayList<String>();
        command.add("curl");
        command.add("-u");
        command.add(getEnvironmentVariable("go_server_username") + ":" + getEnvironmentVariable("go_server_password"));
        command.add(goServerUrl() + requestPath);
        if (!proxy_url.trim().isEmpty() & useProxy) {
            command.add("--proxy");
            command.add(getEnvironmentVariable("proxy_url"));
        }
        command.add("-o");
        command.add(destinationFilePath);

        return new ProcessBuilder(command);
    }
}
