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
package org.finra.herd.swaggergen.test.definitionGenerator;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.finra.herd.swaggergen.test.MultiTypedList;

@XmlRootElement
@XmlType
public class MultiTypedListCase
{
    private MultiTypedList<String, String> list;

    public MultiTypedList<String, String> getList()
    {
        return list;
    }

    public void setList(MultiTypedList<String, String> list)
    {
        this.list = list;
    }
}
