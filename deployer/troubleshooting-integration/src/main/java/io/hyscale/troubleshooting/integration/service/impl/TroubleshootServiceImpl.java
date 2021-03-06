/**
 * Copyright 2019 Pramati Prism, Inc.
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
package io.hyscale.troubleshooting.integration.service.impl;

import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.commons.models.K8sAuthorisation;
import io.hyscale.troubleshooting.integration.builder.TroubleshootingContextCollector;
import io.hyscale.troubleshooting.integration.conditions.PodStatusCondition;
import io.hyscale.troubleshooting.integration.models.DiagnosisReport;
import io.hyscale.troubleshooting.integration.models.Node;
import io.hyscale.troubleshooting.integration.models.ServiceInfo;
import io.hyscale.troubleshooting.integration.models.TroubleshootingContext;
import io.hyscale.troubleshooting.integration.service.TroubleshootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TroubleshootServiceImpl implements TroubleshootService {

    private static final Logger logger = LoggerFactory.getLogger(TroubleshootServiceImpl.class);

    @Autowired
    private TroubleshootingContextCollector contextBuilder;

    @Autowired
    private PodStatusCondition podStatusCondition;

    @Override
    public List<DiagnosisReport> troubleshoot(ServiceInfo serviceInfo, K8sAuthorisation k8sAuthorisation, String namespace) throws HyscaleException {
        try {
            TroubleshootingContext troubleshootingContext = contextBuilder.build(serviceInfo, k8sAuthorisation, namespace);
            executeTroubleshootFlow(troubleshootingContext);
            return troubleshootingContext.getDiagnosisReports();
        } catch (HyscaleException e) {
            logger.error("Error while troubleshooting service {}", serviceInfo.getServiceName(), e);
            throw e;
        }
    }

    private void executeTroubleshootFlow(TroubleshootingContext troubleshootingContext) throws HyscaleException {
        Node current = podStatusCondition;
        try {
            do {
                if (troubleshootingContext.isTrace()) {
                    logger.debug("Executing troubleshooting node {}", current.describe());
                }
                current = current.next(troubleshootingContext);
            } while (current != null);
        } catch (HyscaleException e) {
            logger.error("Error while troubleshooting workflow {}", e);
            throw e;
        }
    }
}
