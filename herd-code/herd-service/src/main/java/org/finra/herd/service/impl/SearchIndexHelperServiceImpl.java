/*
* Copyright 2015 herd contributors
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
package org.finra.herd.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.finra.herd.dao.BusinessObjectDefinitionDao;
import org.finra.herd.dao.config.DaoSpringModuleConfig;
import org.finra.herd.model.api.xml.SearchIndexKey;
import org.finra.herd.model.jpa.BusinessObjectDefinitionEntity;
import org.finra.herd.model.jpa.SearchIndexStatusEntity;
import org.finra.herd.service.SearchIndexHelperService;
import org.finra.herd.service.functional.SearchFunctions;
import org.finra.herd.service.helper.BusinessObjectDefinitionHelper;
import org.finra.herd.service.helper.SearchIndexDaoHelper;

/**
 * An implementation of the helper service class for the search index service.
 */
@Service
@Transactional(value = DaoSpringModuleConfig.HERD_TRANSACTION_MANAGER_BEAN_NAME)
public class SearchIndexHelperServiceImpl implements SearchIndexHelperService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchIndexHelperServiceImpl.class);

    @Autowired
    private BusinessObjectDefinitionDao businessObjectDefinitionDao;

    @Autowired
    private BusinessObjectDefinitionHelper businessObjectDefinitionHelper;

    @Autowired
    private SearchFunctions searchFunctions;

    @Autowired
    private SearchIndexDaoHelper searchIndexDaoHelper;

    @Autowired
    private TransportClient transportClient;

    @Override
    public AdminClient getAdminClient()
    {
        return transportClient.admin();
    }

    @Override
    @Async
    public Future<Void> indexAllBusinessObjectDefinitions(SearchIndexKey searchIndexKey, String documentType)
    {
        // Get a list of all business object definitions.
        final List<BusinessObjectDefinitionEntity> businessObjectDefinitionEntities =
            Collections.unmodifiableList(businessObjectDefinitionDao.getAllBusinessObjectDefinitions());

        // Index all business object definitions.
        businessObjectDefinitionHelper
            .executeFunctionForBusinessObjectDefinitionEntities(searchIndexKey.getSearchIndexName(), documentType, businessObjectDefinitionEntities,
                searchFunctions.getIndexFunction());

        // Perform a simple count validation, index size should equal entity list size.
        validateSearchIndexSize(searchIndexKey.getSearchIndexName(), documentType, businessObjectDefinitionEntities.size());

        // Update search index status to READY.
        searchIndexDaoHelper.updateSearchIndexStatus(searchIndexKey, SearchIndexStatusEntity.SearchIndexStatuses.READY.name());

        // Return an AsyncResult so callers will know the future is "done". They can call "isDone" to know when this method has completed and they can call
        // "get" to see if any exceptions were thrown.
        return new AsyncResult<>(null);
    }

    /**
     * Performs a simple count validation on the specified search index.
     *
     * @param indexName the name of the index
     * @param documentType the document type
     * @param expectedIndexSize the expected index size
     *
     * @return true if index size matches the expected size, false otherwise
     */
    protected boolean validateSearchIndexSize(String indexName, String documentType, int expectedIndexSize)
    {
        final long indexSize = searchFunctions.getNumberOfTypesInIndexFunction().apply(indexName, documentType);

        boolean result = true;
        if (indexSize != expectedIndexSize)
        {
            LOGGER.error("Index validation failed, expected index size {}, does not equal actual index size {}.", expectedIndexSize, indexSize);
            result = false;
        }

        return result;
    }
}
