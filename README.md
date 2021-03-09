# Context Model XACML Transformation

![GitHub license](https://img.shields.io/github/license/Trust40-Project/context-confidentiality-editor)
![GitHub top language](https://img.shields.io/github/languages/top/Trust40-Project/Palladio-Addons-DataProcessing-DynamicExtension-XACMLTransformation)
![GitHub last commit (branch)](https://img.shields.io/github/last-commit/Trust40-Project/Palladio-Addons-DataProcessing-DynamicExtension-XACMLTransformation/NewTransformation)

This project implements a Transformation of the Context Confidentiality Metamodel to the XACML Policy (stands for "eXtensible Access Control Markup Language"). The implementation allows to create a XACML Policy for a valid Context Confidentiality Metamodel which can be used for access control requests directly. For a better usability a plugin launcher was implemented as well which allows to navigate in the file system and the workspace.

The project was implemented within the scope of the practical master course *Ingenieursmäßige Software-Entwicklung* at the Karlsruhe Institute of Technology.

## Table of contents
* [Technologies](#technologies)
* [Installation](#installation)
* [Functionalities](#functionalities)
* [Plugin Launch](#plugin-launch)
* [Design decisions](#design-decisions)
* [Tests](#tests)
* [Further development](#further-development)
* [Authors](#authors)

## Technologies:
The project is built with:

* Eclipse (Version: 2020-12 4.18.0)
* Java 11 (AdoptOpenJDK build 11.0.10+9)
* Maven (Version: 3.6.3)

Following dependencies exist:

* XACML 3.0 ([xacml](https://mvnrepository.com/artifact/com.att.research.xacml/xacml/3.0) and [xacml-pdp](https://mvnrepository.com/artifact/com.att.research.xacml/xacml-pdp/3.0))
* [Context Metamodel](https://github.com/Trust40-Project/Palladio-Addons-ContextConfidentiality-Metamodel)

## Installation
For the installation and further development of plugins follow these steps:
1. Clone and import the *XACMLTransformation* repository 
    *git clone https://github.com/Trust40-Project/Palladio-Addons-DataProcessing-DynamicExtension-XACMLTransformation.git*
    and checkout the branch **NewTransformation**
1. Download and install maven [download](https://maven.apache.org/download.cgi)
1. Change to the maven_projekt directory and run *mvn clean install*. The build should be successful.

For launching plugins from Eclipse:
1. Download and install Eclipse [download](https://www.eclipse.org/downloads/)
1. Download and install *Eclipse PDE (Plug-in Development Environment)*
    * Go to Eclipse &rarr; Help &rarr; Eclipse Marketplace
    * Search and install Eclipse PDE (Plug-in Development Environment)
1. Import the project from the maven_project directory together with the nested projetcs.
1. Import the project from the XACML_Lib direcctory with dependencies.
1. Clone and import the *ContextConfidentality Metamodel* repository 
*git clone https://github.com/Trust40-Project/Palladio-Addons-ContextConfidentiality-Metamodel*

Now it should be possible to launch plugins.

## Functionalities
Functionality of the plugin allows to create a valid XACML policy ([wikipedia](https://en.wikipedia.org/wiki/XACML)). The plugin uses a library com.att.research.xacml for creating of XACML elements.
An example of the output policy:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<PolicySet xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17" PolicySetId="Scenarios" Version="1.0" PolicyCombiningAlgId="urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides">
    <Description>all policies combined for policy set with id Scenarios</Description>
    <Target/>
    <Policy PolicyId="AccessOperationBookSelected" Version="1.0" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable">
        <Description>Policy for bookSelected</Description>
        <Target>
            <AnyOf>
                <AllOf>
                    <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">bookSelected</AttributeValue>
                        <AttributeDesignator Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" AttributeId="methodSpecification:signature" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
                    </Match>
                    <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">TravelPlanner &lt;TravelPlanner&gt;</AttributeValue>
                        <AttributeDesignator Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action" AttributeId="methodSpecification:assemblyContext" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
                    </Match>
                </AllOf>
            </AnyOf>
        </Target>
        <Rule RuleId="Permit:bookSelected" Effect="Permit">
            <Description>Context check rule for entity bookSelected</Description>
            <Target>
                <AnyOf>
                    <AllOf>
                        <Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
                            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">SuperUser|Customer</AttributeValue>
                            <AttributeDesignator Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="context:AttributeContext:ContextType:EntityName:User" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
                        </Match>
                    </AllOf>
                </AnyOf>
            </Target>
        </Rule>
        <Rule RuleId="denyIfNotApplicable" Effect="Deny">
            <Description>this rule denies if this case is not applicable</Description>
            <Target/>
        </Rule>
    </Policy>
</PolicySet>

```

For test purposes the following request can be used for the created policy. The policy will permit an access in this case.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Request xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17
http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd"
ReturnPolicyIdList="false" CombinedDecision="false">
    <Attributes Category="urn:oasis:names:tc:xacml:3.0:attribute-category:action">
        <Attribute AttributeId="methodSpecification:signature" IncludeInResult="false">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">bookSelected</AttributeValue>
        </Attribute>
        <Attribute AttributeId="methodSpecification:assemblyContext" IncludeInResult="false">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">TravelPlanner &lt;TravelPlanner&gt;</AttributeValue>
        </Attribute>
    </Attributes>
    <Attributes Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject">
        <Attribute AttributeId="context:AttributeContext:ContextType:EntityName:User" IncludeInResult="false">
            <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Customer</AttributeValue>
        </Attribute>
    </Attributes>
</Request>

```
The plugin expects a Context Confidentiality Model as an input. Model elements are used for creating of different attributes of the output XACML policy.
The output model consists of PolicySet which may contain multiple Policies. Every Policy corresponds to one method specification from the model.
Target tags inside of the policy decide if the policy has to be applied. At this place assemblyContext and signature of the method specification are checked.
If the policy is applied then it is decided to permit or deny an access. To be able to do it the plugin creates rules which have different attributes.
The attributes have values which are obtained from context elements of the input model. The following context attributes are used:

* Single Attribute Context
* Hierarchical Context Bottom Up
* Hierarchical Context Top Down
* Related Context Set

Contexts have different context types and expected values.
Single Attribute Context of the type User with the value Customer results in the following match:

```xml
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
	<AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">Customer</AttributeValue>
    <AttributeDesignator Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="context:AttributeContext:ContextType:EntityName:User" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
</Match>
```
Hierarchical contexts have *including* contexts which have to be considered as well. These are children or parents which have more access rights and should be allowed too.
For example a hierarchical attribute context of the type User with the value Customer may take two different values: Customer itself and SuperUser which has more rights. In this case possible values are split by | building a valid regular expression.

```xml
<Match MatchId="urn:oasis:names:tc:xacml:1.0:function:string-regexp-match">
    <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">SuperUser|Customer</AttributeValue>
    <AttributeDesignator Category="urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" AttributeId="context:AttributeContext:ContextType:EntityName:User" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
</Match>
```
Related contexts describe attributes of other objects which are relevant for the operation. For example if it is necessary to have a partner to complete an operation then there should be a match for partner attributes too:

```xml
AttributeId="context:AttributeContext:ContextType:EntityName:RelatedPartner:Location"
```

Please have a look at */org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.tests/testfiles/models/Scenarios*. The directory contains many different examples of the attributes.

## Plugin Launch

In order to run the plugin from the eclipse, open *Run/Debug Configurations* and start an *Eclipse Application*. After that a new instance of the Eclipse containing new plugins will appear.

For a better convenience the launcher config was implemented. It allows to select an input model and output directory form the file system or workspace. Also an output file name has to be specified.
In *Run/Debug Configurations* of the new instance of Eclipse open *Run/Debug Configurations* again. Choose *XACML Transformation* configuration there:

<img title="Launcher Menu Item" src="Screenshots/menu.png" height="500" />

Then enter values on the XACML Transformation properties tab. You can choose files from the workspace or the file system:
<img title="Launcher Menu Item" src="Screenshots/LauncherConfiguration.png" height="200" />

Click *Apply - Run/Debug*. If the transformation finishes successfully, you will get a message about it:

<img title="Message" src="Screenshots/message.png" height="130" />

## Design Decisions
* Separating a GUI and a transformation logic to two different bundles. A bundle with the logic can be reused by other projects.

* Handlers are responsible for a control of the plugin logic
<img title="Handlers" src="Screenshots/handlers.png" width="500" />

* Creators are responsible for creating top elements of the XACML (*PolicySet* and *Policy*)
<img title="Policy Creators" src="Screenshots/creators.png" width="500" />

* Matches classes represent different types of matches which are used for taking decisions
<img title="Matches" src="Screenshots/Matches.png" width="500" />

* Model Loader loads a valid *Confidential Access Specification Model*
<img title="ModelLoader" src="Screenshots/ModelLoader.png" width="500" />

* XACMLPolicyWriter was taken from the com.att.research.xacml and adopted to the plugin. There are some changes because of the problem with the javax.xml.bind package, which does not exist in the Java11 and has to be imported. (See [JAXB on Java 9, 10, 11 and beyond](https://www.jesperdj.com/2018/09/30/jaxb-on-java-9-10-11-and-beyond/)). Also because of this problem two packages ( javax.xml.bind and com.sun.xml.bind.v2) have to be imported in the Manifest files of the plugin. 
</p><img title="Writer" src="Screenshots/XACMLWriter.png" width="500" />

* XACML_Lib contains a new version (3.0) of the com.att.research.xacml and com.att.research.xacml-pdp libraries. And comparing to earlier ThirdParty project it contains all dependencies just as jar files, there is no need to decompile them all.

## Tests

Thanks to generic implementation of tests and many different test files a high test coverage of about 95% could be reached. Remaining percents can not be reached because of plugin development specifity as some code can not be mocked or run from tests.

<img title="Tests" src="Screenshots/tests.png" width="500" />

## Further development

1. It should be checked if it is possible to reimplement functionalities of the com.att.research.xacml and com.att.research.xacml-pdp libraries in order to be able to avoid extra dependencies.
1. As the project de.uka.ipd.sdq.workflow.launchconfig has some problems with tabs, I implemented some functionality of the project inside of the launch project. It would be better to fix the problems in the de.uka.ipd.sdq.workflow.launchconfig/ project and use it from the launch project. There are some small bugs and deprecated classes/methods.

## Authors
- [Vladimir Solovyev](https://github.com/vladsolovyev)