/*
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
package cn.edu.ruc.iir.paraflow.metaserver.action;

import cn.edu.ruc.iir.paraflow.commons.exceptions.ActionParamNotValidException;
import cn.edu.ruc.iir.paraflow.commons.exceptions.ParaFlowException;
import cn.edu.ruc.iir.paraflow.metaserver.connection.Connection;
import cn.edu.ruc.iir.paraflow.metaserver.connection.ResultList;
import cn.edu.ruc.iir.paraflow.metaserver.proto.MetaProto;
import cn.edu.ruc.iir.paraflow.metaserver.utils.SQLTemplate;

import java.util.ArrayList;
import java.util.Optional;

public class GetDbTblIdAction extends Action
{
    @Override
    public ActionResponse act(ActionResponse input, Connection connection) throws ParaFlowException
    {
        Optional<Object> dbIdOp = input.getProperties("dbId");
        if (dbIdOp.isPresent()) {
            long dbId = (long) dbIdOp.get();
            String sqlStatement = SQLTemplate.findTblIdWithoutName(dbId);
            ResultList resultList = connection.executeQuery(sqlStatement);
            MetaProto.StringListType stringList;
            int size = 0;
            if (!resultList.isEmpty()) {
                //result
                ArrayList<String> result = new ArrayList<>();
                size = resultList.size();
                for (int i = 0; i < size; i++) {
                    result.add(resultList.get(i).get(0));
                }
                stringList = MetaProto.StringListType.newBuilder()
                        .addAllStr(result)
                        .setIsEmpty(false)
                        .build();
            }
            else {
                  stringList = MetaProto.StringListType.newBuilder()
                        .setIsEmpty(true)
                        .build();
            }
            input.setParam(stringList);
            input.setProperties("size", size);
        }
        else {
            throw new ActionParamNotValidException();
        }
        return input;
    }
}
