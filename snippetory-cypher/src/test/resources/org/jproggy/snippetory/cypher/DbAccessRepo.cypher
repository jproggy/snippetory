/// Copyright JProggy
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

// $fillSimpleTable{
CREATE (:simple {name:'Horst', price:1.0, ext_id:'sale1'})
CREATE (:simple {name:'Kuno', price:22.0, ext_id:'test2'})
CREATE (:simple {name:'Bruno', price:333.0, ext_id:'whatsoever'})
CREATE (:simple {name:'Karl', price:101.3, ext_id:'test4'})
CREATE (:simple {name:'Egon', price:123.45, ext_id:'test6'})
// }$
  
// $insertSimpleTable{
// $values{
CREATE (:simple { name:$name, price:$price, ext_id:$ext_id, xx:$xx })
//}$
// }$

// $deleteSimpleTable{
MATCH (n:simple
// ${
{ ext_id: $ext_id }
// }$
)
DELETE n
// }$

// $selectSimpleTable{
MATCH (n:simple)
WHERE 1=1
// ${
AND n.ext_id STARTS WITH $ext_id
// }$ $name{
AND n.name $(default="IS NULL"){= $name}$
// }$
RETURN n.name, n.price, n.ext_id, n.xx
// ${
ORDER BY $order(delimiter=', ')
// }$
// }selectSimpleTable$

// $nulls{
MATCH (:simple)
RETURN count(*), null AS colNullStr, 'something' AS colStr
// }$
