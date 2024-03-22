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

// $test1{
MATCH (n:tblTest1)
WHERE value = $value1
RETURN n
// }test1$
// $test2{
MATCH (n:tblTest1)
WHERE value = :value1 or value = :value2
RETURN n
// }test2$
// $test3{
MATCH (n:tblTest1)
WHERE value = :value1 or value = :value2 or value = :value3
RETURN n
// }test3$
// $test4{
MATCH (n:tblTest1)
WHERE value = :value1$t1{ or value = :value}$${ or value = :value2}$
RETURN n
// }test4$
// $test5{
MATCH (n:tblTest1)
WHERE value = :value1${ or value = :value2}$$t1{ or value = :value}$
RETURN n
// }test5$
