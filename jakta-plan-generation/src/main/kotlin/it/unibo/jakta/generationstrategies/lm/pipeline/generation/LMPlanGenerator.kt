package it.unibo.jakta.generationstrategies.lm.pipeline.generation

import it.unibo.jakta.generationstrategies.lm.pipeline.generation.impl.MultiStepGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ProgramParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.IncompleteTagParsing
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.InvalidGoal
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.InvalidGuard
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.InvalidStep
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.InvalidTrigger
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.ProcessorFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.UnknownTagType
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagType
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler

interface LMPlanGenerator : LMGenerator {
    companion object {
        fun multistep(
            requestHandler: RequestHandler,
            responseParser: ResponseParser = ProgramParser.of(),
        ): LMPlanGenerator =
            MultiStepGenerator(requestHandler, responseParser)

        fun getErrorMsgFromParsingFailure(parserResult: PlanGenerationParserFailure): String =
            when (parserResult) {
                is IncompleteTagParsing -> {
                    """
                There is an incomplete tag in your response. 
                The '${parserResult.incompleteTag}' tag was opened but never properly closed.
                Please make sure all XML-style tags are properly closed with a matching closing tag.
                
                For example, if you open with <${parserResult.incompleteTag.name}>, 
                make sure to close with </${parserResult.incompleteTag.name}>
                
                The incomplete content was: ${parserResult.rawContent}
                """
                }

                is InvalidGoal -> {
                    """
                The goal you specified could not be parsed correctly: '${parserResult.rawContent}'.
                Goals should be clear, actionable statements.
                Please ensure your goal is properly formatted and doesn't contain syntax errors.
                """
                }

                is InvalidGuard -> {
                    """
                The guard condition could not be parsed: '${parserResult.rawContent}'.
                Guards should be logical expressions that evaluate to true or false.
                Please check the syntax and ensure you're using valid operators and variables.
                """
                }

                is InvalidStep -> {
                    """
                The step you provided couldn't be parsed: '${parserResult.rawContent}'.
                Steps should be clear, executable actions.
                Please ensure your step is formatted correctly according to our system's requirements.
                Remember to enclose individual steps in backticks (`) when needed.
                """
                }

                is InvalidTrigger -> {
                    """
                The trigger you specified couldn't be parsed: '${parserResult.rawContent}'.
                Triggers should clearly define when a plan should activate.
                Please check the syntax and format of your trigger and ensure it follows the expected structure.
                Triggers typically need specific formatting to be recognized by our system.
                """
                }

                is ProcessorFailure -> {
                    """
                The content inside the <${parserResult.tagType.name}> tag: '${parserResult.rawContent} could not be processed'.
                The content doesn't match the expected format for this tag type.
                Please review the requirements for ${parserResult.tagType.name} content and
                ensure your input follows the correct structure.
                This tag might require a specific format that wasn't followed.
                """
                }

                is UnknownTagType -> {
                    """
                I encountered an unknown tag type: '${parserResult.tagType.name}' with content: '${parserResult.rawContent}'.
                Please use only the supported tags in your response.
                The supported tags are: ${TagType.entries.joinToString { it.name }}.
                Using unsupported tags prevents proper processing of your response.
                """
                }
            }
    }
}
