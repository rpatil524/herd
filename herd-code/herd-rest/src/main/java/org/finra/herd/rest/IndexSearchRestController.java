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
package org.finra.herd.rest;

import static org.finra.herd.model.dto.SecurityFunctions.FN_INDEX_SEARCH_POST;
import static org.finra.herd.ui.constants.UiConstants.REST_URL_BASE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Set;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.finra.herd.model.api.xml.IndexSearchRequest;
import org.finra.herd.model.api.xml.IndexSearchResponse;
import org.finra.herd.service.IndexSearchService;

/**
 * The REST controller that handles global indexSearch REST requests.
 */
@RestController
@RequestMapping(value = REST_URL_BASE, produces = {"application/xml", "application/json"})
@Api(tags = "Index Search")
public class IndexSearchRestController extends HerdBaseController
{
    @Autowired
    private IndexSearchService indexSearchService;

    /**
     * The index search POST method.
     *
     * @param fields the set of fields that are to be returned in the index search response (accepts: displayname and shortdescription)
     * @param match the set of match fields that the search will be restricted to (accepts: column)
     * @param request the index search request
     * @return the index search response
     */
    @RequestMapping(value = "/indexSearch", method = POST, consumes = {"application/xml", "application/json"})
    @Secured(FN_INDEX_SEARCH_POST)
    public IndexSearchResponse indexSearch(@RequestParam(value = "fields", required = false, defaultValue = "") Set<String> fields,
        @RequestParam(value = "match", required = false, defaultValue = "") Set<String> match, @RequestBody IndexSearchRequest request)
    {
        return indexSearchService.indexSearch(request, fields, match);
    }
}
