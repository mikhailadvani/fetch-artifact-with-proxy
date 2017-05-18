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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.go.plugin.api.task.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

// TODO: execute your task and setup stdout/stderr to pipe the streams to GoCD
public class FetchArtifactWithProxyTaskExecutor {
    private final TaskConfig taskConfig;
    private final Context context;
    private final JobConsoleLogger console;
    private final Map environmentVariables;

    public FetchArtifactWithProxyTaskExecutor(TaskConfig config, Context taskContext, JobConsoleLogger consoleLogger) {
        taskConfig = config;
        context = taskContext;
        console = consoleLogger;
        environmentVariables = this.context.getEnvironmentVariables();
    }

    public Result execute() {
        try {
            return fetchArtifact();
        } catch (Exception e) {
            return new Result(false, "Error making HTTP GET call", e);
        }
    }

    private Result fetchArtifact() throws IOException, InterruptedException {
        GoStage stage = new GoStage(getEnvironmentVariable("GO_PIPELINE_NAME"),
                                    Integer.parseInt(getEnvironmentVariable("GO_PIPELINE_COUNTER")),
                                    getEnvironmentVariable("GO_STAGE_NAME"),
                                    Integer.parseInt(getEnvironmentVariable("GO_STAGE_COUNTER")));
        Set<GoStage> upstreamPipelines = deduplicatedUpstreamPipelines(getPipelineMaterials(stage, new HashSet<GoStage>()));

        return new Result(true, "Made API call");
    }

    private void printPipelines(Set<GoStage> pipelines) {
        for (GoStage goStage : pipelines) {
            this.console.printLine("?????????????" + goStage.print());
        }
    }

    private Set<GoStage> deduplicatedUpstreamPipelines(Set<GoStage> upstreamStages) {
        Iterator<GoStage> iterator = upstreamStages.iterator();
        while (iterator.hasNext()) {
            GoStage stage = iterator.next();
            if (stage.greaterOrSameVersionAvailable(upstreamStages)) {
                this.console.printLine(stage.print() + stage);
                iterator.remove();
            }
        }
        return upstreamStages;
    }

    private String curl(String path, Boolean useProxy) throws IOException, InterruptedException {
        ProcessBuilder curl = createCurlCommand(path, useProxy, Boolean.FALSE);
        curl.environment().putAll(this.context.getEnvironmentVariables());
        Process curlProcess = curl.start();
        this.console.readErrorOf(curlProcess.getErrorStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(curlProcess.getInputStream()));
        String line;
        String output = "";
        while ((line = reader.readLine()) != null) {
            output = output + line;
        }
        curlProcess.waitFor();
        reader.close();
        curlProcess.destroy();
        return output;
    }

    private ProcessBuilder createCurlCommand(String path, Boolean useProxy, Boolean saveFile) {
        List<String> command = new ArrayList<String>();
        command.add("curl");
        if (useProxy) { command.add(proxyUrl() + path); }
        else { command.add(goServerUrl() + path); }
        command.add("-s");
        if (saveFile) {
            command.add("-o");
            String destinationFilePath = this.context.getWorkingDir() + "/" + this.taskConfig.getDestination();
            command.add(destinationFilePath);
        }
        command.add("-u");
        command.add(getEnvironmentVariable("GO_SERVER_USERNAME") + ":" + getEnvironmentVariable("GO_SERVER_PASSWORD"));
        command.add("-k");

        return new ProcessBuilder(command);
    }

    private String proxyUrl() {
        return getEnvironmentVariable("ARTIFACT_FETCH_PROXY_HOST") + ":" + getEnvironmentVariable("ARTIFACT_FETCH_PROXY_PORT");
    }

    private String goServerUrl() {
        return getEnvironmentVariable("GO_SERVER_URL").replace("/go","");
    }

    private String getEnvironmentVariable(String variableName) {
        return this.environmentVariables.get(variableName).toString();
    }

    private List<GoStage> immediateUpstreamPipelines() {
        List<GoStage> stages = new ArrayList<GoStage>();
        Map environmentVariablesMap = this.context.getEnvironmentVariables();
        Set<Map.Entry<String, String>> entrySet = environmentVariablesMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if (entry.getKey().startsWith("GO_DEPENDENCY_LOCATOR")) {
                String[] parts = entry.getValue().split("/");
                stages.add(new GoStage(parts[0], Integer.parseInt(parts[1]), parts[2], Integer.parseInt(parts[3])));
            }
        }
        return stages;
    }

    private Set<GoStage> getPipelineMaterials(GoStage currentStage, Set<GoStage> stages) throws IOException, InterruptedException {
        JsonParser jsonParser = new JsonParser();
        JsonArray material_revisions = jsonParser.parse(curl(currentStage.getPipelineUrlPath(),Boolean.FALSE)).getAsJsonObject()
                                                                            .get("build_cause").getAsJsonObject()
                                                                            .get("material_revisions").getAsJsonArray();

        for (JsonElement material_revision : material_revisions) {
            if (material_revision.getAsJsonObject().get("material").getAsJsonObject().get("type").getAsString().equals("Pipeline")) {
                GoStage upstreamStage = new GoStage(material_revision.getAsJsonObject().get("modifications").getAsJsonArray().get(0).getAsJsonObject().get("revision").getAsString());
                if (!upstreamStage.isIn(stages)) {
                    stages.add(upstreamStage);
                    stages.addAll(getPipelineMaterials(upstreamStage, stages));
                }
            }
        }
        return stages;
    }

    private Result oldFetchArtifact() throws IOException, InterruptedException {
        ProcessBuilder curl = createCurlCommand("", Boolean.FALSE, Boolean.FALSE);
        this.console.printLine("Launching command: " + curl.command());
        List<GoStage> immediateUpstreamStages = immediateUpstreamPipelines();
        for (GoStage stage : immediateUpstreamStages) {
            this.console.printLine(stage.print());
        }
        this.console.printLine(immediateUpstreamPipelines().toString());
        Process curlProcess = curl.start();
        this.console.readErrorOf(curlProcess.getErrorStream());
        this.console.readOutputOf(curlProcess.getInputStream());

        int exitCode = curlProcess.waitFor();
        curlProcess.destroy();

        if (exitCode != 0) {
            return new Result(false, "Failed downloading file. Please check the output");
        }

        return new Result(true, "Downloaded file: " + this.taskConfig.getDestination());
    }
}
