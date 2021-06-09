<a href="https://www.jproggy.org/snippetory/intact-templates/"><img src="https://www.jproggy.org/img/snippetorytx60.png" /></a>

# A code generation platform for java

## Fluent API
```java
Syntaxes.FLUYT.parse("Hello $who").set("who", "world").render(System.out);
```
## Simple Syntax
### Locations
```
$name
```
Locations can have [attributes].
```
$label(pad="15" pad.fill="."): $name(default="Snippetory")
```
### Regions
```
$fields{
$name(pad="15"pad.fill="."): $value(number="#.###,##" date="long")    
}$
```
Regions allow loops or conditions
```java
void renderFields(Map<String, Object> fields, Template fieldsTpl) {
    field.forEach(k, v -> 
        fieldsTpl
            .get("fields")
            .set("name", k)
            .set("value", v)
            .render()
    )
}
```
Regions have attributes like locations.
```
$products(delimiter="------------\n"){
...
}$
```
### Comments
```java
/// start with a triple slash
```
### Syntax selector
To keep the [template intact] there are several [syntaxes].
```html
<!-- Syntax:FLUYT_X -->
<nav>
<ul>
  <t:menu>
  <li><a href="$page(enc='url')">$text</a></li>
  </t:menu>
</ul>
</nav>
```
The tag syntax allowes the IDE to support in closing the tags and keep the overview with code folding...
```java
// Syntax:FLUYT_CC
// $getters{
  public $type get$Name$() {
    return $name$;
  }
// }$
```
And having valid java code lets the IDE maintain the templates during re-factorings. 

## [SQL] module
With the StatementRepository templates produce prepared statements in JDBC
```sql
-- $variables{
set :currency = 'EUR';
set :catIds = 17;
-- }$
SELECT products.*
FROM products
LEFT OUTER JOIN prices ON ( 
      products.id = prices.productId 
  AND prices.currency = :currency 
)
WHERE products.id IN (
    SELECT prodCat.product.id
    FROM prodCat
    /*${*/
    WHERE prodCat.categoryId IN (:catIds/*delimiter=', '*/)
    /*}$*/
)
```
Having prepared variables makes it easy to test the intact statement, a fluent API gives access to the data.
```java
Statement stmt = new SqlContext()
  .uriResolver(UriResolver.resource("org/jproggy/sample"))
  .connections(() -> connection)
  .getRepository("DbAccessRepo.sql")
  .get("statement1");

catIds.forEach(id -> stmt.append("catId", id));
List<Object[]> result = stmt.set("currency", "USD")
  .list(SQL.objects());
```
## So much more
* The platform is [extensible] concerning
  * [Formats]
  * [Encodings]
  * [Syntaxes]
* A `TemplateWrapper` helps with implementing cross cutting concerns
* Groovy integration
* ...

Further information can be found on the [homepage] or the [project blog].

[extensible]: https://www.jproggy.org/snippetory/ExtensionExample.html
[formats]: https://www.jproggy.org/snippetory/formats/
[encodings]: https://www.jproggy.org/snippetory/encodings/
[syntaxes]: https://www.jproggy.org/snippetory/syntax/#FLUYT
[SQL]: https://www.jproggy.org/snippetory/sql/
[template intact]: https://www.jproggy.org/snippetory/intact-templates/
[attributes]: https://www.jproggy.org/snippetory/syntax/#Attributes
 [homepage]: https://www.jproggy.org/snippetory/ "Documentation for Snippetory"
[project blog]: https://snippetory.wordpress.com 
