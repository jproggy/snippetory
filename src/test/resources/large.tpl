<s:XML_ALIKE />
<t:XML_ALIKE>
<t:email>
{v:salutation} {v:name},

{v:text}

{v:name stretch='40'}{v:quantity stretch='10r'} {v:price stretch='12r'} {v:sum stretch='14r'}
====================================================================================
<t:cart_entry>
{v:name stretch='40'}{v:quantity stretch='10r'} {v:price stretch='12r'} {v:sum stretch='14r'} {v:currency}
</t:cart_entry>
-------------------------------------------------------------------------------------
{v:total-label                                         stretch='62r'} {v:total stretch='14r'} {v:currency}

{v:footer}
</t:email>
</t:XML_ALIKE>


//Syntax:C_COMMENTS
/* ${ C_COMMENTS */
// ${bean.java
package /*${package*/org.jproggy.snippetory.toolyng.beanery/*}*/;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.TemplateWrapper;
// ${ class

/*${i}*/public class /*${name case="camelizeUpper"*/Bean/*}*/ extends TemplateWrapper {
/*${i}*/  public /* ${ name case="camelizeUpper"*/Bean/*}*/ (Template template) {
/*${i}*/      super(template);
/*${i}*/  }
//${ region prefix="\n"
/*${i}*/  public /*${name case="camelizeUpper"*/Bean/*}*/ get/*${name case="camelizeUpper"}*/() {
/*${i}*/    return new /*${name case="camelizeUpper"*/Bean/*}*/(get("/*${name}*/")); 
/*${i}*/  } 
// region }
//${ location prefix="\n"
/*${i}*/  public /*${parent case="camelizeUpper"*/Bean/*}*/ set/*${name case="camelizeUpper"}*/(Object value) {
/*${i}*/    set("/*${name}*/", value);  
/*${i}*/    return this; 
/*${i}*/  } 
// location }
/// use region with full line mark up to avoid additional line breaks 
//${ classes
//   classes }
/*${i*/ /*i}*/} 
// class }
// bean.java}
/* C_COMMENTS} */


--Syntax:FLUYT
$FLUYT{
$bean.java{
package $package;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.TemplateWrapper;
$class{

$i$public class $name(case="camelizeUpper") extends TemplateWrapper {
$i  public $name(case="camelizeUpper") (Template template) {
$i      super(template);
$i  }
$region(prefix="\n"){
$i  public $name(case="camelizeUpper") get$name(case="camelizeUpper")() {
$i    return new $name(case="camelizeUpper")(get("$name")); 
$i  } 
}$
$location(prefix="\n"){
$i  public $parent(case="camelizeUpper") set$name(case="camelizeUpper")(Object value) {
$i    set("$name", value);  
$i    return this; 
$i  } 
}$
/// use region with full line mark up to avoid additional line breaks 
$classes{
}$
$i{ }$} 
}class$
}bean.java$
$email{
$salutation $name,

$text

$name(stretch='40') $quantity(stretch='10r') $price(stretch='12r') $sum(stretch='18r')
====================================================================================
$cart_entry{
$name(stretch='40') $quantity(stretch='10r') $price(stretch='12r') $(stretch='14r'){$sum $currency}$
}$
-------------------------------------------------------------------------------------
$total-label(                                       stretch='62r') $total(stretch='14r') $currency

$footer
}$
}FLUYT$
//Syntax:FLUYT_CC
// $bean_cc{
package /* $package( */org.jproggy.snippetory.toolyng.beanery/* ) */;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.TemplateWrapper;
// $class{

/*$i*/public class /*$name( case="camelizeUpper"*/Bean/*)*/ extends TemplateWrapper {
/*$i*/  public /*$name(case="camelizeUpper"*/Bean/*)*/ (Template template) {
/*$i*/      super(template);
/*$i*/  }
// $region( prefix="\n" ){
/*$i*/  public /*$name(case="camelizeUpper"*/Bean/*)*/ getBean() { //$name(case="camelizeUpper" backward="get(Bean)") 
/*$i*/    return new $name/*case="camelizeUpper"*/(get("$name")); 
/*$i*/  } 
// }region$ 
// $location( prefix="\n" ){
/*$i*/  public /*$parent(case="camelizeUpper"*/Bean/*)*/ setBean(Object value) {//$name(
          case="camelizeUpper" backward="set(Bean)") 
/*$i*/    set("$name", value);  
/*$i*/    return this; 
/*$i*/  } 
// }location$
/// use region with full line mark up to avoid additional line breaks 
//$classes{
//}$
/*$i{*/  /*}$*/} 
// }class$
// }$