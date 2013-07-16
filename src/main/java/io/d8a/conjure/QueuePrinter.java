/*
 * Druid - a distributed column store.
 * Copyright (C) 2012  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.d8a.conjure;

import com.google.common.base.Throwables;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueuePrinter implements Printer {
    private final BlockingQueue<Object> queue;
    long waitTime;
    TimeUnit unit;

    public QueuePrinter(BlockingQueue<Object> queue, long waitTime, TimeUnit unit) {
        this.waitTime = waitTime;
        this.unit = unit;
        this.queue = queue;
    }

    @Override
    public void print(Object obj) {
        try {
            queue.offer(obj, waitTime, unit);
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

}
