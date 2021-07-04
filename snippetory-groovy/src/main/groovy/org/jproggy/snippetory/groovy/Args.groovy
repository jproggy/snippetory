package org.jproggy.snippetory.groovy

import org.codehaus.groovy.runtime.InvokerHelper

class Args {

    static Closure getClosure(Object[] args) {
        if (args) {
            def lastParam = args[-1]
            if (lastParam instanceof Closure) return lastParam
        }
    }

    static List toList(Object[] args, closure) {
        if (closure) {
            if (args.length == 1) {
                return []
            }
            return args[0..-2]
        }
        InvokerHelper.asList(args)
    }

}
