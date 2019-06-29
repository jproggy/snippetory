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

-- $createSimpleTable{
CREATE TABLE simple (
  simple_id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
  name VARCHAR(255),
  price DECIMAL(10,3),
  ext_id VARCHAR(45) NOT NULL,
  xx TIME,
  PRIMARY KEY (simple_id))
  
-- $mysql{
CREATE TABLE simple (
  simple_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  price DECIMAL(10,3),
  ext_id VARCHAR(45) NOT NULL,
  xx TIME,
  PRIMARY KEY (simple_id),
  INDEX ext_id (ext_id));
-- }$

-- $sqlite{
CREATE TABLE simple (
  simple_id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(255),
  price DECIMAL(10,3),
  ext_id VARCHAR(45) NOT NULL,
  xx TIME);
-- }$

-- $postgres{
CREATE TABLE simple (
  simple_id serial NOT NULL,
  name VARCHAR(255),
  price DECIMAL(10,3),
  ext_id VARCHAR(45) NOT NULL,
  xx TIME,
  PRIMARY KEY (simple_id));
-- }$
-- }$

-- $fillSimpleTable{
INSERT INTO simple 
(name, price, ext_id)
VALUES 
('Horst', 1.0, 'sale1'),
('Kuno', 22.0, 'test2'),
('Bruno', 333.0, 'whatsoever'),
('Karl', 101.3, 'test4'),
('Egon', 123.45, 'test6')
-- }$
  
-- $insertSimpleTable{
INSERT INTO simple 
(name, price, ext_id, xx)
VALUES 
/* $values(delimiter=",\n"){*/(:name, :price, :ext_id, :xx)/*}$*/
-- }$

-- $deleteSimpleTable{
DELETE FROM simple
-- ${
WHERE ext_id = :ext_id
-- }$
-- }$

-- $selectSimpleTable{
SELECT simple_id, name, price, ext_id, xx FROM simple
WHERE 1=1
-- ${
AND ext_id like :ext_id || '%'
-- $mysql{
AND ext_id like concat(:ext_id, '%')
-- }$
-- }$ $name{
AND name /*$(default="IS NULL"){ */ = :name -- }$
-- }$
-- }selectSimpleTable$

-- $nulls{
SELECT 
  count(*),
  cast(null as VARCHAR(255)) AS colNullStr, 
  cast(null as Integer) AS colNullInt, 
  'something' AS colStr, 
  1 AS colNum 
FROM simple
-- $mysql{
SELECT null AS colNullStr, null AS colNullInt, 'something' AS colStr, 1 AS colNum
-- }$
-- }$
