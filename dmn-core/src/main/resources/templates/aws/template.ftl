AWSTemplateFormatVersion: "2010-09-09"
Transform: AWS::Serverless-2016-10-31
Description: >
  ${stackName}

  ${modelName}

# More info about Globals: https://github.com/awslabs/serverless-application-model/blob/master/docs/globals.rst
Globals:
  Function:
    Timeout: 50
    Runtime: java11
    MemorySize: 512
    # Integration with AWS X-Ray https://docs.aws.amazon.com/lambda/latest/dg/java-tracing.html
    Tracing: Active
    AutoPublishAlias: live
    DeploymentPreference:
      Type: AllAtOnce
    # https://docs.aws.amazon.com/lambda/latest/dg/configuration-concurrency.html
    # https://aws.amazon.com/blogs/aws/new-provisioned-concurrency-for-lambda-functions/
    ProvisionedConcurrencyConfig:
      ProvisionedConcurrentExecutions: 5

Resources:
<#list functionResources as r>
  ${r.functionName}:
    Type: AWS::Serverless::Function # More info about Function Resource: https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-function.html
    Properties:
      FunctionName: ${r.functionName}
      CodeUri: ${r.codeUri}
      Handler: ${r.handler}
      Runtime: java11
      MemorySize: 512
      Tracing: Active # Tracing https://docs.aws.amazon.com/lambda/latest/dg/java-tracing.html
      Environment: # More info about Env Vars: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#environment-object
        Variables:
          PARAM1: VALUE
      Events:
        ${r.functionName}:
          Type: Api # More info about API Event Source: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#api
          Properties:
            Path: /${r.restPath}
            Method: get

</#list>
Outputs:
  # ServerlessRestApi is an implicit API created out of Events key under Serverless::Function
  # Find out more about other implicit resources you can reference within SAM
  # https://github.com/awslabs/serverless-application-model/blob/master/docs/internals/generated_resources.rst#api
<#list functionResources as r>
  ${r.functionName}Api:
    Description: "API Gateway endpoint URL for Prod stage for ${r.functionName}"
    Value: !Sub "https://${r"${ServerlessRestApi}"}.execute-api.${r"${AWS::Region}"}.amazonaws.com/Prod/${r.restPath}"
  ${r.functionName}:
    Description: "ARN of ${r.functionName}"
    Value: !GetAtt ${r.functionName}.Arn
  ${r.functionName}IamRole:
    Description: "Implicit IAM Role created for ${r.functionName}"
    Value: !GetAtt ${r.functionName}Role.Arn

</#list>
