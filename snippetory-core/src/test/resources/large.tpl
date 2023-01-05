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
<s:XML_ALIKE />
<t:XML_ALIKE>
<t:email>
{v:salutation} {v:name},

{v:text}

{v:name pad='40'}{v:quantity pad='10' pad.align='right'} {v:price pad='12' pad.align='right'} {v:sum pad='14' pad.align='right'}
====================================================================================
<t:cart_entry>
{v:name pad='40'}{v:quantity pad='10' pad.align='right'} {v:price pad='12' pad.align='right'} {v:sum pad='14' pad.align='right'} {v:currency}
</t:cart_entry>
-------------------------------------------------------------------------------------
{v:total-label                                         pad='62' pad.align='right'} {v:total pad='14' pad.align='right'} {v:currency}

{v:footer}
</t:email>
</t:XML_ALIKE>


--Syntax:FLUYT
$FLUYT{
$bean.java{
package $package;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.util.TemplateWrapper;
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

$name(pad='40') $quantity(pad='10' pad.align='right') $price(pad='12' pad.align='right') $sum(pad='18' pad.align='right')
====================================================================================
$cart_entry{
$name(pad='40') $quantity(pad='10' pad.align='right') $price(pad='12' pad.align='right') $(pad='14' pad.align='right'){$sum $currency}$
}$
-------------------------------------------------------------------------------------
$total-label(                                       pad='62' pad.align='right') $total(pad='14' pad.align='right') $currency

$footer
}$
}FLUYT$
//Syntax:FLUYT_CC
// $bean_cc{
package /* $package( */org.jproggy.snippetory.toolyng.beanery/* ) */;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.spi.util.TemplateWrapper;
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