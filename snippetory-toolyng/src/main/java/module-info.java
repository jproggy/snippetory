module snippetory.toolyng {
    requires snippetory.core;
    requires junit;
    exports org.jproggy.snippetory.toolyng.beanery;
    exports org.jproggy.snippetory.toolyng.test;
    exports org.jproggy.snippetory.toolyng.letter;
    opens org.jproggy.snippetory.toolyng.letter;
}