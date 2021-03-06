//    Copyright 2020 Google Inc.
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        https://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

package com.google.fhir.protogen;

import com.google.fhir.common.InvalidFhirException;
import com.google.fhir.common.ResourceValidator;
import com.google.fhir.proto.CodeSystemConfig;
import com.google.fhir.proto.PackageInfo;
import com.google.fhir.proto.Terminologies;
import com.google.fhir.r4.core.Bundle;
import com.google.fhir.r4.core.CodeSystem;
import com.google.fhir.r4.core.CodeSystemContentModeCode;
import com.google.fhir.r4.core.DateTime;
import java.time.LocalDate;

/**
 * Generator for creating CodeSystem and ValueSet resources from Terminologies protos from
 * profile_config.proto
 */
final class TerminologyGenerator {
  private final PackageInfo packageInfo;
  private final DateTime creationDateTime;
  private final ResourceValidator resourceValidator = new ResourceValidator();

  TerminologyGenerator(PackageInfo packageInfo, LocalDate creationTime) {
    this.packageInfo = packageInfo;
    this.creationDateTime = GeneratorUtils.buildCreationDateTime(creationTime);
  }

  Bundle generateCodeSystems(Terminologies terminologies) {
    Bundle.Builder bundle = Bundle.newBuilder();
    for (CodeSystemConfig codeSystemConfig : terminologies.getCodeSystemList()) {
      bundle
          .addEntryBuilder()
          .getResourceBuilder()
          .setCodeSystem(buildCodeSystem(codeSystemConfig));
    }

    return bundle.build();
  }

  private CodeSystem buildCodeSystem(CodeSystemConfig config) {
    CodeSystem.Builder builder = CodeSystem.newBuilder();
    builder.getNameBuilder().setValue(config.getName());
    builder.getTitleBuilder().setValue(config.getName());
    if (!config.getDescription().isEmpty()) {
      builder.getDescriptionBuilder().setValue(config.getDescription());
    }
    builder.getStatusBuilder().setValue(config.getStatus());
    builder.setDate(creationDateTime);
    builder.getPublisherBuilder().setValue(packageInfo.getPublisher());
    builder.getContentBuilder().setValue(CodeSystemContentModeCode.Value.COMPLETE);

    String url =
        config.getUrlOverride().isEmpty()
            ? packageInfo.getBaseUrl() + "/" + config.getName()
            : config.getUrlOverride();
    builder.getUrlBuilder().setValue(url);

    for (CodeSystemConfig.Concept concept : config.getConceptList()) {
      addConcept(builder, concept);
    }

    CodeSystem codeSystem = builder.build();
    try {
      resourceValidator.validateResource(codeSystem);
    } catch (InvalidFhirException e) {
      System.out.println("Invalid Generated CodeSystem: " + e.getMessage());
      System.out.println(codeSystem);
      throw new IllegalArgumentException(e);
    }
    return codeSystem;
  }

  private static void addConcept(CodeSystem.Builder builder, CodeSystemConfig.Concept concept) {
    CodeSystem.ConceptDefinition.Builder conceptBuilder = builder.addConceptBuilder();
    conceptBuilder.getCodeBuilder().setValue(concept.getCode());
    if (!concept.getDisplay().isEmpty()) {
      conceptBuilder.getDisplayBuilder().setValue(concept.getDisplay());
    }
    if (!concept.getDefinition().isEmpty()) {
      conceptBuilder.getDefinitionBuilder().setValue(concept.getDefinition());
    }

    // TODO: handle child concepts.
  }
}
