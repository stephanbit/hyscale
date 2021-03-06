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
package io.hyscale.controller.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.hyscale.commons.constants.ToolConstants;
import io.hyscale.commons.exception.CommonErrorCode;
import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.servicespec.commons.exception.ServiceSpecErrorCodes;
import io.hyscale.servicespec.commons.fields.HyscaleSpecFields;
import io.hyscale.servicespec.commons.model.service.ServiceSpec;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ServiceSpecUtil {

    private static final Logger logger = LoggerFactory.getLogger(ServiceSpecUtil.class);

    public static String getServiceName(File serviceFile) throws HyscaleException{
        if (serviceFile == null) {
            return null;
        }
        try {
            ServiceSpec serviceSpec = new ServiceSpec(FileUtils.readFileToString(serviceFile, ToolConstants.CHARACTER_ENCODING));
            JsonNode serviceNodeValue = serviceSpec.get(HyscaleSpecFields.name);
            if (serviceNodeValue == null) {
                HyscaleException hyscaleException = new HyscaleException(ServiceSpecErrorCodes.MISSING_FIELD_IN_SERVICE_FILE,HyscaleSpecFields.name);
                logger.error(hyscaleException.getMessage());
                throw hyscaleException;
            }
            return serviceNodeValue.asText();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new HyscaleException(CommonErrorCode.FAILED_TO_READ_FILE, serviceFile.getPath());
        }
    }
}
