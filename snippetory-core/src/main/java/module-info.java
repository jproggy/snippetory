module snippetory.core {
    exports org.jproggy.snippetory.engine to snippetory.sql;
    exports org.jproggy.snippetory.engine.build to snippetory.sql;
    exports org.jproggy.snippetory.engine.spi;
    exports org.jproggy.snippetory.spi;
    exports org.jproggy.snippetory.util;
    exports org.jproggy.snippetory;
    opens org.jproggy.snippetory.spi;
    opens org.jproggy.snippetory;
    opens org.jproggy.snippetory.util;
    requires java.desktop;
    requires java.sql;
    uses org.jproggy.snippetory.spi.Configurer;
}