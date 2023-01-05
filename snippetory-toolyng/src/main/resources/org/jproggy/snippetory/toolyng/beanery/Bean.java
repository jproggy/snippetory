//Syntax:FLUYT_CC
package /*$package(*/org.jproggy.snippetory.toolyng.beanery/*)*/;

import org.jproggy.snippetory.Template;
import org.jproggy.snippetory.util.TemplateWrapper;
// $class{

/*$i*/public class $RegionTpl$ extends TemplateWrapper {
/*$i*/	public $RegionTpl$(Template template) {
/*$i*/		super(template);
/*$i*/	}
/*$i*/	@Override
/*$i*/	public Template wrap(Template template) {
/*$i*/		return template;
/*$i*/	}
// $region( prefix="\n" ){
/*$i*/	public $RegionTpl$ get$Region$() { 
/*$i*/		return new $RegionTpl$(get("$region")); 
/*$i*/	} 
// }region$ 
// $location( prefix="\n" ){
/*$i*/  public $RegionTpl$ set$Name$(Object value) { 
/*$i*/    set("$name", value);  
/*$i*/    return this; 
/*$i*/  } 
/*$i*/  public $RegionTpl$ append$Name$(Object value) {
/*$i*/    append("$name", value);  
/*$i*/    return this; 
/*$i*/  } 
// }location$
/// use region with full line mark up to avoid additional line breaks 
//$classes{
//}$
/*$i{*/  /*}$*/} 
// }class$