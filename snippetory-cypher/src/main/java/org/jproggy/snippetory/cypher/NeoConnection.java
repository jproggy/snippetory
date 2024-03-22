package org.jproggy.snippetory.cypher;

import java.util.List;
import java.util.function.Function;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionConfig;

public class NeoConnection {
    private final Session session;

    public NeoConnection(Session session) {
        this.session = session;
    }

    public int execute(Statement s) {
        session.executeWrite(tx -> tx.run(s.toQuery()));
        return 3;
    }

    public List<Record> list(Statement s) {
        return run(s).list();
    }

    public  <T> List<T> list(Statement s, Function<Record, T> f) {
        return run(s).list(f);
    }

    public Record one(Statement s) {
        return run(s).single();
    }

    public Result run(Statement s) {
        return session.run(s.toQuery(), TransactionConfig.empty());
    }

}
