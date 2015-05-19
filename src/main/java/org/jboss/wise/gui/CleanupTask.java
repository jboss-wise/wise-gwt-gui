/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.wise.gui;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CleanupTask<T> {

   private TreeMap<Long, Holder<T>> refs = new TreeMap<Long, Holder<T>>(); //TreeMap to ensure key-based ordering

   public CleanupTask(boolean register) {
      if (register) {
         CleanupHelper.addTask(this);
      }
   }

   public synchronized void addRef(T obj, long expTimeMillis, CleanupCallback<T> cb) {
      refs.put(expTimeMillis, new Holder<T>(cb, obj));
   }

   public synchronized void refsCleanup() {
      long curTime = System.currentTimeMillis();
      boolean done = false;
      for (Iterator<Entry<Long, Holder<T>>> it = refs.entrySet().iterator(); it.hasNext() && !done; ) {
         Entry<Long, Holder<T>> entry = it.next();
         if (curTime > entry.getKey().longValue()) {
            Holder<T> h = entry.getValue();
            h.getCallback().cleanup(h.getData());
            it.remove();
         } else {
            done = true;
         }
      }
   }

   public synchronized void refsCleanupNoChecks() {
      for (Iterator<Entry<Long, Holder<T>>> it = refs.entrySet().iterator(); it.hasNext(); ) {
         Entry<Long, Holder<T>> entry = it.next();
         Holder<T> h = entry.getValue();
         h.getCallback().cleanup(h.getData());
         it.remove();
      }
   }

   public synchronized void removeRef(T obj) {
      boolean done = false;
      final Holder<T> r = new Holder<T>(obj);
      for (Iterator<Entry<Long, Holder<T>>> it = refs.entrySet().iterator(); it.hasNext() && !done; ) {
         Entry<Long, Holder<T>> entry = it.next();
         Holder<T> h = entry.getValue();
         if (h.equals(r)) {
            it.remove();
            done = true;
         }
      }
   }

   public static interface CleanupCallback<T> {
      public void cleanup(T data);
   }

   private static final class Holder<S> implements Comparable<Holder<S>> {
      private CleanupCallback<S> callback;
      private S data;

      public Holder(S data) {
         if (data == null) {
            throw new IllegalArgumentException();
         }
         this.data = data;
      }

      public Holder(CleanupCallback<S> callback, S data) {
         this(data);
         this.callback = callback;
      }

      public CleanupCallback<S> getCallback() {
         return callback;
      }

      public S getData() {
         return data;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof Holder)) {
            return false;
         }
         @SuppressWarnings("rawtypes")
         Holder d = (Holder) obj;
         return this.data.equals(d.data);
      }

      @Override
      public int hashCode() {
         return this.data.hashCode();
      }

      @Override
      public int compareTo(Holder<S> o) {

         if (this.equals(o)) {
            return 0;
         }
         return 0;
      }
   }
}
