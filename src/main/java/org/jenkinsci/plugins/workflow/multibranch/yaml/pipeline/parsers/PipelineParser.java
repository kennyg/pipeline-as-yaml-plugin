package org.jenkinsci.plugins.workflow.multibranch.yaml.pipeline.parsers;

import org.jenkinsci.plugins.pipeline.modeldefinition.ast.ModelASTPipelineDef;
import org.jenkinsci.plugins.pipeline.modeldefinition.parser.Converter;
import org.jenkinsci.plugins.workflow.multibranch.yaml.pipeline.exceptions.PipelineAsYamlException;
import org.jenkinsci.plugins.workflow.multibranch.yaml.pipeline.interfaces.ParserInterface;
import org.jenkinsci.plugins.workflow.multibranch.yaml.pipeline.models.PipelineModel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.util.LinkedHashMap;
import java.util.Optional;

public class PipelineParser extends AbstractParser implements ParserInterface<PipelineModel> {

    private String jenkinsFileAsYamlContent;
    private PipelineModel pipelineModel;

    public PipelineParser(String jenkinsFileAsYamlContent){
        this.jenkinsFileAsYamlContent = jenkinsFileAsYamlContent;
        this.yamlNodeName = PipelineModel.directive;
    }

    @Override
    public Optional<PipelineModel> parse()  {
        try {
            LinkedHashMap jenkinsFileHashMap = yaml.load(this.jenkinsFileAsYamlContent);
            LinkedHashMap pipelineNode = this.getChildNodeAsLinkedHashMap(jenkinsFileHashMap);
            this.pipelineModel = PipelineModel.builder()
                    .agent(new AgentParser(pipelineNode).parse())
                    .post(new PostParser(pipelineNode).parse())
                    .environment(new EnvironmentParser(pipelineNode).parse())
                    .tools(new ToolsParser(pipelineNode).parse())
                    .options(new OptionsParser(pipelineNode).parse())
                    .parameters(new ParametersParser(pipelineNode).parse())
                    .triggers(new TriggersParser(pipelineNode).parse())
                    .stages(new StagesParser(pipelineNode).parse())
                    .build();
            return Optional.ofNullable(this.pipelineModel);
        }
        catch (PipelineAsYamlException p) {
            return Optional.empty();
        }
    }

    public static ModelASTPipelineDef parse(String jenkinsFileAsYamlContent){
        ModelASTPipelineDef modelASTPipelineDef = Converter.scriptToPipelineDef(jenkinsFileAsYamlContent);
        System.out.println("");
        return modelASTPipelineDef;
    }

}