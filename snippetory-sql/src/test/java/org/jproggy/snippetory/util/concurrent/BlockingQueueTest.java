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

package org.jproggy.snippetory.util.concurrent;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class BlockingQueueTest {
  ExecutorService runner =  Executors.newCachedThreadPool();
  static Integer[][] data;
  final static int DATA_SETS = 10;
  final static int DATA_SIZE = 10_000;

  @BeforeClass
  public static void startup() {
    data = new Integer[DATA_SETS][];
    int i = 0;
    for (int set = 0; set < DATA_SETS; set++) {
      data[set] = new Integer[DATA_SIZE];
      for (int pos = 0; pos < DATA_SIZE; pos++) {
        data[set][pos] = i;
        ++i;
      }
    }
  }

  @Test( expected=QueueClosedException.class)
  public void testToString() throws Exception {
    ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(15);
    Source<String> src3;
    try (Sink<String> snk = queue.sink();
        Source<String> src1 = queue.source();
        Source<String> src2 = queue.source();) {
      snk.put("test1");
      assertEquals("[test1]", queue.toString());
      snk.put("test2");
      assertEquals("[test1, test2]", queue.toString());
      assertEquals("test1", src1.iterator().next());
      assertEquals("[test2]", queue.toString());
      assertEquals("test2", src2.iterator().next());
      assertEquals("[]", queue.toString());
      src3 = queue.source();
    }
    assertFalse(src3.iterator().hasNext());
    src3.close();
    queue.sink();
  }

  @Test
  public void test1_1() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(100);
    Future<Boolean> sendResult = send(queue, data[0]);
    Future<List<Integer>> result = receive(queue);
    monitor(sendResult, queue);
    assertTrue(sendResult.get());
    List<Integer> list = result.get();
    Set<Integer> set = new HashSet<>(list);
    assertEquals(DATA_SIZE, set.size());
    assertEquals(DATA_SIZE, list.size());
    assertEquals(DATA_SIZE, queue.taken());
  }

  @Test
  public void testClose() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
    Future<Boolean> sendResult = sendEndless(queue, data[0]);
    Future<List<Integer>> result = receive(queue);
    Thread.sleep(5);
    queue.close(false);
    List<Integer> list = result.get();
    assertEquals(list.size(), queue.taken());
    try {
      sendResult.get();
    } catch (ExecutionException e) {
      assertEquals(QueueClosedException.class, e.getCause().getClass());
    }
  }

  @Test
  public void testClose5() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
    Future<Boolean> sendResult = sendEndless(queue, data[0]);
    Future<List<Integer>> result1 = receive(queue);
    Future<List<Integer>> result2 = receive(queue);
    Future<List<Integer>> result3 = receive(queue);
    Future<List<Integer>> result4 = receive(queue);
    Future<List<Integer>> result5 = receive(queue);
    Thread.sleep(15);
    queue.close(false);
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    addChecked(set, result2.get());
    addChecked(set, result3.get());
    addChecked(set, result4.get());
    addChecked(set, result5.get());
    assertEquals(queue.taken(), set.size());
    try {
      sendResult.get();
    } catch (ExecutionException e) {
      assertEquals(QueueClosedException.class, e.getCause().getClass());
    }
  }

  @Test
  public void test3_1_15() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
    send(queue, data[0]);
    send(queue, data[1]);
    send(queue, data[2]);
    Future<List<Integer>> result = receive(queue);
    monitor(result, queue);
    List<Integer> list = result.get();
    Set<Integer> set = new HashSet<>(list);
    assertEquals(DATA_SIZE * 3, set.size());
    assertEquals(DATA_SIZE * 3, list.size());
    assertEquals(DATA_SIZE * 3, queue.taken());
  }

  @Test
  public void test3_1_100() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(100);
    send(queue, data[0]);
    send(queue, data[1]);
    send(queue, data[2]);
    Future<List<Integer>> result = receive(queue);
    monitor(result, queue);
    List<Integer> list = result.get();
    Set<Integer> set = new HashSet<>(list);
    assertEquals(DATA_SIZE * 3, set.size());
    assertEquals(DATA_SIZE * 3, list.size());
    assertEquals(DATA_SIZE * 3, queue.taken());
  }

  public void test10_1(BlockingQueue<Integer> queue) throws Exception {
    send(queue, data[0]);
    send(queue, data[1]);
    send(queue, data[2]);
    send(queue, data[3]);
    send(queue, data[4]);
    send(queue, data[5]);
    send(queue, data[6]);
    send(queue, data[7]);
    send(queue, data[8]);
    send(queue, data[9]);
    Future<List<Integer>> result = receive(queue);
    List<Integer> list = result.get();
    Set<Integer> set = new HashSet<>(list);
    assertEquals(DATA_SIZE * 10, set.size());
    assertEquals(DATA_SIZE * 10, list.size());
    assertEquals(DATA_SIZE * 10, queue.taken());
  }

  @Test
  public void test1_3() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
    Future<List<Integer>> result1 = receive(queue);
    Future<List<Integer>> result2 = receive(queue);
    Future<List<Integer>> result3 = receive(queue);
    send(queue, data[0]);
    monitor(result1, queue);
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    addChecked(set, result2.get());
    addChecked(set, result3.get());
    assertEquals(DATA_SIZE * 1, set.size());
    assertEquals(DATA_SIZE * 1, queue.taken());
  }

  @Test
  public void test1_3_10() throws Exception {
    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(15);
    Future<List<Integer>> result1 = receive(queue);
    Future<List<Integer>> result2 = receive(queue);
    Future<List<Integer>> result3 = receive(queue);
    try (Sink<Integer> sink = queue.sink()) {
      for (int set = 0; set < DATA_SETS; set++) {
        for (Integer n : data[set]) {
          sink.put(n);
        }
      }
    }
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    addChecked(set, result2.get());
    addChecked(set, result3.get());
    assertEquals(DATA_SIZE * 10, set.size());
    assertEquals(DATA_SIZE * 10, queue.taken());
  }

  @Test
  @Ignore // inherently slow as of short queue
  public void test10_10_1() throws Exception {
    test10_10(new ArrayBlockingQueue<Integer>(1));
  }

  @Test
  public void test10_10_10() throws Exception {
    test10_10(new ArrayBlockingQueue<Integer>(10));
  }

  @Test
  public void test10_10_100() throws Exception {
    test10_10(new ArrayBlockingQueue<Integer>(100));
  }

  @Test
  public void test10_10_1000() throws Exception {
    test10_10(new ArrayBlockingQueue<Integer>(1000));
  }

  @Test
  public void test10_10_10000() throws Exception {
    test10_10(new ArrayBlockingQueue<Integer>(10000));
  }

  @Test
  @Ignore
  public void test1_10_10000() throws Exception {
    test1_10(new ArrayBlockingQueue<Integer>(10000));
  }

  @Test
  public void test1_10_1000() throws Exception {
    test1_10(new ArrayBlockingQueue<Integer>(1000));
  }

  @Test
  public void test10_1_1000() throws Exception {
    test10_1(new ArrayBlockingQueue<Integer>(1000));
  }

  @Test
  public void test1_1_1000() throws Exception {
    test1_1(new ArrayBlockingQueue<Integer>(1000));
  }

  public void test10_10(BlockingQueue<Integer> queue) throws Exception {
    Future<List<Integer>> result1 = receive(queue);
    Future<List<Integer>> result2 = receive(queue);
    Future<List<Integer>> result3 = receive(queue);
    Future<List<Integer>> result4 = receive(queue);
    Future<List<Integer>> result5 = receive(queue);
    Future<List<Integer>> result6 = receive(queue);
    Future<List<Integer>> result7 = receive(queue);
    Future<List<Integer>> result8 = receive(queue);
    Future<List<Integer>> result9 = receive(queue);
    Future<List<Integer>> result10 = receive(queue);
    send(queue, data[0]);
    send(queue, data[1]);
    send(queue, data[2]);
    send(queue, data[3]);
    send(queue, data[4]);
    send(queue, data[5]);
    send(queue, data[6]);
    send(queue, data[7]);
    send(queue, data[8]);
    send(queue, data[9]);
    monitor(result1, queue);
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    addChecked(set, result2.get());
    addChecked(set, result3.get());
    addChecked(set, result4.get());
    addChecked(set, result5.get());
    addChecked(set, result6.get());
    addChecked(set, result7.get());
    addChecked(set, result8.get());
    addChecked(set, result9.get());
    addChecked(set, result10.get());
    assertEquals(DATA_SIZE * 10, set.size());
    assertEquals(DATA_SIZE * 10, queue.taken());
  }

  public void test1_1(BlockingQueue<Integer> queue) throws Exception {
    Future<List<Integer>> result1 = receive(queue);
    sendAll(queue);
    monitor(result1, queue);
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    assertEquals(DATA_SIZE * 10, set.size());
    assertEquals(DATA_SIZE * 10, queue.taken());
  }

  public void test1_10(BlockingQueue<Integer> queue) throws Exception {
    Future<List<Integer>> result1 = receive(queue);
    Future<List<Integer>> result2 = receive(queue);
    Future<List<Integer>> result3 = receive(queue);
    Future<List<Integer>> result4 = receive(queue);
    Future<List<Integer>> result5 = receive(queue);
    Future<List<Integer>> result6 = receive(queue);
    Future<List<Integer>> result7 = receive(queue);
    Future<List<Integer>> result8 = receive(queue);
    Future<List<Integer>> result9 = receive(queue);
    Future<List<Integer>> result10 = receive(queue);
    sendAll(queue);
    monitor(result1, queue);
    Set<Integer> set = new HashSet<>();
    addChecked(set, result1.get());
    addChecked(set, result2.get());
    addChecked(set, result3.get());
    addChecked(set, result4.get());
    addChecked(set, result5.get());
    addChecked(set, result6.get());
    addChecked(set, result7.get());
    addChecked(set, result8.get());
    addChecked(set, result9.get());
    addChecked(set, result10.get());
    assertEquals(DATA_SIZE * 10, set.size());
    assertEquals(DATA_SIZE * 10, queue.taken());
  }

  void monitor(Future<?> f, BlockingQueue<?> queue) throws InterruptedException {
    while (!f.isDone()) {
      Thread.sleep(0, 1);
      println("" + (System.nanoTime()) + " / " + queue.usage());
    }
  }

  <T> void addChecked(Set<T> target, List<T> data) {
    int oldSize = target.size();
    target.addAll(data);
    assertEquals(oldSize + data.size(), target.size());
  }

  private static void println(String x) {
    //System.out.println(x);
  }

  Future<Boolean> send(final BlockingQueue<Integer> target, final Integer[] data) {
    return runner.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try (Sink<Integer> sink = target.sink()) {
          for (Integer n: data) {
            sink.put(n);
          }
        }
        return Boolean.TRUE;
      }
    });
  }

  Future<Boolean> sendAll(final BlockingQueue<Integer> target) {
    return runner.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try (Sink<Integer> sink = target.sink()) {
          for (Integer[] set : data) {
            for (Integer n : set) {
              sink.put(n);
            }
          }
        }
        return Boolean.TRUE;
      }
    });
  }

  Future<Boolean> sendEndless(final BlockingQueue<Integer> target, final Integer[] data) {
    return runner.submit(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        try (Sink<Integer> sink = target.sink()) {
          while (true) {
            for (Integer n: data) {
              sink.put(n);
            }
          }
        }
      }
    });
  }

  Future<List<Integer>> receive(final BlockingQueue<Integer> data) {
    return runner.submit(new Callable<List<Integer>>() {
      @Override
      public List<Integer> call() throws Exception {
        try (Source<Integer> source = data.source()) {
          List<Integer> result = new ArrayList<>();
          for (final Integer n: source) result.add(n);
          return result;
        }
      }
    });
  }
}
