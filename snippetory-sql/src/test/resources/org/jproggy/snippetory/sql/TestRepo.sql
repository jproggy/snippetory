-- $test1{
SELECT * 
FROM tblTest1 
WHERE value = :value1
-- }test1$
-- $test2{
SELECT * 
FROM tblTest1 
WHERE value = :value1 or value = :value2
-- }test2$
-- $test3{
SELECT * 
FROM tblTest1 
WHERE value = :value1 or value = :value2 or value = :value3
-- }test3$
-- $test4{
SELECT * 
FROM tblTest1 
WHERE value = :value1/*$t1{*/ or value = :value/*}$${*/ or value = :value2/*}$*/
-- }test4$
-- $test5{
SELECT * 
FROM tblTest1 
WHERE value = :value1/*${*/ or value = :value2/*}$$t1{*/ or value = :value/*}$*/
-- }test5$
