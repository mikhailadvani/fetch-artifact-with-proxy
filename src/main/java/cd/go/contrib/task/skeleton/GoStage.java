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

import java.util.List;

/**
 * Created by mikhailadvani on 5/1/17.
 */
public class GoStage {
    private final String pipelineName;
    private final Integer pipelineCounter;
    private final String stageName;
    private final Integer stageCounter;

    public GoStage(String pipeline, Integer pc, String stage, Integer sc) {
        pipelineName = pipeline;
        pipelineCounter = pc;
        stageName = stage;
        stageCounter = sc;
    }

    public GoStage(String revision) {
        String [] split_revision = revision.split("/");
        pipelineName = split_revision[0];
        pipelineCounter = Integer.parseInt(split_revision[1]);
        stageName = split_revision[2];
        stageCounter = Integer.parseInt(split_revision[3]);
    }

    public Boolean isIn(List<GoStage> stages) {
        for (GoStage stage : stages ) {
            if (this.isEqual(stage)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isEqual(GoStage stage2) {
        return ( this.pipelineName.equals(stage2.pipelineName) ) & ( this.stageName.equals(stage2.stageName) ) &
               ( this.pipelineCounter.equals(stage2.pipelineCounter) ) & ( this.stageCounter.equals(stage2.stageCounter) ) ;
    }

    public String print() {
        return this.pipelineName + "/" + this.pipelineCounter.toString() + "/" + this.stageName + "/" + this.stageCounter.toString();
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public Integer getPipelineCounter() {
        return pipelineCounter;
    }

    public String getPipelineUrlPath() {
        return "/go/api" + "/pipelines/" + this.getPipelineName() + "/instance/" + this.getPipelineCounter().toString();
    }
}